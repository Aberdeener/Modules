command tp {
    alias teleport;
    alias to;
    
    [string:player] {
        run tp player;
        help Teleports you to a player.;
        perm utils.teleport.tpa;
        type player;
    }
    [string:player] [string:player2] {
        run tp2 player player2;
        help Teleports the first player to the second.;
        perm utils.teleport.tpa;
    }
    [int:x] [int:y] [int:z] {
    	run tploc x y z;
    	help Teleports you to specific coords.;
    	perm utils.teleport.tploc;
    	type player;
    }
    
    [string:player] [int:x] [int:y] [int:z] {
    	run tploc2 player x y z;
    	help Teleports a player to specific coords.;
    	perm utils.teleport.tploc.other;
    }
}

command tphere {
    alias tph;
    alias teleoprthere;
    perm utils.teleport.tp;
    type player;
    
    [string:player] {
        run tphere player;
        help Teleports the player to you.;
        perm utils.teleport.tp.here;
    }
}

command tpa {
    alias tpr;
    alias tpask;
    alias teleportask;
    perm utils.teleport.tpa;
    type player;
    
    [string:player] {
        run tpa player;
        help Request to teleport to a player.;
    }
}

command tpahere {
    alias tpah;
    alias tprhere;
    alias tpaskhere;
    alias teleportaskhere;
    perm utils.teleport.tpa;
	type player;
	
    [string:player] {
        run tpahere player;
        help Request a player to teleport to you,;
        help ask another player to teleport to you.;
    }
}

command tpall {
    perm utils.teleport.tpall;
    
    [empty] {
        run tpall;
        help Teleports everyone to you.;
        type player;
    }
    [string:player] {
        run tpall2 player;
        help Teleports everyone to the specified player.;
    }
}

command tpaccept {
    alias tpyes;
    perm utils.teleport.request;
    type player;
    
    [empty] {
        run tpaccept;
        help Accepts the latest pending tpa request.;
    }
    [string:player] {
        run tpaccept2 player;
        help Accepts the specified pending tpa request.;
    }
}

command tpcancel {
    alias tpastop;
    perm utils.teleport.request;
    type player;
    
    [empty] {
        run tpacancel;
        help Cancels the latest outgoing pending tpa request.;
    }
    [string:player] {
        run tpacancel2 player;
        help Cancels the specific outgoing pending tpa request.;
    }
}

command tpdeny {
    alias tpno;
    perm utils.teleport.request;
    type player;
    
    [empty] {
        run tpdeny;
        help Denies the latest pending tpa request.;
    }
    [string:player] {
        run tpdeny2 player;
        help Denies the specified pending tpa request.;
    }
}

command tplist {
    alias etplist;
    alias tpl;
    alias etpl;
    perm utils.teleport.request;
    type player;
    
    [empty] {
        run tplist;
        help Shows you a list of all the incoming tpa requests.;
    }
}

command tptoggle {
    perm utils.teleport.toggle;
    type player;
    
    [string:status] {
        run tptoggle status;
        help sets your tpa status (All, ToMe, ToThem, None);
    }
}