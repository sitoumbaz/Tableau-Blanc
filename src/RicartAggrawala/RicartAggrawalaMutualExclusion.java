package RicartAggrawala;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
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
	public String strRoute = null;
	
	public int next = 0;
	public int arity = 0;
	public int netSize = 0;
	//
	public int speed = 2;

	// just for managin displaying routing table in the log
	public boolean iAmReady = false;

	private int H; /* estampille locale */
	private int HSC; /* estampille de demande de section critique */
	private boolean R; /* Bool��en indiquand si un processus est
						   * demandeur de section critique
								 */

	// ensemble des processus dont l'envoi de REL est differ��.
	// <procId>
	private ArrayList<Integer> X = null;
	Integer Nrel; // Nombre des REL attendu
	Integer V; //Nombre des voisins d'unprocessus 
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
		X = new ArrayList<Integer>();
		netSize = getNetSize();
		arity = getArity();
		procId = getId();
		logFile = "ricart_proc-"+procId;
		Nrel = 0;
		V = this.getNetSize()-1;
		HSC = 0;
		H = 0; 
		R = false;
		
		//Generateur aleatoire
		Random rand = new Random();
		
		/* Begin setting up route */
		
		routing();
		/* End setting up route */
		
		try {
			Thread.sleep(1500);
		} catch (InterruptedException ie) {
			Logger.write(logFile,"Proc-" + procId + " : Error" + ie.getMessage());
		}
		
		
		lanceur = new Lanceur("Tableau Blanc Proc" + getId());
		lanceur.start();
		
		
		
		Logger.write(logFile,"Proc-"+procId+" I launch the white board named Tableau Blanc Proc"+procId);
		motTest = new MoteurTest();
		
		messageListener = new MessageListener(this);
		messageListener.start();
		Logger.write(logFile,"Proc-" + procId + " I start my message Listerner");
			
		while (true) {

				// Wait for some time
			    int time = (3 + rand.nextInt(10)) * speed * 1000;
				try{
					
					Logger.write(logFile,"Proc-" + procId + ":  wait for " + time);
					Thread.sleep(time);
				}
				catch (InterruptedException ie) {
					Logger.write(logFile,"Proc-" + procId + " : Error" + ie.getMessage());
				}
				drawNewForm();
			
			}
		
	}
	
	/**
	 * Synchronized function which allow to set up 
	 * the routing table of each process
	 * 
	 * @return void
	 * 
	 */
	
	public synchronized void routing(){
		
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
			routing.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	/**
	 * Function which allow to simulate the drawing action on the white board
	 * @Return void
	 * 
	 */
	
	public void drawNewForm(){
		
		motTest.creerForme();
		p1 = motTest.getPoint1();
		p2 = motTest.getPoint2();
		typeForm = motTest.getChoixForme();
		for(int i=0; i<getNetSize(); i++){
			if(i != procId){
				
				int door = myRouter.getDoorOnMyRoute(i);
				RulesMessage ms = new RulesMessage(MsgType.REQ, procId,i,HSC);
				boolean send = sendTo(door, ms);
				Logger.write(logFile,"Proc-" + procId + ": Send REQ to "+ms.procRecipient+" on door "+door);
			}
		}
		Logger.write(logFile,"Proc-" + procId+ " : Create form, wait critical section  befor drawing");
		askCriticalSection();
		Logger.write(logFile,"Proc-" + procId + ":  Enter the Critical Section, draw form and publish");
		lanceur.ajouteForme(p1, p2, typeForm);
		sendForm();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ie) {
			Logger.write(logFile,"Proc-" + procId + " : Error" + ie.getMessage());
		}
		endCriticalSection();
		
	}
	

	/**************************************** Rules of Ricart Agrawalla Algorithme **************************/
	
	/**
	 * Rule 1 : ask for critical section
	 * @return void 
	 * 
	 */
	public synchronized  void askCriticalSection(){
		
		Nrel = V;
		R = true;
		HSC = H + 1;
		while(this.Nrel != 0){
			
			try {
				System.out.println("procId-"+procId+" WAITING NREL = "+Nrel);
				this.wait();
				System.out.println("procId-"+procId+" OUI HALLO NREL = "+Nrel);
			} catch (InterruptedException e) {e.printStackTrace();}
		}
		
		System.out.println("procId-"+procId+" BYE BYE GET NOTIFY NREL = "+Nrel);
		
	}
	
	/**
	 * Rule 2 : receiv req send
	 * @return void 
	 * @param instance of RulesMessage class
	 * 
	 */
	public synchronized void receiveReq(RulesMessage ms){
		
		int door = myRouter.getDoorOnMyRoute(ms.procRecipient);
		if(ms.procRecipient == procId){
						
			door = myRouter.getDoorOnMyRoute(ms.procId);
			H = Math.max(H, ms.H) + 1;
			Logger.write(logFile,"Proc-"+procId+" Receive REQ of proc-"+ms.procId+" on door "+door+"");
			if(R && ((HSC < ms.H) || ((HSC == ms.H) && procId < ms.procId))){
				
				Logger.write(logFile,"Proc-"+procId+" Adding proc-"+ms.procId+" in my queue list");
				X.add(ms.procId);
			}
			else{
				
				Logger.write(logFile,"Proc-"+procId+" I can immediatly send the REL to proc-"+ms.procId+"");
				RulesMessage mrel = new RulesMessage(MsgType.REL, procId,ms.procId,0);
				door = myRouter.getDoorOnMyRoute(ms.procId);
				this.sendTo(door, mrel);
			}
			
		}
		else{
			
			this.sendTo(door, ms);
		}
		
	}

	/**
	 * Rule 3 : receiv REL.
	 * @return void 
	 * @param instance of RulesMessage class
	 * 
	 */
	public  synchronized void receiveRel(final RulesMessage rm) {
		
		
		if (rm.procRecipient == procId) {
		    
			    System.out.println("procId-"+procId+" SEND NOTIFY NREL = "+Nrel);
				this.Nrel--;
				this.notify();

		} 
		else {
			
			next = this.myRouter.getDoorOnMyRoute(rm.procRecipient);
			Logger.write(logFile,"proc-" + procId+":Receive REL, do not need it, I forward it to "+ rm.procRecipient + " on door " + next);
			boolean sent = this.sendTo(next, rm);
		}
	}

	/**
	 * Rule 4 : release critical section.
	 * @return void 
	 * 
	 */
	void endCriticalSection() {
		
		R = false;
		Logger.write(logFile,"Proc-"+procId+" : Release Critical Section");
		if(X.size() == 0){
			
			Logger.write(logFile,"Proc-"+procId+" No message received for asking critical section");
			return;
		}
		
		for (int i = X.size()-1; i >= 0 ; i--) {
			
			int proc = X.get(i);
			RulesMessage mrel = new RulesMessage(MsgType.REL, procId,proc,0);
			int door = this.myRouter.getDoorOnMyRoute(proc);
			sendTo(door, mrel);
			Logger.write(logFile,"Proc-"+procId+" Send REL to "+mrel.procRecipient+" on door "+door);
			
		}
		X.clear();
		
	}
	
	/**
	 *  Rule 5 : receive Form && I send the form if only the next process is
	 *  different of the owner of the form
	 *  @return void
	 *  @param final instace of FormMessage class
	 * 
	 * */
	
	public synchronized void receiveFormMessage(final FormMessage form) {
		// TODO Auto-generated method stub
		if (form.nextProcId == procId) {
			
			Logger.write(logFile,"Proc-" + procId + ": Receive form of " + form.procId);
			lanceur.ajouteForme(form.point1, form.point2, form.typeForm);
			
		} else {

			next = myRouter.getDoorOnMyRoute(form.nextProcId);
			Logger.write(logFile,"Proc-" + procId + ": Receive form of " + form.procId+" send it to the recipient Proc-"+form.nextProcId+" on door "+next);
			sendTo(next, form);
			
		}

	}
	
	/*********************************** end of rules of Ricart Agrawalla Algorithme ***********************/
	
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

				if (myRouter.getDoorOnMyRoute(i) == -1 & m.getRoutingTable()[i] > -1) {

					myRouter.setDoorToMyRoute(i, d.getNum());
					myRouter.complete++;
				}
			}
		}

		if (m.getMsgType() == MsgType.READY) {

			for (int i = 0; i < getNetSize(); i++) {

				if (myRouter.getDoorOnMyRoute(i) == -1 & m.getRoutingTable()[i] > -1) {

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
	 * This function allow us to receive a ExtendRoute message 
	 * @return void
	 * @param final integer value of a door message listener
	 */ 
	public void recoitExtendRouteMessage(ExtendRouteMessage m, final int  door) {

		if (m.getMsgType() == MsgType.TABLE) {

			for (int i = 0; i < getNetSize(); i++) {

				if (myRouter.getDoorOnMyRoute(i) == -1 & m.getRoutingTable()[i] > -1) {

					myRouter.setDoorToMyRoute(i, door);
					myRouter.complete++;
				}
			}
		}

		if (m.getMsgType() == MsgType.READY) {

			for (int i = 0; i < getNetSize(); i++) {

				if (myRouter.getDoorOnMyRoute(i) == -1 & m.getRoutingTable()[i] > -1) {

					myRouter.setDoorToMyRoute(i, door);
					myRouter.complete++;
				}
			}
			if (!myRouter.getStateOfProc(m.getMyProcId())) {

				myRouter.ProcBecomeReady(m.getMyProcId(), true);
				myRouter.ready++;
				sendRouteMessage(m, door);
			}
		}
	}
	
	/**
	 *  Function for routing process
	 *  @return void
	 *  @param final instance of ExtendRouteMessage
	 *  @param final integer value of a door message listener
	 */
	public void sendRouteMessage(	final ExtendRouteMessage mr,final int exceptDoor) {

		for (int i = 0; i < getArity(); i++) {

			if (exceptDoor != i) {

				boolean send = this.sendTo(i, mr);
			}

		}
	}
	
	/**
	 * This function allow us to access to protected 
	 * receive function outside of the class
	 * @return instance of Message
	 * @param final integer value of a door message listener
	 */ 
	public synchronized Message recoit(final Door d) {

		Message m = receive(d);
		return m;
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
	 *  Function which allow us to send form to all process except the initiator
	 *  @return void
	 */ 
	public void sendForm(){
		
		Color bg = Color.blue;
		Color fg = Color.red;
		for(int i=0; i<getNetSize(); i++){
			
			if(i != procId){
				
				FormMessage form = new FormMessage(MsgType.FORME, procId,i, p1, p2, tailleForm, typeForm, bg, fg);
				int door = myRouter.getDoorOnMyRoute(i);
				boolean send = this.sendTo(door, form);	
			}
		}
		
	}
	
	/**
	 * This function allow to write in the log the routing table of each processus
	 * @return void
	 * 
	 */
	private void writeRoute(){
		
		String str = "\n\n#### Route of Proc-" + procId + " ######\n";
		for (int i = 0; i < getNetSize(); i++) {

			str += "Door " + myRouter.getDoorOnMyRoute(i)+ " connected to procId-" + i + "\n";
		}
		Logger.write(logFile,str);
	}
	
}
