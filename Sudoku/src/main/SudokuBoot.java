package main;

import java.io.File;

import jade.wrapper.AgentContainer;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import jade.core.ProfileException;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import utils.Constants;

/**
 * Classe de lancement du conteneur d'agent secondaire pour la résolution 
 * de Sudoku à partir d'un fichier de configuration.
 */
public class SudokuBoot {
	public static void main(String[] args) {
		try {
			//Récupération du chemin absolu du fichier de configuration
			String config_path = SudokuBoot.class.getResource(File.separator + Constants.AUXILIARY_CONFIG).getPath();
			ContainerController cc = Runtime.instance().createAgentContainer(new ProfileImpl(config_path));
			
			//Lancement des agents
			cc.createNewAgent("simulation", "agent.SimulationAgent", null).start();;
			cc.createNewAgent("environment", "agent.EnvironmentAgent", null).start();;
			for(int i = 0; i < Constants.ANALYSE_AGENTS_COUNT; ++i) cc.createNewAgent("analyse" + i, "agent.AnalyseAgent", null).start();
		} catch(ProfileException | StaleProxyException e) {
			System.out.println("Unable to start auxiliary container.");
			e.printStackTrace();
		}
	}
}
