package agent;

import java.io.File;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import main.JenaMain;
import utils.Constants;
import utils.DFUtils;

public class KBAgent extends Agent {
	private static final long serialVersionUID = 1L;

	@Override
	public void setup(){
		System.out.println("Agent " + getLocalName() + " started.");
		DFUtils.registerAgent(this, Constants.KB_AGENT, Constants.KB_AGENT);
		
		addBehaviour(new KBBehaviour(this));
	}
	
	class KBBehaviour extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;

		
		public KBBehaviour(Agent a) {
			super(a);
		}


		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			
			ACLMessage message = getAgent().receive(mt);
			if(message == null) block();
			else {
				String query = message.getContent();
				System.out.println(query);
				String ontoPath = JenaMain.class.getResource(File.separator + Constants.ONTOLOGY).getPath();
				ACLMessage answer = message.createReply();
				answer.setContent(runSelectQuery(query, ontoPath));
				answer.setPerformative(ACLMessage.INFORM);
				getAgent().send(answer);
			}
		}
		
		private String runSelectQuery(String queryStr, String ontoPath) {
			Model model = ModelFactory.createDefaultModel();
			FileManager.get().readModel(model, ontoPath);
			Query query = QueryFactory.create(queryStr);
			QueryExecution qExec = QueryExecutionFactory.create(query, model);
			ResultSet r = qExec.execSelect();
			String results = ResultSetFormatter.asText(r);
			qExec.close();
			return results;
		}
	}
}
