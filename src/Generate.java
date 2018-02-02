import java.io.File;
import java.io.IOException;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import com.gams.api.GAMSDatabase;
import com.gams.api.GAMSJob;
import com.gams.api.GAMSOptions;
import com.gams.api.GAMSParameter;
import com.gams.api.GAMSSet;
import com.gams.api.GAMSWorkspace;
import com.gams.api.GAMSWorkspaceInfo;

/**
 *  This example demonstrates how to retrieve an input for GAMS Transport Model
 *  from an Excel file (transport.xlsx) using JExcelApi, a open source java API
 *  for reading/writing Excel (See {@link http://jexcelapi.sourceforge.net/}).
 */

public class Generate {

	private static int iCount = 0;
	private static int jCount = 0;
    private static String[][] benefit = null;
    private static String[][] cost = null;
    private static String[][] value = null;
    
    // input : former gougou style spread sheet
    // output : gdk file name
	public static void main(String input, String output) {

        // create a workspace
        GAMSWorkspaceInfo  wsInfo  = new GAMSWorkspaceInfo();
        System.out.println(System.getProperty("user.dir"));
        wsInfo.setWorkingDirectory("C:\\Users\\willy\\Desktop\\Cross");
        GAMSWorkspace ws = new GAMSWorkspace(wsInfo);
        
        String dirpath = ws.workingDirectory();
        getData(new File(input));
    	setData(ws,output);
 
    }
	
	private static String setPath(String dirpath, int num){
		return dirpath + "\\data" + num + ".xls";
	}
	
	private static void getData(File inputFile){
		Workbook w;
        try {
            w = Workbook.getWorkbook(inputFile);

            Sheet benefits = w.getSheet("benefits");
            benefit = new String[benefits.getRows()+1][benefits.getColumns()+1];
            for (int j = 0; j < benefits.getColumns(); j++){
                for (int i = 0; i < benefits.getRows(); i++){
                	if(i == 0)
                		benefit[i][j] = "Region" + j;
                	else if(j == 0)
                		benefit[i][j] = "Participant" + i;
                	else
                		benefit[i][j] = benefits.getCell(j, i).getContents();
                }
            }
            jCount = benefits.getColumns();
            iCount = benefits.getRows();

            Sheet costs = w.getSheet("costs");
            cost = new String[costs.getRows()][costs.getColumns()];

            for (int j = 0; j < costs.getColumns(); j++){
                for (int i = 0; i < costs.getRows(); i++){
                	if(i == 0)
                		cost[i][j] = "Region" + j;
                	else if(j == 0)
                		cost[i][j] = "Participant" + i;
                	else
                		cost[i][j] = costs.getCell(j, i).getContents();
                }
            }
            
            Sheet values = w.getSheet("values");
            value = new String[values.getRows()][values.getColumns()];
            for (int j = 0; j < values.getColumns(); j++){
                for (int i = 0; i < values.getRows(); i++){
                	if(i == 0)
                		value[i][j] = "Region" + (j+1);
                	else
                		value[i][j] = values.getCell(j, i).getContents();
                }
            }

            w.close();
        } catch (IOException e) {
        	e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
	}
	
    private static void setData(GAMSWorkspace ws, String output){
    	// Creating the GAMSDatabase and fill with the workbook data
        GAMSDatabase db = ws.addDatabase();

        GAMSSet i = db.addSet("i", 1, "Participant");
        GAMSSet j = db.addSet("j", 1, "Region");
        GAMSSet k = db.addSet("k", 1, "Value");
        GAMSParameter benefitParam = db.addParameter("b", 2, "");
        GAMSParameter costParam = db.addParameter("c", 2, "Cost");
        GAMSParameter valueParam = db.addParameter("v", 2, "");

        k.addRecord("Value");
        
        for (int ic = 1; ic < iCount; ic++)
        	i.addRecord(benefit[ic][0]);
        for (int jc = 1; jc < jCount; jc++) {
            j.addRecord( benefit[0][jc] );
            valueParam.addRecord(new String [] {"Value", value[0][jc-1] }).setValue( Double.valueOf(value[1][jc-1]).doubleValue() );
            String[] data = null;
            for(int ic = 1; ic < iCount; ic++){
            	data =  new String[] { benefit[ic][0], benefit[0][jc] };
            	//System.out.println(benefit[ic][0] + " to " + benefit[0][jc]);
            	benefitParam.addRecord( data ).setValue( Double.valueOf(benefit[ic][jc]) );
            	costParam.addRecord( data ).setValue( Double.valueOf(cost[ic][jc]) );
            }
        }
        // Create and run the GAMSJob
        GAMSOptions opt = ws.addOptions();
        GAMSJob task = ws.addJobFromString(model);
        //File("C:\\Users\\Dog\\Desktop\\data\\IIS\\Generate.gms");
        opt.defines("data", db.getName());
        task.run(opt, db);
        System.out.println(System.getProperty("user.dir")+"\\"+output);
        task.OutDB().export(System.getProperty("user.dir")+"\\"+output);
        //task.OutDB().export("C:\\Users\\Willy\\Desktop\\Participant-Selection-Problem-master\\Experiments\\GenerateGDX\\" + "Dog"  ".gdx" );
    }
    
    static String model =
            " Sets                                                                       \n"+
            "      i   		    participant 	                                         \n"+
            "      j        	  region                                                 \n"+
            "	   k			region value											 \n"+
            "                                                                            \n"+
            " Parameters                                                                 \n"+
            "      b(i,j)   benefit of each participant   							     \n"+
            "	   c(i,j)   cost of each participant			   						 \n"+
            "      v(k,j)     region value												 \n"+
            "                                                                            \n"+
            "$gdxin %data%                                                         	     \n"+
            "$load i j k b c v                                               		     \n"+
            "$gdxin                                                                      \n";
    
}
