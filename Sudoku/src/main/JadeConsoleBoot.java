package main;

import java.io.File;

import jade.core.ProfileException;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import utils.Constants;

/**
 * Classe de lancement de la console JADE à partir d'un fichier de configuration,
 * via un conteneur d'agent principal.
 */
public class JadeConsoleBoot {
	public static void main(String[] args) {
		try {
			//Récupération du chemin absolu du fichier de configuration
			String config_path = JadeConsoleBoot.class.getResource(File.separator + Constants.GUI_CONFIG).getPath();
			Runtime.instance().createMainContainer(new ProfileImpl(config_path));
		} catch (ProfileException e) {
			System.out.println("Unable to start main container.");
			e.printStackTrace();
		}
	}
}
