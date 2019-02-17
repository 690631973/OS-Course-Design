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

public class Request {
	int pid;
	String type;
	int tp;
	int nReq;
	Request(int pid, String type, int nReq) {
		this.pid = pid;
		this.type = type;
		this.nReq = nReq;
		this.tp = type.charAt(0) - 'A';
	}

	@Override
	public String toString() {
		return "Process "+pid +" request resource"+type+" by "+nReq;
	}
}
