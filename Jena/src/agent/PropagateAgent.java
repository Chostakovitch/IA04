package agent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import utils.Constants;
import utils.DFUtils;

public class PropagateAgent extends Agent {
	private static final long serialVersionUID = 1L;

	@Override
	public void setup() {
		System.out.println("Agent " + getLocalName() + " started.");
		
		addBehaviour(new PropagateBehaviour(this));
	}
	
	class PropagateBehaviour extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;
		private static final String prefix = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
				+ "PREFIX td5: <http://www.utc.fr/>\n";

		public PropagateBehaviour(PropagateAgent propagateAgent) {
			super(propagateAgent);
		}

		@Override
		public void action() {
			ACLMessage message = receive();
			if(message == null) block();
			else {
				switch(message.getPerformative()) {
				//Demande de la console JADE
				case ACLMessage.REQUEST:
					AID kbAgent = DFUtils.findFirstAgent(getAgent(), Constants.KB_AGENT, Constants.KB_AGENT);
					ACLMessage toSend = new ACLMessage(ACLMessage.REQUEST);
					toSend.addReceiver(kbAgent);
					toSend.setContent(prefix + message.getContent());
					getAgent().send(toSend);
					break;
				//Réception d'une réponse de l'agent KB
				case ACLMessage.INFORM:
					System.out.println(message.getContent());
					break;
				}
			}
		}
	}
}
