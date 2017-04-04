package behaviour;

import java.util.List;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.AnalyseModel;
import model.RegisterModel;
import model.Cell;
import model.Model;
import utils.Constants;
import utils.DFUtils;

/**
 * Behaviour d'analyse principal. Gère deux sous-behaviour :
 * l'enregistrement auprès de la simulation,
 * puis les interactions avec l'environnement.
 */
public class AnalyseBehaviour extends SequentialBehaviour {
	private static final long serialVersionUID = 1L;

	public AnalyseBehaviour(Agent a) {
		super(a);
		addSubBehaviour(new RegisterBehaviour());
		addSubBehaviour(new ResolutionBehaviour());
	}

	/**
	 * Behaviour chargé du référencement de l'agent courant
	 * vers l'agent de simulation. 
	 */
	class RegisterBehaviour extends OneShotBehaviour {
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			//Le performative Subscribe dépeint l'idée de recevoir des notifications ultérieures
			ACLMessage message = new ACLMessage(ACLMessage.SUBSCRIBE);
			message.addReceiver(DFUtils.findFirstAgent(getAgent(), Constants.SIMULATION_DF, Constants.SIMULATION_DF));
			RegisterModel model = new RegisterModel(getAgent().getLocalName());
			message.setContent(model.serialize());
			getAgent().send(message);
		}
	}
	
	/**
	 * Behaviour chargé de l'implémentation des algorithmes de résolution
	 * du Sudoku. Travaille sur des cellules représentant indifféremment une ligne,
	 * une colonne ou un carré dans la grille. Répond à l'agent d'environnement.
	 */
	class ResolutionBehaviour extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = getAgent().receive(mt);
			if(message == null) block();
			else {
				AnalyseModel model = Model.deserialize(message.getContent(), AnalyseModel.class);
				
				//Résolution effective, modification des cellules
				performResolution(model.getCells());
				
				ACLMessage answer = message.createReply();
				answer.setPerformative(ACLMessage.INFORM);
				answer.setContent(model.serialize());
				getAgent().send(answer);
			}
		}

		/**
		 * Méthode effective de résolution. Implémente les quatre algorithmes
		 * décrits dans l'énoncé.
		 * @param cells Ensemble de GRID_SIZE cellules.
		 * @see Constants 
		 */
		private void performResolution(List<Cell> cells) {
			//TODO implémenter les algorithmes
		}
	}
}
