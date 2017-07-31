import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

public class online_k_means {

	static Vector< Vector<Double> > dataset = new Vector< Vector<Double> >();
	static Vector< Vector<Double> > clusters = new Vector< Vector<Double> >();
	static int N, M, K;
	static double rate;

	/* Main function */

	public static void main(String[] args) throws IOException {
		// Computing
		ReceiveData();
		SetClusters();
		double error = online_k_means_algorithm();

		// Results
		PrintClusterCentres();
		PrintError(error);
	}

	/* Algorithm data functions */

	static void ReceiveData() throws IOException{
		try {

			Scanner in = new Scanner(System.in);
			System.out.print("Path of dataset file: \n");
			BufferedReader br = new BufferedReader(new FileReader(new File(in.nextLine())));
			System.out.println("Number of clusters: ");
			K = in.nextInt();
			in.close();

			String line = null;
			while ( (line = br.readLine()) != null ){
				String[] splitLine = line.split(" ");
				Vector<Double> d = new Vector<>();
				for ( String s : splitLine){
					d.add(Double.parseDouble(s));
				}
				dataset.add(d);
			}
			br.close();

			N = dataset.size();
			M = dataset.get(0).size();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	static void SetClusters(){
		for (int k=0; k<K; k++){
			Vector<Double> random = new Vector<Double>();
			for (int j=0; j<M; j++){
				random.add( Math.random() );
			}
			clusters.add(random);
		}
	}

	/* Algorithm helper functions */

	static double EuclidDist(Vector<Double> x, Vector<Double> u){
		double dist = 0;
		for (int j=0; j<M; j++)
			dist += Math.pow(x.get(j) - u.get(j), 2);
		return Math.sqrt(dist);
	}

	static double Error(Vector<Double> Nj, Vector<Vector<Integer>> clusterings){
		double sos=0;
		for (int k=0; k<K; k++)
			for (int i=0; i<Nj.get(k); i++)
				sos += Math.pow( EuclidDist(dataset.get( clusterings.get(k).get(i) ), clusters.get(k)), 2 );
		return sos;
	}

	/* Algorithm core function */

	static double online_k_means_algorithm(){

		double error = -1;

		//while ( /* stopping condition*/ ){
		for (int s=0; s<12000; s++){

			/*
			 *	The Nj vector is a vector of k entries where each entry represents a cluster centre.
			 *	The element of each entry is the number of data points x that have been assigned to that cluster centre
			 *
			 *	The clusterings vector is a vector of k entries where each entry represents a cluster centre.
			 *	The elements of each entry are the indices of the data points x that have been
			 *	assigned to that cluster centre.
			 */
			Vector<Double> Nj = new Vector<Double>();
			Vector<Vector<Integer>> clusterings = new Vector<Vector<Integer>>();
			for (int k=0; k<K; k++){
				Vector<Integer> v = new Vector<Integer>();
				clusterings.add(v);
			}

			for (int x=0; x<N; x++){

				int cluster = -1;
				double min = 1000000;

				// ASSIGN each data point x to a cluster
				for (int k=0; k<K; k++){
					double dist = EuclidDist( dataset.get(x), clusters.get(k) );
					if ( dist <= min ){
						min = dist;
						cluster = k;
					}
				}
				clusterings.get(cluster).add(x);

				// UPDATE the respective cluster centre
				for (int j=0; j<M; j++){
					rate = 1/(s+1);
					double uVal = clusters.get(cluster).get(j);
					double xVal = dataset.get(x).get(j);
					clusters.elementAt(cluster).set( j,  uVal + rate * (xVal - uVal) );
				}
			}

			// Set values of Nj
			for (int k=0; k<K; k++){
				double points = clusterings.get(k).size();
				Nj.add(points);
			}

			// ERROR calculation based on sum-of-squares
			error = Error(Nj, clusterings);

		}
		return error;
	}

	/* Algorithm results functions */

	static void PrintClusterCentres(){
		System.out.println("\nCluster centres: ");
		for (int i=0; i<clusters.size(); i++){
			String str = "";
			for (int j=0; j<clusters.get(i).size(); j++)
				str += ( clusters.get(i).get(j) + " " );
			System.out.println(str);
		}
	}

	static void PrintError(double error){
		System.out.println( "\nSum of squares error:\n" + error + '\n');
	}

}
