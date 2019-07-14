package com.chat.rest.dto;

import javax.validation.constraints.NotBlank;

public class UserDto {
	
	@NotBlank(message = "Name may not be null")
	private String name;
	
	public UserDto() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
