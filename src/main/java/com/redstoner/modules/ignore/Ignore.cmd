command ignore {
    perm utils.ignore;
	[string:player] {
		run ignore player;
		type player;
		help Ignores or Unignores a player.;
	}
	[empty] {
		run list;
		type player;
		help Lists everyone you ignore.;
	}
}
command unignore {
    perm utils.ignore;
	[string:player] {
		run unignore player;
		type player;
		help Unignore a player.;
	}
}