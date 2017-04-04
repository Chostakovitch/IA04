package model;

import java.util.ArrayList;
import java.util.List;

import utils.Constants;

//TODO TOSTRING

/**
 * Modèle représentant une grille de Sudoku.
 */
public class Grid {
	private Cell[][] grid;

	/**
	 * Constructeur de recopie.
	 * @param grid Grille de Sudoku à copier.
	 */
	public Grid(Cell[][] grid) {
		if(grid.length != Constants.GRID_SIZE || grid[0].length != Constants.GRID_SIZE)
			throw new IllegalArgumentException("Wrong dimensions of grid");
		this.grid = copyArray(grid);
	}
	
	public Grid() {
		this.grid = new Cell[Constants.GRID_SIZE][Constants.GRID_SIZE]; 
	}
	
	/**
	 * Copie générique d'une matrice de cellules.
	 * @param matrix Matrice de cellules.
	 * @return Copie de la matrice de celulles.
	 * @see Cell
	 */
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
	
	public void setGrid(Cell[][] grid) {
		this.grid = grid;
	}
	
	public List<Cell> getLine(int index) {
		List<Cell> line = new ArrayList<>();
		if(index > Constants.GRID_SIZE) return line;
		for(int i = 0; i < grid.length; ++i) line.add(grid[index][i].copy());
		return line;
	}
	
	public List<Cell> getColumn(int index) {
		List<Cell> column = new ArrayList<>();
		if(index > Constants.GRID_SIZE) return column;
		for(int i = 0; i < grid.length; ++i) column.add(grid[i][index].copy());
		return column;
	}
	
	public List<Cell> getSquare(int index) {
		List<Cell> square = new ArrayList<>();
		if(index > Constants.GRID_SIZE) return square;
		//Division entière, pas équivalent à l'identité
		int lines = (index / 3) * 3;
		int columns = (index % 3) * 3;
		for(int i = lines; i < lines + 3; ++i) {
			for(int j = columns; j < columns + 3; ++j) {
				square.add(grid[i][j].copy());
			}
		}
		return square;
	}
	
	public void setLineWithIntersection(int index, List<Cell> line) {
		if(index > Constants.GRID_SIZE) return;
		for(int i = 0; i < grid.length; ++i) {
			Cell newCell = line.get(i);
			newCell.getPossibleValues().retainAll(grid[index][i].getPossibleValues());
			grid[index][i] = newCell;
		}
	}
	
	public void setColumnWithIntersection(int index, List<Cell> column) {
		if(index > Constants.GRID_SIZE) return;
		for(int i = 0; i < grid.length; ++i) {
			Cell newCell = column.get(i);
			newCell.getPossibleValues().retainAll(grid[index][i].getPossibleValues());
			grid[i][index] = newCell;
		}
	}
	
	public void setSquareWithIntersection(int index, List<Cell> square) {
		if(index > Constants.GRID_SIZE) return;
		//Division entière, pas équivalent à l'identité
		int lines = (index / 3) * 3;
		int columns = (index % 3) * 3;
		for(int i = lines; i < lines + 3; ++i) {
			for(int j = columns; j < columns + 3; ++j) {
				Cell newCell = square.get((i - lines) * 3 + (j - columns)).copy();
				newCell.getPossibleValues().retainAll(grid[index][i].getPossibleValues());
				grid[i][j] = newCell;
			}
		}
	}
}
