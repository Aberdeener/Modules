command socialspy {
    format {
        run config_format_default;
        help Resets your format back to the default: &e%s;
    }
    format [string:format...] {
        run config_format format;
        help Specifies your ss format. Use /socialspy format_help to get info about how the format works.;
    }
    format_help {
        run format_help;
        help Displays info about the format command;
    }
    prefix {
        run config_prefix_default;
        help Resets your color back to the default (light gray color code);
    }
    prefix [string:prefix] {
        run config_prefix prefix;
        help Sets your prefix to the specified term.;
    }
    commands list {
        run commands_list;
        help Displays all commands you're listening to.;
    }
    commands add [string:command] {
        run commands_add command;
        help Adds a command to the list of commands that you're listening to.;
    }
    commands del [string:command] {
        run commands_del command;
        help Deletes a command from the list of commands that you're listening to.;
    }
    stripcolor on {
        run stripcolor_on;
    }
    stripcolor off {
        run stripcolor_off;
    }
    stripcolor {
        run stripcolor;
    }
    on {
        run on;
    }
    off {
        run off;
    }
    [empty] {
        run toggle;
    }
    perm utils.socialspy;
    type player;
    migrate {
        run migrate;
        type console;
    }
    test {
        run test;
        type console;
    }
}