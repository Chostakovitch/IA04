package behaviour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
			//Pour chaque entier de la liste des possibles, associe les indexs des cellules où il est présent
			Map<Integer, List<Integer>> counts = new HashMap<>();
			//Initialisation
			for(int i = 0; i < 9; ++i) counts.put(i, new ArrayList<>());
			
			//Parcours de la liste des cellules
			for(int i = 0; i < cells.size(); ++i) {
				Cell cell = cells.get(i);
				//Algorithme 1 : une seule valeur possible
				if(firstAlgo(cell))
					//Algorithme 2 : mise à jour après modification de la valeur effective
					updateValue(cells, cell.getValue());
				
				//Référencement dans la Map pour l'algorithme 3
				else for(int possibleValue : cell.getPossibleValues()) counts.get(possibleValue).add(i);
			}
			
			//Algorithme 3 : une valeur présente dans une seule liste des possibles
			for(Entry<Integer, List<Integer>> entry : counts.entrySet()) {
				//Si une valeur n'est présente que dans une liste, on l'assigne et on vide la liste
				if(entry.getValue().size() == 1) {
					Cell cellToUpdate = cells.get(entry.getValue().get(0));
					cellToUpdate.setValue(entry.getKey());
					cellToUpdate.getPossibleValues().clear();
				}
			}
			
			//TODO implémenter les algorithmes
		}
		
		/**
		 * Implémentation du premier algorithme qui travaille sur une cellule unique.
		 * @param cell Cellule à traiter
		 * @return true si l'algorithme est applicable sur la cellule (i.e. si elle a été modifiée)
		 */
		private boolean firstAlgo(Cell cell) {
			List<Integer> possibleValues = cell.getPossibleValues();
			//Si une seule valeur possible, elle est choisie
			if(possibleValues.size() == 1) {
				int newValue = possibleValues.get(0);
				cell.setValue(newValue);
				possibleValues.clear();
				return true;
			}
			return false;
		}
		
		/**
		 * Méthode chargée d'éliminer une valeur de la liste des possibles
		 * lorsqu'elle a été assignée. Par exemple, si 3 est assigné à une cellule
		 * par les algorithmes de résolution, 3 est supprimé de la liste des possibles
		 * de toutes les cellules. Implémentation de l'algorithme 2.
		 * @param cells Liste de cellules
		 * @param newValue Valeur à supprimer des listes des possibles.
		 */
		private void updateValue(List<Cell> cells, int newValue) {
			for(Cell cell : cells) cell.getPossibleValues().remove(Integer.valueOf(newValue));
		}
	}
}
