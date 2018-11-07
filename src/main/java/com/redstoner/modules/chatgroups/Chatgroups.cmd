command cgt {
	[empty] {
		help Toggles your cgtoggle status.;
		type player;
		run cgtoggle;
	}
}
command cgkey {
	[string:key] {
		help Sets your chatgroup key.;
		run setcgkey key;
		type player;
	}
}
command cgsay {
	[string:message...] {
		help Chats in your chatgroup.;
		run cgsay message;
	}
}
command cg {
	join [string:group] {
		help Joins a chatgroup.;
		run cgjoin group;
	}
	leave {
		help Leaves your chatgroup.;
		run cgleave;
	}
	info {
		help Displays info about your chatgroup.;
		run cginfo;
	}
				
}