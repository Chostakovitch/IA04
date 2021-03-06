package agent;

import behaviour.SimulationBehaviour;
import jade.core.Agent;
import utils.Constants;
import utils.DFUtils;

/**
 * Agent de simulation. 
 * Son behaviour principal est de type séquentiel et orchestre la résolution du Sudoku.
 */
public class SimulationAgent extends Agent{
	private static final long serialVersionUID = 1L;

	@Override
	public void setup() {
		System.out.println("Agent " + getLocalName() + " started");
		
		//Enregistrement via le DF, pour les agents d'analyse
		DFUtils.registerAgent(this, Constants.SIMULATION_DF, Constants.SIMULATION_DF);
		
		//Lancement du behaviour séquentiel contenant les sous-behaviours
		addBehaviour(new SimulationBehaviour(this));
	}
}
