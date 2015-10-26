package Gui;

import java.awt.Point;

/**
 * Lanceur d'application.
 */
public class Lanceur/* extends Thread */{
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

	/*
	 * public void run() {
	 * 
	 * tbui = new TableauBlancUI(this.tittle); }
	 */

	public void ajouteForme(final Point p1, final Point p2, final int formeID) {

		// refaire en thread et mettre un while qui att que le tbui soit plus
		// null
		if (tbui != null) {
			tbui.ajouteForme(p1, p2, formeID);
		} else {
			System.out.println("le tableau est nulle comme toi");
		}
	}
}
