<<<<<<< HEAD
package Gui;
=======
package gui;
>>>>>>> f4180e22aa71db9881ba7bc75dcf042d441b9072

import java.util.EventListener;

/**
 * Ecouteur pour la creation d'une forme
 */
public interface SelectionListener extends EventListener {
	/**
	 * Affiche un message de selection.
	 * 
	 * @param msg
	 *            Le message.
	 */
	public void messageSelection(String msg);

	/**
	 * Fin de la selection.
	 * 
	 * @param forme
	 *            La forme saisie.
	 */
	public void finDeSelection(Forme forme);
}
