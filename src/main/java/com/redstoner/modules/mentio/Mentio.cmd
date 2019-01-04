command mentio {
    perm utils.mentio;
    add [string:trigger] {
        help Triggers you when the trigger gets said.;
        run addmentio trigger;
    }
    delete [string:trigger] {
        help Deletes a mentio.;
        run delmentio trigger;
    }
    list {
        help Lists your mentios.;
        run listmentios;
    }
    type player;
}