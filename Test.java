import java.util.*;
import java.io.*;

public class Test{
	public static void main(String args[]){
		try{
			BufferedReader reader = new BufferedReader(new FileReader("insts.txt"));
			String inst = null;
			while((inst = reader.readLine())!= null) {
				if(inst.contains("pid")) {
					System.out.println(inst.split(" ", 2)[1]);
					getInsts(reader);
				}
				
			}
		} catch(Exception e) {e.printStackTrace();}
	}

	static String[] getInsts(BufferedReader reader) {
		ArrayList<String> ret = new ArrayList<String>();
		String inst = null;
		try{
			while(true) {
				inst = reader.readLine();
				if(inst == null || inst.equals("done")) break;
				ret.add(inst);
				System.out.println(inst);
			}
		} catch(Exception e) {e.printStackTrace();}
		
		String[] ret2 = new String[ret.size()];
		ret.toArray(ret2);
		return ret2;
	}
}

	
