package Gui;

import java.awt.Point;

/**
 * Lanceur d'application.
 */
public class Lanceur extends Thread {
	/**
	 * Point d'entr��e de l'��x��cutable.
	 * 
	 * @param args
	 *            Les arguments de la ligne de commande.
	 */
	private String tittle;
	TableauBlancUI tbui;
	public Lanceur(final String tittle) {

		this.tittle = tittle;
		tbui = new TableauBlancUI(this.tittle);
	}

	public TableauBlancUI getTbUI() {
		return tbui;
	}

	public void run() {

		tbui = new TableauBlancUI(this.tittle);
	}

	public void ajouteForme(final Point p1, final Point p2, final int formeID) {

		tbui.ajouteForme(p1, p2, formeID);

	}
}
