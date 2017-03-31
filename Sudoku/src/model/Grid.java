package model;

public class Grid {
	private static int SIZE = 9;
	private Cell[][] grid;

	public Grid(Cell[][] grid) {
		if(grid.length != SIZE || grid[0].length != SIZE)
			throw new IllegalArgumentException("Wrong dimensions of grid");
		this.grid = copyArray(grid);
	}
	
	public Grid() {
		this.grid = new Cell[SIZE][SIZE]; 
	}
	
	private static Cell[][] copyArray(Cell[][] matrix) {
		if(matrix == null) return null;
		int lines = matrix.length;
		int columns = matrix[0].length;
		Cell[][] newMatrix = new Cell[lines][columns];
		for(int i = 0; i < lines; ++i) {
			newMatrix[i] = new Cell[columns];
			for(int j = 0; i < columns; ++j)
				newMatrix[i][j] = matrix[i][j].copy();
		}
		return newMatrix;
	}

	public Cell[][] getGrid() {
		return grid;
	}
}
