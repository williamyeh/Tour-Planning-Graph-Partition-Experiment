import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.neos.client.ResultCallback;
import org.neos.gams.SolutionData;
import org.neos.gams.SolutionParser;

public class JobParser implements ResultCallback {
	int tNum;
	int solver;
	String tName;
	
	public JobParser(String tName){
		this.tName = tName;
		tNum = Integer.parseInt(tName.split(" ")[0]);
		solver = Integer.parseInt(tName.split(" ")[1]);
	}
	
	public void handleJobInfo(int jobNo, String pass) {
		//System.out.println("Job Number : " + jobNo);
		//System.out.println("Password   : " + pass);
		/*
		String solverName[] = {"Gurobi", "MOSEK", "XpressMP", "CPLEX", "Cbc"};
        FileWriter fw;
        try {
			fw = new FileWriter("ID_PWD.txt",true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(solverName[solver]+" "+tNum+" "+jobNo+" "+pass+"\n");
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        */
	}
	
 	
	public void handleFinalResult(String results){
		
		
		SolutionParser parser = new SolutionParser(results);
		int statusCode = parser.getSolverStatusCode();
        int modelStatusCode = parser.getModelStatusCode();
        String solverStatus = parser.getSolverStatus(); 
        String modelStatus = parser.getModelStatus();
        double objective = parser.getObjective();
        String solverName[] = {"Gurobi", "MOSEK", "XpressMP", "CPLEX", "Cbc"};
        
        
        FileWriter fw;
        try {
			fw = new FileWriter("AllData.txt",true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(objective+" "+this.solver+" "+this.tNum+"\n");
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		
		System.out.println("\n"+ tName + " Solver Done!!");

	}
}