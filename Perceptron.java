import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

public class Perceptron {
	
	static int N=0, m=0;
	
	public static void main(String[] args) throws IOException {	
		Vector<double[]> dataset = ReceiveData();
		
		N = dataset.size(); m = dataset.get(0).length;
		double[][] X = Inputs(dataset);
		double[] T = Targets(dataset), W = Weights();
		double rate = 0.25;
		
		PerceptLearn(X, T, W, rate);	
	}
	
	static Vector<double[]> ReceiveData() throws IOException{
		try {
			
			System.out.print("Name of dataset file: ");
			Scanner in = new Scanner(System.in);
			BufferedReader br = new BufferedReader(new FileReader(new File(in.nextLine())));
			in.close();
			
			String line = null;
			Vector<double[]> dataset = new Vector<double[]>();
			while ( (line = br.readLine()) != null ){
				double[] elements = new double[line.split(" ").length];
				for (int i=0; i<line.split(" ").length; i++)
					elements[i] = Double.parseDouble(line.split(" ")[i]);
				dataset.add(elements);
			}
			br.close();
			
			return dataset;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	static double[][] Inputs(Vector<double[]> dataset){
		double[][] X = new double[N][m];
		for (int i=0; i<N; i++)
			for (int j=0; j<m; j++)
				X[i][j] = dataset.get(i)[j];
		for (int i=0; i<N; i++)
			X[i][m-1] = -1;
		return X;
	}
	
	static double[] Targets(Vector<double[]> dataset){
		double[] T = new double[N];
		for (int i=0; i<N; i++)
			T[i] = dataset.get(i)[m-1];
		return T;
	}
	
	static double[] Weights(){
		double[] W = new double[m];
		Random r = new Random();
		for (int i=0; i<m; i++)
			W[i] = -1 + r.nextDouble()*2;
		return W;
	}
	
	static void Shuffle(double X[][], double T[], int Y[]){
		int[] random = new int[N];
		
		for (int r=0; r<N; r++)
			random[r] = 0 + (int)(Math.random() * (N-1)); 
		
		for (int r=0; r<15; r+=2){
			double[] hX = X[random[r]];
			X[random[r]] = X[random[r+1]];
			X[random[r+1]] = hX;
			
			double hT = T[random[r]];
			T[random[r]] = T[random[r+1]];
			T[random[r+1]] = hT;
			
			int hY = Y[random[r]];
			Y[random[r]] = Y[random[r+1]];
			Y[random[r+1]] = hY;
		}		
	}
	
	static int Percept(double W[], double x[]){
		int h = 0;
		for (int i=0; i<m; i++){
			h += ( W[i]*x[i] );
		}
		int action = (h>0) ? 1 : 0;
		return action;
	}
	
	static void PerceptLearn(double X[][], double T[], double W[], double rate){
		double error = 100;
		int[] Y = new int[N];
		
		for (int p=0; p<12000; p++){
			error=0;
			
			// randomization of data points
			Shuffle(X, T, Y);
			
			// learning algorithm
			for (int k=0; k<N; k++){
				int y = Percept(W, X[k]);
				Y[k] = y;
				for (int i=0; i<m; i++)
					W[i] = W[i] + rate * (T[k] - y) * X[k][i];	
				error += Math.abs(T[k] - Y[k]);
			}
		}
		
		// printing
		PrintWeights(W);
		System.out.println("error    : " + error);
	}
	
	static void PrintWeights(double W[]){
		for (int i=0; i<W.length; i++){
			System.out.println("weight " + (i+1) + " : " + W[i]);
		}
	}

}
