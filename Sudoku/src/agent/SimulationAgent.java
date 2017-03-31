package agent;

import behaviour.SimulationBehaviour;
import jade.core.Agent;
import jade.domain.FIPAException;
import utils.DFUtils;

/**
 * Agent de simulation. 
 * Son behaviour principal est de type séquentiel et orchestre la résolution du Sudoku.
 */
public class SimulationAgent extends Agent{
	private static final long serialVersionUID = 1L;

	@Override
	public void setup() {
		System.out.println(getLocalName() + " started");
		
		//Enregistrement via le DF, pour les agents d'analyse
		try {
			DFUtils.registerAgent(this, "simulation", "simulation");
		} catch (FIPAException e) {
			System.out.println("cannot register " + getLocalName());
		}
		
		//Lancement du behaviour séquentiel contenant les sous-behaviours
		addBehaviour(new SimulationBehaviour(this));
	}
}
