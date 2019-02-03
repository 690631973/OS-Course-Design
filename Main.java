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

public class Main extends Application {

    Stage window;
	ObservableList<Process> prosRunning = FXCollections.observableArrayList();
	Scheduler duler = new Scheduler(prosRunning);
	HashMap<String, List<Process>> pros;
	
	@Override
    public void start(Stage primaryStage)  {
        initGui(primaryStage);
		(new Thread(duler)).start();
	}

   
	void initGui(Stage primaryStage) {
		window = primaryStage;
        window.setTitle("OS");
		
		
        Button button = new Button("rank");
		button.setOnAction(e -> duler.buttonClicked());

		HBox hb = new HBox();
		ListView<Process> prosRunningList = new ListView<>();
		prosRunningList.setItems(prosRunning);
        prosRunningList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		prosRunningList.setPadding(new Insets(30,30,30,30));


		
		ListView<String> instsList = new ListView<>();
		ObservableList insts = FXCollections.observableArrayList("");
		instsList.setItems(insts);
        instsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		instsList.setPadding(new Insets(30,30,30,30));

		VBox vb = new VBox();
		vb.setPadding(new Insets(30,30,30,30));
		Label lb1 = new Label("hi");
		Label lb2 = new Label("bye");
		lb1.setPrefWidth(100);
		lb2.setPrefWidth(100);
		vb.getChildren().addAll(lb1, lb2);
		
		BorderPane bd = new BorderPane();
		bd.setPadding(new Insets(30,30,30,30));
		bd.setCenter(listView);
		bd.setRight(instsList);
		bd.setTop(button);
		
		bd.setLeft(vb);
		
        Scene scene = new Scene(bd, 800, 600);
        window.setScene(scene);
        window.show();
    }

    
   
	public static void main(String[] args) {
        launch(args);
    }
}




class Scheduler extends Task<Void> {

	ObservableList<Process> pros;
	
	Scheduler(ObservableList<Process> pros) {
		this.pros = pros;
	}

	@Override
	protected Void call() {
	
		for(int i=1; i<10 ; i++) {
			try{
				Thread.sleep(1000);
			} catch(Exception e) {}
			Process p = new Process(i);
			p.setPri(i);
			pros.addAll(p);
			
		}
		return null;
	}
	void buttonClicked(){
		Platform.runLater( new Runnable() {
				@Override
				public void run() {
					for(int i=0; i<3 ; i++) {
						pros.get(i).setPri(3-i);
					}
					FXCollections.sort(pros);	
				}
			} );
    }
}


class Process implements Comparable<Process> {
	String inst;
	int pc = 0;
	int pid;
	int pri;
	Process(int pid) {
		this.pid = pid;
	}

	void setPri(int pri) {
		this.pri = pri;
	}
	
	
	public void run() {
		String [] strs = {"1", "2", "3", "4"};
		try{
			// while(true) {
			// 	inst = strs[pc];
			// 	pc = (pc+1) % 4;
			// }
			while(true);
		} catch(Exception e) {return;}
	}

	@Override
	public String toString() {
		return "Process "+ String.valueOf(pid);
	}

	@Override
	public int compareTo(Process o) {
		return this.pri - o.pri;
	}
}
