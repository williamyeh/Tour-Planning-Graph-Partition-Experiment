import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcException;
import org.neos.client.FileUtils;
import org.neos.client.NeosJobXml;
import org.neos.client.NeosXmlRpcClient;
import org.neos.client.ResultReceiver;

public class AnotherRetriever {
   
   private static final String HOST="neos-server.org";
   private static final String PORT="3332";
   // test case num, id/pwd
   public static String[][][] id_pwd = new String[5][101][2]; 
   public static int tNum;
   // args[0] is the test case num to be retrieved
   public static void main(String[] args) throws FileNotFoundException{
      /* create NeosXmlRpcClient object with server information */
      NeosXmlRpcClient client = new NeosXmlRpcClient(HOST, PORT);

      Integer currentJob = 0;
      String currentPassword = "";
      String result = "";
      int i=Integer.parseInt(args[0]);
      tNum = i;
      getIdPwd();
     
      try {
    	 client.connect();
    	 for(int j=0 ; j<5 ; j++)
    	 {
	    	 JobParser jobParser = new JobParser(i+" "+j);
	         ResultReceiver receiver = new ResultReceiver(client, jobParser, Integer.valueOf(id_pwd[j][i][0]), id_pwd[j][i][1]);
	         receiver.run();
    	 }
      } catch (XmlRpcException e) {
         System.out.println("Error retrieving a job :" + e.getMessage());
         return;
      }
      /* print results to standard output */
 	  
   }	
   
   public static void getIdPwd() throws FileNotFoundException
   {
	   File in = new File("ID_PWD.txt"); 
	   Scanner sc = new Scanner(in);
		for(int i=1 ; i<=tNum ; i++)
		{
			System.out.println("get id pwd to "+i);
			for(int j=0 ; j<5 ; j++)
			{
				sc.next();
				sc.next();
				id_pwd[j][i][0] = sc.next();
				id_pwd[j][i][1] = sc.next();
			}
		}
   }
}