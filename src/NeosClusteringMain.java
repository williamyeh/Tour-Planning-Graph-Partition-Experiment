import java.io.IOException;

public class NeosClusteringMain {
	
	public int n;
	public int k;
	public int m;
	public int w;
	// the node cost matrix for gp
	public int nodes[][];
	public int mat[][];
	public int remat[][];
	public Data data; 

	public NeosClusteringMain(Data data)
	{
		this.data = data;
	}
	/**
	 * read graph file without coordinate, i.e. get the cost matrix of nodes
	 * use gams and neos to cluster the partitions
	 * run gpmetis without depots
	 * @throws IOException 
	 */
	public void GamsCluster(String input)
	{
		
	}

}
