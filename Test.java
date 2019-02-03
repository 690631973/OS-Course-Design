import java.util.*;
import java.io.*;
import java.util.concurrent.atomic.*;
class Pro extends Thread {

	@Override
	public void run() {
		for(int i=0; i<3 ; i++) {
			try{ Thread.sleep(500);
			} catch(InterruptedException e) {}

			System.out.println("run");
		}
		System.out.println("switch");
	}
}

class Container implements Runnable{
	@Override
	public void run() {
		Pro p = new Pro();
		p.start();

		// while(true) {
			
	
		// }
	}	
}

public class Test{
	public static void main(String args[]){
		(new Thread(new Container())).start();
	}
}
