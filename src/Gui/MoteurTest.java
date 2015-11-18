package Gui;

import java.awt.Point;
/*
 *
 * Classe permettant de générer des formes aléatoirement.
 *
 */
public class MoteurTest {
	// coordonnées max possibles du point
	private final int max = 250;
	// corrdonnées min possibles du premier point
	private final int min = 107;
	// id de la forme
	private int choixForme;
	// premier point
	private Point point1;
	// deuxieme point
	private Point point2;

	public MoteurTest() {
	}

	/**
	 * 
	 * Méthode permettant de générer une forme aléatoirement
	 */
	public void creerForme() {

		java.util.Random rand = new java.util.Random();

		int choixForme = rand.nextInt((3) + 1);
		setChoixForme(choixForme);

		int p1x = rand.nextInt((max - min) + 1) + min;
		int p1y = rand.nextInt((max - min) + 1) + min;
		Point p1 = new Point(p1x, p1y);
		setPoint1(p1);

		int p2x = rand.nextInt((p1x + min - p1x - min) + 1) + p1x - min;
		int p2y = rand.nextInt((p1y + min - p1y - min) + 1) + p1y - min;
		Point p2 = new Point(p2x, p2y);
		setPoint2(p2);
	}

	public Point getPoint1() {
		return point1;
	}

	public void setPoint1(final Point point1) {
		this.point1 = point1;
	}

	public Point getPoint2() {
		return point2;
	}

	public void setPoint2(final Point point2) {
		this.point2 = point2;
	}

	public int getChoixForme() {
		return choixForme;
	}

	public void setChoixForme(final int choixForme) {
		this.choixForme = choixForme;
	}

}
