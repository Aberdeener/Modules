command check {
	perm utils.check;
	
	[string:player] {
		run checkCommand player;
		help Get info on a player;
	}
}
command ipinfo {
	perm utils.check;
	
	[string:ip] {
		run ipinfo ip;
		help Get the info about the given IP.;
	}
}