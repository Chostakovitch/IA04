package agent;

import behaviour.AnalyseBehaviour;
import behaviour.EnvironmentBehaviour;
import jade.core.Agent;
import utils.Constants;
import utils.DFUtils;

/**
 * Agent d'analyse. S'enregistre auprès de la simulation 
 * et implémente les algorithmes de résolution du Sudoku.
 */
public class AnalyseAgent extends Agent {
private static final long serialVersionUID = 1L;
	
	@Override
	public void setup() {
		System.out.println("Agent " + getLocalName() + " started.");
		System.out.println(getLocalName());
		DFUtils.registerAgent(this, Constants.ANALYSE_DF, getLocalName());
		
		//Behaviour séquentiel
		addBehaviour(new AnalyseBehaviour(this));
	}
}
