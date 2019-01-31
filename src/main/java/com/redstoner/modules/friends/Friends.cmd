command friends {
	alias friend;
	perm utils.friends;
	type player;
	
    add [string:name] {
        run add name;
        help Adds a friend to your friend list.;
    }
    add [string:name] [string:group] {
        run add_grouped name group;
        help Adds a friend to a group of friends;
        perm utils.friends.groups;
    }
    remove [string:name] {
        run del name;
        help Removes a friend from your friend list.;
    }
    remove [string:name] [string:group] {
        run del_grouped name group;
        help Removes a friend from a group of friends;
        perm utils.friends.groups;
    }
    list {
        run list;
        help Shows a list of all your friends.;
    } 
    list [string:group] {
        run list_group group;
        help Shows a list of all friends in that group.;
        perm utils.friends.groups;
    }
    groups {
        run list_groups;
        help Shows all your friend groups that have at least one person in them.;
        perm utils.friends.groups;
    }
    group {
    	[string:groupName] {
    		run list_group groupName;
        	help Shows a list of all friends in that group.;
        	perm utils.friends.groups;
    	}
    	run list_groups;
        help Shows all your friend groups that have at least one person in them.;
        perm utils.friends.groups;
    }
}