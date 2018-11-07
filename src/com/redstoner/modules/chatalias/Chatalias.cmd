command alias {
    add [flag:-r] [string:keyword] [string:replacement...] {
        help Adds a new alias. Set -r to make it a regex-alias.;
        run addalias -r keyword replacement;
    }
    del [flag:-r] [string:keyword] {
        help Deletes an alias. -r indicates if it was a regex-alias.;
        run delalias -r keyword;
    }
    list {
        help Lists your aliases.;
        run listaliases;
    }
    perm utils.alias;
    type player;
}