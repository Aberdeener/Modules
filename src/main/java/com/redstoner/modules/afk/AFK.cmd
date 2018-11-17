command afk {
    alias eafk;
    alias away;
    alias eaway;
    perm utils.afk;
    [empty] {
        run afk;
    }
    [optional:-s] {
        run afks -s;
    }
    [optional:-s] [string:reason...] {
        run afk2 -s reason;
    }
}

command update_afk_listeners {
    [empty] {
        run update_afk_listeners;
        perm utils.afk.admin;
    }
}