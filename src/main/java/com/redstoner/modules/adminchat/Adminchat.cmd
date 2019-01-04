command ac {
    perm utils.ac;
	[string:message...] {
		help Sends a message in Admin Chat;
		run ac_msg message;
	}
}
command acn {
    perm utils.ac;
	[string:name] [string:message...] {
		help Sends a message in Admin Chat;
		type console;
		run acn_msg name message;
	}
}
		
command ackey {
    perm utils.ac;
	[string:key] {
		help Sets your Admin Chat key;
		type player;
		run setackey key;
	}
}

command act {
    perm utils.ac;
	on {
		help Turns on act;
		run act_on;
	}
	off {
		help Turns off act;
		run act_off;
	}
	[empty] {
		help toggles Admin Chat;
		run act;
	}
}