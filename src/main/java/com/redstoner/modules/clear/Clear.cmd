command clear{
    [empty] {
        help Clears your inventory;
        type player;
        perm utils.clear;
        run clear;
    }
    [string:player] {
        help Clears someone elses inventory;
        perm utils.clear.other;
        run clearother player;
    }
}