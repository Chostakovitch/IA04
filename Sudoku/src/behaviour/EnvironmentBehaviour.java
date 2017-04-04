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

public class EnvironmentBehaviour extends SequentialBehaviour {
	private static final long serialVersionUID = 1L;
	private Grid grid;

	public EnvironmentBehaviour(Agent a, Integer[] cells) {
		super(a);
		this.grid = generateGridFromArray(cells);
		addSubBehaviour(new AdvanceSimulationBehaviour(a));
	}
	
	class AdvanceSimulationBehaviour extends Behaviour {
		private static final long serialVersionUID = 1L;
		
		public AdvanceSimulationBehaviour(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			ACLMessage message = getAgent().receive();
			if(message == null) block();
			else {
				switch(message.getPerformative()) {
				case ACLMessage.REQUEST:
					SimulationModel model = Model.deserialize(message.getContent(), SimulationModel.class);
					ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
					request.addReceiver(DFUtils.findFirstAgent(getAgent(), Constants.ANALYSE_DF, model.getAgent().getName()));
					int index = model.getIndex();
					request.setContent(new AnalyseModel(getCellListFromIndex(index)).serialize());
					request.setConversationId(String.valueOf(model.getIndex()));
					getAgent().send(request);
					break;
				case ACLMessage.INFORM:
					AnalyseModel result = Model.deserialize(message.getContent(), AnalyseModel.class);
					setCellListFromIndex(result.getCells(), Integer.parseInt(message.getConversationId()));
				}
			}
		}

		@Override
		public boolean done() {
			return isGridResolved();
		}
		
		private List<Cell> getCellListFromIndex(int index) {
			if(index <= 9) return grid.getLine(index);
			else if(index <= 18) return grid.getColumn(index);
			return grid.getSquare(index);
		}
		
		private void setCellListFromIndex(List<Cell> cells, int index) {
			if(index <= 9) grid.setLineWithIntersection(index, cells);
			else if(index <= 18) grid.setColumnWithIntersection(index, cells);
			grid.setSquareWithIntersection(index, cells);
		}
		
		private boolean isGridResolved() {
			Cell[][] cells = grid.getGrid();
			for(int i = 0; i < cells.length; ++i) {
				for(int j = 0; j < cells[0].length; ++j) {
					if(cells[i][j].getValue() == 0) return false;
				}
			}
			return true;
		}
	}
	
	class SendEndNotificationBehaviour extends OneShotBehaviour {
		private static final long serialVersionUID = 1L;

		public SendEndNotificationBehaviour(Agent a) {
			super(a);
		}

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
