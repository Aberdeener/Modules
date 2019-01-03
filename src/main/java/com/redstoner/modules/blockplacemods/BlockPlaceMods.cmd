command bpm {
    alias set;
    alias toggle;

    perm blockplacemods.use;

    type player;

    [empty] {
        help Lists the block place mods and their statuses.;
        run list_mods;
    }

    reset [string:mod] {
        Help Resets the specified mod's settings to the default value.;
        run reset_mod mod;
    }

    [string:mod] {
        help Toggles a block place mod.;
        run toggle_mod mod;
    }

    [string:mod] [string:value] {
        help Sets the specified mod's state to the specified value. Only works for mods that have a state.;
        run set_mod_value mod value;
    }
}