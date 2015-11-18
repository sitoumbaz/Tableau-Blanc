package Listener;
import Lelann.LelannMutualExclusion;
import Message.ExtendRouteMessage;
import Message.MsgType;
import Message.RouteMessage;
import Naimi.NaimiTreilMutualExclusion;
import RicartAggrawala.RicartAggrawalaMutualExclusion;
import Router.MyRouter;
import visidia.simulation.process.algorithm.Algorithm;
import visidia.simulation.process.messages.Door;

// Reception thread
public class RoutingListener extends Thread {
	
	/** Init Instances of principal Algorithme used in the project*/
	LelannMutualExclusion algo1;
	RicartAggrawalaMutualExclusion algo2;
	NaimiTreilMutualExclusion algo3;
	
	/** Init Instances of MyRouter class */
	MyRouter myRouter;
	
	
	/**
	 * Constructor
	 * @return void 
	 * @param instance of Algorithm class
	 * @param instance of MyRouter class
	 * 
	 */
	public RoutingListener(Algorithm a, MyRouter myRouter) {
		
		this.myRouter = myRouter;
		
		if(a instanceof LelannMutualExclusion){
			
			algo1 = (LelannMutualExclusion)a;
		
		}
		
		if(a instanceof RicartAggrawalaMutualExclusion){
					
			algo2 = (RicartAggrawalaMutualExclusion)a;
		}
		
		if(a instanceof NaimiTreilMutualExclusion){
			
			algo3 = (NaimiTreilMutualExclusion)a;
		}

	}

	@Override
	public void run() {

		Door d = new Door();

		if(algo1 instanceof LelannMutualExclusion){
				
			synchronized(algo1){
				
				listenLelanRouting(algo1, d);
			}
			
		}
		
		else if(algo2 instanceof RicartAggrawalaMutualExclusion){
			
			synchronized(algo2){
				
				listenRicartAggrawalaRouting(algo2, d);
			}
			
		}
		
		else if(algo3 instanceof NaimiTreilMutualExclusion){
			
			synchronized(algo3){
				
				listenerNaimiTreilRouting(algo3, d);
			}
			
		}
	}
	
	
	/**
	 * Function which allow managing Message Rules of Routing table process
	 * @return void 
	 * @param instance of RicartAggrawalaMutualExclusion class
	 * @param instance of Door class
	 * 
	 */
	
	private synchronized void listenRicartAggrawalaRouting(RicartAggrawalaMutualExclusion algorithme, Door d){
		
		// Send message Route
		for (int i = 0; i < algorithme.arity; i++) {

			RouteMessage mr = new RouteMessage(MsgType.ROUTE, algorithme.procId);
			boolean send = algorithme.envoiTo(i, mr);
		}

		// Receive all message Route
		int i = 0;
		while (i < algorithme.arity) {

			Door door = new Door();
			RouteMessage mr = algorithme.recoitRoute(door);
			myRouter.setDoorToMyRoute(mr.getProcId(), door.getNum());
			myRouter.complete++;
			i++;
		}
		
		// Stay awaiting while my routing table is not complete
		while (myRouter.complete < algorithme.netSize) {

			// Send my Routing table to my neighbors
			if (algorithme.arity > 1) {

				ExtendRouteMessage mr = new ExtendRouteMessage(MsgType.TABLE,algorithme.procId, algorithme.procId);
				mr.setRoutingTable(myRouter.getMyRoute());
				algorithme.sendRouteMessage(mr, -1);
			}

			Door door = new Door();
			algorithme.recoitExtendRouteMessage(door);
		}
		myRouter.ProcBecomeReady(algorithme.procId, true);
		
		
		ExtendRouteMessage mr = new ExtendRouteMessage(MsgType.READY, algorithme.procId,algorithme.procId);
		mr.setRoutingTable(myRouter.getMyRoute());
		algorithme.sendRouteMessage(mr, -1);

		// Stay awaiting while my routing table is not complete
		while (myRouter.ready < algorithme.netSize) {

			Door door = new Door();
			algorithme.recoitExtendRouteMessage(door);
		}
		algorithme.iAmReady = true;
		algorithme.notify();
	}
	
	
	/**
	 * Function which allow managing Message Rules of Routing table process
	 * @return void 
	 * @param instance of LelannMutualExclusion class
	 * @param instance of Door class
	 * 
	 */
	private synchronized void listenLelanRouting(LelannMutualExclusion algorithme, Door d){
		
		
		/** Send ROUTE message to all my neighbors */
		for (int i = 0; i < algo1.arity; i++) {

			RouteMessage mr = new RouteMessage(MsgType.ROUTE, algorithme.procId);
			boolean send = algorithme.envoiTo(i, mr);
		}
		
		/** Wait receiving also ROUTE message from all my neighbors */
		int i = 0;
		while (i < algorithme.arity) {

			Door door = new Door();
			RouteMessage mr = algorithme.recoitRoute(door);
			
			/** For each ROUTE message receiving, I add in my table proc-Id and the door corresponding */
			myRouter.setDoorToMyRoute(mr.getProcId(), door.getNum());
			myRouter.complete++;
			i++;
		}
		
		// Stay awaiting while my routing table is not complete
		while (myRouter.complete < algorithme.netSize) {

			// Send my Routing table to my neighbors
			if (algorithme.arity > 1) {

				ExtendRouteMessage mr = new ExtendRouteMessage(MsgType.TABLE,algorithme.procId, algorithme.procId);
				mr.setRoutingTable(myRouter.getMyRoute());
				algorithme.sendRouteMessage(mr, -1);
			}

			Door door = new Door();
			algorithme.recoitExtendRouteMessage(door);
		}
		myRouter.ProcBecomeReady(algorithme.procId, true);
		
		
		ExtendRouteMessage mr = new ExtendRouteMessage(MsgType.READY, algorithme.procId,algorithme.procId);
		mr.setRoutingTable(myRouter.getMyRoute());
		algorithme.sendRouteMessage(mr, -1);

		// Stay awaiting while my routing table is not complete
		while (myRouter.ready < algorithme.netSize) {

			Door door = new Door();
			algorithme.recoitExtendRouteMessage(door);
		}
		algorithme.iAmReady = true;
		algorithme.notify();
	}
	
	
	/**
	 * Function which allow managing Message Rules of Routing table process
	 * @return void 
	 * @param instance of NaimiTreilMutualExclusion class
	 * @param instance of Door class
	 * 
	 */
	private synchronized void listenerNaimiTreilRouting(NaimiTreilMutualExclusion algorithme, Door d){
		
		//Envoi message ROUTE a tous mes voisins
		for (int i = 0; i < algorithme.arity; i++) {

			RouteMessage mr = new RouteMessage(MsgType.ROUTE, algorithme.procId);
			boolean send = algorithme.envoiTo(i, mr);
		}

		int i = 0;
		//Envoi je recois tous les messages ROUTE provenant de mes voisins
		while (i < algorithme.arity) {

			Door door = new Door();
			RouteMessage mr = algorithme.recoitRoute(door);
			myRouter.setDoorToMyRoute(mr.getProcId(), door.getNum());
			myRouter.complete++;
			i++;
		}
		
		// Stay awaiting while my routing table is not complete
		while (myRouter.complete < algorithme.netSize) {

			// Send my Routing table to my neighbors
			if (algorithme.arity > 1) {

				ExtendRouteMessage mr = new ExtendRouteMessage(MsgType.TABLE,algorithme.procId, algorithme.procId);
				mr.setRoutingTable(myRouter.getMyRoute());
				algorithme.sendRouteMessage(mr, -1);
			}

			Door door = new Door();
			// Je recois tous les messages TABLE ou READY des mes voisins
			algorithme.recoitExtendRouteMessage(door);
		}
		myRouter.ProcBecomeReady(algorithme.procId, true);
		
		ExtendRouteMessage mr = new ExtendRouteMessage(MsgType.READY, algorithme.procId,algorithme.procId);
		mr.setRoutingTable(myRouter.getMyRoute());
		algorithme.sendRouteMessage(mr, -1);

		// Stay awaiting while my routing table is not complete
		while (myRouter.ready < algorithme.netSize) {

			Door door = new Door();
			algorithme.recoitExtendRouteMessage(door);
		}
		algorithme.iAmReady = true;
		algorithme.notify();
	}
	
}
