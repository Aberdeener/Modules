command setmotd {
    [string:motd...] {
        help Sets the motd. Use --reset to reset to default;
        run setmotd motd;
    	perm utils.motd.set;
    }
}
command getmotd {
    [empty] {
        help Returns the motd;
        run getmotd;
    	perm utils.motd.get;
    }
}