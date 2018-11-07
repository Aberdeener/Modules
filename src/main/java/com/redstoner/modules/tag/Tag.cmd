command tag {
    add [string:player] [string:tag...] {
        help Tags a player.;
        run addtag player tag;
        perm utils.tag;
    }
    del [string:player] [int:id] {
        help Removes a tag.;
        run deltag player id;
        perm utils.tag;
    }
    check [string:player] {
        help Lists all tags of a player.;\
        run checktag player;
        perm utils.tag;
    }
    [string:player] [string:tag...] {
        help Tags a player.;
        run addtag player tag;
        perm utils.tag;
    }
}