package com.sportmonks.data.structure;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sportmonks.data.entity.Country;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "data" })
public class Countries extends AbstractEndPointResponse {

	@JsonProperty("data")
	private List<Country> data = null;

	@JsonProperty("data")
	public List<Country> getData() {
		return data;
	}

	@JsonProperty("data")
	public void setData(List<Country> data) {
		this.data = data;
	}

}