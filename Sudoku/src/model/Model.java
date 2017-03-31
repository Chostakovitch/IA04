package model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class Model {
	private static final ObjectMapper mapper = new ObjectMapper();
	
	public String serialize() {
		try {
			return getMapper().writeValueAsString(this);
		} catch(JsonProcessingException ex) { return null; }
	}
	
	public abstract void deserialize(String serial);
	
	protected static ObjectMapper getMapper() {
		return mapper;
	}
}
