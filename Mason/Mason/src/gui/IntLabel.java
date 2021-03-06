package gui;

import model.Ant;
import model.FoodGroup;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.SimplePortrayal2D;
import sim.portrayal.simple.LabelledPortrayal2D;

public class IntLabel extends LabelledPortrayal2D {
	private static final long serialVersionUID = 1L;
	
	public IntLabel(SimplePortrayal2D child, String label) {
		super(child, label);
	}

	@Override
	public String getLabel(Object object, DrawInfo2D info) {
		if(object instanceof Ant) {
			return ((Ant)object).toString();
		}
		else if(object instanceof FoodGroup) {
			return ((FoodGroup)object).toString();
		}
		return "Unknown Object";
	}
}
