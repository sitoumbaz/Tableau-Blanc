package whiteboard.src.gui;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Implementation de Forme pour le dessin d'un rectangle.
 */
public class FormeRectangle extends Forme {
	/**
	 * Constructeur.
	 * 
	 * @param bg
	 *            L'arriere plan.
	 * @param fg
	 *            L'avant plan.
	 * @param trait
	 *            L'épaisseur du trait.
	 */
	public FormeRectangle(Color bg, Color fg, float trait) {
		super(bg, fg, trait);
	}

	/**
	 * Méthode de dessin de l'arriere plan.
	 * 
	 * @param g
	 *            Le contexte de le dessin.
	 */
	public void dessineArrierePlan(Graphics2D g) {
		int x = Math.min(p1.x, p2.x);
		int y = Math.min(p1.y, p2.y);
		int width = Math.abs(p1.x - p2.x);
		int height = Math.abs(p1.y - p2.y);
		g.fillRect(x, y, width, height);
	}

	/**
	 * Méthode de dessin de l'avant plan.
	 * 
	 * @param g
	 *            Le contexte de le dessin.
	 */
	public void dessineAvantPlan(Graphics2D g) {
		int x = Math.min(p1.x, p2.x);
		int y = Math.min(p1.y, p2.y);
		int width = Math.abs(p1.x - p2.x);
		int height = Math.abs(p1.y - p2.y);
		g.drawRect(x, y, width, height);
	}

	/**
	 * Retourne vrai si cette forme est définit par 2 points, faux pour un
	 * point.
	 * 
	 * @return vrai si cette forme est définit par 2 points, faux pour un point.
	 */
	public boolean aDeuxPoints() {
		return true;
	}
}
