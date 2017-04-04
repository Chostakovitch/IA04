package agent;

import behaviour.AnalyseBehaviour;
import behaviour.EnvironmentBehaviour;
import jade.core.Agent;
import utils.Constants;
import utils.DFUtils;

public class AnalyseAgent extends Agent {
private static final long serialVersionUID = 1L;
	
	@Override
	public void setup() {
		System.out.println("Agent " + getLocalName() + " started.");
		System.out.println(getLocalName());
		DFUtils.registerAgent(this, Constants.ANALYSE_DF, getLocalName());
		
		addBehaviour(new AnalyseBehaviour(this));
	}
}
