command bc {
	[string:message...] {
		help Sends a message in BuildTeam Chat;
		perm utils.bc;
		run bc_msg message;
	}
}
command bcn {
	[string:name] [string:message...] {
		help Sends a message in BuildTeam Chat;
		perm utils.bc;
		type console;
		run bcn_msg name message;
	}
}
		
command bckey {
	[string:key] {
		help Sets your BuildTeam Chat key;
		perm utils.bc;
		type player;
		run setbckey key;
	}
}

command bct {
	on {
		help Turns on bct;
		perm utils.bc;
		run bct_on;
	}
	off {
		help Turns off bct;
		perm utils.bc;
		run bct_off;
	}
	[empty] {
		help toggles BuildTeam Chat;
		perm utils.bc;
		run bct;
	}
}";