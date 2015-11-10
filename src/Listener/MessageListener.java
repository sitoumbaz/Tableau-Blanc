package Listener;

import Lelann.LelannMutualExclusion;
import Lelann.TokenMessage;
import Message.FormMessage;
import Naimi.NaimiTreilMutualExclusion;
import RicartAggrawala.RicartAggrawalaMessage;
import RicartAggrawala.RicartAggrawalaMutualExclusion;
import Router.ExtendRouteMessage;
import visidia.simulation.process.algorithm.Algorithm;
import visidia.simulation.process.messages.Door;
import visidia.simulation.process.messages.Message;

// Reception thread
public class MessageListener extends Thread {

	Algorithm algo;
	LelannMutualExclusion algo1;
	RicartAggrawalaMutualExclusion algo2;
	NaimiTreilMutualExclusion algo3;
	
	
	public MessageListener(final Algorithm a) {
		
		algo = a;
		if(a instanceof LelannMutualExclusion){
			
			algo1 = (LelannMutualExclusion)a;
		}
		
		if(a instanceof RicartAggrawalaMutualExclusion){
					
			algo2 = (RicartAggrawalaMutualExclusion)a;
		}
		
		if(a instanceof RicartAggrawalaMutualExclusion){
			
			algo3 = (NaimiTreilMutualExclusion)a;
		}
		

	}

	@Override
	public void run() {

		Door d = new Door();

		while (true) {
			
			if(algo1 instanceof LelannMutualExclusion){
				
				
				listenLelanMessage(algo1, d);
			}
			
			if(algo2 instanceof RicartAggrawalaMutualExclusion){
				
				
				listenRicartAggrawalaMessage(algo2, d);
			}
			
			if(algo3 instanceof NaimiTreilMutualExclusion){
				
				
				listenerNaimiTreilMessage(algo3, d);
			}
			
		}
	}
	
	private void listenRicartAggrawalaMessage(RicartAggrawalaMutualExclusion algo2, Door d){
		
		Message m_rec = algo2.recoit(d);
		if(m_rec instanceof RicartAggrawalaMessage){
		
			System.out.println("Proc-"+algo2.procId+" Listener receive message on door "+d.getNum());
			RicartAggrawalaMessage m = (RicartAggrawalaMessage)m_rec;
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
	
	
	private void listenLelanMessage(LelannMutualExclusion algo1, Door d){
		
		Message m_rec = algo1.recoit(d);
		if(m_rec instanceof TokenMessage){
			
			System.out.println(" Message type Proc id "+algo1.procId);
			TokenMessage m = null;
			try{
				m =  (TokenMessage)m_rec;
				
			}catch(Exception e){
				
				System.out.println(" Mince "+e.getMessage());
			}
			
			System.out.println(" Message type "+m.type);
			switch (m.getMsgType()) {

				case TOKEN :
					
					System.out.println("Call send "+algo1.procId);
					algo1.receiveTOKEN(m);
					
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
	

	private void receiveFormeMessage(Algorithm algo, Message m_, int door){
		
		FormMessage m = (FormMessage)m_;
		if(algo instanceof LelannMutualExclusion){
			
			LelannMutualExclusion lelan = (LelannMutualExclusion)algo;
			switch (m.getMsgType()) {

				case FORME :
					lelan.receiveFormMessage(m);
					break;
	
				default :
					System.out.println("Error message type");
			}
		}
		
		if(algo instanceof RicartAggrawalaMutualExclusion){
			
			RicartAggrawalaMutualExclusion ricart = (RicartAggrawalaMutualExclusion)algo;
			switch (m.getMsgType()) {

				case FORME :
					ricart.receiveFormMessage(m);
					break;
	
				default :
					System.out.println("Error message type");
			}
			
		}
		
	}
	
	private void listenerNaimiTreilMessage(NaimiTreilMutualExclusion algo32, Door d){
		
		
	}
}
