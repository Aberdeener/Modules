command bc {
    perm utils.buildchat;
	[string:message...] {
		help Sends a message in BuildTeam Chat;
		run bc_msg message;
	}
}
command bcn {
    perm utils.buildchat.name;
	[string:name] [string:message...] {
		help Sends a message in BuildTeam Chat;
		type console;
		run bcn_msg name message;
	}
}
		
command bckey {
    perm utils.buildchat;
	[string:key] {
		help Sets your BuildTeam Chat key;
		type player;
		run setbckey key;
	}
}

command bct {
    perm utils.buildchat;
	on {
		help Turns on bct;
		run bct_on;
	}
	off {
		help Turns off bct;
		run bct_off;
	}
	[empty] {
		help toggles BuildTeam Chat;
		run bct;
	}
}