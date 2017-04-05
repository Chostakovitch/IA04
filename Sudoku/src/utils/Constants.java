package utils;

/**
 * Classe utilitaire contenant les constantes utiles aux agents.
 */
public class Constants {
	public static final String GUI_CONFIG = "main_config";
	public static final String AUXILIARY_CONFIG = "secondary_config";
	public static int ANALYSE_AGENTS_COUNT = 27;
	public static String ENVIRONMENT_DF = "environment";
	public static String SIMULATION_DF = "simulation";
	public static String ANALYSE_DF = "analyse";
	
	/**
	 * Largeur et longueur d'une grille de Sudoku
	 */
	public static Integer GRID_SIZE = 9;
	
	/**
	 * Fr�quence d'envoi des demandes � l'agent d'environnement
	 */
	public static long SIMULATION_FREQUENCY = 5000;
	
	public static Integer[] GRID_1 = new Integer[] { 5, 0, 0, 0, 0, 4, 0, 0, 8, 0, 1, 0, 9, 0, 7, 0, 0, 0, 0, 9, 2, 8, 5, 0, 7, 0, 6, 7, 0, 0, 3, 0, 1, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 2, 0, 8, 0, 0, 1, 1, 0, 8, 0, 3, 2, 4, 9, 0, 0, 0, 0, 1, 0, 6, 0, 5, 0, 3, 0, 0, 7, 0, 0, 0, 0, 2 };
}