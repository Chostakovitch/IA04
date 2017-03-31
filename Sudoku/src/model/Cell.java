package model;
import java.util.List;

public class Cell {
	private int value;
	private List<Integer> possibleValues;
	
	public Cell(){
		this.value = 0;
	}

	public Cell(int value) {
		this.value = value;
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
	
	public Cell copy() {
		return new Cell(getValue());
	}
}
