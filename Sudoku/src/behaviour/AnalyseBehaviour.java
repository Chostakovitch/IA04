package behaviour;

import java.util.List;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.AnalyseModel;
import model.RegisterModel;
import model.Cell;
import model.Model;
import utils.Constants;
import utils.DFUtils;

public class AnalyseBehaviour extends SequentialBehaviour {
	private static final long serialVersionUID = 1L;

	public AnalyseBehaviour(Agent a) {
		super(a);
		addSubBehaviour(new RegisterBehaviour());
		addSubBehaviour(new ResolutionBehaviour());
	}

	class RegisterBehaviour extends OneShotBehaviour {
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			ACLMessage message = new ACLMessage(ACLMessage.SUBSCRIBE);
			message.addReceiver(DFUtils.findFirstAgent(getAgent(), Constants.SIMULATION_DF, Constants.SIMULATION_DF));
			RegisterModel model = new RegisterModel(getAgent().getLocalName());
			message.setContent(model.serialize());
			getAgent().send(message);
		}
	}
	
	class ResolutionBehaviour extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = getAgent().receive(mt);
			if(message == null) block();
			else {
				AnalyseModel model = Model.deserialize(message.getContent(), AnalyseModel.class);
				performResolution(model.getCells());
				ACLMessage answer = message.createReply();
				answer.setPerformative(ACLMessage.INFORM);
				answer.setContent(model.serialize());
				getAgent().send(answer);
			}
		}

		private void performResolution(List<Cell> cells) {
			
		}
	}
}
