command me { 
    perm utils.chat.me; 
    [string:text...] { 
        help /me's in chat.; 
        run me text; 
    } 
} 
command action { 
    perm utils.chat.action; 
    [string:text...] { 
        help /action's in chat.; 
        run action text; 
    } 
} 
command chat {
    alias speak;
    perm utils.chat;
    [string:message...] {
        run chat message;
        help A way to speak in normal chat with normal formatting if you have ACT or CGT on.; 
    }
}
command chatn {
    alias speakn;
    perm utils.chat.chatn;
    [string:name] [string:message...] {
        run chatn name message;
        help A way to speak in normal chat with normal formatting for console users.; 
    }
}
command shrug {
    perm utils.chat.shrug;
    [string:message...] {
        run shrug message;
        help Appends the shrug emoticon to the end of your message.; 
    }
    [empty] {
        run shrugnoarg;
        help Just the shrug emoticon.; 
    }
}
command say {
    perm utils.chat.say;
    [string:message...] {  
        run say message;
        help A replacement for the default say command to make the format be more consistant.; 
    } 
}
command sayn {
    perm utils.chat.sayn;
    [string:name] [string:message...] { 
        type console;
        run sayn name message;
        help A replacement for the default say command to make the format be more consistant.; 
    }
}

command mute {
        perm utils.chat.mute;
    [string:player] {
        run mute player;
        help Mutes a player.;
    }
}

command print {
    perm utils.chat.print;
    [string:message...] { 
        run print message;
        help A way to just print something in to chat with all the formatting things a user has.; 
    } 
}

command unmute {
    perm utils.chat.mute;
    [string:player] {
        run unmute player;
        help Unmutes a player.;
    }
}

command chatonly {
    alias co;
    perm utilschat.chatonly;
    [empty] {
        run chatonly;
        help Shows that you're onlu able to chat, nothing else.;
        type player;
    }
}

command resetchatformating {
	run resetformating;
	help Resets the formatting to defaults.;
	type console;
}
