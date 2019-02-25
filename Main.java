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


class DebugThread extends Thread {
	@Override
	public void run() {
		
	}	
}
						
public class Main extends Application {
    Stage window;
	Scheduler duler = new Scheduler();
	Scene scene;
	
 	@Override
    public void start(Stage primaryStage)  {
        initGui(primaryStage);
		Thread t = new Thread(duler);
		t.setDaemon(true);
		t.start();
	}

		void initScene() {
		BorderPane bd = new BorderPane();
		bd.setPadding(new Insets(30,30,30,30));
		scene = new Scene(bd, 1200, 1000);

		
		HBox top = new HBox();
		top.setSpacing(20);
		top.setPadding(new Insets(20,20,20,20));
		Label tfDeadLock = new Label();
		tfDeadLock.textProperty().bind(duler.tfDeadLock.textProperty());

		
		Button btnReleaseDeadLock = new Button("ReleaseDeadLock");
		btnReleaseDeadLock.setOnAction(e->duler.releaseDeadLock());

		Button btnAvoidDeadLock = new Button("Avoid DeadLock");
		btnAvoidDeadLock.setOnAction(e->duler.avoidDeadLock());

		Button btnDebugNext = new Button("Next(Debug)");
		btnAvoidDeadLock.setOnAction(e->duler.next());
		
		top.getChildren().addAll(tfDeadLock, btnReleaseDeadLock, btnAvoidDeadLock, btnDebugNext);
		bd.setTop(top);
		
		
	
		VBox left = new VBox();
		left.setSpacing(10);
		left.setPadding(new Insets(20,20,20,20));
		Label lbPages = new Label("Page Table");
		ListView<PageCell> lsPages= new ListView();
		lsPages.setItems(duler.pages);

		Label lbMemo = new Label("Memory");
		ListView<MemoCell>lsMemo= new ListView();
		lsMemo.setItems(duler.memo);

		left.getChildren().addAll(lbPages, lsPages, lbMemo, lsMemo);
		bd.setLeft(left);

		
		VBox center = new VBox();
		center.setSpacing(20);
		center.setPadding(new Insets(20,20,20,20));
		Label msg = new Label();
		msg.textProperty().bind(duler.messageProperty());
		center.getChildren().addAll(msg);
		ListView<Process> runningList = listFactory(duler.running);
		ListView<Process> readyList = listFactory(duler.ready);
		ListView<Process> blockedList = listFactory(duler.blocked);
		Label lbRunning = new Label("Running:");
		Label lbReady = new Label("Ready:");
		Label lbBlocked = new Label("Blocked:");
		
		center.getChildren().addAll(lbRunning, runningList,lbReady, readyList,lbBlocked, blockedList);
		bd.setCenter(center);

		VBox right = new VBox();
		right.setSpacing(20);
		

		for(Resource res  : duler.res) {
			Label tf = new Label();
			Label tfr = res.tf;
			tf.textProperty().bind(tfr.textProperty());
			right.getChildren().addAll(tf);
		}
		
		ListView<Request> lsRequestPending = new ListView<>();
		lsRequestPending.setItems(duler.requestPending);
		ListView<Request> lsRequestAllocated = new ListView<>();
		lsRequestAllocated.setItems(duler.requestAllocated);
		right.getChildren().addAll(new Label("Pending Request"), lsRequestPending, new Label("Allocated Request"), lsRequestAllocated);
		bd.setRight(right);
		
		
	}
	void initGui(Stage primaryStage) {
		window = primaryStage;
        window.setTitle("Visual Simulated Linux2.6 Process And Memory Management");
		initScene();
		window.setScene(scene);
		
		scene.getStylesheets().add
			(Main.class.getResource("style.css").toExternalForm());
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

