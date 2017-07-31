public class QLearning {

	public static void main(String[] args) {

		double[][][] Q = new double[10][10][4];
		double[][] Policy = new double[10][10];

		// Repeat (for each episode)
		for (int episode=0; episode<200; episode++) {
			
			boolean stop = false;
			int I = 0, J = 0;
			int A = (int) (0 + Math.random() * 4);
			do {

                int Anew = (int) (0 + Math.random() * 4);

                int Inew = 0, Jnew = 0;
				if (Anew == 0) {
					Inew = I;
					if (J-1>-1 && J-1<10)
						Jnew = J-1;
					else
						Jnew = J;
				}
				else if (Anew == 1) {
					if (I-1>-1 && I-1<10)
						Inew = I-1;
					else
						Inew = I;
					Jnew = J;
				}
				else if (Anew == 2) {
					Inew = I;
					if (J+1>-1 && J+1<10)
						Jnew = J+1;
					else
						Jnew = J;
				}
				else if (Anew == 3) {
					if (I+1>-1 && I+1<10)
						Inew = I+1;
					else
						Inew = I;
					Jnew = J;
				}

				double Reward = getReward(I, J, Inew, Jnew);

                double maxAction = Math.max( Math.max( Q[I][J][0], Q[I][J][1] ) , Math.max( Q[I][J][2], Q[I][J][3] ) );

				if ( ((I!=9) && (J!=9)) || ((I!=9) && (J!=0)) )
					Q[I][J][A] = Q[I][J][A] + 0.3 * ( Reward + 0.9 * maxAction - Q[I][J][A] );
				else
					Q[I][J][A] = Q[I][J][A] + 0.3 * ( Reward - Q[I][J][A] );

				I = Inew; J = Jnew;
				
				if ( ((I==9) && (J==9)) || ((I==0) && (J==9)) )
					stop = true;

			} while ( !stop );
		}

		Policy = getPolicy(Q, Policy);
		printPolicy(Policy);

	}

	public static int InewFromA(int I, int A) {
		int Inew = 0;

		if (A == 1)
			Inew = I - 1;
		else if (A == 3)
			Inew = I + 1;

		if (Inew>-1 && Inew<10)
			return Inew;
		else
			return I;
	}

	public static int JnewFromA(int J, int A) {
		int Jnew = 0;

		if (A == 0)
			Jnew = J - 1;
		else if (A == 2)
			Jnew = J + 1;

		if (Jnew>-1 && Jnew<10)
			return Jnew;
		else
			return J;
	}

	public static double getReward(int I, int J, int Inew, int Jnew) {

		double newv = 0;

		// Barriers
		if ( barrier(I, J, Inew, Jnew) ) {
			newv = -5;
			//System.out.println("barrier");
		}

		// Partial Barriers
		else if (
				( (Inew==1 && Jnew==8) && (Inew==I-1) || (Inew==2 && Jnew==8) && (Inew==I+1) ) ||
				( (Inew==6 && Jnew==9) && (Inew==I-1) || (Inew==7 && Jnew==9) && (Inew==I+1) ) ||
				( (Inew==7 && Jnew==1) && (Jnew==J+1) || (Inew==7 && Jnew==0) && (Jnew==J-1) )
		   ) {
			newv = -3;
			//System.out.println("partial barrier");
		}

		// Teleport Cell a
		else if (Inew==1 && Jnew==3) {
			newv = (0.6)*(-1) + (0.4)*(-1);
			//System.out.println("teleport cell a");
		}

		// Teleport Cell b
		else if (Inew==4 && Jnew==1) {
			newv = (0.4)*(-1) + (0.6)*(-1);
			//System.out.println("teleport cell b");
		}

		// Teleport Cell c
		else if (Inew==8 && Jnew==4) {
			newv = (0.7)*(-1) + (0.3)*(-1);
			//System.out.println("teleport cell c");
		}

		// Off Grid
		else if ( (Inew>9) || (Inew<0) || (Jnew>9) || (Jnew<0) ) {
			newv = -5;
			//System.out.println("off grid");
		}

		// Goal Cell
		else if ( (Inew==9) && (Jnew==9) ) {
			newv = 100;
			//System.out.println("goal cell");
		}

		// Open Cell (none of the above)
		else {
			newv = -1;
			//System.out.println("open cell");
		}

		return newv;

	}

	public static boolean barrier(int I, int J, int Inew, int Jnew) {
		if (
				// down and up barriers
				( (Inew==0 && Jnew==1) && (Inew==I-1) || (Inew==1 && Jnew==1) && (Inew==I+1) ) ||
				( (Inew==0 && Jnew==2) && (Inew==I-1) || (Inew==1 && Jnew==2) && (Inew==I+1) ) ||
				( (Inew==1 && Jnew==6) && (Inew==I-1) || (Inew==2 && Jnew==6) && (Inew==I+1) ) ||
				( (Inew==1 && Jnew==7) && (Inew==I-1) || (Inew==2 && Jnew==7) && (Inew==I+1) ) ||
				( (Inew==6 && Jnew==8) && (Inew==I-1) || (Inew==7 && Jnew==8) && (Inew==I+1) ) ||
				( (Inew==6 && Jnew==1) && (Inew==I-1) || (Inew==7 && Jnew==1) && (Inew==I+1) ) ||
				( (Inew==6 && Jnew==2) && (Inew==I-1) || (Inew==7 && Jnew==2) && (Inew==I+1) ) ||
				( (Inew==6 && Jnew==3) && (Inew==I-1) || (Inew==7 && Jnew==3) && (Inew==I+1) ) ||
				// right and left barriers
				( (Inew==1 && Jnew==3) && (Jnew==J+1) || (Inew==1 && Jnew==2) && (Jnew==J-1) ) ||
				( (Inew==0 && Jnew==6) && (Jnew==J+1) || (Inew==0 && Jnew==5) && (Jnew==J-1) ) ||
				( (Inew==1 && Jnew==6) && (Jnew==J+1) || (Inew==1 && Jnew==5) && (Jnew==J-1) ) ||
				( (Inew==6 && Jnew==8) && (Jnew==J+1) || (Inew==6 && Jnew==7) && (Jnew==J-1) ) ||
				( (Inew==8 && Jnew==1) && (Jnew==J+1) || (Inew==8 && Jnew==0) && (Jnew==J-1) ) ||
				( (Inew==9 && Jnew==1) && (Jnew==J+1) || (Inew==9 && Jnew==0) && (Jnew==J-1) )
		   )
			return true;
		else
			return false;
	}

	public static double[][] getPolicy(double[][][] Q, double[][] Policy) {
		for (int i=0; i<Q.length; i++) {
			for (int j=0; j<Q[0].length; j++) {
                double max = -100000;
				if (Q[i][j][0]>max && Q[i][j][0]!=0)
					max = Q[i][j][0];
				if (Q[i][j][1]>max && Q[i][j][1]!=0)
					max = Q[i][j][1];
				if (Q[i][j][2]>max && Q[i][j][2]!=0)
					max = Q[i][j][2];
				if (Q[i][j][3]>max && Q[i][j][3]!=0)
					max = Q[i][j][3];
				if (max==-100000)
					max = 0;
				Policy[i][j] = max;
			}
		}
		return Policy;
	}

	public static void printPolicy(double[][] Policy) {
		System.out.println("Policy");
		for (int i=Policy.length-1; i>=0; i--) {
			String s = "";
			for (int j=0; j<Policy[0].length; j++) {
				s += ( (double) Math.round(Policy[i][j]*100)/100 ) + " ";
			}
			System.out.println(s);
		}
	}

}
