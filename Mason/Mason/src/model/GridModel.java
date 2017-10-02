package model;

import java.util.Random;

import sim.engine.SimState;
import sim.engine.Stoppable;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;
import util.Constants;

public class GridModel extends SimState {
	public SparseGrid2D grid = new SparseGrid2D(Constants.GRID_SIZE, Constants.GRID_SIZE);
	private static final Random random = new Random();
	private int numInsects = Constants.NUM_INSECT;
	
	public GridModel(long seed) {
		super(seed);
	}

	@Override
	public void start() {
		super.start();
		grid.clear();
		addFood();
		addAnts();
		numInsects = Constants.NUM_INSECT;
	}
	
	public SparseGrid2D getGrid() {
		return grid;
	}

	public void setGrid(SparseGrid2D grid) {
		this.grid = grid;
	}

	private void addAnts() {
		for(int i = 0; i < Constants.NUM_INSECT; ++i) {
			Ant ant = new Ant(i);
			grid.setObjectLocation(ant, getFreeLocation());
			Stoppable stop = schedule.scheduleRepeating(ant);
			ant.setStop(stop);
		}
	}
	
	private void addFood() {
		for(int i = 0; i < Constants.NUM_FOOD_CELL; ++i)
			grid.setObjectLocation(new FoodGroup(), getFreeLocation());
	}
	
	private Int2D getFreeLocation() {
		Int2D location = null;
		do {
			location = new Int2D(random.nextInt(grid.getWidth()), random.nextInt(grid.getHeight()) );
		} while (grid.getObjectsAtLocation(location.x,location.y) != null);
		return location;
	}

	public int getNumInsects() {
		return numInsects;
	}

	public void setNumInsects(int numInsects) {
		this.numInsects = numInsects;
	}
	
	public void decNumInsects() {
		--numInsects;
	}
}