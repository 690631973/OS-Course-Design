import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.*;
import javafx.concurrent.*;
import javafx.beans.property.*;
import java.util.concurrent.atomic.*;

class Process  implements Comparable<Process>  {
	static int nextPid = 0;
	final int INTERVAL = 1;
	final int INTERVAL_EACH_INST = 200;
	String []insts;
	int pc = 0;
	int pid;
	int pri;
	double vruntime;
	int runtime;
	Scheduler duler;
	HashMap<Integer,Integer> allocated;
	HashMap<Integer,Integer> pending;
	boolean done = false;
	boolean blocked = false;
	Process(Scheduler duler, int pid, String[] insts) {
		this.duler = duler;
		// pid = nextPid++;
		pri = 5;
		runtime = 10;
		vruntime = runtime*pri;
		this.insts = insts;
		this.pid = pid; 
		
	}

	public void run() {
		Platform.runLater(new Runnable() {
				@Override
				public void run () {
					if(pc >= insts.length || vruntime<=0) {
						System.out.println("Process "+pid+" done!");
						done = true;
						return ;
					}
					System.out.println("Process "+pid+" run ");
					for(int i=0; i<INTERVAL ; i++) {
						try{
							Thread.sleep(INTERVAL_EACH_INST);
						} catch(InterruptedException e) {}
						exec();
						runtime--;
						pc++;
					}
					vruntime = runtime*pri;
				}
			});
	}
	void exec() {
		assert(pc < insts.length);
		String []inst = insts[pc].split(" ", 4);
		if(inst[0].equals("request")) {
			request(inst[1], Integer.valueOf(inst[2]));
		}
	
	}

	void request(String tp, int nReq) {
		Request req = new Request(pid, tp, nReq);
		// the following part should not be put here, but I am too lazy to change. Hope no bad thing happen. 
		duler.requestPending.addAll(req);
		blocked = true;
		// duler.blocked.addAll(duler.running.remove(0));
		// if(!duler.ready.isEmpty()) {
		// 	duler.running.addAll( duler.ready.remove(0));
		// }
	}
	@Override
	public String toString() {
		return "Process "+ String.valueOf(pid);
	}

	@Override
	public int compareTo(Process o) {
		return - (int)(this.vruntime - o.vruntime);
		// return this.pid - o.pid;
	}
}
