<<<<<<< HEAD
package Gui;
=======
package whiteboard.src.gui;
>>>>>>> f4180e22aa71db9881ba7bc75dcf042d441b9072

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;

/**
 * La classe Forme définit le contrat d'une forme à dessiner.
 * 
 * 
 */
public abstract class Forme {
	/** Le premier point de la forme. */
	protected Point p1 = null;
	/** Le second point de la forme (optionnel). */
	protected Point p2 = null;
	/** L'arriere plan. */
	protected Color bg;
	/** L'avant plan. */
	protected Color fg;
	/** L'épaisseur du trait. */
	protected float trait;

	/**
	 * Méthode de dessin de l'arriere plan.
	 * 
	 * @param g
	 *            Le contexte de le dessin.
	 */
	public abstract void dessineArrierePlan(Graphics2D g);

	/**
	 * Méthode de dessin de l'avant plan.
	 * 
	 * @param g
	 *            Le contexte de le dessin.
	 */
	public abstract void dessineAvantPlan(Graphics2D g);

	/**
	 * Retourne vrai si cette forme est définie par 2 points, faux pour un
	 * point.
	 * 
	 * @return vrai si cette forme est définie par 2 points, faux pour un point.
	 */
	public abstract boolean aDeuxPoints();

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
	public Forme(Color bg, Color fg, float trait) {
		this.bg = bg;
		this.fg = fg;
		this.trait = trait;
	}

	/**
	 * Méthode de dessin.
	 * 
	 * @param g
	 *            Le contexte de le dessin.
	 */
	public void paint(Graphics2D g) {
		// sauvegarde du trait.
		Stroke sauveStroke = g.getStroke();
		// application de notre trait.
		g.setStroke(new BasicStroke(trait));

		// sauvegarge de la couleur courrante
		Color sauveColor = g.getColor();
		g.setColor(bg);
		// dessin de l'arriere plan
		dessineArrierePlan(g);
		g.setColor(fg);
		// dessin de l'avant plan
		dessineAvantPlan(g);
		// restauration de la couleur courrante
		g.setColor(sauveColor);

		// restauration du trait
		g.setStroke(sauveStroke);
	}

	/**
	 * Définit le premier point de la forme.
	 * 
	 * @param p1
	 *            Le premier point de la forme.
	 */
	public void setPoint1(Point p1) {
		this.p1 = p1;
	}

	/**
	 * Définit le second point de la forme.
	 * 
	 * @param p2
	 *            Le second point de la forme.
	 */
	public void setPoint2(Point p2) {
		this.p2 = p2;
	}
}
