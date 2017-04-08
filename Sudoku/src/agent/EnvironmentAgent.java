package agent;

import behaviour.EnvironmentBehaviour;
import jade.core.Agent;
import utils.Constants;
import utils.DFUtils;

/**
 * Agent d'environnement. Gère la grille de Sudoku
 * et répond aux exigences de la simulation. Commande
 * les agents d'analyse.
 */
public class EnvironmentAgent extends Agent {
	private static final long serialVersionUID = 1L;
	
	@Override
	public void setup() {
		System.out.println("Agent " + getLocalName() + " started.");
	
		DFUtils.registerAgent(this, Constants.ENVIRONMENT_DF, Constants.ENVIRONMENT_DF);
		
		//Behaviour séquentiel
		addBehaviour(new EnvironmentBehaviour(this, Constants.GRIDS[4]));
	}
}
