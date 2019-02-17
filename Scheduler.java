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
import java.io.*;

class ProcessIniter {
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
					int pid = Integer.valueOf(inst.split(" ", 2)[1]);
					System.out.println(pid);
					String [] pinsts = getInsts(reader);
					Process p = new Process(duler, pid, pinsts);
					duler.pros.addAll(p);
				}
			}
		} catch(Exception e) {e.printStackTrace();}
		duler.running.addAll(duler.pros.get(0));
		duler.ready.addAll(duler.pros);
		duler.ready.remove(0);
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

class Scheduler extends Task<Void> {

	ObservableList<Process> running;
	ObservableList<Process> ready;
	ObservableList<Process> blocked;
	ObservableList<Process> pros;
	ObservableList<Request> request;

	TextField tfDeadLock = new TextField("No DeadLock Found");

	Resource [] res = {new Resource("A", 10), new Resource("B", 10), new Resource("C", 1)};

	ObservableList<Request> requestPending;
	ObservableList<Request> requestAllocated;
	
	
	Scheduler() {
		this.pros = FXCollections.observableArrayList();
		this.running = FXCollections.observableArrayList();
		this.ready = FXCollections.observableArrayList();
		this.blocked = FXCollections.observableArrayList();
		this.request = FXCollections.observableArrayList();
	
		this.requestPending = FXCollections.observableArrayList();
		this.requestAllocated = FXCollections.observableArrayList();
	}


	@Override
	protected Void call() {
		(new ProcessIniter(this)).initPros();
		sched();
		
		return null;
	}

	void request() {
		if(requestPending.isEmpty()) return;
		Request r = requestPending.remove(0);
		if(res[r.tp].request(r) == true) {
			requestAllocated.addAll(r);
		}
		else {
			requestPending.addAll(r);
			checkDeadLock();
		}
	}

	void sched() {
		while(true) {
			try{ Thread.sleep(1000);
			} catch(InterruptedException e) {}
			Platform.runLater(new Runnable() {
					@Override
					public void run() {
						FXCollections.sort(ready);
						Process nextRunning = null;
						Process curRunning = null;
						
						if(!ready.isEmpty()) {
							nextRunning = ready.remove(0);
						}
						if(!running.isEmpty()) {
							curRunning = running.remove(0);
						}
						if(ready.isEmpty() && running.isEmpty()) {
							updateMessage("none process");
							return;
						}
					
						updateMessage(curRunning.toString()+ " runnning");
						boolean done = curRunning.run();
						if(done == true) {
							updateMessage(curRunning.toString()+ " done");
						} else {
							ready.addAll(curRunning);
						}
						running.addAll(nextRunning);
						request();
						
					}
				});
		}
		
	}
	void checkDeadLock() {
		System.out.println("checking....");
		for(Request r1 : requestPending) {
			Resource res1 = res[r1.tp];
			for(Resource res2 : res) {
				ArrayList<Integer> pida = (ArrayList<Integer>)res1.pending.clone();
				ArrayList<Integer> pidb = (ArrayList<Integer>)res2.pending.clone();
				pida.retainAll(res2.allocated); 
				pidb.retainAll(res1.allocated);
				if(pida.isEmpty() || pidb.isEmpty()) continue;
				int a, b;
				a = pida.get(0);
				b = pidb.get(0);
				System.out.println("checkDeadLock success "+a+" "+b);
				tfDeadLock.setText("DeadLock Found:"+a+" "+b+" for "+"res"+res1.type+" and res"+res2.type);
			}
		}
	}
		
	void sort(){
		FXCollections.reverse(pros);	
		FXCollections.reverse(running);	
		FXCollections.reverse(ready);	
		FXCollections.reverse(blocked);	
    }
}



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
	Process(Scheduler duler, int pid, String[] insts) {
		this.duler = duler;
		// pid = nextPid++;
		pri = 5;
		runtime = 10;
		vruntime = runtime*pri;
		this.insts = insts;
		this.pid = pid; 
		
	}

	public boolean run() {
		Platform.runLater(new Runnable() {
				@Override
				public void run () {
					
					if(pc >= insts.length || vruntime<=0) {
						System.out.println("Process "+pid+" done!");
						return ;
					}
					System.out.println("Process "+pid+" run ");
					for(int i=0; i<INTERVAL ; i++) {
						try{ Thread.sleep(INTERVAL_EACH_INST);
						} catch(InterruptedException e) {}
						exec();
						runtime--;
						pc++;
					}
					vruntime = runtime*pri;
				}
			});
		return (pc>=insts.length || vruntime<=0)?true:false;
	}
	void exec() {
		// if(pc == 0 && pid == 0) {
		// 	request("A", 10);
		// }
		// if (pc == 1 && pid == 0) {
		// 	request("B", 10);
		// }

		// if(pc == 1 && pid == 1) {
		// 	request("A", 10);
		// }
		// if (pc == 0 && pid == 1) {
		// 	request("B", 10);
		// }
		assert(pc < insts.length);
		if(pc < insts.length) {
			String []inst = insts[pc].split(" ", 4);
			if(inst[0].equals("request")) {
				request(inst[1], Integer.valueOf(inst[2]));
			}
		} else {
			pc--;
			System.out.println("Process "+pid+" done");
		}
	}

	void request(String tp, int nReq) {
		Request req = new Request(pid, tp, nReq);
		duler.requestPending.addAll(req);
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


