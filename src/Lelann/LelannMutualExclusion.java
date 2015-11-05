package Lelann;

// Java imports
import java.awt.Color;
import java.awt.Point;
import java.util.Random;

import logger.ProcLogger;
import visidia.simulation.process.algorithm.Algorithm;
import visidia.simulation.process.messages.Door;
import visidia.simulation.process.messages.Message;
import Gui.Lanceur;
import Gui.MoteurTest;
import Listener.ReceptionRules;
import Message.FormMessage;
import Message.MsgType;

public class LelannMutualExclusion extends Algorithm {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	// All nodes data
	public int procId;
	public int next = 0;
	public int nextProcId;
	public int speed = 4;
	public int netSize = 0;
	public int arity = 0;
	
	//just for managin displaying routing table in the log
	boolean iAmReady = false;
	// Router
	public MyRouter myRouter;

	// Tableau blanc
	private Lanceur lanceur;
	private Point p1 = null;
	private Point p2 = null;
	private float tailleForm;
	private int typeForm;
	// Pixel Generator
	MoteurTest motTest;

	// Token
	boolean token = false;

	// logger
	ProcLogger log;

	// Critical section thread
	ReceptionRules rr = null;

	// To display the state
	boolean waitForCritical = false;
	boolean inCritical = false;


	@Override
	public String getDescription() {

		return ("Lelann Algorithm for Mutual Exclusion on any type of network");
	}

	@Override
	public Object clone() {
		return new LelannMutualExclusion();
	}

	//
	// Nodes' code
	//
	@Override
	public void init() {

		procId = getId();
		nextProcId = getNextProcId();
		log = new ProcLogger(procId,"Lelann");

		Random rand = new Random();
		netSize = getNetSize();
		arity = getArity();

		// Init routeMap
		myRouter = new MyRouter(getNetSize());
		myRouter.setDoorToMyRoute(getId(), -2);

		setRoutingTable();
		// attente que chaque que les messages aient le temps de se propager
		try {
			Thread.sleep(2500);
		} catch (InterruptedException ie) {
		}

		extendRoutingTable();
		sayIamReady();

		log.logMsg("Proc-"+procId+" I am ready to begin "+ myRouter.ready);
		lanceur = new Lanceur("Tableau Blanc Proc" + getId());
		log.logMsg("Proc-"+procId+" I launch the white board named Tableau Blanc Proc"+procId);
		motTest = new MoteurTest();
		lanceur.start();
		
		
		rr = new ReceptionRules(this);
		rr.start();
		log.logMsg("Proc-"+procId+" I start my message Listerner");

		if (procId == 0) {

			token = false;
			TokenMessage tm = new TokenMessage(MsgType.TOKEN, nextProcId);
			next = myRouter.getDoorOnMyRoute(nextProcId);
			boolean sent = sendTo(next, tm);
			if(!sent){
				
				log.logMsg("Proc-"+procId+" Unable to start process");
			}
			log.logMsg("Proc-"+procId+" I start the prcess by sending token to Proc-"+nextProcId+" on door "+next);
			
		}

		while (true) {

			// Wait for some time
			int time = (3 + rand.nextInt(10)) * speed * 1000;
			log.logMsg("Proc-"+procId+":  wait for " + time);
			try {
				Thread.sleep(time);
			} catch (InterruptedException ie) {log.logMsg("Proc-"+procId+" : Error"+ie.getMessage());}

			// Try to access critical section
			waitForCritical = true;
			askForCritical();

			// Access critical
			waitForCritical = false;
			inCritical = true;

			// displayState();

			// Simulate critical resource use
			time = (1 + rand.nextInt(3)) * 1000;
			try {
				Thread.sleep(time);
			} catch (InterruptedException ie) {}

			// Release critical use
			inCritical = false;
			endCriticalUse();

		}

	}
	// --------------------
	// Rules
	// -------------------

	// Rule 0 : tell to all my neighbor where I am
	synchronized void setRoutingTable() {

		for (int i = 0; i < getArity(); i++) {

			RouteMessage mr = new RouteMessage(MsgType.ROUTE, getId());
			boolean send = sendTo(i, mr);
		}

		int i = 0;
		while (i < getArity()) {

			Door d = new Door();
			RouteMessage mr = recoitRoute(d);
			myRouter.setDoorToMyRoute(mr.procId, d.getNum());
			myRouter.complete++;
			i++;
		}

	}

	// Rule 1 : I need to extend my routing table
	synchronized void extendRoutingTable() {

		// Stay awaiting while my routing table is not complete
		while (myRouter.complete < getNetSize()) {

			// Send my Routing table to my neighbors
			if (getArity() > 1) {

				ExtendRouteMessage mr = new ExtendRouteMessage(MsgType.TABLE,getId(), procId);
				mr.routingTable = myRouter.getMyRoute();
				sendRouteMessage(mr, -1);
			}

			Door d = new Door();
			recoitExtendRouteMessage(d);
		}
		myRouter.ProcBecomeReady(procId, true);

	}

	// Rule 2 : My routing table is complete and I'am waiting other proc to be
	// ready like me
	synchronized void sayIamReady() {

		ExtendRouteMessage mr = new ExtendRouteMessage(MsgType.READY, getId(),procId);
		mr.routingTable = myRouter.getMyRoute();
		sendRouteMessage(mr, -1);

		log.logMsg("Proc-"+procId+" : I am Ready");
		// Stay awaiting while my routing table is not complete
		while (myRouter.ready < getNetSize()) {

			Door d = new Door();
			recoitExtendRouteMessage(d);
		}
		iAmReady=true;
		displayState();
	}

	// Rule 3 : ask for critical section
	synchronized void askForCritical() {

		while (!token) {
			displayState();
			motTest.creerForme();
			p1 = motTest.getPoint1();
			p2 = motTest.getPoint2();
			typeForm = motTest.getChoixForme();
			log.logMsg("Proc-"+procId+" : Create form, wait critical section  befor drawing");
			try {
				this.wait();
			} catch (InterruptedException ie) {
			}
		}
	}
	// Rule 4 : receive TOKEN
	public synchronized void receiveTOKEN(final TokenMessage tm) {

		if (tm.idProc == procId) {
			next = myRouter.getDoorOnMyRoute(getNextProcId());
			if (waitForCritical) {

				lanceur.ajouteForme(p1, p2, typeForm);
				next = myRouter.getDoorOnMyRoute(getNextProcId());
				token = true;
				displayState();
				Color bg = Color.blue;
				Color fg = Color.red;
				FormMessage form = new FormMessage(MsgType.FORME, procId,
						getNextProcId(), p1, p2, tailleForm, typeForm, bg, fg);
				boolean sent = sendTo(next, form);

				log.logMsg("Proc-"+procId+" : Receive token, get in critical section, "
						         + "drawing form and send my form to proc-"+getNextProcId()
						         + " on door "+next);
				notify();

			} else {

				log.logMsg("proc-" + procId + " : Receive token, do not need it, I forward it to "
						+ getNextProcId() + " on door " + next);

				tm.idProc = getNextProcId();
				boolean sent = sendTo(next, tm);
			}

		} else {

			next = myRouter.getDoorOnMyRoute(tm.idProc);
			log.logMsg("proc-" + procId
					+ " : Receive token but do not need it, on door " + next);
			boolean sent = sendTo(next, tm);
		}
	}
    
	// Rule 5 : receive Form && I send the form if only the next proc is different of the owner of the form 
	synchronized public void receiveFormMessage(final FormMessage form) {
	// TODO Auto-generated method stub
		log.logMsg("Proc-"+procId+": Receive form of "+form.procId);
		if(form.nextProcId == procId){
		
			lanceur.ajouteForme(form.point1, form.point2, form.typeForm);
			if( getNextProcId() > form.nextProcId &&  getNextProcId() != form.procId)
			{
				form.nextProcId = getNextProcId();
				next = myRouter.getDoorOnMyRoute(form.nextProcId);
				boolean sent = sendTo(next, form);
			}
			else{
			
				if(getNextProcId() != form.procId){
					
					form.nextProcId = getNextProcId();
					next = myRouter.getDoorOnMyRoute(form.nextProcId);
					boolean sent = sendTo(next, form);
				}
			}
		}
		else{
			
			next = myRouter.getDoorOnMyRoute(form.nextProcId);
			boolean sent = sendTo(next, form);
		}
	
	}

	// Rule 6 :
	void endCriticalUse() {

		next = myRouter.getDoorOnMyRoute(getNextProcId());
		token = false;
		TokenMessage tm = new TokenMessage(MsgType.TOKEN, getNextProcId());
		boolean sent = sendTo(next, tm);
		log.logMsg("proc-" + procId
				+ " : Leave Critical Section send token to " + getNextProcId()
				+ " on door " + next);
		displayState();
	}

	// Determine the next proc id
	public int getNextProcId() {

		nextProcId = procId + 1;
		if (getNetSize() == procId + 1) {

			nextProcId = 0;
		}
		return nextProcId;
	}
	
	// Send message Where Is
	public void sendRouteMessage(	final ExtendRouteMessage mr,
									final int exceptDoor) {

		for (int i = 0; i < getArity(); i++) {

			if (exceptDoor != i) {

				boolean send = sendTo(i, mr);
			}

		}
	}

	// Access to receive function
	public Message recoit(final Door d) {

		Message m = receive(d);
		return m;
	}

	// Access to receive function
	public TokenMessage recoitToken(final Door d) {

		TokenMessage sm = (TokenMessage) receive(d);
		return sm;
	}

	// Access to receive function
	public FormMessage recoitForme(final Door d) {

		FormMessage sm = (FormMessage) receive(d);
		return sm;
	}

	// Access to receive function
	public RouteMessage recoitRoute(final Door d) {

		RouteMessage rm = (RouteMessage) receive(d);
		return rm;
	}

	// Access to receive function
	public void recoitExtendRouteMessage(final Door d) {

		ExtendRouteMessage m = (ExtendRouteMessage) receive(d);
		if (m.type == MsgType.TABLE) {

			for (int i = 0; i < getNetSize(); i++) {

				if (myRouter.getDoorOnMyRoute(i) == -1
						&& m.routingTable[i] > -1) {

					myRouter.setDoorToMyRoute(i, d.getNum());
					myRouter.complete++;
				}
			}
		}

		if (m.type == MsgType.READY) {

			for (int i = 0; i < getNetSize(); i++) {

				if (myRouter.getDoorOnMyRoute(i) == -1
						&& m.routingTable[i] > -1) {

					myRouter.setDoorToMyRoute(i, d.getNum());
					myRouter.complete++;
				}
			}
			if (!myRouter.getStateOfProc(m.myProcId)) {

				myRouter.ProcBecomeReady(m.myProcId, true);
				myRouter.ready++;
				sendRouteMessage(m, d.getNum());
			}
		}
	}

	// Display state
	void displayState() {

		String state = new String("\n");
		state = state + "--------------------------------------\n";
		if (inCritical)
			state = state + "** ACCESS CRITICAL **\n";
		else if (waitForCritical)
			state = state + "* WAIT FOR *\n";
		else
			state = state + "-- SLEEPING --\n";

		if(myRouter.ready == this.getNetSize()){
			
			iAmReady = false;
			state = state + "#### Route processus " + getId() + " ######\n";
			for (int i = 0; i < getNetSize(); i++) {

				state = state + "Door " + myRouter.getDoorOnMyRoute(i)+ " connected to procId-" + i + "\n";
			}
			
		}
		log.logMsg("procId-"+procId+": "+state);
	}

}
