import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

public class Gurobi {
	
	public Data data;

	public Gurobi(Data data)
	{
		this.data = data;
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		File in = new File(args[0]);
		Scanner sc = new Scanner(in);
		int total = sc.nextInt();
		// sales needs to be plus one for Data for indexing from 1
		int sales = sc.nextInt()+1;
		Data data = new Data(total,sales);
		data.parseGeneralGraph(args[0]);
		Gurobi robi = new Gurobi(data);
		robi.gurobiClustering(150);
	}
	
	public void gurobiClustering(int timeLimit) throws IOException
	{	
		// n is the total number of vertices
		// m is the total number of human sensors
		int n = data.TOTALNODES+data.SALESMAN-1;
		int m = data.SALESMAN-1;
		int constraintCount = 0;
		System.out.println("total = "+n+" sales = "+m);
		try {
			GRBEnv env = new GRBEnv("gurobi.log");
			GRBModel model = new GRBModel(env);
			
			GRBVar[][] x = new GRBVar[n+1][m+1];
			for(int i=1 ; i<=n ; i++)
			{
				for(int j=1 ; j<=m ; j++)
				{
					x[i][j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "x" + i + j);
				}
			}
			GRBVar z = model.addVar(0.0, n * 999999, 0, GRB.CONTINUOUS, "z");
			model.update();
			GRBLinExpr expr = new GRBLinExpr();
			expr.addTerm(1.0, z);
			model.setObjective(expr, GRB.MINIMIZE);
			
			// constraint 1 
			// depots are in different cluster
			for(int i=1 ; i<=m ; i++)
			{
				constraintCount++;
				expr = new GRBLinExpr();
				expr.addTerm(1.0, x[i+n-m][i]);
				model.addConstr(expr, GRB.EQUAL, 1.0, "" + constraintCount);
			}
			
			// constraint 2
			// all nodes must be in w cluster
			for(int i=1 ; i<=n-m ; i++)
			{
				constraintCount++;
				expr = new GRBLinExpr();
				for(int j=1 ; j<=m ; j++)
				{
					expr.addTerm(1.0, x[i][j]);
				}
				model.addConstr(expr, GRB.EQUAL, data.w, "" + constraintCount);
			}
			
			// constarint 3
			// z is bounded by the largest cluster
			for(int i=1 ; i<=m ; i++)
			{
				constraintCount++;
				expr = new GRBLinExpr();
				for(int j=1 ; j<=n ; j++)
				{
					expr.addTerm(data.costMatrix[i+n-m][j], x[j][i]);
					expr.addTerm(data.costMatrix[j][i+n-m], x[j][i]);
				}
				expr.addTerm(-1.0, z);
				model.addConstr(expr, GRB.LESS_EQUAL, 0.0, "" + constraintCount);
			}
			
			model.getEnv().set(GRB.DoubleParam.TimeLimit, timeLimit);
			model.optimize();
			
			//logging 
			System.out.println();System.out.println();System.out.println();
			System.out.println("start printing out cluster results");
			
			for (int i = 1; i <= m; i++) {
				System.out.println("cluster No." + i);
				for (int j=1 ; j<=n; j++) 
				{
					if(x[j][i].get(GRB.DoubleAttr.X) == 1)
						System.out.print(" " + j);
				}
				System.out.println();
			}
			
			System.out.println("Start inserting sales");
			for(int i=m+1 ; i<=n ; i++)
			{
				for(int j=1 ; j<=m ; j++)
				{
					if(x[i][j].get(GRB.DoubleAttr.X) == 1)
						data.insertSales(i, j);
				}
			}
			System.out.println("inserting sales done");
			
		}catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
		System.out.println("out of here gurobi clustering!!!");
	}
	
	/*
	public void ipgogo(String args) throws IOException {
		// TODO Auto-generated method stub


		try {
			

			


			// output file name
			String name = args.substring(0, args.length() - 4) + "out.txt";
			FileWriter output = new FileWriter(name, false);
			BufferedWriter outBuf = new BufferedWriter(output);

			System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));

			// assign the amount i;
			int[] amount = new int[DEPOT];

			for (int i = 0; i < DEPOT; i++) {
				// System.out.println("Depot "+i+"went through arc ");
				int t = 0;
				for (int j = 0; j < TOTAL; j++) {
					for (int k = 0; k < TOTAL; k++) {
						int temp = (int) x[j][k][i].get(GRB.DoubleAttr.X);
						if (temp == 1) {
							t++;
						}
					}
				}
				amount[i] = t;
				// System.out.println();
				// System.out.println("z == "+z.get(GRB.DoubleAttr.X));
			}

			// printing var x

			for (int i = 0; i < DEPOT; i++) {
				System.out.println("SALES No." + i);
				System.out.println();
				System.out.println();
				for (int j = 0; j < TOTAL; j++) {
					for (int k = 0; k < TOTAL; k++) {
						System.out.print("  " + x[j][k][i].get(GRB.DoubleAttr.X));
					}
					System.out.println();
				}
				System.out.println();
				System.out.println();
				System.out.println();
			}
			// printing var u

			// outputing the paths
			double tempo = 0;
			for (int i = 0; i < DEPOT; i++) {

				// initializing tour values
				tempo = 0;

				// printing out depot i and it's tour
				outBuf.write("" + amount[i]);
				outBuf.newLine();
				// j1 from index
				// k1 to index
				int j1 = 0, k1 = 0;
				for (int j = 0; j < TOTAL; j++) {
					for (int k = 0; k < TOTAL; k++) {
						int temp = (int) x[j][k][i].get(GRB.DoubleAttr.X);
						if (temp == 1) {

							

							outBuf.write(j + " " + k);
							outBuf.newLine();
						}
					}
				}
				for (int j = DEPOT; j < TOTAL; j++) {
					tempo += cost[i][j] * x[i][j][i].get(GRB.DoubleAttr.X);
					tempo += cost[j][i] * x[j][i][i].get(GRB.DoubleAttr.X);
					for (int k = DEPOT; k < TOTAL; k++) {
						tempo += cost[k][j] * x[k][j][i].get(GRB.DoubleAttr.X);
					}
				}
				outBuf.write(" " + tempo);
				outBuf.newLine();
				System.out.println();
				System.out.println("z == " + z.get(GRB.DoubleAttr.X));
			}
			System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));
			System.out.println("depot = " + DEPOT);
			outBuf.write("" + model.get(GRB.DoubleAttr.ObjVal));
			outBuf.newLine();
			outBuf.write("EOF");
			outBuf.flush();
			outBuf.close();

			for (int i = 0; i < DEPOT; i++) {
				System.out.println("depot " + i);
				for (int j = 0; j < TOTAL; j++) {
					for (int k = 0; k < TOTAL; k++) {
						System.out.print(" " + x[j][k][i].get(GRB.DoubleAttr.X));

					}
					System.out.println();
				}
			}
			System.out.println();
			for (int i = 0; i < TOTAL; i++) {
				for (int j = 0; j < TOTAL; j++) {
					System.out.print(" " + cost[i][j]);
				}
				System.out.println();
			}

			// checking constraint 8
			double temp = 0;
			for (int i = 0; i < DEPOT; i++) {
				temp = 0;
				for (int j = DEPOT; j < TOTAL; j++) {
					temp += cost[i][j] * x[i][j][i].get(GRB.DoubleAttr.X);
					temp += cost[j][i] * x[j][i][i].get(GRB.DoubleAttr.X);
					for (int k = DEPOT; k < TOTAL; k++) {
						temp += cost[k][j] * x[k][j][i].get(GRB.DoubleAttr.X);
					}
				}
				System.out.println(i + "  cost == " + temp);
			}

			// checking constraint 1

			for (int i = 0; i < DEPOT; i++) {
				temp = 0;
				expr = new GRBLinExpr();
				for (int j = DEPOT; j < TOTAL; j++) {
					temp += x[i][j][i].get(GRB.DoubleAttr.X);
				}
				System.out.println(i + " st1 = " + temp);
			}
			// System.out.println(args[2]);
			// System.out.println(args[1]);

			// Dispose of model and environment

			model.dispose();
			env.dispose();

		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
	}

	*/
}
