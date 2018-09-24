command report {
   [string:message...] {
      type player;
      help Report a player or incident;
      run report message;
   }
}
command rp {
   perm utils.report;

   open {
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