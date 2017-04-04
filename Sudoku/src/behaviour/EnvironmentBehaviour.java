package behaviour;

import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import model.AnalyseModel;
import model.Cell;
import model.Grid;
import model.Model;
import model.SimulationModel;
import utils.Constants;
import utils.DFUtils;

/**
 * Behaviour principal pour l'environnement. Ce behaviour 
 * est une interface entre la simulation et l'analyse.
 * Sous l'impulsion de la simulation, il réalise une étape d'analyse
 * pour l'entité de la grille correspondante. Il notifie la simulation
 * lorsque le Sudoku est entièrement résolu.
 */
public class EnvironmentBehaviour extends SequentialBehaviour {
	private static final long serialVersionUID = 1L;
	private Grid grid;

	public EnvironmentBehaviour(Agent a, Integer[] cells) {
		super(a);
		this.grid = generateGridFromArray(cells);
		addSubBehaviour(new AdvanceSimulationBehaviour());
		addSubBehaviour(new SendEndNotificationBehaviour());
	}
	
	/**
	 * Behaviour s'exécutant jusqu'à ce que la grille soit résolue.
	 * Reçoit les ordres de l'agent de simulation et converse avec 
	 * l'agent d'analyse correspondant.
	 */
	class AdvanceSimulationBehaviour extends Behaviour {
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			ACLMessage message = getAgent().receive();
			if(message == null) block();
			else {
				switch(message.getPerformative()) {
				//Message de la simulation
				case ACLMessage.REQUEST:
					SimulationModel model = Model.deserialize(message.getContent(), SimulationModel.class);
					ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
					
					//Le message contient le nom précis de l'agent d'analyse enregistré dans le DF
					request.addReceiver(DFUtils.findFirstAgent(getAgent(), Constants.ANALYSE_DF, model.getAgent().getName()));
					
					//Indexé à partir de 0!
					int index = model.getIndex();
					
					//Récupération des cellules correspondant à la convention
					request.setContent(new AnalyseModel(getCellListFromIndex(index)).serialize());
					
					//L'ID de la conversation correspond à celui de l'entité manipulée (unique pour un tour de simulation)
					request.setConversationId(String.valueOf(model.getIndex()));
					
					//Envoi
					getAgent().send(request);
					break;
				//Message de l'analyse
				case ACLMessage.INFORM:
					AnalyseModel result = Model.deserialize(message.getContent(), AnalyseModel.class);
					setCellListFromIndex(result.getCells(), Integer.parseInt(message.getConversationId()));
					break;
				}
			}
		}

		@Override
		public boolean done() {
			return isGridResolved();
		}
		
		/**
		 * Récupération d'une entité de la grille.
		 * La convention de l'index est la suivante :
		 * - de 0 à GRID_SIZE - 1 : ligne ;
		 * - de GRID_SIZE à GRID_SIZE * 2 - 1 : colonne ;
		 * - de GRID_SIZE * 2 à GRID_SIZE * 3 - 1 : carré.
		 * @param index Indice de l'entité (0-27)
		 * @return Liste de cellules.
		 * @see Constants
		 */
		private List<Cell> getCellListFromIndex(int index) {
			if(index < Constants.GRID_SIZE) return grid.getLine(index % Constants.GRID_SIZE);
			else if(index < Constants.GRID_SIZE * 2) return grid.getColumn(index % Constants.GRID_SIZE);
			return grid.getSquare(index % Constants.GRID_SIZE);
		}
		
		/**
		 * Mise à jour d'une entité de la grille. La convention est la même que pour la récupération
		 * @param cells Liste de cellules
		 * @param index Indice de l'entité
		 * @see getCellListFromIndex
		 */
		private void setCellListFromIndex(List<Cell> cells, int index) {
			if(index < Constants.GRID_SIZE) grid.setLineWithIntersection(index % Constants.GRID_SIZE, cells);
			else if(index < Constants.GRID_SIZE * 2) grid.setColumnWithIntersection(index % Constants.GRID_SIZE, cells);
			grid.setSquareWithIntersection(index % Constants.GRID_SIZE, cells);
		}
		
		/**
		 * Permet de connaître l'état de résolution de la grille.
		 * @return true si la grille est résolue, false sinon.
		 */
		private boolean isGridResolved() {
			Cell[][] cells = grid.getGrid();
			for(int i = 0; i < cells.length; ++i) {
				for(int j = 0; j < cells[0].length; ++j) {
					//Si une seule des valeurs de la grille n'est pas assignée, la résolution n'est pas finie
					if(cells[i][j].getValue() == 0) return false;
				}
			}
			return true;
		}
	}
	
	/**
	 * Behaviour permettant de notifier l'agent de simulation
	 * que la résolution est terminée.
	 */
	class SendEndNotificationBehaviour extends OneShotBehaviour {
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			ACLMessage message = new ACLMessage(ACLMessage.INFORM);
			message.addReceiver(DFUtils.findFirstAgent(getAgent(), Constants.SIMULATION_DF, Constants.SIMULATION_DF));
			getAgent().send(message);
		}
	}

	/**
	 * Génère une grille avec la liste des possibles initialisée par identité
	 * à partir d'un tableau linéaire d'entiers.
	 * @param cells Tableau d'entiers
	 * @return Grille de Sudoku
	 */
	private Grid generateGridFromArray(Integer[] cells) {
		if(cells.length != Constants.GRID_SIZE * Constants.GRID_SIZE) {
			System.err.println("Wrong dimensions for Sudoku grid");
			System.exit(-1);
		}
		
		Cell[][] grid = new Cell[Constants.GRID_SIZE][Constants.GRID_SIZE]; 
		for(int i = 0; i < cells.length; ++i) {
			grid[i / Constants.GRID_SIZE][i % Constants.GRID_SIZE] = new Cell(cells[i]);
		}
		
		Grid returnGrid = new Grid();
		returnGrid.setGrid(grid);
		return returnGrid;
	}
}
