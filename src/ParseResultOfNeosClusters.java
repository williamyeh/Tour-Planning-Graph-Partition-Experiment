import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ParseResultOfNeosClusters {

	public static void main(String[] args) throws IOException, RowsExceededException, WriteException {
		// TODO Auto-generated method stub
		int NUMOFTESTCASES = 1;
		int PARTITIONS = Integer.parseInt(args[0]);
		String name = args[1];
		File in[][] = new File[NUMOFTESTCASES+1][PARTITIONS+1];
		Scanner scan[][] = new Scanner[NUMOFTESTCASES+1][PARTITIONS+1];
		int results[][] = new int[NUMOFTESTCASES+1][PARTITIONS+3];
		System.out.println(System.getProperty("user.dir"));

		// read from output file
		// delete parafile , and probfile 
		for(int i=1 ; i<=NUMOFTESTCASES ; i++)
		{
			for(int j=1 ; j<=PARTITIONS ; j++)
			{
				in[i][j] = new File("output_"+name+"_"+i+"_"+j+".txt");
				Path paraPath = FileSystems.getDefault().getPath(System.getProperty("user.dir")+"\\parafile_"+name+"_"+i+"_"+j+".txt");
				Path probPath = FileSystems.getDefault().getPath(System.getProperty("user.dir")+"\\probfile_"+name+"_"+i+"_"+j+".tsp");
				Files.deleteIfExists(paraPath);
				Files.deleteIfExists(probPath);
				
				scan[i][j] = new Scanner(in[i][j]);
				for(int k=0 ; k<7 ; k++)
				{
					scan[i][j].next();
				}
				results[i][j] = scan[i][j].nextInt();
				scan[i][j].close();
			}
		}
		
		// store objective and total cost
		// delete outputfile
		int max = -1;
		int which = 0 ;
		int countAverage = 0;
		int total = 0;
		for(int i=1 ; i<=NUMOFTESTCASES ; i++)
		{
			max = -1;
			// which max
			which = 0;
			total = 0;
			for(int j=1 ; j<=PARTITIONS ; j++)
			{
				//delete outputFile
				Path outPath = FileSystems.getDefault().getPath(System.getProperty("user.dir")+"\\output_"+name+"_"+i+"_"+j+".txt");
				Files.deleteIfExists(outPath);
				// find max
				total += results[i][j];
				
				if(results[i][j]>max)
				{
					max = results[i][j];
					which = j;
				}
			}
			results[i][PARTITIONS+1] = max;
			results[i][PARTITIONS+2] = total;
			countAverage += max;
		}
		
		// write to excel
		WritableWorkbook GPresults;
		GPresults = Workbook.createWorkbook(new File(name+"Results.xls"));
		WritableSheet rsheet = GPresults.createSheet("results", 0);
		
		for(int i=1 ; i<=NUMOFTESTCASES ; i++)
		{
			for(int j=1 ; j<=PARTITIONS+2 ; j++)
			{
				rsheet.addCell(new Label(j,i,""+results[i][j]));
			}
		}
		rsheet.addCell(new Label(0,0,""+countAverage/NUMOFTESTCASES));
		GPresults.write();
		GPresults.close();
		
		/*
		File in = new File("output_GP_10_1.txt");
		Scanner scan = new Scanner(in);
		for(int i=0 ; i<7 ; i++)
		{
			//System.out.println(scan.next());
			scan.next();
		}
		System.out.println(""+scan.nextInt());
		*/
	}

	public static void parse(int partitions, String name, int numOfTestCases,int pageNum) throws IOException, RowsExceededException, WriteException, BiffException {
		// TODO Auto-generated method stub
		int NUMOFTESTCASES = numOfTestCases;
		int PARTITIONS = partitions;
		System.out.println("inside parse");
		File in[][] = new File[NUMOFTESTCASES+1][PARTITIONS+1];
		Scanner scan[][] = new Scanner[NUMOFTESTCASES+1][PARTITIONS+1];
		int results[][] = new int[NUMOFTESTCASES+1][PARTITIONS+3];
		System.out.println(System.getProperty("user.dir"));

		// read from output file
		// delete parafile , and probfile 
		for(int i=1 ; i<=NUMOFTESTCASES ; i++)
		{
			for(int j=1 ; j<=PARTITIONS ; j++)
			{
				in[i][j] = new File("output_"+name+"_"+i+"_"+j+".txt");
				Path paraPath = FileSystems.getDefault().getPath(System.getProperty("user.dir")+"\\parafile_"+name+"_"+i+"_"+j+".txt");
				Path probPath = FileSystems.getDefault().getPath(System.getProperty("user.dir")+"\\probfile_"+name+"_"+i+"_"+j+".tsp");
				Files.deleteIfExists(paraPath);
				Files.deleteIfExists(probPath);
				
				scan[i][j] = new Scanner(in[i][j]);
				for(int k=0 ; k<7 ; k++)
				{
					scan[i][j].next();
				}
				results[i][j] = scan[i][j].nextInt();
				scan[i][j].close();
			}
		}
		
		// store objective and total cost
		// delete outputfile
		int max = -1;
		int which = 0 ;
		int countAverage = 0;
		int total = 0;
		for(int i=1 ; i<=NUMOFTESTCASES ; i++)
		{
			max = -1;
			// which max
			which = 0;
			total = 0;
			for(int j=1 ; j<=PARTITIONS ; j++)
			{
				//delete outputFile
				Path outPath = FileSystems.getDefault().getPath(System.getProperty("user.dir")+"\\output_"+name+"_"+i+"_"+j+".txt");
				Files.deleteIfExists(outPath);
				// find max
				total += results[i][j];
				
				if(results[i][j]>max)
				{
					max = results[i][j];
					which = j;
				}
			}
			results[i][PARTITIONS+1] = max;
			results[i][PARTITIONS+2] = total;
			countAverage += max;
		}
		
		// write to excel
		Workbook result;
		WritableWorkbook GPresults;
		
		File sheet = new File("Results.xls");
		if(sheet.exists())
		{
			result = Workbook.getWorkbook(sheet);	
			GPresults = Workbook.createWorkbook(sheet,result);
		}
		else
		{
			GPresults = Workbook.createWorkbook(new File("Results.xls"));
		}
		
		WritableSheet rsheet = GPresults.createSheet(name, pageNum);
		
		for(int i=1 ; i<=NUMOFTESTCASES ; i++)
		{
			for(int j=1 ; j<=PARTITIONS+2 ; j++)
			{
				rsheet.addCell(new Label(j,i,""+results[i][j]));
			}
		}
		rsheet.addCell(new Label(0,0,""+countAverage/NUMOFTESTCASES));
		GPresults.write();
		GPresults.close();
		
		/*
		File in = new File("output_GP_10_1.txt");
		Scanner scan = new Scanner(in);
		for(int i=0 ; i<7 ; i++)
		{
			//System.out.println(scan.next());
			scan.next();
		}
		System.out.println(""+scan.nextInt());
		*/
	}

}
