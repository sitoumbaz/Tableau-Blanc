<<<<<<< HEAD
package Gui;
=======
package whiteboard.src.gui;
>>>>>>> f4180e22aa71db9881ba7bc75dcf042d441b9072

import javax.swing.SwingUtilities;

/**
 * Lanceur d'application.
 */
public class Lanceur {
	/**
	 * Point d'entrée de l'éxécutable.
	 * 
	 * @param args
	 *            Les arguments de la ligne de commande.
	 */
	public static final void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new TableauBlancUI();
			}
		});
	}
}
