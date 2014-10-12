package compGeo;

// Makes simple polygons.
// The "count" tab lets you draw points and move them,
// and it counts the number of simple polygons on the sets

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.*;

public class MakeSimple2 {
	static int POINTWID = 2; // size of points

	// the x and y arrays hold the coordinates
	// the B array is the order of the points in the polygon
	// You want to fill the C array with the simple polygon
	static double x[] = new double[200];
	static double y[] = new double[200];

	static int B[] = new int[200]; // the permutation matrix
	static int C[] = new int[200]; // the one that becomes simple
	static int D[] = new int[200]; // the one that becomes very simple
	static Integer E[] = new Integer[200]; // holds the convex hull
	static Integer E2[] = new Integer[200];
	static SimpleFrame myFrame;

	static int numPoints = 6;
	static int numHullPoints = 0;

	public static void main(String args[]) {
		makePolygons();

		// Create the frame to draw on
		myFrame = new SimpleFrame();
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myFrame.setSize(800, 800);
		myFrame.setLocation(400, 100);
		myFrame.setVisible(true);
	}

	public static void makePolygons() {
		// Build an array of random points in the unit square
		for (int i = 0; i < numPoints; i++) {
			x[i] = Math.random();
			y[i] = Math.random();// Sample program
			B[i] = i; // default permutation
		}

		createSimplePolygon();
		createVerySimplePolygon();
		createConvexHull();
	}

	/*
	 * Creates a simply polygon by iteratively removing crossings.
	 * Total polygon perimeter is a monovariant that decreases with
	 * each step of the algorithm, proving that the process will
	 * terminate in a a crossing-free polygon.
	 */
	public static void createSimplePolygon() {
		// Initialize the C[] array with the identity permutation
		for (int i = 0; i < numPoints; i++)
			C[i] = i;

		// Find crossings, and eliminate them
		boolean hasCrossing = true;
		while (hasCrossing) {
			hasCrossing = false;
			for (int i = 0; (i < numPoints - 2); i++)
				for (int j = i + 2; j < numPoints; j++)
					if (crosses(x[C[i]], y[C[i]], x[C[i + 1]], y[C[i + 1]],
							x[C[j]], y[C[j]], x[C[(j + 1) % numPoints]],
							y[C[(j + 1) % numPoints]])) {
						hasCrossing = true;
						int k = i + 1;
						int l = j;
						while (l > k) {
							int temp = C[k];
							C[k] = C[l];
							C[l] = temp;
							l--;
							k++;
						}
					}
		}
	}


	/*
	 * This algorithm makes use of the createSimplePolygon ideas
	 * above, and adds one check. If a point P of the polygon 
	 * lies close to another edge AB of the polygon, then sometimes
	 * it is worth it to remove P from where it is in the polygon and
	 * insert it between A and B in the polygon. 
	 * Thus polygon ...XPY...AB... ==>  ...XY...APB...
	 * 
	 * After making this switch, new crossings might be introduced,
	 * so we run again our createSimple algorithm to find and
	 * eliminate crossings.
	 */
	public static void createVerySimplePolygon() {
		// Initialize the D[] array with the identity permutation
		for (int i = 0; i < numPoints; i++)
			D[i] = i;

		// Find crossings, and eliminate them
		boolean stillTrying = true;
		while (stillTrying) {
			stillTrying = false;
			boolean hasCrossing = true;
			while (hasCrossing) {
				hasCrossing = false;
				for (int i = 0; (i < numPoints - 2); i++)
					for (int j = i + 2; j < numPoints; j++)
						if (crosses(x[D[i]], y[D[i]], x[D[i + 1]], y[D[i + 1]],
								x[D[j]], y[D[j]], x[D[(j + 1) % numPoints]],
								y[D[(j + 1) % numPoints]])) {
							hasCrossing = true;
							int k = i + 1;
							int l = j;
							while (l > k) {
								int temp = D[k];
								D[k] = D[l];
								D[l] = temp;
								l--;
								k++;
							}
						}
			}
			// See if we can make it shorter
			for (int i = 0; i < numPoints; i++)
				for (int j = (i + 1) % numPoints; ((j + 2) % numPoints) != i; j = ((j + 1) % numPoints))
					if (dis2(D[(i + numPoints - 1) % numPoints], D[i], D[(i + 1) % numPoints]) +
							dis(D[j], D[(j + 1) % numPoints]) >
					dis(D[(i + numPoints - 1) % numPoints], D[(i + 1) % numPoints])
					+ dis2(D[j], D[i], D[(j + 1) % numPoints])) {
						stillTrying = true;
						if (i < j) {
							int temp = D[i];
							for (int k = i; k < j; k++)
								D[k] = D[(k + 1) % numPoints];
							D[j] = temp;
						} else {
							int temp = D[i];
							for (int k = i; k > j + 1; k--)
								D[k] = D[(k + numPoints - 1) % numPoints];
							D[j + 1] = temp;
						}
					}
		}
	}

	// Your homework goes here
	static double[][] points;
	public static void createConvexHull(){
		points = new double[numPoints][2];
		for(int i = 0; i<numPoints; i++){
			points[i][0] = x[i];
			points[i][1] = y[i];
		}
		numHullPoints = numPoints;
		E = new Integer[numHullPoints];
		numHullPoints = 3;
		// sort(T[] a, int fromIndex, int toIndex, Comparator<? super T> c)
		Arrays.sort(points, new Comparator<double[]>(){
			public int compare(double[] a, double[] b){
				double x1 = a[0];
				double x2 = b[0];
				return Double.compare(x1, x2);
			}
		});
		for(int i = 0; i < numHullPoints; i++){
			E[i] = i;
			System.out.println(E[i]);
		}			
		System.out.println();
		System.out.println(E[0]);

		int nextPoint;
		for(int i = numHullPoints; i<numPoints; i++){
			nextPoint = i;
			E=updateHull(nextPoint);
		}
	}

	private static Integer[] updateHull(int nextPoint) {
		Integer[] newArray = new Integer[numPoints];
		int newHullPoints = numHullPoints;
		int number = numHullPoints;
		for (int i = 0; i<number-1;i++){
			System.out.println(i);
			System.out.println(E[i]);
			System.out.println(i+1);
			System.out.println(E[i+1]);
			System.out.println();
			if(sigma(		
					points[nextPoint][0],points[nextPoint][1],
					points[E[i]][0],points[E[i]][1],
					points[E[i+1]][0],points[E[i+1]][1]
					)==-1
					){
				System.out.println("Add");			
				System.out.println();
				newArray[i] = nextPoint;
				newHullPoints++;
			}else if(sigma(		
					points[nextPoint][0],points[nextPoint][1],
					points[E[i]][0],points[E[i]][1],
					points[E[i+1]][0],points[E[i+1]][1]
					)!=-1
					){
				newArray[i] = E[i];
				newHullPoints++;

				newArray[newHullPoints-1] = nextPoint;
				newHullPoints++;

			}else{
				break;
			}
		}
		/*
		for(int i = numHullPoints-1;i>0;i--){
			if(sigma(
					points[nextPoint][0],points[nextPoint][1],
					points[E[i]][0],points[E[i]][1],
					points[E[i-1]][0],points[E[i-1]][1]
					)!=1
					){
				if(newArray[i]==E[i]){
				newArray[i] = E[i];
				newHullPoints++;
			}else{
				break;
			}
		}
		 */

		numHullPoints = newHullPoints;
		return newArray;
	}
	/*
	private static boolean isOnHull(int nextPoint) {
		for (int i = 2; i<numHullPoints;i++){
			if(sigma(
					points[E[i-2]][0],points[E[i-2]][1],
					points[E[i-1]][0],points[E[i-1]][1],
					points[nextPoint][0],points[nextPoint][1])
					==-1
					){
				return true;
			}else if(sigma(
					points[nextPoint][0],points[nextPoint][1],
					points[E[numHullPoints-1]][0],points[E[numHullPoints-1]][1],
					points[E[numHullPoints-2]][0],points[E[numHullPoints-2]][1])
					==1
			){
				return true;
			}
		}else{
			return false;
		}
	}
	 */


	// Find the sigma(  (Ax, Ay),  (Bx, By),  (Cx, Cy)  )
	public static int sigma(double Ax, double Ay, double Bx, double By,
			double Cx, double Cy){
		double det = -((Bx-Ax)*(Cy-Ay) - (By-Ay)*(Cx-Ax));
		if(det < 0)
			return -1;
		else if(det > 0)
			return 1;
		else 
			return 0;
	}

	public static double dis2(int i, int j, int k) {
		return Math.sqrt((x[i] - x[j]) * (x[i] - x[j]) + (y[i] - y[j])
				* (y[i] - y[j]))
				+ Math.sqrt((x[k] - x[j]) * (x[k] - x[j]) + (y[k] - y[j])
						* (y[k] - y[j]));
	}

	public static double dis(int i, int j) {
		return Math.sqrt((x[i] - x[j]) * (x[i] - x[j]) + (y[i] - y[j])
				* (y[i] - y[j]));
	}

	/*
	 * Naive crosses method. Assumes points are in general position, which
	 * is good enough for this project.
	 */
	public static boolean crosses(double a, double b, double c, double d,
			double e, double f, double g, double h) {
		double det1a = (c - a) * (f - b) - (e - a) * (d - b);
		double det1b = (c - a) * (h - b) - (g - a) * (d - b);
		double det2a = (e - g) * (b - h) - (a - g) * (f - h);
		double det2b = (e - g) * (d - h) - (c - g) * (f - h);
		return ((det1a * det1b < 0) && (det2a * det2b < 0));
	}

	public static class SimpleFrame extends JFrame {
		public static JSlider numPointsSlider;

		public SimpleFrame() {
			super("Create you own Simple Polygon");
			Container content = getContentPane();
			content.setLayout(new java.awt.BorderLayout());
			JTabbedPane tabbedPane = new JTabbedPane();
			tabbedPane.setPreferredSize(new java.awt.Dimension(300, 400));
			tabbedPane.addTab("Scrambled", new ScrambledPanel());
			tabbedPane.addTab("Simple", new SimplePanel());
			tabbedPane.addTab("Very Simple", new VerySimplePanel());
			tabbedPane.addTab("Convex Hull", new ConvexPanel());
			content.add(tabbedPane, java.awt.BorderLayout.CENTER);

			// Slider for the number of points
			numPointsSlider = new JSlider(
					javax.swing.SwingConstants.HORIZONTAL, 3, 180, numPoints);
			numPointsSlider
			.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(
						javax.swing.event.ChangeEvent evt) {
					numPointsSliderStateChanged(evt);
				}
			});
			content.add(numPointsSlider, java.awt.BorderLayout.SOUTH);
		}

		private void numPointsSliderStateChanged(
				javax.swing.event.ChangeEvent evt) {
			numPoints = numPointsSlider.getValue();
			makePolygons();
			repaint();
		}
	}

	public static class ScrambledPanel extends JPanel {
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;

			// First set the scaling to fit the window
			Dimension size = getSize();
			int Xwid = (int) (0.95 * size.width);
			int Ywid = (int) (0.95 * size.height);

			// First draw the segments
			g2.setColor(Color.red);
			for (int i = 0; i < numPoints; i++)
				g2.drawLine((int) (Xwid * x[B[i]]), (int) (Ywid * y[B[i]]),
						(int) (Xwid * x[B[(i + 1) % numPoints]]),
						(int) (Ywid * y[B[(i + 1) % numPoints]]));

			// Now draw the points
			for (int i = 0; i < numPoints; i++) {
				g2.fillRect((int) (Xwid * x[i]) - POINTWID, (int) (Ywid * y[i])
						- POINTWID, 2 * POINTWID + 1, 2 * POINTWID + 1);
			}
		}
	}

	public static class SimplePanel extends JPanel {
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;

			// First set the scaling to fit the window
			Dimension size = getSize();
			int Xwid = (int) (0.95 * size.width);
			int Ywid = (int) (0.95 * size.height);

			// First draw the segments
			g2.setColor(Color.red);
			for (int i = 0; i < numPoints; i++) {
				if (i == 0)
					g2.setColor(Color.black);
				if (i == 1)
					g2.setColor(Color.green);
				if (i > 1)
					g2.setColor(Color.red);
				g2.drawLine((int) (Xwid * x[C[i]]), (int) (Ywid * y[C[i]]),
						(int) (Xwid * x[C[(i + 1) % numPoints]]),
						(int) (Ywid * y[C[(i + 1) % numPoints]]));
			}
			// Now draw the points
			for (int i = 0; i < numPoints; i++) {
				g2.fillRect((int) (Xwid * x[i]) - POINTWID, (int) (Ywid * y[i])
						- POINTWID, 2 * POINTWID + 1, 2 * POINTWID + 1);
			}
		}
	}

	public static class VerySimplePanel extends JPanel {
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;

			// First set the scaling to fit the window
			Dimension size = getSize();
			int Xwid = (int) (0.95 * size.width);
			int Ywid = (int) (0.95 * size.height);

			// First draw the segments
			g2.setColor(Color.red);
			for (int i = 0; i < numPoints; i++) {
				if (i == 0)
					g2.setColor(Color.black);
				if (i == 1)
					g2.setColor(Color.green);
				if (i > 1)
					g2.setColor(Color.red);
				g2.drawLine((int) (Xwid * x[D[i]]), (int) (Ywid * y[D[i]]),
						(int) (Xwid * x[D[(i + 1) % numPoints]]),
						(int) (Ywid * y[D[(i + 1) % numPoints]]));
			}
			// Now draw the points
			for (int i = 0; i < numPoints; i++) {
				g2.fillRect((int) (Xwid * x[i]) - POINTWID, (int) (Ywid * y[i])
						- POINTWID, 2 * POINTWID + 1, 2 * POINTWID + 1);
			}
		}
	}

	public static class ConvexPanel extends JPanel{
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;

			// First set the scaling to fit the window
			Dimension size = getSize();
			int Xwid = (int) (0.95 * size.width);
			int Ywid = (int) (0.95 * size.height);

			// First draw the segments
			g2.setColor(Color.red);
			for (int i = 0; i < numHullPoints; i++) {
				g2.drawLine((int) (Xwid * points[E[i]][0]), (int) (Ywid * points[E[i]][1]),
						(int) (Xwid * points[E[((i + 1) % numHullPoints)]][0]),
						(int) (Ywid * points[E[(i + 1) % numHullPoints]][1]));
			}
			// Now draw the points
			for (int i = 0; i < numPoints; i++) {
				g2.fillRect((int) (Xwid * points[i][0]) - POINTWID, (int) (Ywid * points[i][1])
						- POINTWID, 2 * POINTWID + 1, 2 * POINTWID + 1);
			}
			for(int i = 0; i< numHullPoints; i++){
				g2.setColor(Color.blue);
				g2.drawString(E[i]+"", (float)points[i][0]*Xwid, (float)points[i][1]*Ywid); //x[i]+" "+y[i]
			}
		}
	}
}