command alias {
    add [flag:-r] [flag:-rnd] [string:keyword] [string:replacement...] {
        help Adds a new alias. Set -r to make it a regex-alias. \nSet -rnd to make it a random alias and use `&e || &b` (with the spaces) to separate the results.;
        run addalias -r -rnd keyword replacement;
    }
    del [flag:-r] [flag:-rnd] [string:keyword] {
        help Deletes an alias. -r indicates if it was a regex-alias and -rnd indicates if it was a random-alias. ;
        run delalias -r -rnd keyword;
    }
    list {
        help Lists your aliases.;
        run listaliases;
    }
    perm utils.alias;
    type player;
}