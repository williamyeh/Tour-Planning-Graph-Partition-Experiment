import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.neos.client.ResultCallback;
import org.neos.gams.SolutionData;
import org.neos.gams.SolutionParser;
import org.neos.gams.SolutionRow;

public class JobResultCluster implements ResultCallback {
	int tNum;
	int solver;
	String tName;
	
	public JobResultCluster(String tName){
		this.tName = tName;
		tNum = Integer.parseInt(tName.split(" ")[0]);
		solver = Integer.parseInt(tName.split(" ")[1]);
	}
	
	public void handleJobInfo(int jobNo, String pass) {
		System.out.println("Job Number : " + jobNo);
		System.out.println("Password   : " + pass);
		/*
		String solverName[] = {"Gurobi", "MOSEK", "XpressMP", "CPLEX", "Cbc"};
        FileWriter fw;
        try {
			fw = new FileWriter("ID_PWD.txt",true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(solverName[0]+" "+tNum+" "+jobNo+" "+pass+"\n");
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
        double x[][];
        SolutionData xx = parser.getSymbol("X", SolutionData.VAR, 2);
        
	    System.out.println("Dimen = "+xx.getRows().size());
	    int max1=0;
	    int max2=0;
			  
	    for(SolutionRow r : xx.getRows()) {
		    if(Integer.parseInt(r.getIndex(0)) > max1)
                max1 = Integer.parseInt(r.getIndex(0));
	
		    if(Integer.parseInt(r.getIndex(1)) > max2)
			    max2 = Integer.parseInt(r.getIndex(1));
	    }
	    x = new double[max1+1][max2+1];
	  
	    for(SolutionRow r : xx.getRows()) {
		    int i = Integer.parseInt(r.getIndex(0));
		    int j = Integer.parseInt(r.getIndex(1));
		    x[i][j] = r.getLevel();
		  //System.out.printf("%s %s - %s - %s\n" , r.getIndex(0),r.getIndex(1),r.getIndex(2), r.getLevel());
	    }
	    
	    FileWriter fw;
		BufferedWriter bw;
	    try {
			fw = new FileWriter(tNum+"c");
			bw = new BufferedWriter(fw);
		    for(int i=1 ; i<max1+1 ; i++)
		    {
		    	for(int j=1 ; j<max2+1 ; j++)
		    	{
		    		if(x[i][j]==1)
		    		{
		    			bw.write((j-1)+"\n");
		    		}
		    	}
		    }
		    bw.flush();
		    bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

        
        System.out.println("\n"+ tName + " Solver Done!!");
        //System.out.println(results);
	}
}