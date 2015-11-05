package RicartAggrawala;

import java.util.HashMap;

import logger.ProcLogger;
import Message.MsgType;
import visidia.simulation.process.algorithm.Algorithm;

public class RicartAggrawala extends Algorithm{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2488961944192749471L;
	
	private int H = 0; /* estampille locale */
	private int HSC = 0; /* estampille de demande de section critique */
	private boolean R = false; /* Booléen indiquand si un processus est demandeur de section critique */
	
	// ensemble des processus dont l'envoi de REL est differé. <procId,procDoor>
	private HashMap<Integer,Integer> X = new HashMap<Integer,Integer>();
	private int Nrel = 0; // Nombre des REL attendu 
	private int V = 0; // Nombre des voisins du processus
	
	private int procId = 0; // My processus Id
	
	 
	private ProcLogger log = null; /* logger */
	
	/*private boolean askCriticalSection = false;
	private boolean inCriticalSection = false;
	private boolean endCriticalSection = false;*/
	
	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		Nrel = getArity();
		procId = getId();
		log = new ProcLogger(procId,"Ricart");
	}
	
	/* Rule 1 : processus ask for critical section */
	private synchronized  void askCriticalSection(){
		
		R = true;
		HSC = H++;
		Nrel = getArity();
		RicartAggrawalaMessage ms = new RicartAggrawalaMessage(MsgType.REQ, procId,0);
		sendReq(ms,Nrel,-1);
		
		while(Nrel != 0){	
			try {
				wait();
			} catch (InterruptedException e) {e.printStackTrace();}
		}
	}
	
	/* Rules 2 : */
	
	/* Allow to send a broadcast message to a specif number of processus */
	private synchronized void sendReq(RicartAggrawalaMessage ms, int nbrProc, int exceptDoor){
		
		for(int i=0; i<nbrProc; i++){
			
			if(i != exceptDoor){
				
				boolean send = sendTo(i, ms);	
			}
		}
	}

}
