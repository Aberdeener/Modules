command ac {
	[string:message...] {
		help Sends a message in Admin Chat;
		perm utils.ac;
		run ac_msg message;
	}
}
command acn {
	[string:name] [string:message...] {
		help Sends a message in Admin Chat;
		perm utils.ac;
		type console;
		run acn_msg name message;
	}
}
		
command ackey {
	[string:key] {
		help Sets your Admin Chat key;
		perm utils.ac;
		type player;
		run setackey key;
	}
}

command act {
	on {
		help Turns on act;
		perm utils.ac;
		run act_on;
	}
	off {
		help Turns off act;
		perm utils.ac;
		run act_off;
	}
	[empty] {
		help toggles Admin Chat;
		perm utils.ac;
		run act;
	}
}