package Lelann;

public class Router {

	private int allRoute[][];
	private int netSize; 
	
	public Router(int netSize){
		
		allRoute = new int[netSize][netSize];
		this.netSize = netSize;
		initTab();
	}
	
	private void initTab(){
		for(int i=0;i<netSize;i++){
			for(int j=0;j<netSize;j++){
				if(i!=j){
					allRoute[i][j] = -1;
				}else{
					allRoute[i][j] = -2;
				}
				
			}
		}
	}
	
	public void setMyRoute(int proc1, int proc2,  int door){
		
		allRoute[proc1][proc2] = door;
	}
	
	public int[][] getMyRoute(){
		
		return allRoute;
	}
	
	/*public int[] generateRoute(int index ){
		
		solveHamiltonian(this.allRoute, index, 0);
		return null;
	}
	
	private boolean solveHamiltonian(int[][] bigRoute, int startProc,  int routeLength){
		
		int[] route = bigRoute[startProc];
		
		bigRoute[startProc] = null;
		
		for(int i=0; i<route.length; i++){
			
			if( 
				(this.netSize-1 == routeLength && route[i] > -1 && i == startProc)
				 ||
				(bigRoute[i] != null && solveHamiltonian(bigRoute, i,  routeLength+1))
				
			  ){
				
					System.out.println("Chemin trouve "+startProc);
					return true;
			}
		}
		return false;
	} */
}
