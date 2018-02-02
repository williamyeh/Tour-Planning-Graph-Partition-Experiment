import org.neos.client.FileUtils;
import org.neos.client.NeosClient;
import org.neos.client.NeosJobXml;
import org.neos.client.NeosXmlRpcClient;
import org.neos.client.ResultReceiver;

public class Retriever implements Runnable{	
	/* set the HOST and the PORT fields of the NEOS XML-RPC server */
	private static final String HOST="neos-server.org";
	private static final String PORT="3332";
	/* set the attribute of the Neos Job */
	private static final String type = "milp";
	private static final String solver[] = {"Gurobi", "MOSEK", "XpressMP", "CPLEX", "Cbc"};
	private static final String input = "GAMS";
	/* create NeosClient object client with server information */
	private static NeosXmlRpcClient client = new NeosXmlRpcClient(HOST, PORT);
	
	/* create NeosJobXml object exJob with problem type nco for nonlinearly */
	/* constrained optimization, KNITRO for the solver, GAMS for the input */
	private static NeosJobXml PSPJob;
	public static String[][] id_pwd = new String[101][2]; 
	
	
	
	public static void main(String[] args) throws InterruptedException {
		
		/* create FileUtils object to facilitate reading model file ChemEq.txt */
		/* into a string called example */
		
		// getID_PWS
		getIdPwd();
	
		for (int j = 1; j <= 1; j++){
			
			for(int i = 0; i < 5; i++){
				JobParser jobParser = new JobParser("");
				ResultReceiver receiver = new ResultReceiver(client, jobParser, Integer.valueOf(id_pwd[j][0]), id_pwd[j][1]);
				receiver.run();
			}
		}
	}
	
	public void run() {
		// TODO Auto-generated method stub
		String Threadname = Thread.currentThread().getName();
		String name = Threadname.substring(0, Threadname.indexOf("ID:"));
		int currentJob = Integer.valueOf(Threadname.substring(Threadname.indexOf("ID:") + "ID:".length(), Threadname.indexOf("PW:")));
		String currentPassword = Threadname.substring(Threadname.indexOf("PW:") + "PW:".length(), Threadname.length());
		JobParser jobParser = new JobParser("");
		ResultReceiver receiver = new ResultReceiver(client, jobParser, currentJob, currentPassword);
		receiver.run();
	}
	
	public static void getIdPwd()
	{
		
	}
}