package agent;

import jade.core.Agent;
import jade.domain.FIPAException;
import utils.DFUtils;

public class SimulationAgent extends Agent{
	private static final long serialVersionUID = 1L;

	@Override
	public void setup(){
		System.out.println(getLocalName() + " initialization");
		try {
			DFUtils.registerAgent(this, "simulation", "simulation");
		} catch (FIPAException e) {
			System.out.println("cannot register " + getLocalName());
		}
	}
	
	
}
