import java.util.*;
import java.io.*;

public class Test{
	public static void main(String args[]){
		 String[] stringWeekdays = 
            {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        
        List listDays = Arrays.asList(stringWeekdays);
        
        
        
        ArrayList<String> aListDays = new ArrayList<String>(listDays);
		for(String s: aListDays) {
			System.out.println(s);
		}
		
	}	
}

	
