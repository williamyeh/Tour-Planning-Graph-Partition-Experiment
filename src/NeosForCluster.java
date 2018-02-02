import org.neos.client.FileUtils;
import org.neos.client.NeosClient;
import org.neos.client.NeosJobXml;

public class NeosForCluster implements Runnable{	
	/* set the HOST and the PORT fields of the NEOS XML-RPC server */
	private static final String HOST="neos-server.org";
	private static final String PORT="3332";
	/* set the attribute of the Neos Job */
	private static final String type = "milp";
	private static final String solver[] = {"Gurobi", "MOSEK", "XpressMP", "CPLEX", "Cbc"};
	private static final String input = "GAMS";
	/* create NeosClient object client with server information */
	private static NeosClient client;
	
	/* create NeosJobXml object exJob with problem type nco for nonlinearly */
	/* constrained optimization, KNITRO for the solver, GAMS for the input */
	private static NeosJobXml PSPJob;
	
	public static void main(String args[]) throws InterruptedException {
		
		/* create FileUtils object to facilitate reading model file ChemEq.txt */
		/* into a string called example */

		// read in test case num from command line
		int j = Integer.parseInt(args[0]);
		System.out.println("test case j = "+j);
		
			FileUtils fileUtils = FileUtils.getInstance(FileUtils.APPLICATION_MODE);
			String gms = fileUtils.readFile(j+"c.gams");
			

			client = new NeosClient(HOST, PORT);
			PSPJob = new NeosJobXml(type, solver[4], input);
			/* add contents of string gms to model field of job XML */
			PSPJob.addParam("model", gms);
			PSPJob.addParam("email", "wdy1312@gmail.con");
			Thread t = new Thread(new NeosForCluster()); // 產生Thread物件
			
			
			t.setName(j+" "+0);
	        t.start(); // 開始執行Runnable.run();
		        

		
	}
	
	public void run() {
		// TODO Auto-generated method stub
		System.out.println(Thread.currentThread().getName());
		JobResultCluster jobResultCluster = new JobResultCluster(Thread.currentThread().getName());
		/* call submitJob() method with string representation of job XML */
		client.submitJobNonBlocking(PSPJob.toXMLString(), jobResultCluster);
	}
}