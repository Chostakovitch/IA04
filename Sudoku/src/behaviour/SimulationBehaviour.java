package behaviour;

import java.util.HashMap;
import java.util.Map;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SimulationBehaviour extends SequentialBehaviour{
	private static final long serialVersionUID = 1L;
	
	private static int NUMBER_OF_AGENTS = 27;
	private Map<Integer, String> agents;
	
	public SimulationBehaviour(Agent agent){
		super(agent);
		agents = new HashMap<>();
		addSubBehaviour(new WaitRegistrationBehaviour(agent));
	}
	
	public class WaitRegistrationBehaviour extends Behaviour {
		private static final long serialVersionUID = 1L;
		private int counter = NUMBER_OF_AGENTS;
		
		public WaitRegistrationBehaviour(Agent agent) {
			super(agent);
		}
		
		@Override
		public void action() {
			MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE); 
			ACLMessage answer = getAgent().receive(messageTemplate);
			if(answer == null) block();
			else {
				--counter;
				
			}
		}

		@Override
		public boolean done() {
			return counter == 0;
		}
		
	}
}
