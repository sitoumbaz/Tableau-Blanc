package RicartAggrawala;

import java.awt.Color;
import java.awt.Point;
import java.util.HashMap;
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

public class RicartAggrawalaMutualExclusion extends Algorithm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2488961944192749471L;

	// Router
	public MyRouter myRouter;
	public int next = 0;
	public int arity = 0;
	public int netSize = 0;
	//
	public int speed = 4;

	// just for managin displaying routing table in the log
	public boolean iAmReady = false;

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

	String logFile = null;
	
	// Tableau blanc
	private Lanceur lanceur;
	private Point p1 = null;
	private Point p2 = null;
	private float tailleForm;
	private int typeForm;
	
	// form Generator
	MoteurTest motTest;
	
	MessageListener messageListener = null;
	RoutingListener routing = null;

	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return new RicartAggrawalaMutualExclusion();
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		Nrel = getNetSize();
		netSize = getNetSize();
		arity = getArity();
		procId = getId();
		int time = 0;
		logFile = "ricart_proc-"+procId;
		
		//Generateur aleatoire
		Random rand = new Random();
		
		/* Begin setting up route */
		
		routing();
		/* End setting up route */
		
		
		Logger.write(logFile,"Proc-"+procId+" I am ready to begin "+ myRouter.ready);
		lanceur = new Lanceur("Tableau Blanc Proc" + getId());
		Logger.write(logFile,"Proc-"+procId+" I launch the white board named Tableau Blanc Proc"+procId);
		motTest = new MoteurTest();
		lanceur.start();
		
		
		messageListener = new MessageListener(this);
		messageListener.start();
		Logger.write(logFile,"Proc-" + procId + " I start my message Listerner");
		if (procId == 0) {
	
			System.out.println("Proc-" + procId + ": Try to access critical section");
			// Try to access critical section
			R = true;
			this.askCriticalSection();

			// Release critical use
			R = false;
			this.endCriticalSection();

		}
		
		while (true) {

			// Wait for some time
			time = (4 + rand.nextInt(10)) * 1000;
			Logger.write(logFile,"Proc-" + procId + ":  wait for " + time);
			try {
				System.out.println("Proc-" + procId + ": time "+time);
				Thread.sleep(time);
			} catch (InterruptedException ie) {
				Logger.write(logFile,"Proc-" + procId + " : Error" + ie.getMessage());
			}
			
			System.out.println("Proc-" + procId + ": Try to access critical section");
			// Try to access critical section
			R = true;
			this.askCriticalSection();
			
			// Release critical use
			R = false;
			this.endCriticalSection();	
		

		}
	}
	// --------------------
	// Rules
	// -------------------
	
	
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
		System.out.println("YEP");
		
		try {
			routing.sleep(1000);
			routing.interrupt();
		
		} catch (InterruptedException e) {e.printStackTrace();}
		
	}

	

	// Access to receive function
	public RouteMessage recoitRoute(final Door d) {

		RouteMessage rm = (RouteMessage) receive(d);
		return rm;
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

				boolean send = this.sendTo(i, mr);
			}

		}
	}

	/* Rule 1 : processus ask for critical section */
	public synchronized  void askCriticalSection(){
		
		HSC = H + 1;
		Nrel = getNetSize()-1;
		System.out.println("Proc-"+procId+" Je suis ici  getNetSize() = "+getNetSize());
		for(int i=0; i<getNetSize(); i++){
			
			if(i != procId){
				
				int door = myRouter.getDoorOnMyRoute(i);
				RulesMessage ms = new RulesMessage(MsgType.REQ, procId,i,HSC);
				boolean send = sendTo(door, ms);
				System.out.println("Proc-" + procId + ": Send REQ to "+ms.procRecipient+" on door "+door);
			}
		}
		
		motTest.creerForme();
		p1 = motTest.getPoint1();
		p2 = motTest.getPoint2();
		typeForm = motTest.getChoixForme();
		Logger.write(logFile,"Proc-"+procId+" : Create form, wait critical section  befor drawing");
		
		while(Nrel != 0){
			try {
				this.wait();
			} catch (InterruptedException e) {e.printStackTrace();}
		}
		
	}
	
	/* Rules 2 : */
	public synchronized void receiveReq(RulesMessage ms){
		
		int door = myRouter.getDoorOnMyRoute(ms.procRecipient);
		if(ms.procRecipient == procId){
			
			
			door = myRouter.getDoorOnMyRoute(ms.procId);
			H = Math.max(H, ms.H) + 1;
			System.out.println("Proc-"+procId+" Receive REQ of proc-"+ms.procId+" on door "+door+"");
			if(R && ((HSC < ms.H) || ((HSC == ms.H) && procId < ms.procId))){
				
				System.out.println("Proc-"+procId+" Adding proc-"+ms.procId+" in my queue list");
				X.put(ms.procId,door);
			}
			else{
				
				System.out.println("Proc-"+procId+" I can immediatly send the REL to proc-"+ms.procId+"");
				RulesMessage mrel = new RulesMessage(MsgType.REL, procId,ms.procId,0);
				this.sendTo(door, mrel);
			}
			
		}
		else{
			
			this.sendTo(door, ms);
		}
		
	}

	/* Rule 3 : */
	/* le processus reçoit le message rel() de j */
	public synchronized void receiveRel(final RulesMessage rm) {

		if (rm.procRecipient == procId) {
			Nrel--;
			if(Nrel == 0){
				
				Color bg = Color.blue;
				Color fg = Color.red;
				lanceur.ajouteForme(p1, p2, typeForm);
				for(int i=0; i<getNetSize(); i++){
					
					if(i != procId){
						
						FormMessage form = new FormMessage(MsgType.FORME, procId,i, p1, p2, tailleForm, typeForm, bg, fg);
						int door = myRouter.getDoorOnMyRoute(i);
						boolean send = this.sendTo(door, form);	
					}
				}
				notify();
			}
		} else {
			
			next = this.myRouter.getDoorOnMyRoute(rm.procRecipient);
			Logger.write(logFile,"proc-" + procId
					+ " : Receive REL, do not need it, I forward it to "
					+ rm.procRecipient + " on door " + next);
			boolean sent = this.sendTo(next, rm);
		}
	}

	/* Rule 4 */
	void endCriticalSection() {

		
		System.out.println("Proc-"+procId+" End critical section");
		if(X.size() == 0){
			
			System.out.println("Proc-"+procId+" No message received for asking critical section");
			return;
		}
		
		for (int i = 0; i < getNetSize(); i++) {
			
			
			if(X.containsKey(i)){
				RulesMessage mrel = new RulesMessage(MsgType.REL, procId,i,0);
				int door = this.myRouter.getDoorOnMyRoute(i);
				sendTo(door, mrel);
				System.out.println("Proc-"+procId+" Send REL to "+mrel.procRecipient+" on door "+door);
				X.remove(i);
			}
		}
		
	}
	
	public synchronized void receiveFormMessage(final FormMessage form) {
		// TODO Auto-generated method stub
		System.out.println("Proc-"+this.procId+" Recoit form destine a "+form.nextProcId);
		if (form.nextProcId == procId) {
			
			Logger.write(logFile,"Proc-" + procId + ": Receive form of " + form.procId);
			lanceur.ajouteForme(form.point1, form.point2, form.typeForm);
			
		} else {

			next = myRouter.getDoorOnMyRoute(form.nextProcId);
			Logger.write(logFile,"Proc-" + procId + ": Receive form of " + form.procId+" send it to the recipient Proc-"+form.nextProcId+" on door "+next);
			sendTo(next, form);
		}

	}
	// Access to receive function
	public synchronized Message recoit(final Door d) {

		Message m = receive(d);
		return m;
	}
	
	public boolean envoiTo(int door, Message message){
		
		return sendTo(door,message);
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
		Logger.write(logFile,"procId-" + procId + ": " + state);
	}
}
