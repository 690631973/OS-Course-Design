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

class DeadLock {
	int pida, pidb;
	int res1tp ,res2tp;
	DeadLock(int pida , int pidb, int res1tp, int res2tp) {
		this.pida = pida;
		this.pidb = pidb;
		this.res1tp = res1tp;
		this.res2tp = res2tp;
	}
}


class Scheduler extends Task<Void> {

	ObservableList<Process> running;
	ObservableList<Process> ready;
	ObservableList<Process> blocked;
	ObservableList<Process> pros;
	ObservableList<Request> request;
	DeadLock lk = null;
	Label tfDeadLock = new Label("No DeadLock Found");

	Resource [] res = {new Resource("A", 10), new Resource("B", 10), new Resource("C", 100)};

	ObservableList<Request> requestPending;
	ObservableList<Request> requestAllocated;
	boolean denyCheckDeadLock = false;
	ObservableList<String> instList;
	ObservableList<PageCell> pages;
	ObservableList<MemoCell> memo;
	ArrayList<Integer> loadTimes;
	int time = 0;
	
	Scheduler() {
		this.pros = FXCollections.observableArrayList();
		this.running = FXCollections.observableArrayList();
		this.ready = FXCollections.observableArrayList();
		this.blocked = FXCollections.observableArrayList();
		this.request = FXCollections.observableArrayList();
	
		this.requestPending = FXCollections.observableArrayList();
		this.requestAllocated = FXCollections.observableArrayList();
		this.instList = FXCollections.observableArrayList();
		this.pages = FXCollections.observableArrayList();
		this.memo = FXCollections.observableArrayList();
		
		for(int i=0; i<100 ; i++) {
			memo.addAll(new MemoCell(i));
		}
		loadTimes = new ArrayList<Integer>();
	}


	@Override
	protected Void call() {
		(new ProcessIniter(this)).initPros();
		sched();
		
		return null;
	}

	void request() {
		if(requestPending.isEmpty()) return ;
		Request r = requestPending.remove(0);
		if(res[r.tp].request(r) == true) {
			requestAllocated.addAll(r);
			requestPending.remove(r);
			for(int i=0; i<blocked.size();i++){
				if(r.pid == blocked.get(i).pid) {
					ready.addAll(blocked.remove(i));
					return;
				}
			}
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
						if(!loadTimes.isEmpty() && time == loadTimes.get(0)) {
							// add all pros at init time, but load it one by one
							int pid = pros.get(0).pid;
							ready.addAll(pros.remove(0));
							loadTimes.remove(0);
							updateMessage("new process: process "+pid+" loaded");
						}
						time++;
						request();
						if(running.isEmpty()) {
							if(!ready.isEmpty()) {
								running.addAll(ready.remove(0));
							}
							else if(!blocked.isEmpty()){
								updateMessage("blocked!");
								return;
							}
							else {
								updateMessage("none process running");
								return;
							}
						}
						Process cur = running.get(0);
						pages.setAll();
						for(PageCell cell : cur.page) {
							pages.addAll(cell);
						}
						
						String [] part_insts = Arrays.copyOfRange(cur.insts, cur.pc, cur.insts.length);
						List<String> ls = Arrays.asList(part_insts);
						instList.setAll(ls);
						
						FXCollections.sort(ready);
						
						cur.run();
						updateMessage(cur.toString()+" running");
						if(cur.done) {
							running.remove(0);
							if(!ready.isEmpty()) {
								running.addAll(ready.remove(0));
							}
						}
						else if(cur.blocked) {
							blocked.addAll(running.remove(0));
							if(!ready.isEmpty()) {
								running.addAll(ready.remove(0));
							}
						}
						else {
							if(!ready.isEmpty()) {
								ready.addAll(running.remove(0));
								running.addAll(ready.remove(0));
							} 
						}
						
						// if(running.isEmpty()) {
						// 	if(!ready.isEmpty()) {
						// 		running.addAll(ready.remove(0));
						// 	}
						// }
					}
				});
		}
		
	}
	void avoidDeadLock() {
		FXCollections.reverse(requestPending);
		denyCheckDeadLock = !denyCheckDeadLock;;
	}
	void checkDeadLock() {
		if(lk != null) return;
		if(denyCheckDeadLock == true) return;
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
				tfDeadLock.setText("DeadLock Found:"+"Process "+a+" "+"Process "+b+" for "+"res"+res1.type+" and res"+res2.type);
				lk = new DeadLock(a, b, res1.tp, res2.tp);
			}
		}
	}

	void releaseDeadLock() {
		Platform.runLater(new Runnable(){
				@Override
				public void run() {
					if(lk==null) return;
					int pida = lk.pida;
					int pidb = lk.pidb;
					requestPending.removeIf(r -> r.pid == pida);
					pros.removeIf(p -> p.pid == pida);
					blocked.removeIf(p -> p.pid == pida);
				
					tfDeadLock.setText("DeadLock released by killing Process "+pida);
					lk = null;
				}
			});
	}
		
	void sort(){
		FXCollections.reverse(pros);	
		FXCollections.reverse(running);	
		FXCollections.reverse(ready);	
		FXCollections.reverse(blocked);	
    }
}





