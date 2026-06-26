package com.iolite.kpcamera.attendance.model.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlgRule {

    public StructuresRule structuresRule;

	public StructuresRule getStructuresRule() {
		return structuresRule;
	}

	public void setStructuresRule(StructuresRule structuresRule) {
		this.structuresRule = structuresRule;
	}
    
}
