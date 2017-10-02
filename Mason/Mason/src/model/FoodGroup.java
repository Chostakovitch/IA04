package model;

import java.util.Random;

import util.Constants;

public class FoodGroup {
	private int quantity;
	
	public FoodGroup() {
		quantity = Constants.MAX_FOOD;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public void decQuantity() {
		--quantity;
	}
	
	public boolean empty() {
		return quantity == 0;
	}

	@Override
	public String toString() {
		return String.valueOf(quantity);
	}
}
