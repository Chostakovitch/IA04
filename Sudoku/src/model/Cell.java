package model;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Modèle représentant une cellule d'une grille de Sudoku.
 * @see Grid
 */
public class Cell {
	private int value;
	private List<Integer> possibleValues;
	
	/**
	 * Constructeur par défaut, pour une cellule indéterminée.
	 * La liste des valeurs possibles est maximale.
	 */
	public Cell(){
		this(0);
	}

	/**
	 * Constructeur pour une cellule déterminée.
	 * La liste des valeurs possibles est vide.
	 * @param value Valeur de la cellule
	 */
	public Cell(int value) {
		this.value = value;
		this.possibleValues = new ArrayList<>();
		if(value == 0) for(int i = 1; i <= 9; ++i) possibleValues.add(i);
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
		return new Cell(getValue(), new ArrayList<Integer>(getPossibleValues()));
	}
}
