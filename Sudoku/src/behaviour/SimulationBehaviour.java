package behaviour;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.Model;
import model.RegisterModel;
import model.SimulationModel;
import utils.Constants;
import utils.DFUtils;

/**
 * Ce behaviour de type séquentiel est composé de trois sous-behaviours :
 * - Attente de l'enregistrement des agents d'analyse ;
 * - Lancement de la simulation en arrière-plan ;
 * - Attente de la résolution du Sudoku notifiée par un agent externe (environnement).
 */
public class SimulationBehaviour extends SequentialBehaviour{
	private static final long serialVersionUID = 1L;
	private Map<Integer, RegisterModel> agents;
	
	public SimulationBehaviour(Agent agent){
		super(agent);
		agents = new HashMap<>();
		addSubBehaviour(new WaitRegistrationBehaviour(agent));
		TickerBehaviour simulation = new PerformSimulationBehaviour(agent, Constants.SIMULATION_FREQUENCY);
		addSubBehaviour(simulation);
		addSubBehaviour(new WaitEnvironmentBehaviour(agent, simulation));
	}
	
	/**
	 * Behaviour dédié à l'attente de l'enregistrement des agents d'analyse.
	 * Termine lorsque l'ensemble des agents sont enregistrés.
	 */
	public class WaitRegistrationBehaviour extends Behaviour {
		private static final long serialVersionUID = 1L;
		private int counter = Constants.ANALYSE_AGENTS_COUNT;
		
		public WaitRegistrationBehaviour(Agent agent) {
			super(agent);
		}
		
		@Override
		public void action() {
			//Les agents d'analyse s'enregistrent via des abonnements
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE); 
			ACLMessage answer = getAgent().receive(mt);
			if(answer == null) block();
			else {
				//Un agent s'est enregistré, on le référence
				RegisterModel model = Model.deserialize(answer.getContent(), RegisterModel.class);
				agents.put(counter--, model);
			}
		}

		@Override
		public boolean done() {
			return counter == 0;
		}
	}
	
	/**
	 * Behaviour permettant de lancer la simulation en envoyant
	 * périodiquement des signaux à l'agent d'environnement.
	 * 
	 * Pas d'implémentation de done(), l'arrêt est déclenché par
	 * l'agent WaitEnvironmentBehaviour via la méthode stop().
	 */
	public class PerformSimulationBehaviour extends TickerBehaviour {
		/**
		 * Référence vers l'agent d'environnement qui fait avancer la résolution
		 */
		AID environment;
		
		public PerformSimulationBehaviour(Agent a, long period) {
			super(a, period);
			environment = DFUtils.findFirstAgent(a, Constants.ENVIRONMENT_DF, Constants.ENVIRONMENT_DF);
		}

		private static final long serialVersionUID = 1L;

		@Override
		public void onTick() {
			//Envoi des demandes aux 27 agents
			for(Entry<Integer, RegisterModel> mapEntry : agents.entrySet()) {
				ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
				message.addReceiver(environment);
				message.setContent(new SimulationModel(mapEntry.getValue(), mapEntry.getKey()).serialize());
				getAgent().send(message);
			}
		}
	}
	
	/**
	 * Behaviour qui attend la notification de l'agent d'environnement
	 * pour terminer la simulation.
	 */
	public class WaitEnvironmentBehaviour extends OneShotBehaviour {
		private static final long serialVersionUID = 1L;
		
		/**
		 * Behaviour de simulation à terminer une fois la notification reçue
		 */
		private TickerBehaviour simulation;
		
		/**
		 * Référence vers l'environnement
		 */
		private AID environment;

		public WaitEnvironmentBehaviour(Agent a, TickerBehaviour simulation) {
			super(a);
			this.simulation = simulation;
			this.environment = DFUtils.findFirstAgent(getAgent(), Constants.ENVIRONMENT_DF, Constants.ENVIRONMENT_DF);
		}

		@Override
		public void action() {
			//On s'assure de matcher un message d'information => depuis <= l'agent d'environnement
			MessageTemplate mt = MessageTemplate.and( 
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchSender(environment)
				);
					
			ACLMessage notif = getAgent().receive(mt);
			if(notif == null) block();
			else {
				//On arrête la simulation
				simulation.stop();
			}
		}
	}
}
