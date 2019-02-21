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
		scene1 = new Scene(bd, 800, 600);
      
		
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
		
		VBox right = new VBox();
		right.setSpacing(20);
		Label lbCurInsts = new Label("Current Instruction List");
		ListView<String> instList = new ListView<>();
		instList.setItems(duler.instList);
        instList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		instList.setPadding(new Insets(30,30,30,30));
		right.getChildren().addAll(lbCurInsts, instList);
		bd.setRight(right);

		VBox vb = new VBox();
		vb.setPadding(new Insets(30,30,30,30));
		Button btnScene2 = new Button("DeadLock Scene");
		btnScene2.setOnAction(e -> window.setScene(scene2));
		vb.getChildren().addAll(btnScene2);
		bd.setLeft(vb);
	
	}
	void initScene2() {
		BorderPane bd = new BorderPane();
		bd.setPadding(new Insets(30,30,30,30));
		scene2 = new Scene(bd, 800, 600);

		
		HBox top = new HBox();
		top.setSpacing(20);
		top.setPadding(new Insets(20,20,20,20));
		TextField tfDeadLock = new TextField();
		tfDeadLock.setPrefColumnCount(30);
		tfDeadLock.setEditable(false);
		tfDeadLock.textProperty().bind(duler.tfDeadLock.textProperty());

		
		Button btnReleaseDeadLock = new Button("ReleaseDeadLock");
		btnReleaseDeadLock.setOnAction(e->duler.releaseDeadLock());

		Button btnAvoidDeadLock = new Button("Avoid DeadLock");
		btnAvoidDeadLock.setOnAction(e->duler.avoidDeadLock());
		
		top.getChildren().addAll(tfDeadLock, btnReleaseDeadLock, btnAvoidDeadLock);
		bd.setTop(top);
		
		
	
		VBox left = new VBox();
		left.setSpacing(10);
		left.setPadding(new Insets(20,20,20,20));
		for(Resource res  : duler.res) {
			TextField tf = new TextField();
			TextField tfr = res.tf;
			tf.setEditable(false);
			tf.textProperty().bind(tfr.textProperty());
			left.getChildren().addAll(tf);
		}
		Label msg = new Label();
		msg.textProperty().bind(duler.messageProperty());
		left.getChildren().addAll(msg);
		bd.setLeft(left);

		
		VBox center = new VBox();
		center.setSpacing(20);
		center.setPadding(new Insets(20,20,20,20));
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
		ListView<Request> lsRequestPending = new ListView<>();
		lsRequestPending.setItems(duler.requestPending);
		ListView<Request> lsRequestAllocated = new ListView<>();
		lsRequestAllocated.setItems(duler.requestAllocated);
		right.getChildren().addAll(new Label("Pending Request"), lsRequestPending, new Label("Allocated Request"), lsRequestAllocated);
		bd.setRight(right);
		
		
	}
	void initGui(Stage primaryStage) {
		window = primaryStage;
        window.setTitle("OS");
		initScene1();
		initScene2();
		window.setScene(scene2);
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

