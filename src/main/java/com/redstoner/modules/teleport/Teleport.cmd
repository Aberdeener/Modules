command teleport {
    alias eteleport;
    alias tp;
    alias etp;
    alias to;
    alias eto;
    alias tpo;
    alias etpo;
    alias tp2p;
    alias etp2p;
    [player:string] {
        run tp player;
        perm utils.teleport.tp;
    }
    [player:string] [player2:string] {
        run tp2 player player2;
        perm utils.teleport.tp.other;
    }
}

command teleporthere {
    alias eteleporthere;
    alias tphere;
    alias etphere;
    alias tpohere;
    alias etpohere;
    perm utils.teleport.tp;
    [player:string] {
        run tph player;
        perm utils.teleport.tp.here;
    }
}

command teleportask {
    alias eteleportask;
    alias tpa;
    alias etpa;
    alias tpr;
    alias etpr;
    alias tpask;
    alias etpask;
    perm utils.teleport.tpa;
    [player:string] {
        run tpa player;
    }
}

command teleportaskhere {
    alias eteleportaskhere;
    alias tpahere,
    alias etpahere;
    alias tprhere;
    alias etrphere;
    alias tpaskhere;
    alias etpaskhere;
    perm utils.teleport.tpa;
    [player:string] {
        run tpah player;
        help ask another player to teleport to you.;
    }
}

command tpall {
    alias etpall;
    perm utils.teleport.tpall;
    [empty] {
        run tpall;
        help Teleports everyone to you.;
    }
    [player] {
        run tpall2 player;
        help Teleports everyone to the specified player.;
    }
}

command tpaall {
    alias etpall;
    perm utils.teleport.tpaall;
    [empty] {
        run tpaall;
        help Sends a tpa request to every player.;
    }
    [player:string] {
        run tpaall2 player;
        help Sends a tpa request to every player.;
    }
}

command tpaccept {
    alias etpaccept;
    alias tpyes;
    alias etpyes;
    perm utils.teleport.request;
    [empty] {
        run tpaccept;
        help Accepts the latest pending tpa request.;
    }
    [index:int] {
        run tpaccept2 index;
        help Accepts the specified pending tpa request.;
    }
}

command tpacancel {
    alias etpacencel;
    perm utils.teleport.request;
    [empty] {
        run tpacancel;
        help Cancels an outgoing pending tpa request.;
    }
}

command tpdeny {
    alias etpdeny;
    alias tpno;
    alias etpno;
    perm utils.teleport.request;
    [empty] {
        run tpdeny;
    }
    [index:int] {
        run tpdeny2 index;
    }
}

command tplist {
    alias etplist;
    alias tpl;
    alias etpl;
    perm utils.teleport.request;
    [empty] {
        run tpl;
    }
}

command tptoggle {
    alias etptoggle;
    perm utils.teleport.toggle;
    [status:string] {
        run tptoggle status;
        help sets your tpa status;
    }
    [command:string] [status:string] {
        run tptoggle2 command status;
        help sets your tpa status for only one command (e.g. tpa/tpahere).;
    } 
}