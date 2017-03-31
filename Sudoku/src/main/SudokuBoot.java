package main;

import java.io.File;

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
			Runtime.instance().createAgentContainer(new ProfileImpl(config_path));
		} catch(ProfileException e) {
			System.out.println("Unable to start auxiliary container.");
			e.printStackTrace();
		}
	}
}
