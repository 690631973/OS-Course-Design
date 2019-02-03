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

public class Main extends Application {

    Stage window;
	Scheduler duler = new Scheduler();
	Scene scene1;
	Scene scene2;
	@Override
    public void start(Stage primaryStage)  {
        initGui(primaryStage);
		Thread t = new Thread(duler);
		t.setDaemon(true);
		t.start();
	}

	void initScene1( ) {
		
		BorderPane bd = new BorderPane();
		bd.setPadding(new Insets(30,30,30,30));
		
		
		HBox top = new HBox();
		top.setPadding(new Insets(20,20,20,20));
		Label msg = new Label();
		msg.textProperty().bind(duler.messageProperty());
		msg.setPrefWidth(100);
		top.getChildren().addAll(msg);
		bd.setTop(top);
       

		VBox listBox = new VBox();
		listBox.setSpacing(5);
		listBox.setPadding(new Insets(10,10,10,10));

		ListView<Process> runningList = listFactory(duler.running);
		ListView<Process> readyList = listFactory(duler.ready);
		ListView<Process> blockedList = listFactory(duler.blocked);
		Label lbRunning = new Label("Running:");
		Label lbReady = new Label("Ready:");
		Label lbBlocked = new Label("Blocked:");
		listBox.getChildren().addAll(lbRunning, runningList,lbReady, readyList,lbBlocked, blockedList);
		bd.setCenter(listBox);
		
		
		ListView<String> instsList = new ListView<>();
		ObservableList insts = FXCollections.observableArrayList("");
		instsList.setItems(insts);
        instsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		instsList.setPadding(new Insets(30,30,30,30));
		bd.setRight(instsList);

		VBox vb = new VBox();
		vb.setPadding(new Insets(30,30,30,30));
		bd.setLeft(vb);
		
        Scene scene1 = new Scene(bd, 800, 600);
        window.setScene(scene1);

	}
	void initScene2( ) {
		
	}
	void initGui(Stage primaryStage) {
		window = primaryStage;
        window.setTitle("OS");
		initScene1();
		initScene2();
		window.show();
	}
	
	ListView<Process> listFactory(ObservableList<Process> items) {
		ListView<Process> list = new ListView<>();
		list.itemsProperty().bindBidirectional(new SimpleListProperty<Process>(items));
        list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		return list;
	}

    
   
	public static void main(String[] args) {
        launch(args);
    }
	
}




class Scheduler extends Task<Void> {

	ObservableList<Process> running;
	ObservableList<Process> ready;
	ObservableList<Process> blocked;
	ObservableList<Process> pros;
	AtomicBoolean flag = new AtomicBoolean(false);
	
	
	Scheduler() {
		this.pros = FXCollections.observableArrayList();
		this.running = FXCollections.observableArrayList();
		this.ready = FXCollections.observableArrayList();
		this.blocked = FXCollections.observableArrayList();
	}

	@Override
	protected Void call() {
		for(int i=0; i<4 ; i++) {
			Process p = new Process();
			pros.addAll(p);
		}
		running.addAll(pros.get(0));
		ready.addAll(pros);
		ready.remove(0);
		sched();
		
		return null;
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
							ready.add(curRunning);
						}
						running.add(nextRunning);
					}
				});
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
	final int INTERVAL = 3;
	final int INTERVAL_EACH_INST = 200;
	String inst;
	int pc = 0;
	int pid;
	int pri;
	double vruntime;
	int runtime;
	Process() {
		pid = nextPid++;
		pri = 5;
		runtime = 2;
		vruntime = runtime*pri;
	}

	void setPri(int pri) {
		this.pri = pri;
	}
	
	public boolean run() {
		Platform.runLater(new Runnable() {
				@Override
				public void run () {
					if(vruntime <=0) {
						System.out.println("Process "+pid+" done!");
						return;
					}
					System.out.println("Process "+pid+" run ");
					for(int i=0; i<INTERVAL ; i++) {
						try{ Thread.sleep(INTERVAL_EACH_INST);
						} catch(InterruptedException e) {}
						
						runtime--;
						pc++;
					}
					vruntime = runtime*pri;
				}
			});
		return vruntime<=0?true:false;
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
