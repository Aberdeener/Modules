command lct {
	alias tilechunks;
	perm utils.tilechunks;
	
	list {
		run list_cmd;
		help re-lists already scanned chunks;
	}
		
	[int:amount] {
		run scan_cmd amount;
		help scans for laggy chunks;
	}
	
	tp [int:number] {
		run tp number;
		help teleports to the specified chunk;
		type player;
	}
}