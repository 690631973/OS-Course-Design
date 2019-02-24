import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.*;
import javafx.concurrent.*;
import javafx.beans.property.*;
import java.util.concurrent.atomic.*;
import java.io.*;

public class ProcessIniter {
	Scheduler duler;
	ProcessIniter(Scheduler duler) {
		this.duler = duler;
	}
	
	void initPros() {
		try{
			BufferedReader reader = new BufferedReader(new FileReader("insts.txt"));
			String inst = null;
			while((inst = reader.readLine())!= null) {
				if(inst.contains("pid")) {
					int pid = Integer.valueOf(inst.split(":", 2)[1]);
					System.out.println(pid);
					String [] pinsts = getInsts(reader);
					Process p = new Process(duler, pid, pinsts);
					duler.pros.addAll(p);
				}
			}
		} catch(Exception e) {e.printStackTrace();}

		
		try{
			BufferedReader reader = new BufferedReader(new FileReader("create_pro_time.txt"));
			String line = "";
			while((line = reader.readLine()) != null) {
				// int pid = Integer.parseInt(line.split(":", 2)[0]);
				int loadTime = Integer.parseInt(line.split(":", 2)[1]);
				duler.loadTimes.add(loadTime);
			}
		} catch(Exception e) {e.printStackTrace();}
		// duler.running.addAll(duler.pros.get(0));
		// duler.ready.addAll(duler.pros);
		// duler.ready.remove(0);
	}
	
	static String[] getInsts(BufferedReader reader) {
		ArrayList<String> ret = new ArrayList<String>();
		String inst = null;
		try{
			while(true) {
				inst = reader.readLine();
				if(inst == null || inst.equals("done")) break;
				ret.add(inst);
			}
		} catch(Exception e) {e.printStackTrace();}
		
		String[] ret2 = new String[ret.size()];
		ret.toArray(ret2);
		return ret2;
	}
}
