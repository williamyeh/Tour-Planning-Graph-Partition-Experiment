import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class Main {

	/**
	 * 
	 * @param args
	 * This is the GP/TPP experiment program,
	 * it reads in a undirected graph, partition the graph, merge with w partitions(todo)
	 * Then uses LKH to compute for the individual TPPs, 
	 * output the assignment and objective values
	 * 
	 * It also outputs gams model for TPP and send it via HTTP to neos server
	 * to compute 
	 * args[0] ufactor number
	 * args[1] times
	 * args[2] testCaseNum
	 * @throws IOException
	 * @throws InterruptedException 
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 * @throws BiffException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException, RowsExceededException, WriteException, BiffException {
		
		// TODO Auto-generated method stub
		int uFactor = Integer.parseInt(args[0]);
		int times = Integer.parseInt(args[1]);
		int numOfTestcases = 1;
		File in[] = new File[numOfTestcases+1];
		Scanner scan[] = new Scanner[numOfTestcases+1]; 
		Data data[] = new Data[numOfTestcases+1];
		GPmain gpMain[] = new GPmain[numOfTestcases+1];
		boolean gpFlag = true;
		boolean mipFlag = false;
		boolean gurobiFlag = true;
		boolean genTest = true;
		int numOfParts=0;
		
		//int i = Integer.parseInt(args[2]);

		// Test case generating block
		if(genTest==true)
		{
			
			//GraphGenerater.gen(args);
		}
		
		
		// The experiment block
		for(int i=1; i <= numOfTestcases ; i++)
		{
			System.out.println("test case running no."+i);
			in[i] = new File(i+".txt");
			scan[i] = new Scanner(in[i]);
			int total = scan[i].nextInt();
			// sales needs to be plus one for Data for indexing from 1
			int sales = scan[i].nextInt()+1;
			numOfParts = sales - 1;
			
			if(gpFlag == true)
			{
			// gp section
				
				data[i] = new Data(total , sales);
				data[i].parseGeneralGraph(i+".txt");
				gpMain[i] = new GPmain(data[i]);
				//read graph file without coordinate, i.e. get the cost matrix of nodes
				System.out.println("THE COST MATRIX");
				for(int j=1 ; j<total+sales ; j++)
				{
					for(int k=1 ; k<total+sales ; k++)
					{
						System.out.print(" "+(int)data[i].costMatrix[j][k]);
					}
					System.out.println();
				}
				System.out.println("end");
				
				/* Dijkstra block
			
				DijkstraAlgorithmSet dijkstrasAlgorithm;
				for(int j=1 ; j<sales ; j++)
				{
					dijkstrasAlgorithm = new DijkstraAlgorithmSet(total+sales-1);
					dijkstrasAlgorithm.dijkstra_algorithm(data[i].costMatrix, total+j);
					
					System.out.println("length = "+dijkstrasAlgorithm.distances.length+" total ="+total);
					for (int k = 1; k <= dijkstrasAlgorithm.distances.length - 1; k++) 
		            {
		                System.out.println((j+total)+" "+ " to " + k + " is "+ dijkstrasAlgorithm.distances[k]);
		                data[i].costMatrix[j+total][k] = dijkstrasAlgorithm.distances[k];
		            }
				}
				*/
				System.out.println("THE COST MATRIX2");
				for(int j=1 ; j<total+sales ; j++)
				{
					for(int k=1 ; k<total+sales ; k++)
					{
						System.out.print(" "+(int)data[i].costMatrix[j][k]);
					}
					System.out.println();
				}
				System.out.println("end");
				/*
				for(int j=sales ; j<total+sales ; j++)
				{
					dijkstrasAlgorithm = new DijkstraAlgorithmSet(total+sales-1);
					dijkstrasAlgorithm.dijkstra_algorithm(data[i].costMatrix, j-sales+1);
					
					System.out.println("length = "+dijkstrasAlgorithm.distances.length+" total ="+total);
					for (int k = 1; k <= dijkstrasAlgorithm.distances.length - 1; k++) 
		            {
		                //System.out.println((j-sales+1)+" "+ " to " + k + " is "+ dijkstrasAlgorithm.distances[k]);
		                data[i].costMatrix[j-sales+1][k] = dijkstrasAlgorithm.distances[k];
		            }
				}
				
				for(int j=1 ; j<total+sales ; j++)
				{
					data[i].costMatrix[j][j] = 999;
				}
				*/
				
				
				gpMain[i].gpNoCoordinate(i+".txt", uFactor, times);
				
				
				
				for(int j=1 ; j<total+sales ; j++)
				{
					data[i].costMatrix[j][j] = 999;
				}
				gpMain[i].data.GPMergeForGeneralGraph(i+".part."+(gpMain[i].data.SALESMAN-1),i);
				// gp end
				System.out.println("gp done");
				System.out.println("THE COST MATRIX");
				for(int j=1 ; j<total+sales ; j++)
				{
					for(int k=1 ; k<total+sales ; k++)
					{
						System.out.print(" "+(int)data[i].costMatrix[j][k]);
					}
					System.out.println();
				}
				System.out.println("end");
			}
			// neos cluster section
			if(mipFlag == true)
			{
				System.out.println("start neos clustering");
				data[i] = new Data(total , sales);
				// parse graph
				data[i].parseGeneralGraph(i+".txt");

				String [] input = new String[3];
				input[0] = i+"";
				// send gams to neos
				ExampleMain.neos(input,data[i]);
				
				// use gurobi to do clustering
				
				// run lkh for each clustering
				data[i].NeosClusterLKHParticipants(i+"c",i);
				System.out.println("gurobi local clustering done");
			}
			if(gurobiFlag == true)
			{
				System.out.println("start gurobi clustering");
				data[i] = new Data(total , sales);
				
				// parse graph
				data[i].parseGeneralGraph(i+".txt");
				
				// construct a reverse cost matrix for mip
				data[i].reverseCostMartixForMip();
				Gurobi robi = new Gurobi(data[i]);
				int timeLimit = 120;
				robi.gurobiClustering(timeLimit);
				data[i].mipClusterRunLKH("gb"+timeLimit,i);
				
				System.out.println("gurobi local clustering done "+timeLimit);
				
				/*
				data[i] = new Data(total , sales);
				// parse graph
				data[i].parseGeneralGraph(i+".txt");
				timeLimit = 600;
				robi = new Gurobi(data[i]);
				robi.gurobiClustering(timeLimit);
				data[i].mipClusterRunLKH("gb"+timeLimit,i);
				System.out.println("gurobi local clustering done "+timeLimit);
				
				
				data[i] = new Data(total , sales);
				// parse graph
				data[i].parseGeneralGraph(i+".txt");
				timeLimit = 5000;
				robi = new Gurobi(data[i]);
				robi.gurobiClustering(timeLimit);
				data[i].mipClusterRunLKH("gb"+timeLimit,i);
				System.out.println("gurobi local clustering done "+timeLimit);
				*/
			}
		}
		System.out.println("out of ex block");
		// the parser block
		// parse all kind of 
		
		
		if(gpFlag)
		{
			System.out.println("start parse gp");
			ParseResultOfNeosClusters.parse(numOfParts, "GP", numOfTestcases,0);	
			System.out.println("end parse gp");
		}
		if(gurobiFlag)
		{
			System.out.println("start parse gurobi");
			ParseResultOfNeosClusters.parse(numOfParts, "gb120", numOfTestcases,1);
			System.out.println("end parse gurobi");
		}
		
		
		
		
		
		
		// read input graph and run GP
		//gpMain.gpNoCoordinate(args[0], uFactor, times);
		
		//gpMain.data.GPMaster2("gpNodes.txt.part."+(gpMain.data.SALESMAN-1));
		
	}

}
