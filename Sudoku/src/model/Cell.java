package model;
import java.util.Arrays;
import java.util.List;

/**
 * Modèle représentant une cellule d'une grille de Sudoku.
 * @see Grid
 */
public class Cell {
	private int value;
	private List<Integer> possibleValues;
	private static final Integer[] initialPossibleValues = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
	
	public Cell(){
		this.value = 0;
	}

	public Cell(int value) {
		this.value = value;
		this.possibleValues = Arrays.asList(initialPossibleValues.clone());
	}
	
	public Cell(int value, List<Integer> possibleValues) {
		this.value = value;
		this.possibleValues = possibleValues;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public List<Integer> getPossibleValues() {
		return possibleValues;
	}
	
	public void setPossibleValues(List<Integer> possibleValues) {
		this.possibleValues = possibleValues;
	}

	public Cell copy() {
		return new Cell(getValue());
	}
}
