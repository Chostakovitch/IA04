package model;

import java.io.IOException;

public class RegisterModel extends Model {
	private String name;

	public RegisterModel() { }
	
	public RegisterModel(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void deserialize(String serial) {
		RegisterModel model;
		try {
			model = getMapper().readValue(serial, this.getClass());
			this.name = model.name;
		} catch (IOException e) { }
	}
}
