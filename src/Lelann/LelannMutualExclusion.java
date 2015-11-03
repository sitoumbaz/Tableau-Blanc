package Lelann;

// Java imports
import java.awt.Color;
import java.awt.Point;
import java.util.Random;

import logger.Logger;
import visidia.simulation.process.algorithm.Algorithm;
import visidia.simulation.process.messages.Door;
import visidia.simulation.process.messages.Message;
import Gui.Lanceur;
import Gui.MoteurTest;

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

	// Router
	public MyRouter myRouter;
	
	// Tableau blanc
	private Lanceur lanceur;
	private Point p1 = null;
	private Point p2 = null;
	private float tailleForm;
	private int typeForm;

	// Token
	boolean token = false;

	// Critical section thread
	ReceptionRules rr = null;

	// To display the state
	boolean waitForCritical = false;
	boolean inCritical = false;

	// Pixel Generator
	MoteurTest motTest;

	@Override
	public String getDescription() {

		return ("Lelann Algorithm for Mutual Exclusion");
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
		Logger log = new Logger(procId);

		Random rand = new Random();
		netSize = getNetSize();
		arity = getArity();

		// Init routeMap
		myRouter = new MyRouter(getNetSize());
		myRouter.setDoorToMyRoute(getId(), -2);

		
		setRoutingTable();
		// displayState();
		try {
			// attendre que chaque proc ait recu le messge de l'autre
			Thread.sleep(5000);
		} catch (InterruptedException ie) {
		}

		extendRoutingTable();
		sayIamReady();

		log.logMsg("Ready(" + getId() + ") = " + myRouter.ready);
		lanceur = new Lanceur("Tableau Blanc Proc" + getId());
		motTest = new MoteurTest();
		lanceur.start();

		// ici le tableau blanc est deja construit
		// attente que tous les procs aient reçu le message ready
		// remplacer par des acusés de reception
		try {
			Thread.sleep(15000);
		} catch (InterruptedException ie) {
		}

		// Start token round
		rr = new ReceptionRules(this);
		rr.start();

		if (procId == 0) {

			token = false;
			TokenMessage tm = new TokenMessage(MsgType.TOKEN, getNextProcId());
			next = myRouter.getDoorOnMyRoute(procId + 1);
			boolean sent = sendTo(next, tm);
		}


		while( true ) {
		    
		    // Wait for some time
		    int time = ( 3 + rand.nextInt(10)) * speed * 1000;
		    System.out.println("Process " + procId + " wait for " + time);
		    try {
			Thread.sleep( time );
		    } catch( InterruptedException ie ) {}
		    
	    	// Try to access critical section
		    waitForCritical = true;
		    askForCritical();

		    // Access critical
		    waitForCritical = false;
		    inCritical = true;
		    
		    //displayState();

		    // Simulate critical resource use
		    time = (1 + rand.nextInt(3)) * 1000;
		    System.out.println("Process " + procId + " enter SC " + time);
		    try {
			Thread.sleep( time );
		    } catch( InterruptedException ie ) {}
		    System.out.println("Process " + procId + " exit SC ");

		    // Release critical use
		    inCritical = false;
		    endCriticalUse();
		    
		while (true) {

			// attente avant la prochaine demande de section critique
			time = (3 + rand.nextInt(10)) * speed * 1000;
			log.logMsg("Wait for " + time);
			try {
				Thread.sleep(time);
			} catch (InterruptedException ie) {}

			// Try to access critical section
			waitForCritical = true;
			askForCritical();

			// Access critical
			waitForCritical = false;
			inCritical = true;

			displayState();

			// Simulate critical resource use
			time = (1 + rand.nextInt(3)) * 1000;
			log.logMsg("Enter SC " + time);
			try {
				Thread.sleep(time);
			} catch (InterruptedException ie) {
			}
			log.logMsg("Exit SC");

			// Release critical use
			inCritical = false;
			endCriticalUse();
		}
		
		
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

				ExtendRouteMessage mr = new ExtendRouteMessage(MsgType.TABLE,
						getId(), procId);
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

		// log.logMsg("myRouter.complete(" + getId() + ") = " +
		// myRouter.complete);
		// Stay awaiting while my routing table is not complete
		while (myRouter.ready < getNetSize()) {

			Door d = new Door();
			recoitExtendRouteMessage(d);
		}

	}

	// Rule 3 : ask for critical section
	synchronized void askForCritical() {

		while (!token) {
			displayState();
			motTest.creerForme();
			p1 = motTest.getPoint1();
			p2 = motTest.getPoint2();
			System.out.println("P1("+p1.getX()+","+p1.getY()+") et P2("+p2.getX()+","+p2.getY()+")");
			typeForm = motTest.getChoixForme();
			lanceur.ajouteForme(p1, p2, typeForm);
			try {
				this.wait();
			} catch (InterruptedException ie) {
			}
		}
	}

	// Rule 4 : receive TOKEN
	synchronized void receiveTOKEN(final TokenMessage tm) {
		

		if (tm.idProc == procId) {
			next = myRouter.getDoorOnMyRoute(getNextProcId());
			if (waitForCritical) {

				next = myRouter.getDoorOnMyRoute(getNextProcId());
				token = true;
				displayState();
				Color bg = Color.blue;
				Color fg = Color.red;
				FormMessage form = new FormMessage(MsgType.FORME,procId,getNextProcId(),p1,p2,tailleForm,typeForm,bg,fg);
				boolean sent = sendTo(next, form);
				System.out.println("proc-" + procId
						+ " : Receive token and need  it,  send form to "
						+ getNextProcId() + " on door " + next);
				// log.logMsg("proc-" + procId
				// + " : Receive token and need  it,  send form to "
				// + getNextProcId() + " on door " + next);
				notify();

				
			}else{
				
				System.out.println("proc-"+procId+" : Receive token, forward to "+getNextProcId()+" on door "+next);
				tm.idProc = getNextProcId();
				boolean sent = sendTo(next, tm);
			}

		} 
		else {

			next = myRouter.getDoorOnMyRoute(tm.idProc);
			System.out.println("proc-" + procId
					+ " : Receive token but do not need it, on door " + next);
			boolean sent = sendTo(next, tm);
		}
	}
    
	// Rule 5 : receive Form
	synchronized public void receiveFormMessage(final FormMessage form) {
		// TODO Auto-generated method stub
		System.out.println("proc-"+procId+" : Receive form forward  to "+getNextProcId()+" on door "+next);
		System.out.println("proc-" + procId + " : Receive form forward  to "
				+ getNextProcId() + " on door " + next);
		next = myRouter.getDoorOnMyRoute(getNextProcId());
		lanceur.ajouteForme(form.point1, form.point2, form.typeForm);
		/* I send the form if only the next proc is different of the owner of this form */
		if(getNextProcId() != form.procId && form.nextProcId != procId){
			
			if( getNextProcId() > form.nextProcId ){
				
				form.nextProcId = getNextProcId();
			}
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
		System.out.println("proc-" + procId
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
	public void sendRouteMessage(	final ExtendRouteMessage mr, final int exceptDoor) {

		for (int i = 0; i < getArity(); i++) {

			if (exceptDoor != i) {

				boolean send = sendTo(i, mr);
			}

		}
	}
	
	// Access to receive function
	@SuppressWarnings("finally")
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

		state = state + "#### Route processus " + getId() + " ######\n";
		for (int i = 0; i < getNetSize(); i++) {

			// if(myRouter.getDoorOnMyRoute(i) > -1){

			state = state + "porte " + myRouter.getDoorOnMyRoute(i)
					+ " connectee au proc " + i + "\n";
			// }
		}

		System.out.println(state);
	}

}
