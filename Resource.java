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

public class Resource {
	int tp;
	String type;
	int n;
	ArrayList<Integer> pending = new ArrayList<Integer>();
	ArrayList<Integer> allocated = new ArrayList<Integer>();
	Label tf = new Label();
	Resource(String type, int n) {
		this.type = type;
		this.tp = type.charAt(0) - 'A';
		this.n = n;
		tf.setText("Resource "+type+ " available: "+n);
	}
	boolean request(Request req) {
		if(req.nReq <= n) {
			n -= req.nReq;
			allocated.add(req.pid);
			tf.setText("Resource "+type+ " available: "+n);
			if(pending.contains(req.pid)){
				pending.remove(new Integer(req.pid));
			}
			return true;
		}
		else {
			pending.add(req.pid);
			return false;
		}
	}
}

