package RicartAggrawala;

import java.awt.Point;
import java.util.HashMap;

import logger.ProcLogger;
import Gui.Lanceur;
import Gui.MoteurTest;
import Lelann.MyRouter;
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
	
	public MyRouter myRouter; /*  Router */
	
	// Tableau blanc
	private Lanceur lanceur;
	private Point p1 = null;
	private Point p2 = null;
	private float tailleForm;
	private int typeForm;
	// form Generator
	MoteurTest motTest;
	
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
		myRouter = new MyRouter(getNetSize());
		
		
		log.logMsg("Proc-"+procId+" I am ready to begin "+ myRouter.ready);
		lanceur = new Lanceur("Tableau Blanc Proc" + getId());
		log.logMsg("Proc-"+procId+" I launch the white board named Tableau Blanc Proc"+procId);
		motTest = new MoteurTest();
		lanceur.start();
	}
	
	/* Rule 1 : processus ask for critical section */
	private synchronized  void askCriticalSection(){
		
		R = true;
		HSC = H++;
		Nrel = getArity();
		RicartAggrawalaMessage ms = new RicartAggrawalaMessage(MsgType.REQ, procId,0,HSC);
		sendReq(ms,Nrel,-1);
		
		motTest.creerForme();
		p1 = motTest.getPoint1();
		p2 = motTest.getPoint2();
		typeForm = motTest.getChoixForme();
		log.logMsg("Proc-"+procId+" : Create form, wait critical section  befor drawing");
		
		while(Nrel != 0){	
			try {
				wait();
			} catch (InterruptedException e) {e.printStackTrace();}
		}
		
	}
	
	/* Rules 2 : */
	private synchronized void receiveReq(RicartAggrawalaMessage ms){
		
		int door = myRouter.getDoorOnMyRoute(ms.procId);
		H = Math.max(H, ms.H);
		if(R && (HSC < ms.H) || ((HSC == ms.H) && this.procId < ms.procId)){
			
			X.put(ms.procId,door);
		}
		else{
			
			RicartAggrawalaMessage mrel = new RicartAggrawalaMessage(MsgType.REL, procId,ms.procId,0);
			this.sendTo(door, mrel);
		}
	}
	
	
	/* Allow to send a broadcast message to a specif number of processus */
	private synchronized void sendReq(RicartAggrawalaMessage ms, int nbrProc, int exceptDoor){
		
		for(int i=0; i<nbrProc; i++){
			
			if(i != exceptDoor){
				
				boolean send = sendTo(i, ms);	
			}
		}
	}

}
