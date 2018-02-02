import org.neos.client.ResultCallback;

public class JobReceiver implements ResultCallback {
   public void handleJobInfo(int jobNo, String pass) {
      System.out.println("Job Number : " + jobNo);
      System.out.println("Password   : " + pass);
   }	
   public void handleFinalResult(String results) {
      System.out.println(results);
   }
}