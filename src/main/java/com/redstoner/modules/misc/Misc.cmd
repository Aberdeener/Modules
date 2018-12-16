command tempadd {
    perm pex;

    [string:user] [string:group] {
        help Adds a user to a group for 1w.;
        run tempadddef user group;
    }

    [string:user] [string:group] [string:duration] {
        help Adds a user to a group for a specified duration.;
        run tempadd user group duration;
    }
}

command echo {
    perm utils.misc.echo;

    [string:text...] {
        help Echoes back to you.;
        run echo text;
    }
}

command ping {
    perm utils.misc.ping;

    [empty] {
        help Pongs :D;
        run ping;
    }

    [string:player] {
        help Gets the specified player's ping.;
        run ping_player player;
    }
}

command sudo {
    perm utils.misc.sudo;

    [string:name] [string:command...] {
        help Sudo'es another user (or console);
        run sudo name command;
    }
}

command hasperm {
	perm utils.misc.hasperm;

    [flag:-f] [string:name] [string:node] {
        help Checks if a player has a given permission node or not. Returns \"true/false\" in chat. When -f is set, it returns it unformatted.;
        run hasperm -f name node;
    }
}

command nightvision {
    alias nv;
    alias illuminate;

    perm utils.misc.nightvision;

    type player;

	[empty] {
		help Gives the player infinte night vision;
		run illuminate;
	}
}

command minecart {
   alias cart;

   perm utils.misc.spawncart;

   type player;
   
   default [string:variation] {
      help Sets a default minecart variation.;
      run minecart_default variation;
   }

   [string:variation] {
      help Spawns a certain minecart;
      run minecart_variation variation;
   }

   [empty] {
      help Spawns a minecart;
      run minecart;
   }
}