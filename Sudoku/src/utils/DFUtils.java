package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class DFUtils {
	public static void registerAgent(Agent agent, String type, String name) throws FIPAException {
		DFAgentDescription dafd = new DFAgentDescription();
		dafd.setName(agent.getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		sd.setName(name);
		dafd.addServices(sd);
		DFService.register(agent, dafd);

	}
	
	public static AID findFirstAgent(Agent agent, String type, String name) throws FIPAException {
		List<AID> agents = findAgents(agent, type, name);
		return agents.size() > 0 ? agents.get(0) : null; 
	}
	
	public static List<AID> findAgents(Agent agent, String type, String name) throws FIPAException {
		List<AID> rec = new ArrayList<>();
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		sd.setName(name);
		template.addServices(sd);
		DFAgentDescription[] result = DFService.search(agent, template);
		for(DFAgentDescription desc : result) {
			rec.add(desc.getName());
		}
		return rec;
	}
}
