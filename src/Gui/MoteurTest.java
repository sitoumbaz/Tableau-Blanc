package Gui;

import java.awt.Point;
import java.util.Scanner;

public class MoteurTest {
	
	private final int max = 200;
	private final int min = 51;
	private int choixForme;
	private Point point1;
	private Point point2;
	
	public MoteurTest(){}

	// On créer une forme de manière aléatoire
	// On commence par séléctionner la forme point, rectangle, elipse, ligne (
	// entre 0 et 3 )
	// ensuite on choisit le premier point a minimum 50 px de la bordure ( pour
	// eviter que la forme ne dépasse le cadre
	// pour finir on choisit le deuxieme point dans un rayon de 50 px du premier
	// pour eviter d'avoir de trop grosses formes qui prennent tout le tableau

	public void creerForme() {
		java.util.Random rand = new java.util.Random();

		int choixForme = rand.nextInt((3) + 1);
		this.setChoixForme(choixForme);
		int p1x = rand.nextInt((max - min) + 1) + min;
		int p1y = rand.nextInt((max - min) + 1) + min;
		Point p1 = new Point(p1x, p1y);
		setPoint1(p1);
		
		int p2x = rand.nextInt((p1x + min - p1x - min) + 1) + p1x - min;
		int p2y = rand.nextInt((p1y + min - p1y - min) + 1) + p1y - min;
		Point p2 = new Point(p2x, p2y);
		setPoint1(p2);
	}

	public Point getPoint1() {
		return point1;
	}

	public void setPoint1(Point point1) {
		this.point1 = point1;
	}

	public Point getPoint2() {
		return point2;
	}

	public void setPoint2(Point point2) {
		this.point2 = point2;
	}

	public int getChoixForme() {
		return choixForme;
	}

	public void setChoixForme(int choixForme) {
		this.choixForme = choixForme;
	}
	
}
