command tag {
    perm utils.tag;
    add [string:player] [string:tag...] {
        help Tags a player.;
        run addtag player tag;
    }
    del [string:player] [int:id] {
        help Removes a tag.;
        run deltag player id;
    }
    check [string:player] {
        help Lists all tags of a player.;\
        run checktag player;
    }
    [string:player] [string:tag...] {
        help Tags a player.;
        run addtag player tag;
    }
}