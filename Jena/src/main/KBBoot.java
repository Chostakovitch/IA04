package main;

import java.io.File;

import jade.core.ProfileException;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import utils.Constants;

/**
 * Classe de lancement du conteneur d'agent secondaire pour l'interrogation de bases de connaissances distantes.
 */
public class KBBoot {
	public static void main(String[] args) {
		try {
			//Récupération du chemin absolu du fichier de configuration
			String config_path = KBBoot.class.getResource(File.separator + Constants.AUXILIARY_CONFIG).getPath();
			ContainerController cc = Runtime.instance().createAgentContainer(new ProfileImpl(config_path));
			
			//Lancement des agents
			cc.createNewAgent(Constants.KB_AGENT, "agent.KBAgent", null).start();
			cc.createNewAgent(Constants.PROPAGATE_AGENT, "agent.PropagateAgent", null).start();
		} catch(ProfileException | StaleProxyException e) {
			System.out.println("Unable to start auxiliary containers.");
			e.printStackTrace();
		}
	}
}