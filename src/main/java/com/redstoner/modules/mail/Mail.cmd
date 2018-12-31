command mail {
	perm utils.mail;
	type player;
	
	[empty] {
        run read;
        help Shows your inbox of mails.;
    }
    read [empty] {
        run read;
        help Shows your inbox of messages.;
    }
    delete [int:id] {
    	run delete id;
    	help Deletes the given message.;
    }
    clear [empty] {
    	run clear;
    	help Clears your inbox.;
    }
    send [string:player] [string:message...] {
    	run send player message;
    	help Send a message to the given player.;
    }
    reply [int:id] [string:message...] {
    	run reply id message;
    	help Reply to a players message.;
    }
    retract [int:id] {
        run retract_id id;
        help Retract the given message, if the player has not read it yet.;
    }
    retract [empty] {
    	run retract;
        help Retract the last message you sent, if the player has not read it yet.;
    }
    archive [empty] {
        run archive_read;
        help Shoes are archived messages.;
    }
    archive read [empty]{
        run archive_read;
        help Shoes are archived messages.;
    }
    archive [int:id] {
    	run archive id;
    	help Archives a message.;
    }
    unarchive [int:id] {
    	run unarchive id;
    	help Unarchives a message.;
    }
    settings theme {
    	[string:s_theme] {
    	    run settings_theme_set s_theme;
            help Sets the theme for your inbox. Available themes are: Light, Dark, and Gold.;
    	}
    	run settings_theme;
        help Shows your current theme setting.;
    }
    settings actions {
    	[string:s_actions] {
    	    run settings_actions_set s_actions;
            help Sets the action set for your inbox. Available options are: \nMinimal: No click actions,\nSimple: Delete, and \nStandard: Delete, and Reply. \nAll actions are still available as commands.;
    	}
    	run settings_actions;
        help Shows your current action set setting.;
    }
    settings names {
    	[string:s_names] {
    	    run settings_names_set s_names;
            help Sets if you want to see player's username or display name. Available options are: username or displayname;
    	}
    	run settings_names;
        help Shows your current names setting.;
    }
    [string:player] [string:message...] {
    	run send player message;
    	help Send a message to the given player.;
    }
}