package RicartAggrawala;

import java.util.HashMap;

import logger.ProcLogger;
import visidia.simulation.process.algorithm.Algorithm;
import visidia.simulation.process.messages.Door;
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

	private int procId = 0; // My processus Id

	private ProcLogger log = null; /* logger */

	/*
	 * private boolean askCriticalSection = false; private boolean
	 * inCriticalSection = false; private boolean endCriticalSection = false;
	 */

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

		// Init routeMap
		myRouter = new MyRouter(getNetSize());
		myRouter.setDoorToMyRoute(getId(), -2);
		log = new ProcLogger(procId, "Ricart");
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
	public void sendRouteMessage(	final ExtendRouteMessage mr,
									final int exceptDoor) {

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
	private synchronized void askCriticalSection() {

		R = true;
		HSC = H++;
		Nrel = getArity();
		RicartAggrawalaMessage ms = new RicartAggrawalaMessage(MsgType.REQ,
				procId, 0, HSC);
		sendReq(ms, Nrel, -1);

		while (Nrel != 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/* Rules 2 : */

	/* Allow to send a broadcast message to a specif number of processus */
	private synchronized void sendReq(	final RicartAggrawalaMessage ms,
										final int nbrProc,
										final int exceptDoor) {

		for (int i = 0; i < nbrProc; i++) {

			if (i != exceptDoor) {

				boolean send = sendTo(i, ms);
			}
		}
	}

	/* Rule 3 : */
	/* le processus reçoit le message rel() de j */
	private synchronized void receieveRel(final RicartAggrawalaMessage rm) {

		if (rm.procRecipient == procId) {
			Nrel--;
			log.logMsg("Message rel reçu");
		} else {
			log.logMsg("proc-" + procId
					+ " : Receive token, do not need it, I forward it to "
					+ getNextProcId() + " on door " + next);

			rm.procRecipient = getNextProcId();
			boolean sent = sendTo(next, rm);
		}
	}

	// Determine the next proc id
	public int getNextProcId() {

		int nextProcId = procId + 1;
		if (getNetSize() == procId + 1) {

			nextProcId = 0;
		}
		return nextProcId;
	}

	/* Rule 4 */
	private synchronized void libereSC() {

		R = false;
		for (int i = 0; i < nbrProc; i++) {

			if (i != exceptDoor) {

				boolean send = sendTo(i, ms);
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
