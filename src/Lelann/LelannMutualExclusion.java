package Lelann;

// Java imports
import java.awt.Point;

import visidia.simulation.process.algorithm.Algorithm;
import visidia.simulation.process.messages.Door;
import Gui.Lanceur;

public class LelannMutualExclusion extends Algorithm {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	// All nodes data
	private int procId;
	private int next = 0;

	// Router
	MyRouter myRouter;

	// Tableau blanc
	Lanceur lanceur;

	// Token
	boolean token = false;

	// To display the state
	boolean waitForCritical = false;
	boolean inCritical = false;

	DisplayFrame df;

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

		// Init routeMap
		myRouter = new MyRouter(getNetSize());
		myRouter.setDoorToMyRoute(getId(), -2);

		setRouteMap();

		try {
			// attendre que chaque proc aie recu le messge de l'autre
			Thread.sleep(1000);
		} catch (InterruptedException ie) {
		}

		while (myRouter.complete < this.getNetSize()) {

			extendRouteMap();
		}

		// Je continue de remplir ma table de routage avec les autres processus
		// manquant
		fillTheRoute();
		lanceur = new Lanceur("Tableau Blanc Proc" + getId());
		Point p1 = new Point(139, 170);
		Point p2 = new Point(144, 101);
		lanceur.ajouteForme(p1, p2, 2);
		// lanceur.start();

		/*
		 * df = new DisplayFrame( getId() ); displayState();
		 */
		try {
			Thread.sleep(5000);
		} catch (InterruptedException ie) {
		}

	}
	// --------------------
	// Rules
	// -------------------

	// Rule 0 : tell to all my neighbor where I am
	synchronized void setRouteMap() {

		for (int i = 0; i < getArity(); i++) {

			RouteMessage mr = new RouteMessage(MsgType.ROUTE, getId());
			boolean send = sendTo(i, mr);
		}

		int i = 0;
		while (i < getArity()) {

			Door d = new Door();
			RouteMessage mr = recoitRoute(d);
			myRouter.setDoorToMyRoute(mr.procId, d.getNum());
			i++;
		}
		setMyRouterIsComplete();

	}

	// Rule 1 : Ask to neighbor if they know other processus
	synchronized void extendRouteMap() {

		for (int procId = 0; procId < this.getNetSize(); procId++) {

			if (myRouter.getDoorOnMyRoute(procId) == -1) {

				ExtendRouteMessage mr = new ExtendRouteMessage(MsgType.WHEREIS,
						getId(), procId);
				sendWhereIsOrHereIs(mr, -1);

				Door d = new Door();
				int door = -1;
				mr = recoitHereOrWhere(d);
				if (mr.type == MsgType.WHEREIS) {

					if (mr.myProcId != getId()) {// While I'm not the initiator
													// of this message, I accept
													// it

						if (myRouter.getDoorOnMyRoute(mr.ProcIdToFind) > -1) {// If
																				// ProcIdToFind
																				// is
																				// connected
																				// to
																				// one
																				// of
																				// my
																				// doors

							mr.addProcId(mr.ProcIdToFind);
							mr.type = MsgType.HEREIS;
							sendTo(d.getNum(), mr);

						} else {

							sendWhereIsOrHereIs(mr, d.getNum());
						}

					}

				} else if (mr.type == MsgType.HEREIS) {

					for (int i = 0; i < mr.getListProc().size(); i++) {

						myRouter.setDoorToMyRoute(mr.getProcId(i), d.getNum());

					}

					myRouter.setDoorToMyRoute(getId(), -2);
					if (mr.myProcId != getId()) {

						door = myRouter.getDoorOnMyRoute(mr.myProcId);

						if (door > -1) {

							sendTo(door, mr);

						} else {

							sendWhereIsOrHereIs(mr, d.getNum());
						}

					}

				} else {

					if (mr.type == MsgType.READY) {

						if (!myRouter.getStateOfProc(mr.myProcId)) {

							myRouter.ready++;
							sendWhereIsOrHereIs(mr, d.getNum());
							myRouter.ProcBecomeReady(mr.myProcId, true);
						}
					}
				}

			}

		}
		setMyRouterIsComplete();

	}

	//
	synchronized void fillTheRoute() {

		System.out.println("myRouter.ready " + myRouter.ready + " "
				+ getNetSize());
		myRouter.ProcBecomeReady(getId(), true);
		myRouter.ready++;
		ExtendRouteMessage mr = new ExtendRouteMessage(MsgType.READY, getId(),
				-1);
		sendWhereIsOrHereIs(mr, -1);

		while (myRouter.ready < getNetSize()) {

			Door d = new Door();
			mr = recoitHereOrWhere(d);
			if (mr.type == MsgType.WHEREIS) {

				if (myRouter.getDoorOnMyRoute(mr.ProcIdToFind) > -1) {

					mr.addProcId(mr.ProcIdToFind);
					mr.type = MsgType.HEREIS;
					sendTo(d.getNum(), mr);
				}
			}
			if (mr.type == MsgType.READY) {

				if (!myRouter.getStateOfProc(mr.myProcId)) {

					myRouter.ready++;
					sendWhereIsOrHereIs(mr, d.getNum());
					myRouter.ProcBecomeReady(mr.myProcId, true);
				}

			}

		}

	}
	// Rule 2 : ask for critical section
	synchronized void askForCritical() {

		while (!token) {
			displayState();
			try {
				this.wait();
			} catch (InterruptedException ie) {
			}
		}
	}

	// Rule 3 : receive TOKEN
	synchronized void receiveTOKEN(final int d) {

		System.out.println("Process " + procId + " reveiced TOKEN from " + d);
		next = (d == 0 ? 1 : 0);

		if (waitForCritical == true) {

			token = true;
			displayState();
			notify();

		} else {
			// Forward token to successor
			TokenMessage tm = new TokenMessage(MsgType.TOKEN);
			boolean sent = sendTo(next, tm);
		}
	}

	// Rule 4 :
	void endCriticalUse() {

		token = false;
		TokenMessage tm = new TokenMessage(MsgType.TOKEN);
		boolean sent = sendTo(next, tm);

		displayState();
	}

	// Send message Where Is
	public void sendWhereIsOrHereIs(final ExtendRouteMessage mr,
									final int exceptDoor) {

		for (int i = 0; i < getArity(); i++) {

			if (exceptDoor != i) {

				boolean send = sendTo(i, mr);
			}

		}
	}
	// Access to receive function
	public TokenMessage recoit(final Door d) {

		TokenMessage sm = (TokenMessage) receive(d);
		return sm;
	}

	// Access to receive function
	public RouteMessage recoitRoute(final Door d) {

		RouteMessage rm = (RouteMessage) receive(d);
		return rm;
	}

	// Access to receive function
	public ExtendRouteMessage recoitHereOrWhere(final Door d) {

		ExtendRouteMessage rm = (ExtendRouteMessage) receive(d);
		return rm;
	}

	// Test if the route map is complete
	public void setMyRouterIsComplete() {

		for (int i = 0; i < getNetSize(); i++) {

			if (myRouter.getDoorOnMyRoute(i) != -1) {

				myRouter.complete++;
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

		df.display(state);
	}
}
