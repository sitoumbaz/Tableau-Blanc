package Lelann;

//Visidia imports
import visidia.simulation.process.algorithm.Algorithm;
import visidia.simulation.process.messages.Door;


public class VitualRing extends Thread {

	
	public int ready;
	public int hereIs;
	public int whereIs;
	private int netSize;
	private int nbrArity;
	public MyRouter myRouter;
	
	
	public void VitualRing(int netSize, int nbrArity){
		
		this.netSize = netSize;
		this.nbrArity = nbrArity;
		
	}
	
	public void run(){
		
	}
	
	// Rule 0 : tell to all my neighbor where I am
    synchronized void setRouteMap() {

    	
    	
    }
	
	
}
