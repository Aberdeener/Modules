command damnspam {
	perm utils.damnspam;
	type player;
	
	[double:seconds] {
		run damnspamSingle seconds;
		help Set single input cooldown for button or lever.;
	}
	
	[double:secondsOff] [double:secondsOn] {
		run damnspamDouble secondsOff secondsOn;
		help Set input cooldown after it's been turned off and turned on (for lever only).;
	}
}