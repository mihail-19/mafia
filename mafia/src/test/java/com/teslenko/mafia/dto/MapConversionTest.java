package com.teslenko.mafia.dto;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.teslenko.mafia.entity.Player;

public class MapConversionTest {
	 ObjectMapper mapper = new ObjectMapper();
	 @JsonSerialize(keyUsing = T1Deserializer.class) 
	  HashMap<Object, Object> hashmap = new HashMap<>();
	@Test
	public void simple() {
		Map<Player, Player> m = new HashMap<>();
		Player p1 = new Player("p1");
		Player p2 = new Player("p2");
		m.put(p1, p2);
		String json;
		
		
		  T1 t1 = new T1(1, "one");
		  T1 t2 = new T1(2, "two");
		  hashmap.put(t1, t2);
		  try {
			json = mapper.writeValueAsString(hashmap);
			System.out.println(json);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	private class T1Deserializer extends JsonSerializer<T1>{

		@Override
		public void serialize(T1 value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			ObjectMapper mapper = new ObjectMapper();
			StringWriter w = new StringWriter();
			mapper.writeValue(w, value);
			gen.writeFieldName(w.toString());
		}
		
	}
	private static class T1{
		private int id;
		private String name;
		
		public T1(int id, String name) {
			super();
			this.id = id;
			this.name = name;
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		@Override
		public String toString() {
			return "\"id\": " + id + ", name: " + name;
		}
	}
}
