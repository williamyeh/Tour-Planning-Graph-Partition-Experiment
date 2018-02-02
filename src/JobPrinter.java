import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.neos.client.ResultCallback;
import org.neos.gams.SolutionData;
import org.neos.gams.SolutionParser;
import org.neos.gams.SolutionRow;

public class JobPrinter implements ResultCallback {
	
	public double[][][] x;
	public int[][] u;
	public void handleJobInfo(int jobNo, String pass) {
		System.out.println("Job Number : " + jobNo);
		System.out.println("Password   : " + pass);
	}
	public void handleFinalResult(String results) {
		//System.out.println(results);
		
		SolutionParser parser = new SolutionParser(results);
	    Scanner sc = new Scanner(results);
	 	for(int i=0 ; i<42 ; i++)
			sc.nextLine();
		sc.next();
		
		//FileWriter fw = new FileWriter();
        System.out.println("The solver is "+sc.next());
        System.out.printf("Solver Status [%d] %s \n", parser.getSolverStatusCode(), 
	    parser.getSolverStatus());
	    System.out.printf("Model Status [%d] %s \n", parser.getModelStatusCode(), 
	    parser.getModelStatus());
	    System.out.printf("Objective :%f \n\n", parser.getObjective());
	       
	
	      /* 
	      SolutionData xx = parser.getSymbol("X", SolutionData.VAR, 3);
	      System.out.println("Dimen = "+xx.getRows().size());
	      int max1=0;
	      int max2=0;
	      int max3=0;
	    		  
	      for(SolutionRow r : xx.getRows()) {
	    	  if(Integer.parseInt(r.getIndex(0)) > max1)
	    		  max1 = Integer.parseInt(r.getIndex(0));
	
	    	  if(Integer.parseInt(r.getIndex(1)) > max2)
	    		  max2 = Integer.parseInt(r.getIndex(1));
	    	  if(Integer.parseInt(r.getIndex(1)) > max3)
	    		  max3 = Integer.parseInt(r.getIndex(2));
	    	  //System.out.printf("%s %s - %s - %s\n" , r.getIndex(0),r.getIndex(1),r.getIndex(2), r.getLevel());
	      }
	      x = new double[max1+1][max2+1][max3+1];
	      
	      for(SolutionRow r : xx.getRows()) {
	    	  int i = Integer.parseInt(r.getIndex(0));
	    	  int j = Integer.parseInt(r.getIndex(1));
	    	  int k = Integer.parseInt(r.getIndex(2));
	    	  x[i][j][k] = r.getLevel();
	    	  //System.out.printf("%s %s - %s - %s\n" , r.getIndex(0),r.getIndex(1),r.getIndex(2), r.getLevel());
	      }
	      for(int k=1 ; k<=max3 ; k++)
	      {
	    	  for(int i=1 ; i<= max1 ; i++)
	    	  {
	    		  for(int j=1 ; j<=max2 ; j++)
	    		  {
	    			  if(x[i][j][k] == 1.0)
	    			  {
	    				  System.out.println("( "+i+","+j+" )"+"   "+k);
	    			  }
	    		  }
	    	  }
	      }
	      */
	}	
	
}