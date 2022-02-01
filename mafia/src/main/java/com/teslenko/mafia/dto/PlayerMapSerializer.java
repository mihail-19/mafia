package com.teslenko.mafia.dto;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.teslenko.mafia.entity.Player;

public class PlayerMapSerializer  extends JsonSerializer<Player>{
	 private ObjectMapper mapper = new ObjectMapper();
	@Override
	public void serialize(Player value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		
	}

}
