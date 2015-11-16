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
import Listener.MessageListener;
import Listener.RoutingListener;
import Message.ExtendRouteMessage;
import Message.FormMessage;
import Message.MsgType;
import Message.RouteMessage;
import Message.TokenMessage;
import Router.MyRouter;

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
	public String strRoute = null;

	// just for managin displaying routing table in the log
	public boolean iAmReady = false;
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
	String logFile = null;

	// Critical section thread
	MessageListener messageListener = null;
	RoutingListener routing = null; 

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
		logFile = "lalan_proc-"+procId;

		Random rand = new Random();
		netSize = getNetSize();
		arity = getArity();

		// Init routeMap
		routing();

		lanceur = new Lanceur("Tableau Blanc Proc" + getId());
		Logger.write(logFile,"Proc-" + procId
				+ " I launch the white board named Tableau Blanc Proc" + procId);
		motTest = new MoteurTest();
		lanceur.start();

		messageListener = new MessageListener(this);
		messageListener.start();
		Logger.write(logFile,"Proc-" + procId + " I start my message Listerner");

		if (procId == 0) {

			token = false;
			TokenMessage tm = new TokenMessage(MsgType.TOKEN, nextProcId);
			next = myRouter.getDoorOnMyRoute(nextProcId);
			boolean sent = sendTo(next, tm);
			if (!sent) {

				Logger.write(logFile,"Proc-" + procId + " Unable to start process");
			}
			Logger.write(logFile,"Proc-" + procId
					+ " I start the prcess by sending token to Proc-"
					+ nextProcId + " on door " + next);

		}

		while (true) {

			// Wait for some time
			int time = (3 + rand.nextInt(10)) * speed * 1000;
			try{
				
				Thread.sleep(time);
			} 
			catch (InterruptedException ie) { Logger.write(logFile,"Proc-" + procId + " : Error" + ie.getMessage());}
			drawNewForm();

		}

	}
	

	/**
	 * Synchronized function which allow to set up 
	 * the routing table of each processus
	 * @return void
	 */
	public synchronized  void routing(){
		
		myRouter = new MyRouter(getNetSize());
		myRouter.setDoorToMyRoute(getId(), -2);
		
		routing = new RoutingListener(this, myRouter);
		routing.start();
		while(!iAmReady){
			
			try{
				this.wait();
			} catch (InterruptedException e) {e.printStackTrace();}
		}
		writeRoute();
		try {
			
			routing.sleep(1000);
			routing.interrupt();
		
		} catch (InterruptedException e) {e.printStackTrace();}
		
		
	}

	/**
	 * Function which allow to simulate the drawing action on the white board
	 * @Return void
	 */
	public void drawNewForm(){
		
		//Create form to draw
		motTest.creerForme();
		p1 = motTest.getPoint1();
		p2 = motTest.getPoint2();
		typeForm = motTest.getChoixForme();
		Logger.write(logFile,"Proc-" + procId+ " : Create form, wait critical section  befor drawing");
		
		// Try to access critical section
		waitForCritical = true;
		askForCritical();
		
		Logger.write(logFile,"Proc-" + procId + ":  Enter the Critical Section, draw form and publish");
		lanceur.ajouteForme(p1, p2, typeForm);		
		// Access critical
		waitForCritical = false;
		inCritical = true;
		Color bg = Color.blue;
		Color fg = Color.red;
		
		//Create message form and send it to the next processus
		FormMessage form = new FormMessage(MsgType.FORME, procId,getNextProcId(), p1, p2, tailleForm, typeForm, bg, fg);
		next = myRouter.getDoorOnMyRoute(getNextProcId());
		boolean sent = sendTo(next, form);
		
		//Wait a bit befor releasing the critical section
		try {
			Thread.sleep(1000);
		} 
		catch (InterruptedException ie) {Logger.write(logFile,"Proc-" + procId + " : Error" + ie.getMessage());}
		
		// Release critical use
		Logger.write(logFile,"Proc-" + procId + ":  Release the Critical Section");
		inCritical = false;
		endCriticalUse();
	}

	/**
	 * Rule 1 : ask for critical section
	 * @return void 
	 * 
	 */
	synchronized void askForCritical() {

		//while the token is false, I stay awaiting a notify signal
		while (!token) {

			try {
				this.wait();
			} 
			catch (InterruptedException ie) {}
		}
	}
	
	/**
	 *  Rule 2 : receive TOKEN
	 *  @return void
	 * 
	 */
	public synchronized void receiveTOKEN(final TokenMessage tm) {

		if (tm.idProc == procId) {
			
			//If the token is destinated to me
			next = myRouter.getDoorOnMyRoute(getNextProcId());
			
			//if i'am waiting the critical section, my token become true and I notify the sleeping processus
			if (waitForCritical) {

				Logger.write(logFile,"Proc-" + procId + ":  Receive token");
				token = true;
				notify();

			}
			else {

				Logger.write(logFile,"proc-" + procId+ " : Receive token, do not need it, I forward it to "+ getNextProcId() + " on door " + next);
				tm.idProc = getNextProcId();
				boolean sent = sendTo(next, tm);
			}

		} 
		else{

			next = myRouter.getDoorOnMyRoute(tm.idProc);
            Logger.write(logFile,"proc-" + procId+ " : Receive token but it is not mine");
			boolean sent = sendTo(next, tm);
		}
	}

	/**
	 *  Rule 5 : receive Form && I send the form if only the next proc is
	 *  different of the owner of the form
	 *  @return void
	 * 
	 * */
	synchronized public void receiveFormMessage(final FormMessage form) {
		// TODO Auto-generated method stub
		
		if (form.nextProcId == procId) {

			lanceur.ajouteForme(form.point1, form.point2, form.typeForm);
			Logger.write(logFile,"Proc-" + procId + ": Receive form of " + form.procId+" draw on my white board");
			if (getNextProcId() > form.nextProcId && getNextProcId() != form.procId) {
				form.nextProcId = getNextProcId();
				next = myRouter.getDoorOnMyRoute(form.nextProcId);
				boolean sent = sendTo(next, form);
				
			} else{

				if (getNextProcId() != form.procId) {

					form.nextProcId = getNextProcId();
					next = myRouter.getDoorOnMyRoute(form.nextProcId);
					boolean sent = sendTo(next, form);
				}
			}
		} else {

			Logger.write(logFile,"Proc-" + procId + ": Receive form of " + form.procId+" let it pass");
			next = myRouter.getDoorOnMyRoute(form.nextProcId);
			boolean sent = sendTo(next, form);
		}

	}

	/**
	 * Release critical section
	 * @return void 
	 * 
	 */
	void endCriticalUse() {

		next = myRouter.getDoorOnMyRoute(getNextProcId());
		token = false;
		TokenMessage tm = new TokenMessage(MsgType.TOKEN, getNextProcId());
		boolean sent = sendTo(next, tm);
		Logger.write(logFile,"proc-" + procId
				+ " : Release Critical Section send token to " + getNextProcId()
				+ " on door " + next);
	}

	/**
	 * Using a ring protocol, in ascending direction, 
	 * Use this function in order to get the next processus to communicate with
	 * @return integer value
	 */
	public int getNextProcId() {

		nextProcId = procId + 1;
		if (getNetSize() == procId + 1) {

			nextProcId = 0;
		}
		return nextProcId;
	}

	/**
	 *  Function for routing processus
	 *  @return void
	 *  @param final instance of ExtendRouteMessage
	 *  @param final integer value of a door message listener
	 */
	public void sendRouteMessage(	final ExtendRouteMessage mr,
									final int exceptDoor) {

		for (int i = 0; i < getArity(); i++) {

			if (exceptDoor != i) {

				boolean send = sendTo(i, mr);
			}

		}
	}

	/**
	 * This function allow us to access to protected 
	 * receive function outside of the class
	 * @return instance of Message
	 * @param final integer value of a door message listener
	 */ 
	
	public Message recoit(final Door d) {

		Message m = receive(d);
		return m;
	}

	
	/**
	 * This function allow us to receive a token message 
	 * @return instance of TokenMessage
	 * @param final integer value of a door message listener
	 */ 
	public TokenMessage recoitToken(final Door d) {

		TokenMessage sm = (TokenMessage) receive(d);
		return sm;
	}

	/**
	 * This function allow us to receive a form message 
	 * @return instance of FormMessage
	 * @param final integer value of a door message listener
	 */ 
	public FormMessage recoitForme(final Door d) {

		FormMessage sm = (FormMessage) receive(d);
		return sm;
	}

	/**
	 * This function allow us to receive a Route message 
	 * @return instance of RouteMessage
	 * @param final integer value of a door message listener
	 */ 
	public RouteMessage recoitRoute(final Door d) {

		RouteMessage rm = (RouteMessage) receive(d);
		return rm;
	}

	
	/**
	 * This function allow us to receive a ExtendRoute message 
	 * @return void
	 * @param final integer value of a door message listener
	 */ 
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
	
	/**
	 * This function allow us to access to protected 
	 * sendTo function outside of the class
	 * @return boolean value
	 * @param final integer value of a door message listener
	 * @param instance of Message
	 */
	
	public boolean envoiTo(int door, Message message){
		
		return sendTo(door,message);
	}
	
	
	/**
	 * This function allow to write in the log the routing table of each processus
	 * @return void
	 * 
	 */
	
	private void writeRoute(){
		
		String str = "#### Route of Proc-" + procId + " ######\n";
		for (int i = 0; i < getNetSize(); i++) {

			str += "Door " + myRouter.getDoorOnMyRoute(i)+ " connected to procId-" + i + "\n";
		}
		Logger.write(logFile,str);
	}


}
