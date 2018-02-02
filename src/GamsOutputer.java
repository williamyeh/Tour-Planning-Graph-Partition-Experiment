import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class GamsOutputer {
	
	public int n;
	public int k;
	public int m;
	public int w;
	// the node cost matrix for gp
	public int [][] costMatrix;
	public int [][] costMatrixDepotFirst;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		GamsOutputer go = new GamsOutputer();
		for(int i=1 ; i<=100 ; i++)
		{
			//go.outputGams(i+".txt",i+".gams");
			//go.outputMiniZinc(i+".txt",i+".mzn");
			go.outputGamsForCluster(i+".txt",i+"c.gams");
		}
		
		
	}
	
	// read in the graph and output it's gams
	public void outputGams(String in, String out) throws IOException
	{
		
		File input = new File(in);
		Scanner sc = new Scanner(input);
		n = sc.nextInt();
		m = k = sc.nextInt();
		w = sc.nextInt();
		
		costMatrix = new int [n+k+1][n+k+1];
		costMatrixDepotFirst = new int [n+k+1][n+k+1];
		// get the entrie graph with depots and nodes
		for(int i=1 ; i<=n+k ; i++)
		{
			for(int j=1 ; j<=n+k ; j++)
			{
				costMatrix[i][j] = sc.nextInt();
			}
		}
		
		// construct a depot first cost matrix for gams
		
		// zone depot
		for(int i=n+1 ; i<=n+k ; i++)
		{
			for(int j=n+1 ; j<=n+k ; j++)
			{
				costMatrixDepotFirst[i-n][j-n] = 99999;
			}
		}
		// zone node
		for(int i=1 ; i<=n ; i++)
		{
			for(int j=1 ; j<=n ; j++)
			{
				costMatrixDepotFirst[i+k][j+k] = costMatrix[i][j];
			}
		}
		// zone depots to nodes
		for(int i=n+1 ; i<=n+k ; i++)
		{
			for(int j=1 ; j<=n ; j++)
			{
				costMatrixDepotFirst[i-n][j+k] = costMatrix[i][j];
			}
		}
		// zone nodes to depots
		for(int i=1 ; i<=n ; i++)
		{
			for(int j=n+1 ; j<=n+k ; j++)
			{
				costMatrixDepotFirst[i+k][j-n] = costMatrix[i][j];
			}
		}
		
		// output GAMS section
		FileWriter fw = new FileWriter(out);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write("SET\n");
		bw.write("I  first /1*"+(n+k)+"/\n");
		bw.write("K(I)  depot1 /1*"+(k)+"/\n");
		bw.write("O(I)  node1 /"+(k+1)+"*"+(n+k)+"/\n");
		bw.write("alias(I,J);\nalias(K,L);\nalias(O,P);\n");
		bw.write("TABLE C(I,J)  node to node\n");
		bw.write("                          ");
		for(int i=1 ; i<=n+k ; i++)
		{
			String temp = ""+i; 
			bw.write(""+i);
			for(int j=0 ; j<26-temp.length();j++)
			{
				bw.write(" ");
			}
			
		}
		bw.newLine();
		
		for(int i=1 ; i<=n+k ; i++)
		{
			String temp = ""+i; 
			bw.write(""+i);
			for(int j=0 ; j<26-temp.length();j++)
			{
				bw.write(" ");
			}
			
			for(int j=1 ; j<=n+k ; j++)
			{
				String temp2 = ""+costMatrixDepotFirst[i][j];
				bw.write(temp2);
				for(int l=0 ; l<26-temp2.length();l++)
				{
					bw.write(" ");
				}
			}
			bw.newLine();
		}
		bw.write(";\n");
		//SCALARS
		bw.write("SCALAR TOTAL "+" /"+(n+k)+"/;");
		bw.newLine();
		bw.write("SCALAR NODES "+" /"+n+"/;");
		bw.newLine();
		bw.write("SCALAR W "+" /"+w+"/;");
		bw.newLine();
		bw.write("SCALAR Y "+" /"+(n+k)*w+"/;");
		bw.newLine();
		bw.write("SCALAR M "+" /"+1+"/;");
		bw.newLine();
		
		//VARIABLES
		bw.write("BINARY VARIABLE X(I,J,K)  x variable ;");
		bw.newLine();
		bw.write("INTEGER VARIABLE U(K,O)    u variable ;");
		bw.newLine();
		bw.write("VARIABLE   Z         objective ;");
		bw.newLine();
		
		bw.write("EQUATIONS");
		bw.newLine();
		//EQUATIONS
		bw.write("CON1(K)   constraint 1");
		bw.newLine();
		bw.write("CON2(K)   constraint 2");
		bw.newLine();
		bw.write("CON3   constraint 3");
		bw.newLine();
		bw.write("CON4(O)   constraint 4");
		bw.newLine();
		bw.write("CON5(O,K)   constraint 5");
		bw.newLine();
		bw.write("CON6(O,K)   constraint 6");
		bw.newLine();
		bw.write("CON7(K)   constraint 7");
		bw.newLine();
		
		bw.write("CON10(O,P,K)   constraint 10");
		bw.newLine();
		bw.write("CON11(O,K)   constraint 11;");
		bw.newLine();
		
		
		bw.write("CON1(K).. SUM(O,X(K,O,K)) =E= 1;");
		bw.newLine();
		bw.write("CON2(K).. SUM(O,X(O,K,K)) =E= 1;");
		bw.newLine();
		bw.write("CON3..  SUM((K,L),X(K,L,K)) =E= 0;");
		bw.newLine();
		bw.write("CON4(O) .. SUM(K,X(K,O,K)) + SUM((P,K),X(P,O,K)) =e= W;");
		bw.newLine();
		bw.write("CON5(O,K) .. X(K,O,K) + SUM(P,X(P,O,K)) =L= 1;");
		bw.newLine();
		bw.write("CON6(O,K) .. X(K,O,K) + SUM(P,X(P,O,K)) - X(O,K,K) - SUM(P,X(O,P,K)) =e= 0;");
		bw.newLine();
		bw.write("CON7(K) .. Z =G= SUM(O,C(K,O)*X(K,O,K) + C(O,K)*X(O,K,K)) + SUM((P,O),C(P,O)*X(P,O,K));");
		bw.newLine();
		
		bw.write("CON10(O,P,K) .. U(K,O) - U(K,P) + (TOTAL-1)*X(O,P,K) + (TOTAL-3)*X(P,O,K) =L= (TOTAL-2);");
		bw.newLine();
		bw.write("CON11(O,K) .. X(O,O,K) =e= 0;");
		bw.newLine();
		
		// remain stuff
		bw.write("MODEL TRANSPORT /ALL/;");
		bw.newLine();
		bw.write("Option Reslim = 28000;");
//		bw.write("Option Reslim = 15000;");
		bw.newLine();
		bw.write("SOLVE TRANSPORT USING MIP MINIMIZING Z ;");
		bw.newLine();
		bw.flush();
		bw.close();

	}

	// read in the graph and output it's gams
	public void outputGamsForCluster(String in, String out) throws IOException
	{
		
		File input = new File(in);
		Scanner sc = new Scanner(input);
		n = sc.nextInt();
		m = k = sc.nextInt();
		w = sc.nextInt();
		
		costMatrix = new int [n+k+1][n+k+1];
		costMatrixDepotFirst = new int [n+k+1][n+k+1];
		// get the entrie graph with depots and nodes
		for(int i=1 ; i<=n+k ; i++)
		{
			for(int j=1 ; j<=n+k ; j++)
			{
				costMatrix[i][j] = sc.nextInt();
			}
		}
		
		// construct a depot first cost matrix for gams
		
		// zone depot
		for(int i=n+1 ; i<=n+k ; i++)
		{
			for(int j=n+1 ; j<=n+k ; j++)
			{
				costMatrixDepotFirst[i-n][j-n] = 99999;
			}
		}
		// zone node
		for(int i=1 ; i<=n ; i++)
		{
			for(int j=1 ; j<=n ; j++)
			{
				costMatrixDepotFirst[i+k][j+k] = costMatrix[i][j];
			}
		}
		// zone depots to nodes
		for(int i=n+1 ; i<=n+k ; i++)
		{
			for(int j=1 ; j<=n ; j++)
			{
				costMatrixDepotFirst[i-n][j+k] = costMatrix[i][j];
			}
		}
		// zone nodes to depots
		for(int i=1 ; i<=n ; i++)
		{
			for(int j=n+1 ; j<=n+k ; j++)
			{
				costMatrixDepotFirst[i+k][j-n] = costMatrix[i][j];
			}
		}
		
		// output GAMS section
		FileWriter fw = new FileWriter(out);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write("SET\n");
		bw.write("I  allnodes /1*"+(n+k)+"/\n");
		bw.write("D(I)  depot set /1*"+(k)+"/\n");
		bw.write("N(I)  nodes set /"+(k+1)+"*"+(n+k)+"/\n");
		bw.write("TABLE C(I,I)  node to node\n");
		bw.write("                          ");
		for(int i=1 ; i<=n+k ; i++)
		{
			String temp = ""+i; 
			bw.write(""+i);
			for(int j=0 ; j<26-temp.length();j++)
			{
				bw.write(" ");
			}
			
		}
		bw.newLine();
		
		for(int i=1 ; i<=n+k ; i++)
		{
			String temp = ""+i; 
			bw.write(""+i);
			for(int j=0 ; j<26-temp.length();j++)
			{
				bw.write(" ");
			}
			
			for(int j=1 ; j<=n+k ; j++)
			{
				String temp2 = ""+costMatrixDepotFirst[i][j];
				bw.write(temp2);
				for(int l=0 ; l<26-temp2.length();l++)
				{
					bw.write(" ");
				}
			}
			bw.newLine();
		}
		bw.write(";\n");
		//SCALARS
		bw.write("SCALAR TOTAL "+" /"+(n+k)+"/;");
		bw.newLine();
		bw.write("SCALAR NODES "+" /"+n+"/;");
		bw.newLine();
		bw.write("SCALAR W "+" /"+w+"/;");
		bw.newLine();
		
		//VARIABLES
		bw.write("BINARY VARIABLE X(I,D)  x variable ;");
		bw.newLine();
		bw.write("VARIABLE   Z         objective ;");
		bw.newLine();
		
		bw.write("EQUATIONS");
		bw.newLine();
		//EQUATIONS
		bw.write("CON1(D) Depots must be in different partitions\n");
		bw.write("CON2(N)  All nodes must be in w partitions\n");
		bw.write("CON3(D)   bounding z;");
		bw.newLine();
		
		
		
		bw.write("CON1(D).. X(D,D) =E= 1;");
		bw.newLine();
		bw.write("CON2(N).. SUM(D,X(N,D)) =E="+w+";");
		bw.newLine();
		bw.write("CON3(D)..  SUM((I),X(I,D)*C(I,D))+SUM((I),X(I,D)*C(D,I)) =L= Z;");
		bw.newLine();
		
		
		// remain stuff
		bw.write("MODEL TRANSPORT /ALL/;");
		bw.newLine();
//		bw.write("Option Reslim = 28000;");
		bw.write("Option Reslim = 5000;");
		bw.newLine();
		bw.write("SOLVE TRANSPORT USING MIP MINIMIZING Z ;");
		bw.newLine();
		bw.flush();
		bw.close();

	}
	
	// read in the graph and output it's miniZinc
	public void outputMiniZinc(String in, String out) throws IOException
	{
		File input = new File(in);
		Scanner sc = new Scanner(input);
		n = sc.nextInt();
		m = k = sc.nextInt();
		w = sc.nextInt();
		
		costMatrix = new int [n+k+1][n+k+1];
		costMatrixDepotFirst = new int [n+k+1][n+k+1];
		// get the entrie graph with depots and nodes
		for(int i=1 ; i<=n+k ; i++)
		{
			for(int j=1 ; j<=n+k ; j++)
			{
				costMatrix[i][j] = sc.nextInt();
			}
		}
		
		// construct a depot first cost matrix for gams
		
		// zone depot
		for(int i=n+1 ; i<=n+k ; i++)
		{
			for(int j=n+1 ; j<=n+k ; j++)
			{
				costMatrixDepotFirst[i-n][j-n] = 99999;
			}
		}
		// zone node
		for(int i=1 ; i<=n ; i++)
		{
			for(int j=1 ; j<=n ; j++)
			{
				costMatrixDepotFirst[i+k][j+k] = costMatrix[i][j];
			}
		}
		// zone depots to nodes
		for(int i=n+1 ; i<=n+k ; i++)
		{
			for(int j=1 ; j<=n ; j++)
			{
				costMatrixDepotFirst[i-n][j+k] = costMatrix[i][j];
			}
		}
		// zone nodes to depots
		for(int i=1 ; i<=n ; i++)
		{
			for(int j=n+1 ; j<=n+k ; j++)
			{
				costMatrixDepotFirst[i+k][j-n] = costMatrix[i][j];
			}
		}
		
		// output minizinc section
		FileWriter fw = new FileWriter(out);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write("int: total = "+(n+k)+";\n");
		bw.write("int: nodes = "+(n-k)+";\n");
		bw.write("int: w = "+w+";\n");
		bw.write("set of int: I = 1.."+(n+k)+";\n");
		bw.write("set of int: J = 1.."+(n+k)+";\n");
		bw.write("set of int: K = 1.."+(k)+";\n");
		bw.write("set of int: L = 1.."+(k)+";\n");
		bw.write("set of int: O ="+(k+1)+".."+(n+k)+";\n");
		bw.write("set of int: P ="+(k+1)+".."+(n+k)+";\n");
		bw.write("array[I,J] of int: c;\n");
		bw.write("c = [ ");
		for(int i=1 ; i<=n+k ; i++)
		{
			bw.write("|");
			for(int j=1 ; j<=n+k ; j++)
			{
				bw.write(costMatrixDepotFirst[i][j]+", ");
			}
			if(i==n+k)
				bw.write("|];\n");
			else 
				bw.write("\n");
		}
		bw.write("% x var\narray[I,J,K] of var bool: x;\n");
		bw.write("% u var\narray[K,O] of var int: u;\n% z var\nvar float: z;\n");
		bw.write("constraint forall(i in K)"
				+ "(   sum(j in O) (x[i,j,i]) = 1);\nconstraint forall(i in K)"
				+ "(   sum(j in O) (x[j,i,i]) = 1);\nconstraint sum(i in K, j in L)"
				+ "( x[i,j,i] )= 0; constraint forall(i in O)("
				+ "  sum(j in K) ( x[j,i,j] )+ sum(k in P, l in K)( x[k,i,l] )= w);\n"
				+ "constraint forall(i in O, j in K)(  x[j,i,j] + sum(k in P) (x[k,i,j])<= 1"
				+ ");\nconstraint forall(i in O, j in K)(  "
				+ "x[j,i,j] + sum(k in P) (x[k,i,j] )- x[i,j,j] - sum(l in P)(x[i,l,j]) = 0);\n"
				+ "constraint forall(i in K)(  z >= sum(j in O) (c[i,j] * x[i,j,i] + c[j,i]*x[j,i,i]) "
				+ "+ sum(k in P , l in O)( c[k,l] * x[k,l,i]));\n"
				+ "constraint forall(i in O, j in P, k in K)("
				+ "  u[k,i] - u[k,j] + (total-1) * x[i,j,k] + (total-3) * x[j,i,k] <= (total-2)"
				+ ");\n"
				+ "constraint forall(i in O, j in K)(  x[i,i,j] = 0);\n"
				+ "\nsolve minimize z;\n");
		bw.flush();
		bw.close();
	}
}
