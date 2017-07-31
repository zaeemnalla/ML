import java.util.ArrayList;
import java.util.Vector;

public class ValueIteration {

	public static void main(String[] args) {

		double[][] V = new double[10][10];
		char[][] Policy = new char[10][10];
		double delta = 0;

		do {
			delta = 0;
			for (int i=0; i<V.length; i++) {
				for (int j=0; j<V[0].length; j++) {
					double v = V[i][j];
					if ( (i==9) && (j==9) )
						continue;
					V[i][j] = doUpdate(i, j, V, Policy);
					delta = Math.max(delta, Math.abs( v - V[i][j] ) );
				}
			}
		} while (delta > 0);

		Policy[9][9] = 'G';
		printResults(V, Policy);

	}

	public static double doUpdate(int i, int j, double[][] V, char[][] Policy) {

		// Array that stores the values that each action (l, u, r, d) gives. Max obtained from here.
		double[] actions = new double[4];

		// left
		actions[0] = calcValue(V, i, j, i, j-1);
		// down
		actions[1] = calcValue(V, i, j, i-1, j);
		// right
		actions[2] = calcValue(V, i, j, i, j+1);
		// up
		actions[3] = calcValue(V, i, j, i+1, j);

		double max = Math.max( Math.max(actions[0], actions[1]), Math.max(actions[2], actions[3]) );
		updatePolicy(i, j, max, actions, Policy);
		return max;

	}

	public static double calcValue(double[][] V, int oldi, int oldj, int newi, int newj) {

		double newv = 0;

		// Barriers
		if ( barrier(oldi, oldj, newi, newj) ) {
			newv = (1)*(-5 + V[oldi][oldj]);
			//System.out.println("barrier");
		}

		// Partial Barriers
		else if (
				( (newi==1 && newj==8) && (newi==oldi-1) || (newi==2 && newj==8) && (newi==oldi+1) ) ||
				( (newi==6 && newj==9) && (newi==oldi-1) || (newi==7 && newj==9) && (newi==oldi+1) ) ||
				( (newi==7 && newj==1) && (newj==oldj+1) || (newi==7 && newj==0) && (newj==oldj-1) )
		   ) {
			newv = (1)*(-3 + V[newi][newj]);
			//System.out.println("partial barrier");
		}

		// Teleport Cell a
		else if (newi==1 && newj==3) {
			newv = (0.6)*(-1 + V[1][7]) + (0.4)*(-1 + V[9][0]);
			//System.out.println("teleport cell a");
		}

		// Teleport Cell b
		else if (newi==4 && newj==1) {
			newv = (0.4)*(-1 + V[7][5]) + (0.6)*(-1 + V[2][1]);
			//System.out.println("teleport cell b");
		}

		// Teleport Cell c
		else if (newi==8 && newj==4) {
			newv = (0.7)*(-1 + V[6][9]) + (0.3)*(-1 + V[5][2]);
			//System.out.println("teleport cell c");
		}

		// Off Grid
		else if ( (newi>9) || (newi<0) || (newj>9) || (newj<0) ) {
			newv = (1)*(-5 + V[oldi][oldj]);
			//System.out.println("off grid");
		}

		// Goal Cell
		else if ( (newi==9) && (newj==9) ) {
			newv = (1)*(100 + V[newi][newj]);
			//System.out.println("goal cell");
		}

		// Open Cell (none of the above)
		else {
			newv = (1)*(-1 + V[newi][newj]);
			//System.out.println("open cell");
		}

		return newv;

	}

	public static boolean barrier(int oldi, int oldj, int newi, int newj) {
		if (
				// down and up barriers
				( (newi==0 && newj==1) && (newi==oldi-1) || (newi==1 && newj==1) && (newi==oldi+1) ) ||
				( (newi==0 && newj==2) && (newi==oldi-1) || (newi==1 && newj==2) && (newi==oldi+1) ) ||
				( (newi==1 && newj==6) && (newi==oldi-1) || (newi==2 && newj==6) && (newi==oldi+1) ) ||
				( (newi==1 && newj==7) && (newi==oldi-1) || (newi==2 && newj==7) && (newi==oldi+1) ) ||
				( (newi==6 && newj==8) && (newi==oldi-1) || (newi==7 && newj==8) && (newi==oldi+1) ) ||
				( (newi==6 && newj==1) && (newi==oldi-1) || (newi==7 && newj==1) && (newi==oldi+1) ) ||
				( (newi==6 && newj==2) && (newi==oldi-1) || (newi==7 && newj==2) && (newi==oldi+1) ) ||
				( (newi==6 && newj==3) && (newi==oldi-1) || (newi==7 && newj==3) && (newi==oldi+1) ) ||
				// right and left barriers
				( (newi==1 && newj==3) && (newj==oldj+1) || (newi==1 && newj==2) && (newj==oldj-1) ) ||
				( (newi==0 && newj==6) && (newj==oldj+1) || (newi==0 && newj==5) && (newj==oldj-1) ) ||
				( (newi==1 && newj==6) && (newj==oldj+1) || (newi==1 && newj==5) && (newj==oldj-1) ) ||
				( (newi==6 && newj==8) && (newj==oldj+1) || (newi==6 && newj==7) && (newj==oldj-1) ) ||
				( (newi==8 && newj==1) && (newj==oldj+1) || (newi==8 && newj==0) && (newj==oldj-1) ) ||
				( (newi==9 && newj==1) && (newj==oldj+1) || (newi==9 && newj==0) && (newj==oldj-1) )
		   )
			return true;
		else
			return false;
	}

	public static void updatePolicy(int i, int j, double max, double[] actions, char[][] Policy) {

		if (max == actions[0]) {
			if ( Policy[i][j-1] != 'u' && !barrier(i, j, i, j-1) ){
				Policy[i][j] = 'd';
				//return;
			}
		}

		if (max == actions[1]) {
			if (Policy[i-1][j] != 'r' && !barrier(i, j, i-1, j) ){
				Policy[i][j] = 'l';
				//return;
			}
		}

		if (max == actions[2]) {
			if (Policy[i][j+1] != 'd' && !barrier(i, j, i, j+1) ){
				Policy[i][j] = 'u';
				//return;
			}
		}

		if (max == actions[3]) {
			if (Policy[i+1][j] != 'l' && !barrier(i, j, i+1, j) ){
				Policy[i][j] = 'r';
				//return;
			}
		}

	}

	public static void printResults(double[][] V, char[][] Policy) {
		// Print value function
		System.out.println("Value Function");
		for (int i=V.length-1; i>=0; i--) {
			String s = "";
			for (int j=0; j<V[0].length; j++) {
				s += V[i][j] + " ";
			}
			System.out.println(s);
		}

		// Print policy function
		System.out.println("\nPolicy Function");
		for (int j=Policy[0].length-1; j>=0; j--) {
			String s = "";
			for (int i=0; i<Policy.length; i++) {
				s += Policy[i][j] + " ";
			}
			System.out.println(s);
		}
	}

}
