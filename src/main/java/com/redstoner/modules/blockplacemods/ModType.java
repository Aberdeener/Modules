package com.redstoner.modules.blockplacemods;

public enum ModType {
	STATELESS("Stateless"),
	INTEGER("Integer"),
	UNSIGNED_INTEGER("Positive Integer"),
	STRING("String"),
	REDSTONE_LEVEL("Redstone Level");
	
	private String asString;
	
	private ModType(String asString) {
		this.asString = asString;
	}
	
	public String asString() {
		return asString;
	}
}
