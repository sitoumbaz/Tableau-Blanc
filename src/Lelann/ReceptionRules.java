package Lelann;

import visidia.simulation.process.messages.Door;
import visidia.simulation.process.messages.Message;

// Reception thread
public class ReceptionRules extends Thread {

	LelannMutualExclusion algo;

	public ReceptionRules(final LelannMutualExclusion a) {

		algo = a;

	}

	@Override
	public void run() {

		Door d = new Door();

		while (true) {
			
		
			Message m_rec = algo.recoit(d);
			if(m_rec instanceof TokenMessage){
				
				System.out.println(" Message type Proc id "+algo.procId);
				TokenMessage m = null;
				try{
					
					
					m =  (TokenMessage)m_rec;
					System.out.println(" Youpi");
					
				}catch(Exception e){
					
					System.out.println(" Mince "+e.getMessage());
				}
				
				System.out.println(" Message type "+m.type);
				switch (m.getMsgType()) {

					case TOKEN :
						
						System.out.println("Call send "+algo.procId);
						algo.receiveTOKEN(m);
						
						break;

					default :
						System.out.println("Error message type");
				}
				
			}
			else if(m_rec instanceof FormMessage){
				
				FormMessage m =  (FormMessage)m_rec;
				int door = d.getNum();

				switch (m.getMsgType()) {

					case FORME :
						algo.receiveFormMessage(m);
						break;

					default :
						System.out.println("Error message type");
				}
				
			}else{
				
				if(m_rec instanceof ExtendRouteMessage ){
					
						System.out.println("Receive message ExtendRouteMessage");
						
				}else{
					
					System.out.println("Error message");
				}
			}
			
		}
	}
}
