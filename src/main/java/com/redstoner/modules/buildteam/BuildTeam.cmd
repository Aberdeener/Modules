command teleport {
    alias tp;
    alias tele;
    [string:player...] {
        run teleport player;
    }
    type player;
}

command team_add {
    perm utils.buildteam.manage;
    [string:player] {
        run team_add player;
    }
}

command team_remove {
    perm utils.buildteam.manage;
    [string:player] {
        run team_remove player;
    }
}
