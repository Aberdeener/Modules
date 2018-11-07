command token {
	perm utils.webtoken;
	
	[empty] {
		help Displays an already generated token;
		type player;
		perm utils.webtoken;
		run token;
	}
}

command gettoken {
	perm utils.webtoken;
	
	[string:email...] {
		help Generates a token used for website authentication;
		type player;
		perm utils.webtoken;
		run gettoken email;
	}
}