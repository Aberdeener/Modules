command lc {
	alias lagchunks;
	perm utils.lagchunks;
	
	list {
		run list_cmd;
		help Re-lists already scanned chunks.;
	}
		
	[int:amount] {
		run scan_cmd amount;
		help Scans for laggy chunks.;
	}
	
	tp [int:number] {
		run tp number;
		help Teleports to the specified chunk.;
		type player;
	}
}
