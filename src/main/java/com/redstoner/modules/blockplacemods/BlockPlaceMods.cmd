command bpm {
    alias set;
    alias toggle;
    alias mod;

    perm blockplacemods.use;
    type player;

	[empty] {
        help Lists the block place mods and their statuses.;
        run list_mods;
    }
	
    list [empty] {
        help Lists the block place mods and their statuses.;
        run list_mods;
    }

    reset [string:mod] {
        help Resets the specified mod's settings to the default value.;
        run reset_mod mod;
    }
	
	toggle [string:mod] {
        help Toggles a block place mod.;
        run toggle_mod mod;
    }
	
	set [string:mod] [string:value] {
        help Sets the specified mod's state to the specified value. Only works for mods that have a state.;
        run set_mod_value mod value;
    }
    
    [string:mod] {
        help Toggles a block place mod.;
        run toggle_mod_no_prefix mod;
    }

    [string:mod] [string:value] {
        help Sets the specified mod's state to the specified value. Only works for mods that have a state.;
        run set_mod_value mod value;
    }
}