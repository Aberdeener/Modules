command anvil {
    perm utils.naming;
	[empty] {
		run anvil;
		type player;
		help Opens anvil GUI.;
	}
}
command name {
    perm utils.naming;
	[string:name...] {
		run name name;
		type player;
		help Names item in hand.;
	}
}
command lore {
    perm utils.naming;
    [optional:-a] [string:lore...] {
		run lore -a lore;
		type player;
		help Adds lore to item in hand. Use &e-a&b to append to the lore.;
	}
}