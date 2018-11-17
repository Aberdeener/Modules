command report {
   perm utils.report;
   [string:message...] {
      run report message;
      help Report a player or incident;
      type player;
   }
}
command undoreport {
    alias retractreport;
    perm utils.report;
	[empty] {
	   run report_retract;
       help Retracts the last report you sent.;
    }
}
command rp {
   perm utils.report.admin;

   list {
      help List all open reports;
      run report_open;
   }
   close [int:id] {
      help Close a report;
      run report_close id;
   }
   tp [int:id] {
      help Teleport to the location of a report;
      run report_tp id;
      type player;
   }
}