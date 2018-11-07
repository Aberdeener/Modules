command tab {
    sort {
        help Resorts the entirety of tab.;
        run sort;
    }
    sort [string:player] {
        help Resorts one player.;
        run sortspecific player;
    }
    perm utils.tab.admin;
}