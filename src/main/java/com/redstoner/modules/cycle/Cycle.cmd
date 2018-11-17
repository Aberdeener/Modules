command cycle {
	perm utils.cycle;
	type player;
    on {
        help Turns on cycle;
        run cycle_on;
    }
    off {
        help Turns off cycle;
        run cycle_off;
    }
}