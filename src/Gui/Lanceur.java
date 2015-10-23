
package Gui;
import javax.swing.SwingUtilities;

/**
 * Lanceur d'application.
 */
public class Lanceur extends Thread {
	/**
	 * Point d'entrée de l'éxécutable.
	 * 
	 * @param args
	 *            Les arguments de la ligne de commande.
	 */
	 private String tittle;
	 
	 public Lanceur(String tittle){
		 
		 this.tittle = tittle;
	 }
	
	public void run(){
		
		new TableauBlancUI(this.tittle);
	}
	
}
