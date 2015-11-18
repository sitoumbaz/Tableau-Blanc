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
	private String title;
	TableauBlancUI tbui;
	public Lanceur(final String title) {

		this.title = title;
	}

	/**
	 * 
	 * Méthode retournant l'interface graphique du tableau blanc
	 * 
	 * @return l'interface graphique du tableau blanc
	 */
	public TableauBlancUI getTbUI() {
		return tbui;
	}

	public void run() {

		tbui = new TableauBlancUI(this.title);
	}

	/**
	 * 
	 * Méthode permettant d'ajouter une forme au tableau blanc
	 * 
	 * @param p1
	 *            premier point
	 * @param p2
	 *            deuxieme point
	 * @param formeID
	 *            id de la forme à tracer.
	 */
	public void ajouteForme(final Point p1, final Point p2, final int formeID) {

		tbui.ajouteForme(p1, p2, formeID);

	}
}
