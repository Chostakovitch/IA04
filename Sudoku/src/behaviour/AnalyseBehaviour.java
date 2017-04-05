package behaviour;

import java.util.ArrayList;
import java.util.Collections;
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
				
				/* Résolution effective, modification des cellules
				 * En tant que les modifications locales peuvent modifier
				 * les listes d'autres cellules (e.g. algo. 2), on choisit
				 * de relancer les algorithmes tant qu'il y a une nouvelle
				 * modification à inférer. */
				boolean changed = false;
				do {
					changed = performResolution(model.getCells());
				} while(changed);
				
				ACLMessage answer = message.createReply();
				answer.setPerformative(ACLMessage.INFORM);
				answer.setContent(model.serialize());
				getAgent().send(answer);
			}
		}

		/**
		 * Méthode effective de résolution. Implémente les quatre algorithmes
		 * décrits dans l'énoncé. Effectue un unique changement avant de terminer
		 * afin de garantir la cohérence des changements ultérieurs.
		 * @param cells Ensemble de GRID_SIZE cellules.
		 * @return booléen indiquant si une modification a été effectuée
		 * @see Constants 
		 */
		private boolean performResolution(List<Cell> cells) {
			//Pour chaque entier de la liste des possibles, associe les indexs des cellules où il est présent
			Map<Integer, List<Integer>> counts = new HashMap<>();
			
			//Initialisation, clés = valeurs possibles dans une grille de Sudoku
			for(int i = 1; i <= 9; ++i) counts.put(i, new ArrayList<>());
			
			//Pour chaque liste des possibles composée de deux éléments, associe les index des cellules où cette liste existe (par égalité au sens Java)
			Map<List<Integer>, List<Integer>> twoValuesCounts = new HashMap<>();
			
			//Les listes des possibles sont ordonnées pour garantir la validité des comparaisons
			for(Cell cell : cells) Collections.sort(cell.getPossibleValues());
			
			//Parcours de la liste des cellules
			for(int i = 0; i < cells.size(); ++i) {
				Cell cell = cells.get(i);
				List<Integer> possibleValues = cell.getPossibleValues();
				//Algorithme 1 : une seule valeur possible
				if(firstAlgo(cell)) return true;
				
				//Algorithme 2 : mise à jour après modification de la valeur effective
				if(secondAlgo(cell, cells)) return true;
				
				//Référencement dans la Map pour l'algorithme 3
				for(int possibleValue : possibleValues) counts.get(possibleValue).add(i);
				
				//Référencement dans la Map pour l'algorithme 4
				if(possibleValues.size() == 2) {
					if(!twoValuesCounts.containsKey(possibleValues)) twoValuesCounts.put(possibleValues, new ArrayList<>());
					twoValuesCounts.get(possibleValues).add(i);
				}
			}
			
			//Algorithme 3 : une valeur présente dans une seule liste des possibles
			if(thirdAlgo(counts, cells)) return true;
			
			//Algorithme 4 : deux uniques valeurs dans deux uniques listes des possibles
			if(fourthAlgo(twoValuesCounts, cells)) return true;
			
			//Aucune modification : toutes les déductions sont faites
			return false;
		}
		
		/**
		 * Implémentation du premier algorithme qui travaille sur une cellule unique.
		 * Si une cellule n'a plus qu'une seule valeur dans sa liste des possibles,
		 * cette valeur lui est assignée et sa liste des possibles est vidée.
		 * @param cell Cellule à traiter
		 * @return true si l'algorithme est applicable sur la cellule (i.e. si elle a été modifiée)
		 */
		private boolean firstAlgo(Cell cell) {
			List<Integer> possibleValues = cell.getPossibleValues();
			//Si une seule valeur possible, elle est choisie
			if(possibleValues.size() == 1) {
				cell.setValue(possibleValues.get(0));
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
		 * @parem cell Cellule à traiter
		 * @param cells Liste de cellules
		 * @return true si l'algorithme a modifié les cellules
		 */
		private boolean secondAlgo(Cell cell, List<Cell> cells) {
			boolean changed = false;
			//Si une des cellules contenait la valeur dans sa liste des possibles, changed vaudra true et elle est supprimée
			if(cell.getPossibleValues().isEmpty()) {
				for(Cell otherCell : cells) changed |= otherCell.getPossibleValues().remove(Integer.valueOf(cell.getValue()));
			}
			return changed;
		}
		
		/**
		 * Implémentation du troisième algorithme. Si une valeur n'est contenue que dans 
		 * une seule liste des possibles, elle est assignée à la cellule concernée et
		 * sa liste des possibles est vidée.
		 * @param counts Map liant les entiers de la grille aux indices de leurs occurences dans la liste des possibles des cellules
		 * @param cells Ensemble de cellules concernées par l'indice indiqué dans counts
		 * @return true si l'algorithme a modifié les cellules
		 */
		private boolean thirdAlgo(Map<Integer, List<Integer>> counts, List<Cell> cells) {
			for(Entry<Integer, List<Integer>> entry : counts.entrySet()) {
				//Si une valeur n'est présente que dans une liste, on l'assigne et on vide la liste
				if(entry.getValue().size() == 1) {
					Cell cellToUpdate = cells.get(entry.getValue().get(0));
					cellToUpdate.setValue(entry.getKey());
					cellToUpdate.getPossibleValues().clear();
					return true;
				}
			}
			return false;
		}
		
		/**
		 * Implémentation du quatrième algorithme. Si deux et seulement deux listes des possibles
		 * contiennent deux et seulement deux valeurs identiques, ces valeurs sont supprimées 
		 * des autres listes des possibles (il n'y a alors que deux choix possibles).
		 * 
		 * L'état des cellules est supposé sain, i.e. il ne peut exister plus de deux listes des possibles
		 * composées des deux mêmes valeurs uniquement (= non résolvable).
		 * @param counts Map liant les listes des possibles à deux entiers aux indices des cellules correspondantes 
		 * @param cells Ensemble de cellules concernées par l'indice indiqué dans counts
		 * @return true si l'algorithme a modifié les cellules
		 */
		private boolean fourthAlgo(Map<List<Integer>, List<Integer>> counts, List<Cell> cells) {
			for(Entry<List<Integer>, List<Integer>> entry : counts.entrySet()) {
				//Si la liste des possibles est présente dans exactement deux cellules, on supprime les valeurs des autres listes
				if(entry.getValue().size() == 2) {
					for(int i = 0; i < cells.size(); ++i) {
						//Suppression sauf pour les cellules concernées par la liste à deux éléments
						if(!entry.getValue().contains(i)) {
							cells.get(i).getPossibleValues().removeAll(entry.getKey());
						}
					}
					return true;
				}
			}
			return false;
		}
	}
}
