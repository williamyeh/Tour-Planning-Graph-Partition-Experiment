import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.neos.client.FileUtils;
import org.neos.client.NeosClient;
import org.neos.client.NeosJob;
import org.neos.client.NeosJobXml;
import org.neos.gams.SolutionData;
import org.neos.gams.SolutionParser;
import org.neos.gams.SolutionRow;

public class ExampleMain {
	/* set the HOST and the PORT fields of the NEOS XML-RPC server */
	private static final String HOST = "neos-server.org";
	private static final String PORT = "3332";
	private static final String solver[] = { "Gurobi", "MOSEK", "XpressMP", "CPLEX", "Cbc" };
	
	public static void main(String[] args) throws IOException {
		
		File in = new File(args[0]+".txt");
		Scanner sc = new Scanner(in);
		int total = sc.nextInt();
		int sales = sc.nextInt()+1;
		Data data = new Data(total, sales);
		ExampleMain.neos(args,data);
	}
	
	public static void neos(String[] args, Data data) throws IOException {
		/* create NeosClient object client with server information */
		NeosClient client = new NeosClient(HOST, PORT);

		/*
		 * create NeosJobXml object exJob with problem type nco for nonlinearly
		 */
		/*
		 * constrained optimization, KNITRO for the solver, GAMS for the input
		 */

		/*
		 * create FileUtils object to facilitate reading model file ChemEq.txt
		 */
		/* into a string called example */
		
		
		String caseNum = args[0];
		String gams = "";

		

		FileUtils fileUtils = FileUtils.getInstance(FileUtils.APPLICATION_MODE);
		gams = caseNum + "c.gams";
		String example = fileUtils.readFile(gams);
		


		NeosJobXml exJob = new NeosJobXml("milp", solver[4], "GAMS");
		/* add contents of string example to model field of job XML */
		exJob.addParam("model", example);
		exJob.addParam("email", "wdy1312@gmail.con");
		/* call submitJob() method with string representation of job XML */

		NeosJob job = client.submitJob(exJob.toXMLString());
		/* print results to standard output */

		String results = job.getResult();
		/*
		 * Scanner sc = new Scanner(results); for(int j=0 ; j<42 ; j++)
		 * sc.nextLine(); sc.next();
		 */
		SolutionParser parser = new SolutionParser(results);
		int statusCode = parser.getSolverStatusCode();
		int modelStatusCode = parser.getModelStatusCode();
		String solverStatus = parser.getSolverStatus();
		String modelStatus = parser.getModelStatus();
		double objective = parser.getObjective();
		String solverName[] = { "Gurobi", "MOSEK", "XpressMP", "CPLEX", "Cbc" };
		double x[][];
		SolutionData xx = parser.getSymbol("X", SolutionData.VAR, 2);

		int max1 = 0;
		int max2 = 0;

		for (SolutionRow r : xx.getRows()) {
			if (Integer.parseInt(r.getIndex(0)) > max1)
				max1 = Integer.parseInt(r.getIndex(0));

			if (Integer.parseInt(r.getIndex(1)) > max2)
				max2 = Integer.parseInt(r.getIndex(1));
		}
		x = new double[max1 + 1][max2 + 1];

		for (SolutionRow r : xx.getRows()) {
			int i = Integer.parseInt(r.getIndex(0));
			int j = Integer.parseInt(r.getIndex(1));
			x[i][j] = r.getLevel();
			// System.out.printf("%s %s - %s - %s\n" ,
			// r.getIndex(0),r.getIndex(1),r.getIndex(2), r.getLevel());
		}
		for(int i=data.SALESMAN ; i<=max1 ; i++)
		{
			for(int j=1 ; j<=max2 ; j++)
			{
				if(x[i][j] == 1)
				{
					data.insertSales(i, j);
				}
			}
		}
		//data.printSales();
		int count[] = new int[data.SALESMAN];
		for (int i = 1; i < max1 + 1; i++) {
			for (int j = 1; j < max2 + 1; j++) {
				if (x[i][j] == 1) {
					//System.out.println(i+" is in "+j);
					count[j]++;
				}
			}
		}
		
		for(int i=1 ; i<=max2 ; i++)
		{
			//System.out.print(i+" count is "+count[i]+"\n");
		}
		
		System.out.println("\n" + caseNum + " Solver Done!!");

		

	}
}