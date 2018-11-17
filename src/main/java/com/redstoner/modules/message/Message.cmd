command message {
    alias m;
    alias em;
    alias msg;
    alias emsg;
    alias t;
    alias et;
    alias tell;
    alias etell;
    alias w;
    alias ew;
    alias whisper;
    alias ewhisper;
    perm utils.message;
    [string:player] [string:message...] {
        run message player message;
        help Sends a direct message to a player.;
    }
}

command reply {
    alias r;
    alias er;
    alias ereply;
    perm utils.message;
    [string:message...] {
        run reply message;
        help Sends a direct message to the last person you talked to.;
    }
}

command pmtoggle {
    perm utils.message.toggle;
    [empty] {
        help Turns off your toggle.;
        type player;
        run pmtoggle_off;
    }
    [string:player] {
        help Turns on your pmtoggle and locks onto <player>.;
        type player;
        run pmtoggle player;
    }
}