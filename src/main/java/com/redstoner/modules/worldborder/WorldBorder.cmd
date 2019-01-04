command worldborder {
    alias wb;
    perm utils.worldborder;
    get [string:world] {
    	run getwb world;
    	help Gets the current info about the given world's worder.;
    }
	set [string:world] [int:cx] [int:cz] [int:r] {
		run setwb world cx cz r;
		help Sets the world border with the given center (cx,cz) and a given radius (r).;
	}
	remove [string:world] {
	    run remwb world;
	    help Removes the World Border from the given world.;
	}
}