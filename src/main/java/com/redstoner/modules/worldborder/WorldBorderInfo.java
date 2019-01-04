package com.redstoner.modules.worldborder;

import org.json.simple.JSONObject;

public class WorldBorderInfo {

	private final String message;
	private final int cx, cz, r;
	private final int maxX, minX, maxZ, minZ;
	
	public WorldBorderInfo(int cx, int cz, int r) {
		this.cx = cx;
		this.cz = cz;
		this.r = r;
		this.maxX = cx + r;
		this.minX = cx - r;
		this.maxZ = cz + r;
		this.minZ = cz - r;
		this.message = "&7centered at &e(" + cx + "," + cz + ")&7 with a radius of &e" + r + "&7.";
	}
	
	public boolean isCordanateWithinBounds(int x, int z) {
		return x > minX && x < maxX && z > minZ && z < maxZ;
	}
	
	public String getMessage() {
		return message;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject() {
		JSONObject j = new JSONObject();
		
		j.put("cx", cx);
		j.put("cz", cz);
		j.put("r", r);
		
		return j;
	}
	
	public static WorldBorderInfo fromJSONObject(JSONObject j) {
		return new WorldBorderInfo(((Long)j.get("cx")).intValue(),
				                   ((Long)j.get("cz")).intValue(), ((Long)j.get("r")).intValue());
	}
	
}
