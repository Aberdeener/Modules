command lol {
    alias kek;

    add [string:text...] {
        help Lols a text.;
        run addlol text;
        perm utils.lol.admin;
    }
    del [int:id] {
        help Unlols a lol.;
        run dellol id;
        perm utils.lol.admin;
    }
    set [int:id] [string:text...] {
        help Relols a lol.;
        run setlol id text;
        perm utils.lol.admin;
    }
    id [int:id] {
        help Lols specifically.;
        run lolid id;
        perm utils.lol.id;
    }
    list [int:page] {
        help Shows lols.;
        run listlols page;
        perm utils.lol.list;
    }
    list {
        help Shows lols.;
        run listlolsdef;
        perm utils.lol.list;
    }
    search [flag:-i] [string:text...] {
        help Search lols.;
        run searchlol -i text;
        perm utils.lol.search;
    }
    match [flag:-i] [string:regex...] {
        help Search lols. But better.;
        run matchlol -i regex;
        perm utils.lol.match;
    }
    page [int:num] {
    	help Shows you page <num>. Used in /lol search, /lol match, and /lol list.;
    	run page num;
    	perm utils.lol.page;
    }
    [empty] {
        help Lols.;
        run saylol;
        perm utils.lol;
    }
}