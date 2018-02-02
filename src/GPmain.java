import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;


public class GPmain {

	/**
	 * @param args
	 */
	public int n;
	public int k;
	public int m;
	public int w;
	// the node cost matrix for gp
	public int nodes[][];
	public int mat[][];
	public int remat[][];
	public Data data; 
	
	// initialize data
	public GPmain(Data demo)
	{
		this.data = demo;
	}
	public GPmain()
	{
		
	}

	public int distGeneral(int fromX, int fromY , int toX , int toY){

		double result = 0;
		int xSquare = 0;
		int ySquare = 0;
		int ret;
		xSquare = fromX - toX;
		xSquare = xSquare*xSquare;	
		ySquare = fromY - toY;
		ySquare = ySquare*ySquare;	
		result = Math.sqrt((double)(xSquare + ySquare));
		ret = (int)Math.round(result);
		return ret;

	}

	// read graph file and run gpmetis
	// run gp with depots
	public void gpGo (int tnum) throws IOException {
		// TODO Auto-generated method stub
		
		String in = tnum+".txt";
		File file = new File(in);
		Scanner sc = null;
		try {
			sc = new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int max;
		
		n = sc.nextInt();
		k = sc.nextInt() - 1;
		// the zoom parameter
		sc.nextInt();
		// the w
		w = sc.nextInt();
		
		// initialize nodes and matrices
		nodes = new int[n+1+k][2];
		mat = new int[n+1+k][n+1+k];
		remat = new int[n+1+k][n+1+k];

		// parse depots
		for(int i=1 ; i<=k ; i++)
		{
			nodes[i][0] = sc.nextInt();
			nodes[i][1] = sc.nextInt();
		}
		// get rid of centroids
		for(int i=1 ; i<=k ; i++)
		{
			sc.nextInt();
			sc.nextInt();
		}
		
		// parse nodes
		for(int i=k+1 ; i<= n+k ; i++)
		{
			nodes[i][0] = sc.nextInt();
			nodes[i][1] = sc.nextInt();
		}
		
		//test nodes
		for(int i=1 ; i<= k+n ; i++)
		{
			System.out.println(i+"  "+nodes[i][0]+" "+nodes[i][1]);
			
		}
		// construct mat
		max = 0;
		
		// zone 1
		// depots cannot go to anthoer depot
		for(int i=1 ; i<=k ; i++)
		{
			for(int j=1 ; j<=k ; j++)
			{
				mat[i][j] = 99999;
			}
		}
		
		// zone 2 finding max
		for(int i=1 ; i<=k ; i++)
		{
			for(int j=k+1 ; j<=k+n ; j++)
			{
				mat[i][j] = distGeneral(nodes[i][0],nodes[i][1],nodes[j][0],nodes[j][1]);
				if(mat[i][j] > max)
					max = mat[i][j];
			}
		}
		
		// zone 3 finding max
		for(int i=k+1 ; i<=k+n ; i++)
		{
			for(int j=1 ; j<=k ; j++)
			{
				mat[i][j] = distGeneral(nodes[i][0],nodes[i][1],nodes[j][0],nodes[j][1]);
				if(mat[i][j] > max)
					max = mat[i][j];
			}
		}
		
		// zone 4 finding max
		for(int i = k+1 ; i<= k+n ; i++)
		{
			for(int j = k+1 ; j <= k+n ; j++)
			{
				if(i==j)
				{
					mat[i][j] = 99999;
				}
				else
				{
					mat[i][j] = distGeneral(nodes[i][0],nodes[i][1],nodes[j][0],nodes[j][1]);
					if(mat[i][j] > max)
						max = mat[i][j];
				}
			}
		}
		

		// construct remat
		
		// zone 1
		
		for(int i=1 ; i<=k ; i++)
		{
			for(int j=1 ; j<=k ; j++)
			{
				remat[i][j] = 1;
			}
		}
		
		// zone 2
		for(int i=1 ; i<=k ; i++)
		{
			for(int j=k+1 ; j<=k+n ; j++)
			{
				remat[i][j] = max + 1 - mat[i][j]; 
				remat[i][j] = remat[i][j]*remat[i][j]*remat[i][j];
			}
		}
		
		// zone 3
		for(int i=k+1 ; i<=k+n ; i++)
		{
			for(int j=1 ; j<=k ; j++)
			{
				remat[i][j] = max + 1 - mat[i][j]; 
				remat[i][j] = remat[i][j]*remat[i][j]*remat[i][j]; 
			}
		}
		
		// zone 4
		for(int i = k+1 ; i<= k+n ; i++)
		{
			for(int j = k+1 ; j <= k+n ; j++)
			{
				if(i==j)
				{
					remat[i][j] = 99999;
				}
				else
				{
					remat[i][j] = max + 1 - mat[i][j]; 
					remat[i][j] = remat[i][j]*remat[i][j]*remat[i][j];
				}
			}
		}
		
	
		// output GP file
		String gpFileName = tnum+"gp.txt";
		FileWriter output = new FileWriter(gpFileName,false);
		BufferedWriter buf = new BufferedWriter(output);
		buf.write(""+(n+k)+" "+(n+k)*((n+k)-1)/2+" 001");
		buf.newLine();
		
		// first k depots
		for(int i=1 ; i<=k ; i++)
		{	
			// node weight
			//buf.write(""+(n*10)+" ");
			for(int j=1 ; j<=k+n ; j++)
			{
				if(i==j)
					continue;
				// node edgeweight
				buf.write(""+j+" "+remat[i][j]+" ");
			}
			buf.newLine();
		}
		
		
		
		// the rest nodes
		for(int i=k+1 ; i<=k+n ; i++)
		{
			//buf.write("1 ");
			for(int j=1 ; j<=k+n ; j++)
			{
				if(i==j)
					continue;
				// node edgeweight
				buf.write(""+j+" "+remat[i][j]+" ");
				
			}
			buf.newLine();
		}
		buf.flush();
		buf.close();
	
		// run gpmetis
		FileWriter gpFile = new FileWriter("gp.bat",false);
		BufferedWriter gpBuf = new BufferedWriter(gpFile);
		
		gpBuf.write("gpmetis.exe "+gpFileName+" "+k);
		gpBuf.newLine();
		gpBuf.flush();
		gpBuf.close();
		String cmd[] = new String[3];
		cmd[0] = "cmd";
		cmd[1] = "/C";
		cmd[2] = "gp.bat";
		Process p = Runtime.getRuntime().exec(cmd);
		
		try {
			p.waitFor();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		p.destroy();
		
		
	}
	
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
	public static void graphGenerator(String outputName, int nmwMinMax[]) throws IOException
	{
		FileWriter output = new FileWriter(outputName,false);
		BufferedWriter o = new BufferedWriter(output);
		int n;
		int m;
		int max;
		int min;
		n = nmwMinMax[0];
		m = nmwMinMax[1];
		min = nmwMinMax[3];
		max = nmwMinMax[4];
		
		o.write(nmwMinMax[0]+"\n");
		o.write(nmwMinMax[1]+"\n");
		o.write(nmwMinMax[2]+"\n");
		Random rn = new Random();
		for(int i=0 ; i<n+m ; i++)
		{
			for(int j=0 ; j<n+m ; j++)
			{
				o.write(Math.abs((rn.nextInt()%(max - min))+min)+" ");
			}
			o.write("\n");
		}
		o.flush();
		o.close();
	}
	
	/**
	 * read graph file without coordinate, i.e. get the cost matrix of nodes
	 * 
	 * construct the reversed matrix and play tricks
	 * run gpmetis without depots
	 * @throws IOException 
	 */
	public void gpNoCoordinate(String input , int uFactor , int times) throws IOException
	{
		int testCaseNum;
		File in = new File(input);
		Scanner sc = new Scanner(in);
		n = sc.nextInt();
		m = k = sc.nextInt();
		w = sc.nextInt();
		this.data.w = w;
		nodes = new int [n+k+1][n+k+1];
		mat = new int [n+1][n+1];
		remat = new int [n+1][n+1];
		int max = 0;
		
		/*
		System.out.println(n+" "+k);
		// get the entrie graph with depots and nodes
		for(int i=1 ; i<=n+k ; i++)
		{
			for(int j=1 ; j<=n+k ; j++)
			{
				nodes[i][j] = sc.nextInt();
				data.costMatrix[i][j] = nodes[i][j];  
			}
		}
		*/
		// getting the cost matrix from graph file
		// find max element concurrently
		for(int i=1 ; i<=n ; i++)
		{
			for(int j=1 ; j<=n ; j++)
			{
				mat[i][j] = (int) data.costMatrix[i][j];
				if(mat[i][j]>= max)
				{
					max = mat[i][j];
				}
			}
		}
		// construct reverse matrix
		for(int i=1 ; i<=n ; i++)
		{
			for(int j=1 ; j<=n ; j++)
			{
				remat[i][j] = max+1 - mat[i][j];
				if(times == 3)
				{
					remat[i][j] = remat[i][j]*remat[i][j]*remat[i][j]; 
				}else if (times ==2)
				{
					remat[i][j] = remat[i][j]*remat[i][j]; 	
				}
			}
		}
		// write graph file
		// output GP file
		String gpFileName = input.substring(0, input.length()-4);
		FileWriter output = new FileWriter(gpFileName,false);
		BufferedWriter buf = new BufferedWriter(output);
		buf.write(""+(n)+" "+(n)*((n)-1)/2+" 001");
		buf.newLine();
	
		// the rest nodes
		for(int i=1 ; i<=n ; i++)
		{
			//buf.write("1 ");
			for(int j=1 ; j<=n ; j++)
			{
				if(i==j)
					continue;
				// node edgeweight
				buf.write(""+j+" "+remat[i][j]+" ");
			}
			buf.newLine();
		}
		buf.flush();
		buf.close();
		// run graph partition tool
		// run gpmetis
		FileWriter gpFile = new FileWriter("gp"+gpFileName+".bat",false);
		BufferedWriter gpBuf = new BufferedWriter(gpFile);
		
		gpBuf.write("gpmetis.exe -ufactor="+uFactor+" "+gpFileName+" "+k);
		gpBuf.newLine();
		gpBuf.flush();
		gpBuf.close();
		String cmd[] = new String[3];
		cmd[0] = "cmd";
		cmd[1] = "/C";
		cmd[2] = "gp"+gpFileName+".bat";
		Process p = Runtime.getRuntime().exec(cmd);
		
		try {
			p.waitFor();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		p.destroy();
		
		
	}
	
	// read TPP input file and run gpmetis
	// run gp without the depots
	// edge = max - edge
	public void gpGo2 (int tnum , int times) throws IOException {
		// TODO Auto-generated method stub
		
		String in = tnum+".txt";
		File file = new File(in);
		Scanner sc = null;
		try {
			sc = new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int max;
		
		//GPmain gp = new GPmain(demo);
		
		n = sc.nextInt(); // num of nodes
		k = sc.nextInt() - 1; // num of sales
		
		// the zoom parameter redundant
		sc.nextInt();
		// the w
		w = sc.nextInt();
		
		// initialize nodes and matrices
		
		nodes = new int[n+1][2];
		mat = new int[n+1][n+1];
		remat = new int[n+1][n+1];

		// parse depots redundant here in the gpGo2
		for(int i=1 ; i<=k ; i++)
		{
			sc.nextInt();
			sc.nextInt();
		}
		// get rid of centroids
		for(int i=1 ; i<=k ; i++)
		{
			sc.nextInt();
			sc.nextInt();
		}
		
		// parse nodes
		for(int i=1 ; i<= n ; i++)
		{
			nodes[i][0] = sc.nextInt();
			nodes[i][1] = sc.nextInt();
		}
		
		//test nodes
		for(int i=1 ; i<= n ; i++)
		{
			System.out.println(i+"  "+nodes[i][0]+" "+nodes[i][1]);
			
		}
		// construct mat
		max = 0;
		
		
		
		// finding max and constructing mat[i][j]
		for(int i = 1 ; i<= n ; i++)
		{
			for(int j = 1 ; j <= n ; j++)
			{
				if(i==j)
				{
					mat[i][j] = 99999;
				}
				else
				{
					mat[i][j] = distGeneral(nodes[i][0],nodes[i][1],nodes[j][0],nodes[j][1]);
					if(mat[i][j] > max)
						max = mat[i][j];
				}
			}
		}
		
		/*
		//test mat
		for(int i=1 ; i<= k+n ; i++)
		{
			for(int j = k+1 ; j <= k+n ; j++)
			{
				System.out.print(mat[i][j]+" ");
			}
			System.out.println();
		}
		*/
		// construct remat
		for(int i = 1 ; i<= n ; i++)
		{
			for(int j = 1 ; j <= n ; j++)
			{
				if(i==j)
				{
					remat[i][j] = 99999;
				}
				else
				{
					remat[i][j] = max + 1 - mat[i][j]; 
					remat[i][j] =  remat[i][j]+1; //here magic
					//square
					for(int k=0 ; k<times ; k++)
					{
						remat[i][j] = remat[i][j]*remat[i][j]; 
					}
				}
			}
		}

		// output GP file
		String gpFileName = tnum+"gpNodes.txt";
		FileWriter output = new FileWriter(gpFileName,false);
		BufferedWriter buf = new BufferedWriter(output);
		buf.write(""+(n)+" "+(n)*((n)-1)/2+" 001");
		buf.newLine();
		

		// the rest nodes
		for(int i=1 ; i<=n ; i++)
		{
			//buf.write("1 ");
			for(int j=1 ; j<=n ; j++)
			{
				if(i==j)
					continue;
				// node edgeweight
				buf.write(""+j+" "+remat[i][j]+" ");
				
			}
			buf.newLine();
		}
		buf.flush();
		buf.close();
	
		// run gpmetis
		FileWriter gpFile = new FileWriter("gp.bat",false);
		BufferedWriter gpBuf = new BufferedWriter(gpFile);
		
		gpBuf.write("gpmetis.exe -ufactor=500 "+gpFileName+" "+k);
		gpBuf.newLine();
		gpBuf.flush();
		gpBuf.close();
		String cmd[] = new String[3];
		cmd[0] = "cmd";
		cmd[1] = "/C";
		cmd[2] = "gp.bat";
		Process p = Runtime.getRuntime().exec(cmd);
		
		try {
			p.waitFor();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		p.destroy();
		
		
	}
	
	// read graph file and run gpmetis to partition the graph
	// run gp without the depots
	// edge = square(max - edge)
	public void gpGo3 (int tnum) throws IOException {
		// TODO Auto-generated method stub
		
		String in = tnum+".txt";
		File file = new File(in);
		Scanner sc = null;
		try {
			sc = new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int max;
		
		//GPmain gp = new GPmain(demo);
		
		n = sc.nextInt(); // num of nodes
		k = sc.nextInt() - 1; // num of sales
		
		// the zoom parameter redundant
		sc.nextInt();
		// the w
		w = sc.nextInt();
		
		// initialize nodes and matrices
		
		nodes = new int[n+1][2];
		mat = new int[n+1][n+1];
		remat = new int[n+1][n+1];

		// parse depots redundant here in the gpGo2
		for(int i=1 ; i<=k ; i++)
		{
			sc.nextInt();
			sc.nextInt();
		}
		// get rid of centroids
		for(int i=1 ; i<=k ; i++)
		{
			sc.nextInt();
			sc.nextInt();
		}
		
		// parse nodes
		for(int i=1 ; i<= n ; i++)
		{
			nodes[i][0] = sc.nextInt();
			nodes[i][1] = sc.nextInt();
		}
		
		//test nodes
		for(int i=1 ; i<= n ; i++)
		{
			System.out.println(i+"  "+nodes[i][0]+" "+nodes[i][1]);
			
		}
		// construct mat
		max = 0;
		
		
		
		// finding max and constructing mat[i][j]
		for(int i = 1 ; i<= n ; i++)
		{
			for(int j = 1 ; j <= n ; j++)
			{
				if(i==j)
				{
					mat[i][j] = 99999;
				}
				else
				{
					mat[i][j] = distGeneral(nodes[i][0],nodes[i][1],nodes[j][0],nodes[j][1]);
					if(mat[i][j] > max)
						max = mat[i][j];
				}
			}
		}
		
		/*
		//test mat
		for(int i=1 ; i<= k+n ; i++)
		{
			for(int j = k+1 ; j <= k+n ; j++)
			{
				System.out.print(mat[i][j]+" ");
			}
			System.out.println();
		}
		*/
		// construct remat
		for(int i = 1 ; i<= n ; i++)
		{
			for(int j = 1 ; j <= n ; j++)
			{
				if(i==j)
				{
					remat[i][j] = 99999;
				}
				else
				{
					remat[i][j] = max + 1 - mat[i][j]; 
					remat[i][j] = remat[i][j]*remat[i][j]; //here magic
				}
			}
		}

		// output GP file
		String gpFileName = tnum+"gpNodes.txt";
		FileWriter output = new FileWriter(gpFileName,false);
		BufferedWriter buf = new BufferedWriter(output);
		buf.write(""+(n)+" "+(n)*((n)-1)/2+" 001");
		buf.newLine();
		

		// the rest nodes
		for(int i=1 ; i<=n ; i++)
		{
			//buf.write("1 ");
			for(int j=1 ; j<=n ; j++)
			{
				if(i==j)
					continue;
				// node edgeweight
				buf.write(""+j+" "+remat[i][j]+" ");
				
			}
			buf.newLine();
		}
		buf.flush();
		buf.close();
	
		// run gpmetis
		FileWriter gpFile = new FileWriter("gp.bat",false);
		BufferedWriter gpBuf = new BufferedWriter(gpFile);
		
		gpBuf.write("gpmetis.exe -ufactor=500 "+gpFileName+" "+k);
		gpBuf.newLine();
		gpBuf.flush();
		gpBuf.close();
		String cmd[] = new String[3];
		cmd[0] = "cmd";
		cmd[1] = "/C";
		cmd[2] = "gp.bat";
		Process p = Runtime.getRuntime().exec(cmd);
		
		try {
			p.waitFor();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		p.destroy();
		
		
	}
	
	
	public void storePartition(String partitionFile){
		int SALESMAN = data.SALESMAN;
		int TOTALNODES = data.TOTALNODES;
		//initializing
		
		// map sales num to partition num
		int mapping [] = new int[SALESMAN];
		int count [] = new int[SALESMAN];
		int partitions[][] = new int[SALESMAN][TOTALNODES];
		
		File file = new File(partitionFile);
		Scanner sc = null;
		try {
			sc = new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// init
		for(int i=0; i<SALESMAN; i++)
		{
			mapping[i] = 0;
			count[i] = 0;
		}
		
		// scan and store nodes to partition array
		for(int i=1; i<=TOTALNODES; i++)
		{
			int whichSales =sc.nextInt();
			// plus one to let the partitions starts with one
			whichSales++;
			System.out.println(i+" "+whichSales);
			partitions[whichSales][count[whichSales]]= i;
			count[whichSales]++;
		}
	}
	
	
	// read partition produced by gpGo2
	public void gpBack2(int tNum)throws IOException
	{
		int SALESMAN = data.SALESMAN;
		int TOTALNODES = data.TOTALNODES;
		//initializing
		String in = tNum+"gpNodes.txt.part."+(SALESMAN-1);
		System.out.println("GPM input file "+in);
		
		// map sales num to partition num
		int mapping [] = new int[SALESMAN];
		int count [] = new int[SALESMAN];
		int partitions[][] = new int[SALESMAN][TOTALNODES];
		
		File file = new File(in);
		Scanner sc = null;
		try {
			sc = new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// init
		for(int i=0; i<SALESMAN; i++)
		{
			mapping[i] = 0;
			count[i] = 0;
		}
		
		
		
		// scan and store nodes to partition array
		for(int i=1; i<=TOTALNODES; i++)
		{
			int whichSales =sc.nextInt();
			// plus one to let the partitions starts with one
			whichSales++;
			System.out.println(i+" "+whichSales);
			partitions[whichSales][count[whichSales]]= i;
			count[whichSales]++;
		}
		
		
		
		int avrDist [][] = new int[SALESMAN][SALESMAN];
		int min = 99999999; //magic number
		int choiseToMerge [] = new int[SALESMAN];
		boolean chosen [] = new boolean[SALESMAN];  
		// mark every partition as unchosen, i.e. false
		for(int i=1 ; i<SALESMAN ; i++)
		{
			chosen[i] = false;
		}
		
		// merge partitions with depots
		// merging depot no.i
		for(int i=1 ; i<SALESMAN; i++)
		{
			
			// find the closest partition on average
			// scan through all partitions
			for (int j=1 ; j<SALESMAN ; j++)
			{
				// skip ahead if this partition is chosen
				if(chosen[j]==true)
				{
					continue;
				}
				int temp = 0;
				
				// to do!! int connectivity = 0;
				// scan through nodes in partition
				System.out.print("Sales no."+j+" currentNodes ");
				for (int k=0 ; k<count[j] ; k++)
				{
					
					int currentNode = partitions[j][k];
					System.out.print(currentNode+" ");
					temp += data.distMatrix[TOTALNODES+i][currentNode];
				}
				System.out.println();
				// compute average distance toward partition j for depot i
				avrDist[i][j] = temp / (count[j]+1);
				if(min > avrDist[i][j])
				{
					min = avrDist[i][j];
					choiseToMerge[i] = j;
				}
			}
			System.out.println("hello!!!   sales "+i+" choose "+choiseToMerge[i]);
			chosen[choiseToMerge[i]] = true;
			min = 99999999;
			// check if the connectivity is great enough
			// to do!! check connectivity for 
			// merge it to sales
			for(int j=0 ; j<count[choiseToMerge[i]] ; j++)
			{
				data.sales[i][j] = partitions[choiseToMerge[i]][j];
			}
		}
	}
	
	
	// read graph file and run gpmetis
	// run gp without the depots
	// finding the best ufactor
	public void gpGo3 (int tnum,int ufactor) throws IOException {
		// TODO Auto-generated method stub
		
		String in = tnum+".txt";
		File file = new File(in);
		Scanner sc = null;
		try {
			sc = new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int max;
		
		//GPmain gp = new GPmain();
		
		n = sc.nextInt(); // num of nodes
		k = sc.nextInt() - 1; // num of sales
		
		// the zoom parameter redundant
		sc.nextInt();
		// the w
		w = sc.nextInt();
		
		// initialize nodes and matrices
		
		nodes = new int[n+1][2];
		mat = new int[n+1][n+1];
		remat = new int[n+1][n+1];

		// parse depots redundant here in the gpGo2
		for(int i=1 ; i<=k ; i++)
		{
			sc.nextInt();
			sc.nextInt();
		}
		// get rid of centroids
		for(int i=1 ; i<=k ; i++)
		{
			sc.nextInt();
			sc.nextInt();
		}
		
		// parse nodes
		for(int i=1 ; i<= n ; i++)
		{
			nodes[i][0] = sc.nextInt();
			nodes[i][1] = sc.nextInt();
		}
		
		//test nodes
		for(int i=1 ; i<= n ; i++)
		{
			System.out.println(i+"  "+nodes[i][0]+" "+nodes[i][1]);
			
		}
		// construct mat
		max = 0;
		
		
		
		// zone 4 finding max and constructing mat[i][j]
		for(int i = 1 ; i<= n ; i++)
		{
			for(int j = 1 ; j <= n ; j++)
			{
				if(i==j)
				{
					mat[i][j] = 99999;
				}
				else
				{
					mat[i][j] = distGeneral(nodes[i][0],nodes[i][1],nodes[j][0],nodes[j][1]);
					if(mat[i][j] > max)
						max = mat[i][j];
				}
			}
		}
		
		/*
		//test mat
		for(int i=1 ; i<= k+n ; i++)
		{
			for(int j = k+1 ; j <= k+n ; j++)
			{
				System.out.print(mat[i][j]+" ");
			}
			System.out.println();
		}
		*/
		// construct remat
		for(int i = 1 ; i<= n ; i++)
		{
			for(int j = 1 ; j <= n ; j++)
			{
				if(i==j)
				{
					remat[i][j] = 99999;
				}
				else
				{
					remat[i][j] = max + 1 - mat[i][j]; 
					remat[i][j] = remat[i][j]*remat[i][j]*remat[i][j];
				}
			}
		}

		// output GP file
		String gpFileName = tnum+"gpNodes.txt";
		FileWriter output = new FileWriter(gpFileName,false);
		BufferedWriter buf = new BufferedWriter(output);
		buf.write(""+(n)+" "+(n)*((n)-1)/2+" 001");
		buf.newLine();
		

		// the rest nodes
		for(int i=1 ; i<=n ; i++)
		{
			//buf.write("1 ");
			for(int j=1 ; j<=n ; j++)
			{
				if(i==j)
					continue;
				// node edgeweight
				buf.write(""+j+" "+remat[i][j]+" ");
				
			}
			buf.newLine();
		}
		buf.flush();
		buf.close();
	
		// run gpmetis
		FileWriter gpFile = new FileWriter("gp.bat",false);
		BufferedWriter gpBuf = new BufferedWriter(gpFile);
		
		gpBuf.write("gpmetis.exe -ufactor="+ufactor+" "+gpFileName+" "+k);
		gpBuf.newLine();
		gpBuf.flush();
		gpBuf.close();
		String cmd[] = new String[3];
		cmd[0] = "cmd";
		cmd[1] = "/C";
		cmd[2] = "gp.bat";
		Process p = Runtime.getRuntime().exec(cmd);
		
		try {
			p.waitFor();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		p.destroy();
		
		
	}
	
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		
		File file = new File(args[0]);
		Scanner sc = null;
		try {
			sc = new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int max;
		
		GPmain gp = new GPmain();
		gp.n = sc.nextInt();
		gp.k = sc.nextInt() - 1;
		// the zoom parameter
		sc.nextInt();
		// the w
		gp.w = sc.nextInt();
		
		// initialize nodes and matrices
		gp.nodes = new int[gp.n+1+gp.k][2];
		gp.mat = new int[gp.n+1+gp.k][gp.n+1+gp.k];
		gp.remat = new int[gp.n+1+gp.k][gp.n+1+gp.k];

		// parse depots
		for(int i=1 ; i<=gp.k ; i++)
		{
			gp.nodes[i][0] = sc.nextInt();
			gp.nodes[i][1] = sc.nextInt();
		}
		// get rid of centroids
		for(int i=1 ; i<=gp.k ; i++)
		{
			sc.nextInt();
			sc.nextInt();
		}
		
		// parse nodes
		for(int i=gp.k+1 ; i<= gp.n+gp.k ; i++)
		{
			gp.nodes[i][0] = sc.nextInt();
			gp.nodes[i][1] = sc.nextInt();
		}
		
		//test gp.nodes
		for(int i=1 ; i<= gp.k+gp.n ; i++)
		{
			System.out.println(i+"  "+gp.nodes[i][0]+" "+gp.nodes[i][1]);
			
		}
		// construct mat
		max = 0;
		
		// zone 1
		// depots cannot go to anthoer depot
		for(int i=1 ; i<=gp.k ; i++)
		{
			for(int j=1 ; j<=gp.k ; j++)
			{
				gp.mat[i][j] = 99999;
			}
		}
		
		// zone 2 finding max
		for(int i=1 ; i<=gp.k ; i++)
		{
			for(int j=gp.k+1 ; j<=gp.k+gp.n ; j++)
			{
				gp.mat[i][j] = gp.distGeneral(gp.nodes[i][0],gp.nodes[i][1],gp.nodes[j][0],gp.nodes[j][1]);
				if(gp.mat[i][j] > max)
					max = gp.mat[i][j];
			}
		}
		
		// zone 3 finding max
		for(int i=gp.k+1 ; i<=gp.k+gp.n ; i++)
		{
			for(int j=1 ; j<=gp.k ; j++)
			{
				gp.mat[i][j] = gp.distGeneral(gp.nodes[i][0],gp.nodes[i][1],gp.nodes[j][0],gp.nodes[j][1]);
				if(gp.mat[i][j] > max)
					max = gp.mat[i][j];
			}
		}
		
		// zone 4 finding max
		for(int i = gp.k+1 ; i<= gp.k+gp.n ; i++)
		{
			for(int j = gp.k+1 ; j <= gp.k+gp.n ; j++)
			{
				if(i==j)
				{
					gp.mat[i][j] = 99999;
				}
				else
				{
					gp.mat[i][j] = gp.distGeneral(gp.nodes[i][0],gp.nodes[i][1],gp.nodes[j][0],gp.nodes[j][1]);
					if(gp.mat[i][j] > max)
						max = gp.mat[i][j];
				}
			}
		}
		
		/*
		//test gp.mat
		for(int i=1 ; i<= gp.k+gp.n ; i++)
		{
			for(int j = gp.k+1 ; j <= gp.k+gp.n ; j++)
			{
				System.out.print(gp.mat[i][j]+" ");
			}
			System.out.println();
		}
		*/
		// construct remat
		
		// zone 1
		
		for(int i=1 ; i<=gp.k ; i++)
		{
			for(int j=1 ; j<=gp.k ; j++)
			{
				gp.remat[i][j] = 1;
			}
		}
		
		// zone 2
		for(int i=1 ; i<=gp.k ; i++)
		{
			for(int j=gp.k+1 ; j<=gp.k+gp.n ; j++)
			{
				gp.remat[i][j] = max + 1 - gp.mat[i][j]; 
				gp.remat[i][j] = gp.remat[i][j]*gp.remat[i][j];
			}
		}
		
		// zone 3
		for(int i=gp.k+1 ; i<=gp.k+gp.n ; i++)
		{
			for(int j=1 ; j<=gp.k ; j++)
			{
				gp.remat[i][j] = max + 1 - gp.mat[i][j]; 
				gp.remat[i][j] = gp.remat[i][j]*gp.remat[i][j]; 
			}
		}
		
		// zone 4
		for(int i = gp.k+1 ; i<= gp.k+gp.n ; i++)
		{
			for(int j = gp.k+1 ; j <= gp.k+gp.n ; j++)
			{
				if(i==j)
				{
					gp.remat[i][j] = 99999;
				}
				else
				{
					gp.remat[i][j] = max + 1 - gp.mat[i][j]; 
					gp.remat[i][j] = gp.remat[i][j]*gp.remat[i][j];
				}
			}
		}
		
		
		
		
		// output GP file
		
		String subin = args[0].replace(".txt", "");
		FileWriter output = new FileWriter(subin+"gp.txt",false);
		BufferedWriter buf = new BufferedWriter(output);
		buf.write(""+(gp.n+gp.k)+" "+(gp.n+gp.k)*((gp.n+gp.k)-1)/2+" 011");
		buf.newLine();
		
		// first k depots
		for(int i=1 ; i<=gp.k ; i++)
		{	
			// node weight
			buf.write(""+(gp.n)+" ");
			//buf.write("1 ");
			for(int j=1 ; j<=gp.k+gp.n ; j++)
			{
				if(i==j)
					continue;
				// node edgeweight
				buf.write(""+j+" "+gp.remat[i][j]+" ");
			}
			buf.newLine();
		}
		
		
		
		// the rest nodes
		for(int i=gp.k+1 ; i<=gp.k+gp.n ; i++)
		{
			buf.write("1 ");
			for(int j=1 ; j<=gp.k+gp.n ; j++)
			{
				if(i==j)
					continue;
				// node edgeweight
				buf.write(""+j+" "+gp.remat[i][j]+" ");
				
			}
			buf.newLine();
		}
		buf.flush();
		buf.close();
		
	}

}
