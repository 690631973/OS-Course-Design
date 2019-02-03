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


public class Main extends Application {

    Stage window;
	// ObservableList<Process> pros = FXCollections.observableArrayList();
	// ObservableList<Process> running = FXCollections.observableArrayList();
	// ObservableList<Process> ready = FXCollections.observableArrayList();
	// ObservableList<Process> blocked = FXCollections.observableArrayList();
	Scheduler duler = new Scheduler();

	
	@Override
    public void start(Stage primaryStage)  {
        initGui(primaryStage);
		Thread t = new Thread(duler);
		t.setDaemon(true);
		t.start();
	}

   
	void initGui(Stage primaryStage) {
		window = primaryStage;
        window.setTitle("OS");
		
		
        Button button = new Button("Sort");
		button.setOnAction(e -> duler.sort());

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

		
		
		ListView<String> instsList = new ListView<>();
		ObservableList insts = FXCollections.observableArrayList("");
		instsList.setItems(insts);
        instsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		instsList.setPadding(new Insets(30,30,30,30));

		VBox vb = new VBox();
		vb.setPadding(new Insets(30,30,30,30));
		Label lb1 = new Label("hi");
		Label lb2 = new Label();
		lb2.textProperty().bind(duler.messageProperty());
		lb1.setPrefWidth(100);
		lb2.setPrefWidth(100);
		vb.getChildren().addAll(lb1, lb2);
		
		BorderPane bd = new BorderPane();
		bd.setPadding(new Insets(30,30,30,30));
		bd.setCenter(listBox);
		bd.setRight(instsList);
		bd.setTop(button);
		
		bd.setLeft(vb);
		
        Scene scene = new Scene(bd, 800, 600);
        window.setScene(scene);
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
	
	
	Scheduler() {
		this.pros = FXCollections.observableArrayList();
		this.running = FXCollections.observableArrayList();
		this.ready = FXCollections.observableArrayList();
		this.blocked = FXCollections.observableArrayList();
	}

	@Override
	protected Void call() {
		for(int i=1; i<10 ; i++) {
			Process p = new Process(i);
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
						Process nextRunning = ready.remove(0);
						Process quitRunning = running.remove(0);
						
						running.add(nextRunning);
			
						ready.add(quitRunning);
						
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


class Process implements Comparable<Process> {
	String inst;
	int pc = 0;
	int pid;
	int pri;
	double vruntime;
	int runtime;
	Process(int pid) {
		this.pid = pid;
	}

	void setPri(int pri) {
		this.pri = pri;
	}
	synchronized void run() {
		try{ Thread.sleep(300);
		} catch(InterruptedException e) {}
		System.out.println("Process "+pid+"run ");
		runtime--;
		vruntime = runtime*pri;
		
	}
	
	


	@Override
	public String toString() {
		return "Process "+ String.valueOf(pid);
	}

	@Override
	public int compareTo(Process o) {
		return - (this.vruntime - o.vruntime);
	}
}
