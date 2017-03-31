package behaviour;

import java.util.HashMap;
import java.util.Map;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.Model;
import model.RegisterModel;
import utils.Constants;

/**
 * Ce behaviour de type séquentiel est composé de trois sous-behaviours :
 * - Attente de l'enregistrement des agents d'analyse ;
 * - Lancement de la simulation en arrière-plan ;
 * - Attente de la résolution du Sudoku notifiée par un agent externe (environnement).
 */
public class SimulationBehaviour extends SequentialBehaviour{
	private static final long serialVersionUID = 1L;
	private Map<Integer, String> agents;
	
	public SimulationBehaviour(Agent agent){
		super(agent);
		agents = new HashMap<>();
		addSubBehaviour(new WaitRegistrationBehaviour(agent));
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
				agents.put(counter--, model.getName());
			}
		}

		@Override
		public boolean done() {
			return counter == 0;
		}
		
	}
}
