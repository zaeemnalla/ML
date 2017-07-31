import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

public class NeuralNetwork {
	
	// hid_nodes is the number of hidden nodes excluding the augmented -1
	static int N=0, m=0, hid_nodes=0, out_nodes=0;
	private static Scanner in = new Scanner(System.in);
	
	public static void main(String[] args) throws IOException {
		GetNumNodes();
		Vector<String[]> dataset = ReceiveData();
		
		N = dataset.size(); m = dataset.get(0).length;
		double[][] X = Inputs(dataset), W = Weights(X[0].length, hid_nodes), U = Weights(hid_nodes+1, out_nodes);
		double[][] T = Targets(dataset);
		double rate = 0.25;
		
		NNLearn(X, W, U, T, rate);	
	}
	
	static void GetNumNodes(){
		System.out.print("Number of hidden nodes: ");
		hid_nodes = in.nextInt();
		
		System.out.print("Number of output nodes: ");
		out_nodes = in.nextInt();
		
		in.nextLine();
	}
	
	static Vector<String[]> ReceiveData() throws IOException{
		try {
			
			System.out.print("Path of dataset file: ");
			BufferedReader br = new BufferedReader(new FileReader(new File(in.nextLine())));
			System.out.println("");
			in.close();
			
			String line = null;
			Vector<String[]> dataset = new Vector<String[]>();
			while ( (line = br.readLine()) != null )
				dataset.add(line.split(" "));
			br.close();
			
			return dataset;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	static double[][] Inputs(Vector<String[]> dataset){
		double[][] X = new double[N][m];
		for (int i=0; i<N; i++)
			for (int j=0; j<m-1; j++)
				X[i][j] = Double.parseDouble(dataset.get(i)[j]);
		for (int i=0; i<N; i++)
			X[i][m-1] = -1;
		return X;
	}
	
	/*
	first_layer_nodes includes the augmented -1 (since a weight goes to it from the second layer)
	sec_layer_nodes excludes the augmented -1 (since a weight doesn't go from it to first layer)
	 */
	static double[][] Weights(int first_layer_nodes, int sec_layer_nodes){
		double[][] weights = new double[first_layer_nodes][sec_layer_nodes];
		Random r = new Random();
		for (int i=0; i<first_layer_nodes; i++)
			for (int j=0; j<sec_layer_nodes; j++)
				weights[i][j] = -1 + r.nextDouble()*2;
		return weights;
	}
	
	static double[][] Targets(Vector<String[]> dataset){
		double[][] T = new double[N][out_nodes];
		double value = 0;
		for (int i=0; i<N; i++){
		
			try {
				value = Double.parseDouble(dataset.get(i)[m-1]);
			} catch (Exception e) {
				value = AlphabetToNum(dataset.get(i)[m-1]);
			}
			
			double[] t = new double[out_nodes];
			if (out_nodes > 1)
				t[(int) value] = 1;
			else
				t[0] = value;
			T[i] = t;
		}
		return T;
	}
	
	static double AlphabetToNum(String s){
		double target=-1;
		switch (s.toUpperCase()) {
			case "A": target=0.00;
			case "B": target=1.00;
			case "C": target=2.00;
		
		}
		return target;
	}
	
	static ArrayList<Integer> CreateOrder(){
		ArrayList<Integer> Order = new ArrayList<Integer>();
		for (int i=0; i<N; i++)
			Order.add(i);
		return Order;
	}
	
	static double[] Percept(double data_point[], double weights[][], double output[]){
		int h = 0, beta = 1;
		
		for (int j=0; j<weights[0].length; j++) {
			for (int i=0; i<weights.length; i++)
				h += ( weights[i][j]*data_point[i] );
			output[j] = 1 / (1 + Math.pow(Math.E, -beta*h));
		}
		
		return output;
	}
	
	static double[] BinaryOut(double output[]){
		int index_of_max = 0;
		for (int i=0; i<output.length; i++) 
			if (output[i] >= output[index_of_max])
				index_of_max = i;
		for (int i=0; i<output.length; i++) 
			output[i] = (i == index_of_max) ? 1 : 0;
		return output;
	}
	
	static void NNLearn(double X[][], double W[][], double U[][], double T[][], double rate){
		double error = 100;
		double[] A = new double[hid_nodes+1];
		double[][] Y = new double[N][out_nodes];
		ArrayList<Integer> Order = CreateOrder();
		
		for (int p=0; p<12000; p++){
			error=0;
			
			// randomization of data points
			Collections.shuffle(Order);
			
			// learning algorithm
			for (int k=0; k<N; k++){
				
				// feed_forward step
				int index = Order.get(k);
				A = Percept(X[index], W, A);
				A[hid_nodes] = -1;
				Y[index] = BinaryOut(Percept(A, U, Y[index]));
				
				// weight_update step
				
				//-sigma calculations
				double[] DeltaJO = new double[Y[0].length];
				for (int i=0; i<DeltaJO.length; i++)
					DeltaJO[i] = Y[index][i] * (1 - Y[index][i]) * (T[index][i] - Y[index][i]);
				
				
				double[] DeltaKH = new double[A.length]; 

				for (int i=0; i<DeltaKH.length; i++){
					double summation = 0;
					for (int c=0; c<out_nodes; c++)
						summation += U[i][c] * DeltaJO[c];
					
					DeltaKH[i] = A[i] * (1 - A[i]) * summation;
				}
				
				//-weight updates
				for (int j=0; j<U[0].length; j++)
					for (int i=0; i<U.length; i++)
						U[i][j] = U[i][j] + rate * DeltaJO[j] * A[i];
				
				for (int j=0; j<W[0].length; j++)
					for (int i=0; i<W.length; i++)
						W[i][j] = W[i][j] + rate * DeltaKH[j] * X[0][i];
				
				
				// error_calculation step
				for (int i=0; i<T[0].length; i++)
					error += Math.pow( (T[index][i] - Y[index][i]) , 2);
			}
		}
		
		error = error/2;
		
		// printing
		PrintResults(W, U, error);
	}
	
	static void PrintWeights(double weights[][]){
		String s;
		for (int i=0; i<weights.length; i++){
			s = "";
			for (int j=0; j<weights[0].length; j++)
				s = s + weights[i][j] + "\t";
			System.out.println(s);
		}
	}
	
	static void PrintResults(double[][] W, double[][] U, double error){
		System.out.println("W");
		PrintWeights(W);
		System.out.println("");
		
		System.out.println("U");
		PrintWeights(U);
		System.out.println("");
		
		System.out.println("Error\n" + error);
	}

}
