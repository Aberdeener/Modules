package com.redstoner.modules.mail;

public enum Theme {
	LIGHT("&7","&c","&9","&8","&e","&7","&2", "&e"),
	DARK("&8","&c","&9","&7","&6","&7","&2", "&e"),
	GOLD("&6","&f","&f","&f","&f","&6","&6", "&6");
	
	private String bracketColor;
	private String deleteColor;
	private String infoColor;
	private String replyColor;
	private String archiveColor;
	private String colonColor;
	private String clearColor;
	private String clearAccentColor;
	
	private Theme(String bracketColor, String deleteColor, String infoColor, String replyColor,
			String archiveColor, String colonColor, String clearColor, String clearAccentColor) {
		this.bracketColor = bracketColor;
		this.deleteColor = deleteColor;
		this.infoColor = infoColor;
		this.replyColor = replyColor;
		this.archiveColor = archiveColor;
		this.colonColor = colonColor;
		this.clearColor = clearColor;
		this.clearAccentColor = clearAccentColor;
	}
	
	public String getBracketColor() {
		return bracketColor;
	}
	public String getDeleteColor() {
		return deleteColor;
	}
	public String getInfoColor() {
		return infoColor;
	}
	public String getReplyColor() {
		return replyColor;
	}
	public String getArchiveColor() {
		return archiveColor;
	}
	public String getColonColor() {
		return colonColor;
	}
	public String getClearColor() {
		return clearColor;
	}
	public String getClearAccentColor() {
		return clearAccentColor;
	}
}
