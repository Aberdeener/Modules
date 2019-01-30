command afk {
    alias eafk;
    alias away;
    alias eaway;

    perm utils.afk;

    [empty] {
        run afk;
    }

    [optional:-s] {
        run afksilent -s;
    }

	[optional:-s] [bool:ignoreMovement] {
	    run afkignore -s ignoreMovement;
	}
	
	[optional:-s] [bool:ignoreMovement] [string:reason...] {
        run afkfull -s ignoreMovement reason;
    }
    
    [optional:-s] [string:reason...] {
        run afkreason -s reason;
    }
}

command update_afk_listeners {
    [empty] {
        run update_afk_listeners;
        perm utils.afk.admin;
    }
}