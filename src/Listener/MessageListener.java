package Listener;

import visidia.simulation.process.algorithm.Algorithm;
import visidia.simulation.process.messages.Door;
import visidia.simulation.process.messages.Message;
import Lelann.LelannMutualExclusion;
import Message.ExtendRouteMessage;
import Message.FormMessage;
import Message.RulesMessage;
import Message.TokenMessage;
import Naimi.NaimiTreilMutualExclusion;
import RicartAggrawala.RicartAggrawalaMutualExclusion;

// Reception thread
public class MessageListener extends Thread {

	/** Init Instances of principal Algorithme used in the project */
	Algorithm algo;
	// instantiation suivant le type d'algo.
	LelannMutualExclusion algo1;
	RicartAggrawalaMutualExclusion algo2;
	NaimiTreilMutualExclusion algo3;

	/**
	 * Constructor
	 * 
	 * @return void
	 * @param instance
	 *            of Algorithm class
	 * 
	 */
	public MessageListener(final Algorithm a) {

		algo = a;
		if (a instanceof LelannMutualExclusion) {

			algo1 = (LelannMutualExclusion) a;
		}

		if (a instanceof RicartAggrawalaMutualExclusion) {

			algo2 = (RicartAggrawalaMutualExclusion) a;
		}

		if (a instanceof NaimiTreilMutualExclusion) {

			algo3 = (NaimiTreilMutualExclusion) a;
		}

	}

	@Override
	public void run() {

		Door d = new Door();
<<<<<<< HEAD

		while (true) {

			if (algo1 instanceof LelannMutualExclusion) {

				listenLelanMessage(algo1, d);
			}

			else if (algo2 instanceof RicartAggrawalaMutualExclusion) {

				listenRicartAggrawalaMessage(algo2, d);
			}

			else if (algo3 instanceof NaimiTreilMutualExclusion) {

=======
		
		if(algo1 instanceof LelannMutualExclusion){
			
			while (true){
			
				listenLelanMessage(algo1, d);
			}
			
		}
		
		else if(algo2 instanceof RicartAggrawalaMutualExclusion){
			
			while (true){
				
				listenRicartAggrawalaMessage(algo2, d);
			}
		}
		
		else if(algo3 instanceof NaimiTreilMutualExclusion){
			
			while (true){
				
>>>>>>> 4788f2f587deb0811291247c8f58f5b43a800787
				listenerNaimiTreilMessage(algo3, d);
			}

		}
	
	}

	/**
	 * Function which allow managing Message Rules of the Naimi-Treil Algorithm
	 * 
	 * @return void
	 * @param instance
	 *            of NaimiTreilMutualExclusion class
	 * @param instance
	 *            of Door class
	 * 
	 */
	private void listenerNaimiTreilMessage(	final NaimiTreilMutualExclusion algo3,
											final Door d) {

		/* Receive message and test if it is instance of RulesMessage class */
		Message m_rec = algo3.recoit(d);
		if (m_rec instanceof RulesMessage) {

			RulesMessage m = (RulesMessage) m_rec;
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

	/**
	 * Function which allow managing Message Rules of the Ricart Agrwala
	 * Algorithm
	 * 
	 * @return void
	 * @param instance
	 *            of RicartAggrawalaMutualExclusion class
	 * @param instance
	 *            of Door class
	 * 
	 */
	private void listenRicartAggrawalaMessage(	final RicartAggrawalaMutualExclusion algo2,
												final Door d) {

		Message m_rec = algo2.recoit(d);
		if (m_rec instanceof RulesMessage) {

			RulesMessage m = (RulesMessage) m_rec;
			switch (m.getMsgType()) {

				case REQ :

					algo2.receiveReq(m);

					break;

				case REL :

					algo2.receiveRel(m);
<<<<<<< HEAD

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

=======
					
					
				break;
	
				default :
					System.out.println("Error message type");
			}
		}
		else if(m_rec instanceof FormMessage){
			
			receiveFormeMessage(algo, m_rec,d.getNum());
			
		}else{
			
			if(m_rec instanceof ExtendRouteMessage ){
				
					
					ExtendRouteMessage m = (ExtendRouteMessage)m_rec;
					algo2.recoitExtendRouteMessage(m, d.getNum());
					synchronized(algo2){
						
						if(algo2.myRouter.ready == algo2.netSize){
							
							algo2.iAmReady = true;
							algo2.notify();
						}
						
					}
					
			}else{
				
>>>>>>> 4788f2f587deb0811291247c8f58f5b43a800787
				System.out.println("Error message");
			}
		}
	}

	/**
	 * Function which allow managing Message Rules of the Lelan Algorithm
	 * 
	 * @return void
	 * @param instance
	 *            of LelannMutualExclusion class
	 * @param instance
	 *            of Door class
	 * 
	 */
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

	/**
	 * Function which allow managing receiving form for each instance of
	 * LelannMutualExclusion,RicartAggrawalaMutualExclusion and
	 * NaimiTreilMutualExclusion classes
	 * 
	 * @return void
	 * @param instance
	 *            of Algorithm class
	 * @param instance
	 *            of Message class
	 * @param instance
	 *            of Door class
	 * 
	 */
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

		else if (algo instanceof RicartAggrawalaMutualExclusion) {

			RicartAggrawalaMutualExclusion ricart = (RicartAggrawalaMutualExclusion) algo;
			switch (m.getMsgType()) {

				case FORME :
					ricart.receiveFormMessage(m);
					break;

				default :
					System.out.println("Error message type");
			}

		} else if (algo instanceof NaimiTreilMutualExclusion) {

			NaimiTreilMutualExclusion naimi = (NaimiTreilMutualExclusion) algo;
			switch (m.getMsgType()) {

				case FORME :
					naimi.receiveFormMessage(m);
					break;

				default :
					System.out.println("Error message type");
			}
		}
	}

}
