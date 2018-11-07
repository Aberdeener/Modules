command anvil {
	[empty] {
		run anvil;
		type player;
		help Opens anvil GUI.;
		perm utils.anvil;
	}
}
command name {
	[string:name...] {
		run name name;
		type player;
		help Names item in hand.;
		perm utils.name;
	}
}
command lore {
    [optional:-a] [string:lore...] {
		run lore -a lore;
		type player;
		help Adds lore to item in hand.;
		perm utils.lore;
	}
}