import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ScanTest {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		File file = new File("res.txt");
		Scanner sc = new Scanner(file);
		for(int i=0 ; i<42 ; i++)
			System.out.println(sc.nextLine());
		System.out.println(sc.next());
		System.out.println(sc.next());
			
	}

}
