package Naimi;

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
import Listener.MessageListener;
import Listener.RoutingListener;
import Message.ExtendRouteMessage;
import Message.FormMessage;
import Message.MsgType;
import Message.RouteMessage;
import Message.RulesMessage;
import Router.MyRouter;

public class NaimiTreilMutualExclusion extends Algorithm {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4859711938313072495L;
	// All nodes data
	public int procId;
	public int next = 0;
	public int owner = 0;
	public int speed = 2;
	public int netSize = 0;
	public int arity = 0;
	public boolean token = false;
	public boolean SCI = false;

	// just displaying routing table in the log
	public boolean iAmReady = false;

	// Router
	public MyRouter myRouter;
	public String strRoute = null;

	// Tableau blanc
	private Lanceur lanceur;
	private Point p1 = null;
	private Point p2 = null;
	private float tailleForm;
	private int typeForm;

	// Moteur de test
	MoteurTest motTest;

	// logger
	String logFile = null;

	// Critical section thread
	MessageListener messageListener = null;
	RoutingListener routing = null;

	@Override
	public String getDescription() {

		return ("Naimi Treil Algorithm for Mutual Exclusion");
	}

	@Override
	public Object clone() {
		return new NaimiTreilMutualExclusion();
	}

	//
	// Nodes' code
	//
	@SuppressWarnings("static-access")
	@Override
	public void init() {

		procId = getId();
		netSize = getNetSize();
		arity = getArity();

		// Create logger name
		logFile = "naimi_proc-" + procId;

		/* Begin setting up route */
		routing();
		/* End setting up route */

		lanceur = new Lanceur("Tableau Blanc Proc" + getId());
		Logger.write(logFile, "Proc-" + procId
				+ " I launch the white board named Tableau Blanc Proc" + procId);
		motTest = new MoteurTest();
		lanceur.start();

		messageListener = new MessageListener(this);
		messageListener.start();

		// Generateur aleatoire
		Random rand = new Random();

		owner = 0;
		if (procId == 0) {

			token = true;
			owner = -2;
		}

		while (true) {

			// Wait for some time
			int time = (3 + rand.nextInt(10)) * speed * 1000;
			Logger.write(logFile, "Proc-" + procId + ":  wait for " + time);
			try {
				Thread.sleep(time);
			} catch (InterruptedException ie) {
				Logger.write(logFile,
						"Proc-" + procId + " : Error" + ie.getMessage());
			}
			drawNewForm();
		}

	}

	/**
	 * Synchronized function which allow to set up the routing table of each
	 * processus
	 * 
	 * @return void
	 */
	public synchronized void routing() {

		myRouter = new MyRouter(getNetSize());
		myRouter.setDoorToMyRoute(getId(), -2);

		routing = new RoutingListener(this, myRouter);
		routing.start();
		while (!iAmReady) {

			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		writeRoute();
		try {

			routing.sleep(1000);
			routing.interrupt();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Function which allow to simulate the drawing action on the white board
	 * 
	 * @Return void
	 */

	public void drawNewForm() {

		motTest.creerForme();
		p1 = motTest.getPoint1();
		p2 = motTest.getPoint2();
		typeForm = motTest.getChoixForme();
		Logger.write(logFile, "Proc-" + procId
				+ " : Create form, wait critical section  befor drawing");
		askCriticalSection();
		Logger.write(logFile, "Proc-" + procId
				+ ":  Enter the Critical Section, draw form and publish");
		lanceur.ajouteForme(p1, p2, typeForm);
		sendForm();
		try {
			Thread.sleep(2500);
		} catch (InterruptedException ie) {
			Logger.write(logFile,
					"Proc-" + procId + " : Error" + ie.getMessage());
		}
		endCriticalSection();

	}

	/********************************** Rules of the Algorithm of Naimi Treil ********************************/

	/**
	 * Rule 1 : ask for critical section
	 * 
	 * @return void
	 * 
	 */
	public synchronized void askCriticalSection() {

		SCI = true;
		if (owner != -2) {

			RulesMessage m = new RulesMessage(MsgType.REQ, this.procId, owner,
					0);
			int door = myRouter.getDoorOnMyRoute(owner);
			boolean sent = sendTo(door, m);
			Logger.write(logFile, "Proc-" + procId
					+ " : Send REQ to owner Proc-" + owner);
			owner = -2;
			while (!token) {

				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * Rule 2 : receive req
	 * 
	 * @return void
	 * @param instance
	 *            of RulesMessage class
	 * 
	 */
	public synchronized void receiveReq(final RulesMessage m) {

		int door = myRouter.getDoorOnMyRoute(m.procRecipient);
		if (m.procRecipient == procId) {
			Logger.write(logFile, "Proc-" + procId + " : Receive REQ of Proc-"
					+ m.procId);
			if (owner == -2) {

				if (SCI) {

					next = m.procId;
					Logger.write(logFile, "Proc-" + procId + " : Add Proc-"
							+ m.procId + " as the next Proc to be elected");
				} else {

					RulesMessage m_jeton = new RulesMessage(MsgType.TOKEN,
							procId, m.procId, 0);
					door = myRouter.getDoorOnMyRoute(m.procId);
					boolean send = sendTo(door, m_jeton);
					Logger.write(logFile, "Proc-" + procId
							+ " : Send TOKEN to Proc-" + m.procId);
					token = false;
					owner = m.procId;
				}
			} else {

				Logger.write(logFile, "Proc-" + procId
						+ " : I am not the owner, I send REQ to owner Proc-"
						+ owner);
				door = myRouter.getDoorOnMyRoute(owner);
				m.procRecipient = owner;
				boolean send = sendTo(door, m);
			}

		}

		else {

			boolean send = sendTo(door, m);
		}

	}

	/**
	 * Rule 3 : receiv JETON.
	 * 
	 * @return void
	 * @param instance
	 *            of RulesMessage class
	 * 
	 */
	public synchronized void receiveJeton(final RulesMessage m) {

		int door = myRouter.getDoorOnMyRoute(m.procRecipient);
		if (procId == m.procRecipient) {

			Logger.write(logFile, "Proc-" + procId
					+ " : Receive TOKEN Jeton :) ");
			token = true;
			notify();
		} else {

			boolean send = sendTo(door, m);
		}
	}

	/**
	 * Rule 4 : receive Form && I send the form if only the next proc is
	 * different of the owner of the form
	 * 
	 * @return void
	 * 
	 * */
	synchronized public void receiveFormMessage(final FormMessage form) {
		Logger.write(logFile, "Proc-" + this.procId + " Recoit form destine a "
				+ form.nextProcId);
		if (form.nextProcId == procId) {

			Logger.write(logFile, "Proc-" + procId + ": Receive form of "
					+ form.procId);
			lanceur.ajouteForme(form.point1, form.point2, form.typeForm);

		} else {

			int door = myRouter.getDoorOnMyRoute(form.nextProcId);
			Logger.write(logFile, "Proc-" + procId + ": Receive form of "
					+ form.procId + " send it to the recipient Proc-"
					+ form.nextProcId + " on door " + next);
			sendTo(door, form);
		}

	}

	// Rules 4 : Release Critical Section
	public void endCriticalSection() {

		SCI = false;
		token = false;
		Logger.write(logFile, "Proc-" + procId + " : " + next);
		if (next != -2 && next != procId) {

			int door = myRouter.getDoorOnMyRoute(next);
			RulesMessage m_jeton = new RulesMessage(MsgType.TOKEN, procId,
					next, 0);
			boolean send = sendTo(door, m_jeton);
			Logger.write(logFile, "Proc-" + procId
					+ " : Release Critical Section and Send TOKEN to Proc-"
					+ next);
			owner = next;
			next = -2;
		}

	}
	/********************************* End Rules of the Algorithm of Naimi Treil *****************************/

	/**
	 * This function allow us to receive a ExtendRoute message
	 * 
	 * @return void
	 * @param final integer value of a door message listener
	 */
	public void recoitExtendRouteMessage(final Door d) {

		ExtendRouteMessage m = (ExtendRouteMessage) receive(d);

		// Je recois les message TABLE, je mets a
		// jour la ou la valeur est -1 et j'incremente la variable complete

		if (m.getMsgType() == MsgType.TABLE) {

			for (int i = 0; i < getNetSize(); i++) {

				if (myRouter.getDoorOnMyRoute(i) == -1
						&& m.getRoutingTable()[i] > -1) {

					myRouter.setDoorToMyRoute(i, d.getNum());
					myRouter.complete++;
				}
			}
		}

		// Je recois les message READY, je mets a
		// jour la ou la valeur est false et j'incremente la variable ready
		// En oute les message READY viens aussi avec la derniere version de la
		// table,
		// je mets à jour la ou la valeur est -1 et j'incremente la variable
		// complete
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

	/**
	 * This function allow us to receive a Route message
	 * 
	 * @return instance of RouteMessage
	 * @param final integer value of a door message listener
	 */
	public RouteMessage recoitRoute(final Door d) {

		RouteMessage rm = (RouteMessage) receive(d);
		return rm;
	}

	// Send message Where Is
	public void sendRouteMessage(	final ExtendRouteMessage mr,
									final int exceptDoor) {

		for (int i = 0; i < getArity(); i++) {

			if (exceptDoor != i) {

				boolean send = this.sendTo(i, mr);
			}

		}
	}

	/********************************** End of Rules of setting the route *******************************************/

	// Access to receive function
	public Message recoit(final Door d) {

		Message m = receive(d);
		return m;
	}

	// Send form
	public void sendForm() {

		Color bg = Color.blue;
		Color fg = Color.red;
		for (int i = 0; i < getNetSize(); i++) {

			if (i != procId) {

				FormMessage form = new FormMessage(MsgType.FORME, procId, i,
						p1, p2, tailleForm, typeForm, bg, fg);
				int door = myRouter.getDoorOnMyRoute(i);
				boolean send = this.sendTo(door, form);
			}
		}

	}

	/**
	 * This function allow us to access to protected sendTo function outside of
	 * the class
	 * 
	 * @return boolean value
	 * @param final integer value of a door message listener
	 * @param instance
	 *            of Message
	 */

	public boolean envoiTo(final int door, final Message message) {

		return sendTo(door, message);
	}

	/**
	 * This function allow to write in the log the routing table of each
	 * processus
	 * 
	 * @return void
	 * 
	 */

	private void writeRoute() {

		String str = "#### \n\nRoute of Proc-" + procId + " ######\n";
		for (int i = 0; i < getNetSize(); i++) {

			str += "Door " + myRouter.getDoorOnMyRoute(i)
					+ " connected to procId-" + i + "\n";
		}
		Logger.write(logFile, str);
	}
}
