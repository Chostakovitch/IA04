package agent;

import behaviour.EnvironmentBehaviour;
import jade.core.Agent;
import utils.Constants;
import utils.DFUtils;

public class AnalyseAgent extends Agent {
private static final long serialVersionUID = 1L;
	
	@Override
	public void setup() {
		System.out.println("Agent " + getLocalName() + " started.");
	
		DFUtils.registerAgent(this, Constants.ANALYSE_DF, Constants.ANALYSE_DF);
		
		addBehaviour();
	}
}
