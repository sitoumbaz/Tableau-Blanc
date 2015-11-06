package RicartAggrawala;

import java.awt.Color;
import java.awt.Point;
import java.util.HashMap;

import logger.ProcLogger;
import visidia.simulation.process.algorithm.Algorithm;
import visidia.simulation.process.messages.Door;
import visidia.simulation.process.messages.Message;
import Gui.Lanceur;
import Gui.MoteurTest;
import Listener.MessageListener;
import Message.FormMessage;
import Message.MsgType;
import Router.ExtendRouteMessage;
import Router.MyRouter;
import Router.RouteMessage;

public class RicartAggrawalaMutualExclusion extends Algorithm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2488961944192749471L;

	// Router
	public MyRouter myRouter;
	public int next = 0;

	// just for managin displaying routing table in the log
	boolean iAmReady = false;

	private int H = 0; /* estampille locale */
	private int HSC = 0; /* estampille de demande de section critique */
	private boolean R = false; /*
								 * Bool��en indiquand si un processus est
								 * demandeur de section critique
								 */

	// ensemble des processus dont l'envoi de REL est differ��.
	// <procId,procDoor>
	private HashMap<Integer, Integer> X = new HashMap<Integer, Integer>();
	private int Nrel = 0; // Nombre des REL attendu
	private int V = 0; // Nombre des voisins du processus

	public int procId = 0; // My processus Id

	private ProcLogger log = null; /* logger */
	
	// Tableau blanc
	private Lanceur lanceur;
	private Point p1 = null;
	private Point p2 = null;
	private float tailleForm;
	private int typeForm;
	
	// form Generator
	MoteurTest motTest;
	
	MessageListener messageListener = null;

	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return new RicartAggrawalaMutualExclusion();
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		Nrel = getArity();
		procId = getId();

		// Init routeMap
		myRouter = new MyRouter(getNetSize());
		myRouter.setDoorToMyRoute(getId(), -2);
		log = new ProcLogger(procId, "Ricart");
		
		/* Begin setting up route */
		
		setRoutingTable();
		// attente que chaque que les messages aient le temps de se propager
		try {
			Thread.sleep(2500);
		} catch (InterruptedException ie) {
		}

		extendRoutingTable();
		sayIamReady();
		
		/* End setting up route */
		
		
		log.logMsg("Proc-"+procId+" I am ready to begin "+ myRouter.ready);
		lanceur = new Lanceur("Tableau Blanc Proc" + getId());
		log.logMsg("Proc-"+procId+" I launch the white board named Tableau Blanc Proc"+procId);
		motTest = new MoteurTest();
		lanceur.start();
		
		
		messageListener = new MessageListener(this);
		messageListener.start();
		log.logMsg("Proc-" + procId + " I start my message Listerner");
	}
	// --------------------
	// Rules
	// -------------------

	// Rule 0.1 : tell to all my neighbor where I am
	synchronized void setRoutingTable() {

		for (int i = 0; i < getArity(); i++) {

			RouteMessage mr = new RouteMessage(MsgType.ROUTE, getId());
			boolean send = sendTo(i, mr);
		}

		int i = 0;
		while (i < getArity()) {

			Door d = new Door();
			RouteMessage mr = recoitRoute(d);
			myRouter.setDoorToMyRoute(mr.getProcId(), d.getNum());
			myRouter.complete++;
			i++;
		}

	}

	// Access to receive function
	public RouteMessage recoitRoute(final Door d) {

		RouteMessage rm = (RouteMessage) receive(d);
		return rm;
	}

	// Rule 0.2 : I need to extend my routing table
	synchronized void extendRoutingTable() {

		// Stay awaiting while my routing table is not complete
		while (myRouter.complete < getNetSize()) {

			// Send my Routing table to my neighbors
			if (getArity() > 1) {

				ExtendRouteMessage mr = new ExtendRouteMessage(MsgType.TABLE,
						getId(), procId);
				mr.setRoutingTable(myRouter.getMyRoute());
				sendRouteMessage(mr, -1);
			}

			Door d = new Door();
			recoitExtendRouteMessage(d);
		}
		myRouter.ProcBecomeReady(procId, true);

	}

	// Access to receive function
	public void recoitExtendRouteMessage(final Door d) {

		ExtendRouteMessage m = (ExtendRouteMessage) receive(d);
		if (m.getMsgType() == MsgType.TABLE) {

			for (int i = 0; i < getNetSize(); i++) {

				if (myRouter.getDoorOnMyRoute(i) == -1
						&& m.getRoutingTable()[i] > -1) {

					myRouter.setDoorToMyRoute(i, d.getNum());
					myRouter.complete++;
				}
			}
		}

		if (m.getMsgType() == MsgType.READY) {

			for (int i = 0; i < getNetSize(); i++) {

				if (myRouter.getDoorOnMyRoute(i) == -1
						&& m.getRoutingTable()[i] > -1) {

					myRouter.setDoorToMyRoute(i, d.getNum());
					myRouter.complete++;
				}
			}
			if (!myRouter.getStateOfProc(m.getMyProcId())) {

				myRouter.ProcBecomeReady(m.getMyProcId(), true);
				myRouter.ready++;
				sendRouteMessage(m, d.getNum());
			}
		}
	}

	// Send message Where Is
	public void sendRouteMessage(	final ExtendRouteMessage mr,final int exceptDoor) {

		for (int i = 0; i < getArity(); i++) {

			if (exceptDoor != i) {

				boolean send = sendTo(i, mr);
			}

		}
	}

	// Rule 0.3 : My routing table is complete and I'am waiting other proc to be
	// ready like me
	synchronized void sayIamReady() {

		ExtendRouteMessage mr = new ExtendRouteMessage(MsgType.READY, getId(),
				procId);
		mr.setRoutingTable(myRouter.getMyRoute());
		sendRouteMessage(mr, -1);

		log.logMsg("Proc-" + procId + " : I am Ready");
		// Stay awaiting while my routing table is not complete
		while (myRouter.ready < getNetSize()) {

			Door d = new Door();
			recoitExtendRouteMessage(d);
		}
		iAmReady = true;
		displayState();
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
	public synchronized void receiveReq(RicartAggrawalaMessage ms){
		
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

	/* Rule 3 : */
	/* le processus reçoit le message rel() de j */
	public synchronized void receiveRel(final RicartAggrawalaMessage rm) {

		if (rm.procRecipient == procId) {
			Nrel--;
			log.logMsg("Proc"+procId+" Receive "+(getArity() - Nrel)+" REL");
			if(Nrel == 0){
				
				displayState();
				Color bg = Color.blue;
				Color fg = Color.red;
				FormMessage form = new FormMessage(MsgType.FORME, procId,0, p1, p2, tailleForm, typeForm, bg, fg);
				boolean sent = sendTo(next, form);
				notify();
			}
		} else {
			
			next = this.myRouter.getDoorOnMyRoute(rm.procRecipient);
			log.logMsg("proc-" + procId
					+ " : Receive token, do not need it, I forward it to "
					+ rm.procRecipient + " on door " + next);
			boolean sent = sendTo(next, rm);
		}
	}
	
	/* Rule 4*/
	/* Le processus entre en section critique */
	private synchronized void getInCriticalSection(){
		
		lanceur.ajouteForme(p1, p2, typeForm);
	}

	/* Rule 4 */
	private synchronized void libereSC() {

		R = false;
		for (int i = 0; i < this.getNetSize(); i++) {

			if(X.containsKey(i)){
				
				RicartAggrawalaMessage mrel = new RicartAggrawalaMessage(MsgType.REL, procId,0,0);
				mrel.procRecipient = i;
				int door = this.myRouter.getDoorOnMyRoute(i);
				this.sendTo(door, mrel);
				X.remove(i);
			}
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
	
	/* Allow to send a broadcast message to a specif number of processus */
	private synchronized void sendForm(FormMessage ms, int nbrProc, int exceptDoor){
		
		for(int i=0; i<nbrProc; i++){
			
			if(i != exceptDoor){
				
				int door = myRouter.getDoorOnMyRoute(i);
				ms.nextProcId = i;
				boolean send = sendTo(door, ms);	
			}
		}
	}
	
	synchronized public void receiveFormMessage(final FormMessage form) {
		// TODO Auto-generated method stub
		
		if (form.nextProcId == procId) {
			
			log.logMsg("Proc-" + procId + ": Receive form of " + form.procId);
			lanceur.ajouteForme(form.point1, form.point2, form.typeForm);
			
		} else {

			next = myRouter.getDoorOnMyRoute(form.nextProcId);
			log.logMsg("Proc-" + procId + ": Receive form of " + form.procId+" send it to the recipient Proc-"+form.nextProcId+" on door "+next);
			boolean sent = sendTo(next, form);
		}

	}
	// Access to receive function
	public Message recoit(final Door d) {

		Message m = receive(d);
		return m;
	}
	
	// Display state
	void displayState() {

		String state = new String("\n");
		state = state + "--------------------------------------\n";
		/*if (inCritical)
			state = state + "** ACCESS CRITICAL **\n";
		else if (waitForCritical)
			state = state + "* WAIT FOR *\n";
		else
			state = state + "-- SLEEPING --\n";
		*/
		if (myRouter.ready == this.getNetSize()) {

			iAmReady = false;
			state = state + "#### Route processus " + getId() + " ######\n";
			for (int i = 0; i < getNetSize(); i++) {

				state = state + "Door " + myRouter.getDoorOnMyRoute(i)
						+ " connected to procId-" + i + "\n";
			}

		}
		log.logMsg("procId-" + procId + ": " + state);
	}
}
