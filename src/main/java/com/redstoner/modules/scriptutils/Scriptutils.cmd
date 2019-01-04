command script_restart {
    [string:timeout] [string:name] [string:reason] {
        help Prints bukkit restart message;
        type console;
        run script_restart timeout name reason;
    }
}
command script_stop {
    [string:timeout] [string:name] [string:reason] {
        help Prints bukkit shut down message;
        type console;
        run script_stop timeout name reason;
    }
}
command script_restart_abort {
    [empty] {
        help Prints the restart abort message;
        type console;
        run script_restart_abort;
    }
}
command script_stop_abort {
    [empty] {
        help Prints the shut down abort message;
        type console;
        run script_stop_abort;
    }
}
command script_backup_begin {
    [empty] {
        help Prints the backup started message, saves all worlds and turns off world saving;
        type console;
        run script_backup_begin;
    }
}
command script_backup_end {
    [empty] {
        help Prints the backup finished message and turns on world saving;
        type console;
        run script_backup_end;
    }
}
command script_backup_error {
    [empty] {
        help Prints the backup error message and turns on world saving;
        type console;
        run script_backup_error;
    }
}
command script_backup_database_begin {
    [empty] {
        help Prints the database backup started message and admin-chat warning;
        type console;
        run script_backup_database_begin;
    }
}
command script_backup_database_dumps {
    [empty] {
        help Prints the database dumps cmpression started message;
        type console;
        run script_backup_database_dumps;
    }
}
command script_backup_database_end {
    [string:size] {
        help Prints the database finished message and backup size in admin-chat;
        type console;
        run script_backup_database_end size;
    }
}
command script_backup_database_error {
    [empty] {
        help Prints the database backup error message;
        type console;
        run script_backup_database_error;
    }
}
command script_backup_database_abort {
    [empty] {
        help Prints the database backup abort message;
        type console;
        run script_backup_database_abort;
    }
}
command script_shutdown {
    [string:reason] {
        help Saves all worlds, kicks players and shuts down the server;
        type console;
        run script_shutdown reason;
    }
}