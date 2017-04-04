package agent;

import behaviour.EnvironmentBehaviour;
import jade.core.Agent;
import utils.Constants;
import utils.DFUtils;

public class EnvironmentAgent extends Agent {
	private static final long serialVersionUID = 1L;
	
	@Override
	public void setup() {
		System.out.println("Agent " + getLocalName() + " started.");
	
		DFUtils.registerAgent(this, Constants.ENVIRONMENT_DF, Constants.ENVIRONMENT_DF);
		
		addBehaviour(new EnvironmentBehaviour(this, Constants.GRID_1));
	}
}
