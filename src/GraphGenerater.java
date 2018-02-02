import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GraphGenerater {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		/**
		 * generate test cases for 
		 * @throws IOException 
		 * @nmwMinMax[] is a array of five elements starting from index 0
		 *      	 [0] is the number of node n
		 * 			 [1] is the number of participants m
		 * 			 [2] is the number indicating how many participants should visit a node
		 * 			 [3] is the lower bound of edge cost to be generated (min)
		 * 			 [4] is the upper bound of edge cost to be generated (max)  
		 */
		
		int nmwMinMax[] = new int[5];
		int numberOfCases = 100;
		int n,m,w,min,max;
		n = 600;
		m = 10;
		w = 5;
		min = 1;
		max = 100;
		nmwMinMax[0] = n;
		nmwMinMax[1] = m;
		nmwMinMax[2] = w;
		nmwMinMax[3] = min;
		nmwMinMax[4] = max;
		GamsOutputer go = new GamsOutputer();
		for(int i=1; i<=numberOfCases ; i++)
		{
			GPmain.graphGenerator(i+".txt", nmwMinMax);
		}
		for(int i=1; i<=numberOfCases ; i++)
		{
			//go.outputGams(i+".txt",i+".gams");
			//go.outputGamsForCluster(i+".txt",i+"c.gams");
			//go.outputMiniZinc(i+".txt",i+".gams");
		}
		
	}
	
	public static void gen(int[] nmwMinMax, int numberOfCases) throws IOException {
		// TODO Auto-generated method stub
		/**
		 * generate test cases for 
		 * @throws IOException 
		 * @nmwMinMax[] is a array of five elements starting from index 0
		 *      	 [0] is the number of node n
		 * 			 [1] is the number of participants m
		 * 			 [2] is the number indicating how many participants should visit a node
		 * 			 [3] is the lower bound of edge cost to be generated (min)
		 * 			 [4] is the upper bound of edge cost to be generated (max)  
		 */
		
		//int nmwMinMax[] = new int[5];
		//int numberOfCases = 1;
		int n,m,w,min,max;
		n = 600;
		m = 10;
		w = 5;
		min = 1;
		max = 1000;
		nmwMinMax[0] = n;
		nmwMinMax[1] = m;
		nmwMinMax[2] = w;
		nmwMinMax[3] = min;
		nmwMinMax[4] = max;
		GamsOutputer go = new GamsOutputer();
		for(int i=1; i<=numberOfCases ; i++)
		{
			GPmain.graphGenerator(i+".txt", nmwMinMax);
		}
		for(int i=1; i<=numberOfCases ; i++)
		{
			go.outputGams(i+".txt",i+".gams");
			go.outputGamsForCluster(i+".txt",i+"c.gams");
			//go.outputMiniZinc(i+".txt",i+".gams");
		}
		
	}

}
