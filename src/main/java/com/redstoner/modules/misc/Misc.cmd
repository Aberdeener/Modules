command tempadd { 
    perm pex; 
    [string:user] [string:group] { 
        help Adds a user to a group for 1w.; 
        run tempadddef user group; 
    } 
    [string:user] [string:group] [string:duration] { 
        help Adds a user to a group for a specified duration.; 
        run tempadd user group duration; 
    } 
} 
command echo {
    perm utils.misc.echo; 
    [string:text...] { 
        help Echoes back to you.; 
        run echo text; 
    } 
} 
command ping { 
    perm utils.misc.ping
    [empty] { 
        help Pongs :D; 
        run ping; 
    } 
    [string:password] { 
        help Pongs :D; 
        run ping2 password; 
    } 
} 
command sudo { 
    perm utils.misc.sudo; 
    [string:name] [string:command...] { 
        help Sudo'es another user (or console); 
        run sudo name command; 
    } 
} 
command hasperm {
	perm utils.misc.hasperm;
    [flag:-f] [string:name] [string:node] { 
        perm utils.hasperm; 
        run hasperm -f name node;
        help Checks if a player has a given permission node or not. Returns \"true/false\" in chat. When -f is set, it returns it unformatted.; 
    } 
}
command nightvision {
    alias nv; 
    perm utils.misc.nightvision;
	[empty] {
		run illuminate; 
		type player;
		help Gives the player infinte night vision; 
		perm utils.illuminate; 
	} 
}
command minecart {
   alias cart;
   perm utils.misc.spawncart;
   type player;
   
  default [string:variation] {
      run minecart_default variation;
      help Sets a default minecart variation.;
   }
   [string:variation] {
      run minecart_variation variation;
      help Spawns a certain minecart;
   }
   [empty] {
      run minecart;
      help Spawns a minecart;
   }   
}