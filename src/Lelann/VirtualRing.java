package Lelann;

//Visidia imports
import visidia.simulation.process.algorithm.Algorithm;
import visidia.simulation.process.messages.Door;


public class VirtualRing extends Thread {

	LelannMutualExclusion algo;
	
	public  VirtualRing(final LelannMutualExclusion a){
		
		algo = a;
	}
	
	public void run(){
		
		while(true){
			Door d = new Door();
			receiveRouteMessage(d);
		}
		
		
		
	}
	
	public synchronized void receiveRouteMessage(Door d){
		
		
	}
	
	
}
