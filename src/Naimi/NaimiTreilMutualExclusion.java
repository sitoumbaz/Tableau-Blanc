package Naimi;

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
	public int speed = 4;
	public int netSize = 0;
	public int arity = 0;
	public boolean token = false;
	public boolean SCI = false;

	// just displaying routing table in the log
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


	// logger
	ProcLogger log;

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
	@Override
	public void init() {
		
		procId = getId();
		netSize = getNetSize();
		arity = getArity();
		// Create logger
		log = new ProcLogger(procId, "Naimi");
		
		/* Begin setting up route */
		routing();
		/* End setting up route */
		
		lanceur = new Lanceur("Tableau Blanc Proc" + getId());
		log.logMsg("Proc-" + procId+ " I launch the white board named Tableau Blanc Proc" + procId);
		motTest = new MoteurTest();
		lanceur.start();
		
		messageListener = new MessageListener(this);
		messageListener.start();
		
		//Generateur aleatoire
		Random rand = new Random();
		
		owner = 0;
		if(procId == 0){
			
			token = true;
			owner = -2;
		}
		
		while(true){
			
			// Wait for some time
			int time = (3 + rand.nextInt(10)) * speed * 1000;
			log.logMsg("Proc-" + procId + ":  wait for " + time);
			try {
				Thread.sleep(time);
			} catch (InterruptedException ie) {
				log.logMsg("Proc-" + procId + " : Error" + ie.getMessage());
			}
			drawNewForm();
		}
		
	}
	
	
	public synchronized  void routing(){
		
		myRouter = new MyRouter(getNetSize());
		myRouter.setDoorToMyRoute(getId(), -2);
		
		routing = new RoutingListener(this,log, myRouter);
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
	
	public void drawNewForm(){
		
		motTest.creerForme();
		p1 = motTest.getPoint1();
		p2 = motTest.getPoint2();
		typeForm = motTest.getChoixForme();
		log.logMsg("Proc-" + procId
				+ " : Create form, wait critical section  befor drawing");
		askCriticalSection();
		
		Color bg = Color.blue;
		Color fg = Color.red;
		lanceur.ajouteForme(p1, p2, typeForm);
		sendForm();
		log.logMsg("Proc-" + procId + ":  Critical Section, wait for " + 2500);
		try {
			Thread.sleep(2500);
		} catch (InterruptedException ie) {
			log.logMsg("Proc-" + procId + " : Error" + ie.getMessage());
		}
		endCriticalSection();
		
	}
	
	/********************************** Rules of the Algorithm of Naimi Treil ********************************/
	
	// Rules 1: Ask the critical section
	
	public synchronized void askCriticalSection(){
		
		SCI = true;
		System.out.println("Proc-"+procId+" : Need critical section ");
		if(owner != -2){
			
			RulesMessage m = new RulesMessage(MsgType.REQ,this.procId, owner,0);
			int door = myRouter.getDoorOnMyRoute(owner);
			boolean sent = sendTo(door,m);
			owner = -2;
			System.out.println("Proc-"+procId+" : Send REQ to owner Proc-"+owner);
			while(!token){
				
				try {
					this.wait();
				} catch (InterruptedException e) {e.printStackTrace();}
			}
			
		}
	}
	
	// Rules 2 : Receive REQ Message
	public synchronized void receiveReq(RulesMessage m){
		
		
		int door = myRouter.getDoorOnMyRoute(m.procRecipient);
		if(m.procRecipient == procId){
			System.out.println("Proc-"+procId+" : Receive REQ of Proc-"+m.procId);
			if(owner == -2){
				
				if(SCI){
					
					next = m.procId;
					System.out.println("Proc-"+procId+" : Add Proc-"+m.procId+" as the next Proc to be elected");
				}
				else{
					
					token = false;
					RulesMessage m_jeton = new RulesMessage(MsgType.TOKEN, procId, m.procId,0);
					door = myRouter.getDoorOnMyRoute(m.procId);
					boolean send = sendTo(door,m_jeton);
					System.out.println("Proc-"+procId+" : Send TOKEN to Proc-"+m.procId);
				}
			}
			else{
				
				System.out.println("Proc-"+procId+" : I am not the owner, I send REQ to owner Proc-"+owner);
				door = myRouter.getDoorOnMyRoute(owner);
				boolean send = sendTo(door,m);
			}
			owner = m.procId;
		}
		
		else{
			
			boolean send = sendTo(door,m);
		}
		
		
		
	}
	
	
	// Rules 3 : Receive JETON (TOKEN) Message
	public synchronized void receiveJeton(RulesMessage m){
		
		int door = myRouter.getDoorOnMyRoute(m.procRecipient);
		if(procId == m.procRecipient){
			
			System.out.println("Proc-"+procId+" : Receive TOKEN Jeton :) ");
			token = true;
			notify();
		}
		else{
			
			boolean send = sendTo(door,m);
		}
	}
	
	//Rules 4 : Release Critical Section
	public void endCriticalSection(){
		
		SCI = false;
		token = false;
		System.out.println("Proc-"+procId+" : "+next);
		if(next != -2 && next != procId){
			
			int door = myRouter.getDoorOnMyRoute(next);
			RulesMessage m_jeton = new RulesMessage(MsgType.TOKEN, procId, next,0);
			boolean send = sendTo(door,m_jeton);
			System.out.println("Proc-"+procId+" : Release Critical Section and Send TOKEN to Proc-"+next);
			next = -2;
		}
		
	}
	/********************************* End Rules of the Algorithm of Naimi Treil *****************************/
	
	
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
	
	// Access to receive function of route Message
	public RouteMessage recoitRoute(final Door d) {

		RouteMessage rm = (RouteMessage) receive(d);
		return rm;
	}
	
	// Send message Where Is
	public void sendRouteMessage(	final ExtendRouteMessage mr,final int exceptDoor) {

		for (int i = 0; i < getArity(); i++) {

			if (exceptDoor != i) {

				boolean send = this.sendTo(i, mr);
			}

		}
	}
		
	/********************************** End of Rules of setting the route *******************************************/
	
	//receive Form && I send the form if only the next proc is
	// different of the owner of the form
	synchronized public void receiveFormMessage(final FormMessage form) {
		// TODO Auto-generated method stub
		System.out.println("Proc-"+this.procId+" Recoit form destine a "+form.nextProcId);
		if (form.nextProcId == procId) {
			
			log.logMsg("Proc-" + procId + ": Receive form of " + form.procId);
			lanceur.ajouteForme(form.point1, form.point2, form.typeForm);
			
		} 
		else {

			int door = myRouter.getDoorOnMyRoute(form.nextProcId);
			log.logMsg("Proc-" + procId + ": Receive form of " + form.procId+" send it to the recipient Proc-"+form.nextProcId+" on door "+next);
			sendTo(door, form);
		}

	}
		
	// Access to receive function
	public Message recoit(final Door d) {

		Message m = receive(d);
		return m;
	}
	
	// Send form 
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
	
	public boolean envoiTo(int door, Message message){
		
		return sendTo(door,message);
	}
}
