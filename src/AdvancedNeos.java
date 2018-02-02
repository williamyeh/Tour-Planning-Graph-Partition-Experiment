import org.neos.client.FileUtils;
import org.neos.client.NeosClient;
import org.neos.client.NeosJobXml;

public class AdvancedNeos implements Runnable{	
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
	
	public static void main(String[] args) throws InterruptedException {
		
		/* create FileUtils object to facilitate reading model file ChemEq.txt */
		/* into a string called example */

		// read in test case num from command line
		int j = Integer.parseInt(args[0]);
		
			FileUtils fileUtils = FileUtils.getInstance(FileUtils.APPLICATION_MODE);
			String gms = fileUtils.readFile(j+".gams");
			for(int i = 0; i < 5; i++){

				client = new NeosClient(HOST, PORT);
				PSPJob = new NeosJobXml(type, solver[i], input);
				/* add contents of string gms to model field of job XML */
				PSPJob.addParam("model", gms);
				PSPJob.addParam("email", "wdy1312@gmail.con");
				Thread t = new Thread(new AdvancedNeos()); // 產生Thread物件
				
				
				t.setName(j+" "+i);
		        t.start(); // 開始執行Runnable.run();
		        
		        //avoid NEOS server fail
		        Thread.sleep(10000);
			}
	}
	
	public void run() {
		// TODO Auto-generated method stub
		System.out.println(Thread.currentThread().getName());
		JobResult jobResult = new JobResult(Thread.currentThread().getName());
		/* call submitJob() method with string representation of job XML */
		client.submitJobNonBlocking(PSPJob.toXMLString(), jobResult);
	}
}