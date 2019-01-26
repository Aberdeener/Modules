command seen {
    perm utils.seen;
    [string:player] {
        help Displays information about a player.;
        run seen player;
    }
    
    [string:player] [flag:ips] {
        help Displays information about a player.;
        run seen2 player ips;
    }
}
command firstseen {
    perm utils.seen.firstseen;
    [empty] {
        run firstseen;
        type player;
        help Gives the date and time they first joined;
    }
    [string:person] {
        run firstseenP person;
        help Gives the date and time when a player first joined;
    }
}
command playtime {
    perm utils.seen.playtime;
    [empty] {
        type player;
        run playtimeDef;
        help Displays your total playtime!;
    }
    [string:name] {
        run playtime name;
        help Displays the playtime of another player. The player must be online!;
    }
}

command uuid {
    [empty] {
        type player;
        run uuidDef;
        perm utils.seen.uuid;
        help Displays your UUID (click to copy);
    }
    [string:name] {
        run uuid name;
        perm utils.seen.uuid.other;
        help Displays someone elses UUID (click to copy);
    }
}