package com.iolite.kpcamera.attendance.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomCocurricularLeaveDeserializer extends JsonDeserializer<List<String>> {

	@Override
	public List<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
	    String value = p.getValueAsString();

	    if ("[]".equals(value)) {
	        return new ArrayList<String>();  // Return an empty list
	    }

	    // Handle other formats (if needed)
	    ArrayNode arrayNode = p.readValueAsTree();
	    List<String> result = new ArrayList<String>();
	    
	    // Traditional for-each loop instead of forEach with lambda
	    for (int i = 0; i < arrayNode.size(); i++) {
	        JsonNode node = arrayNode.get(i);
	        result.add(node.asText());  // Add the text value of each node
	    }

	    return result;
	}
}

