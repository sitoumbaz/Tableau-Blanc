package Listener;

import visidia.simulation.process.algorithm.Algorithm;
import visidia.simulation.process.messages.Door;
import visidia.simulation.process.messages.Message;
import Lelann.LelannMutualExclusion;
<<<<<<< HEAD
import Message.ExtendRouteMessage;
import Message.FormMessage;
import Message.RulesMessage;
=======
import Message.FormMessage;
>>>>>>> 67440165a6b9183bbefc0481318466b53ea1452d
import Message.TokenMessage;
import Naimi.NaimiTreilMutualExclusion;
import RicartAggrawala.RicartAggrawalaMutualExclusion;
<<<<<<< HEAD
import visidia.simulation.process.algorithm.Algorithm;
import visidia.simulation.process.messages.Door;
import visidia.simulation.process.messages.Message;
=======
import Router.ExtendRouteMessage;
>>>>>>> 67440165a6b9183bbefc0481318466b53ea1452d

// Reception thread
public class MessageListener extends Thread {

	Algorithm algo;
	LelannMutualExclusion algo1;
	RicartAggrawalaMutualExclusion algo2;
	NaimiTreilMutualExclusion algo3;

	public MessageListener(final Algorithm a) {

		algo = a;
		if (a instanceof LelannMutualExclusion) {

			algo1 = (LelannMutualExclusion) a;
		}

		else if (a instanceof RicartAggrawalaMutualExclusion) {

			algo2 = (RicartAggrawalaMutualExclusion) a;
		}
<<<<<<< HEAD
		
		if(a instanceof NaimiTreilMutualExclusion){
			
			algo3 = (NaimiTreilMutualExclusion)a;
=======

		else if (a instanceof NaimiTreilMutualExclusion) {

			algo3 = (NaimiTreilMutualExclusion) a;
>>>>>>> 67440165a6b9183bbefc0481318466b53ea1452d
		}

	}

	@Override
	public void run() {

		Door d = new Door();

		while (true) {

			if (algo1 instanceof LelannMutualExclusion) {

				listenLelanMessage(algo1, d);
			}
<<<<<<< HEAD
			
			else if(algo2 instanceof RicartAggrawalaMutualExclusion){
				
				
				listenRicartAggrawalaMessage(algo2, d);
			}
			
			else if(algo3 instanceof NaimiTreilMutualExclusion){
				
				
=======

			if (algo2 instanceof RicartAggrawalaMutualExclusion) {

				listenRicartAggrawalaMessage(algo2, d);
			}

			if (algo3 instanceof NaimiTreilMutualExclusion) {

>>>>>>> 67440165a6b9183bbefc0481318466b53ea1452d
				listenerNaimiTreilMessage(algo3, d);
			}

		}
	}
<<<<<<< HEAD
	

	private void listenerNaimiTreilMessage(NaimiTreilMutualExclusion algo3, Door d){
		
		Message m_rec = algo3.recoit(d);
		if(m_rec instanceof RulesMessage){
		
			
			RulesMessage m = (RulesMessage)m_rec;
			System.out.println("Proc-"+algo3.procId+" Listener receive message "+m.toString()+" on door "+d.getNum());
			switch (m.getMsgType()) {
				
				case REQ :
					
					algo3.receiveReq(m);
					
				break;
				
				case TOKEN :
					
					algo3.receiveJeton(m);
					
				break;
	
				default :
					System.out.println("Error message type");
			}
		}
		else if(m_rec instanceof FormMessage){
			
			receiveFormeMessage(algo, m_rec,d.getNum());
			
		}else{
			
			if(m_rec instanceof ExtendRouteMessage ){
				
					System.out.println("Receive message ExtendRouteMessage");
					
			}else{
				
				System.out.println("Error message");
			}
		}
	}
	
	
	private void listenRicartAggrawalaMessage(RicartAggrawalaMutualExclusion algo2, Door d){
		
		Message m_rec = algo2.recoit(d);
		if(m_rec instanceof RulesMessage){
		
			
			RulesMessage m = (RulesMessage)m_rec;
			System.out.println("Proc-"+algo2.procId+" Listener receive message "+m.toString()+" on door "+d.getNum());
=======

	private void listenRicartAggrawalaMessage(	final RicartAggrawalaMutualExclusion algo2,
												final Door d) {

		Message m_rec = algo2.recoit(d);
		if (m_rec instanceof RicartAggrawalaMessage) {

			System.out.println("Proc-" + algo2.procId
					+ " Listener receive message on door " + d.getNum());
			RicartAggrawalaMessage m = (RicartAggrawalaMessage) m_rec;
>>>>>>> 67440165a6b9183bbefc0481318466b53ea1452d
			switch (m.getMsgType()) {

				case REQ :

					algo2.receiveReq(m);

					break;

				case REL :

					algo2.receiveRel(m);

					break;

				default :
					System.out.println("Error message type");
			}
		} else if (m_rec instanceof FormMessage) {

			receiveFormeMessage(algo, m_rec, d.getNum());

		} else {

			if (m_rec instanceof ExtendRouteMessage) {

				System.out.println("Receive message ExtendRouteMessage");

			} else {

				System.out.println("Error message");
			}
		}
	}

	private void listenLelanMessage(final LelannMutualExclusion algo1,
									final Door d) {

		Message m_rec = algo1.recoit(d);
		if (m_rec instanceof TokenMessage) {

			System.out.println(" Message type Proc id " + algo1.procId);
			TokenMessage m = null;
			try {
				m = (TokenMessage) m_rec;

			} catch (Exception e) {

				System.out.println(" Mince " + e.getMessage());
			}

			System.out.println(" Message type " + m.type);
			switch (m.getMsgType()) {

				case TOKEN :

					System.out.println("Call send " + algo1.procId);
					algo1.receiveTOKEN(m);

					break;

				default :
					System.out.println("Error message type");
			}

		} else if (m_rec instanceof FormMessage) {

			receiveFormeMessage(algo, m_rec, d.getNum());

		} else {

			if (m_rec instanceof ExtendRouteMessage) {

				System.out.println("Receive message ExtendRouteMessage");

			} else {

				System.out.println("Error message");
			}
		}
	}

	private void receiveFormeMessage(	final Algorithm algo,
										final Message m_,
										final int door) {

		FormMessage m = (FormMessage) m_;
		if (algo instanceof LelannMutualExclusion) {

			LelannMutualExclusion lelan = (LelannMutualExclusion) algo;
			switch (m.getMsgType()) {

				case FORME :
					lelan.receiveFormMessage(m);
					break;

				default :
					System.out.println("Error message type");
			}
		}
<<<<<<< HEAD
		
		else if(algo instanceof RicartAggrawalaMutualExclusion){
			
			RicartAggrawalaMutualExclusion ricart = (RicartAggrawalaMutualExclusion)algo;
=======

		if (algo instanceof RicartAggrawalaMutualExclusion) {

			RicartAggrawalaMutualExclusion ricart = (RicartAggrawalaMutualExclusion) algo;
>>>>>>> 67440165a6b9183bbefc0481318466b53ea1452d
			switch (m.getMsgType()) {

				case FORME :
					ricart.receiveFormMessage(m);
					break;

				default :
					System.out.println("Error message type");
			}

		}
<<<<<<< HEAD
		else if( algo instanceof NaimiTreilMutualExclusion){
			
			NaimiTreilMutualExclusion naimi = (NaimiTreilMutualExclusion)algo;
			switch (m.getMsgType()) {

				case FORME :
					naimi.receiveFormMessage(m);
					break;
	
				default :
					System.out.println("Error message type");
			}
		}
=======

	}

	private void listenerNaimiTreilMessage(	final NaimiTreilMutualExclusion algo32,
											final Door d) {

>>>>>>> 67440165a6b9183bbefc0481318466b53ea1452d
	}
	
}
