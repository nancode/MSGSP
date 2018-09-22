import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	public static void main(String args[]){
		File sequenceFile = new File("./src/main/resources/inputdata/sequences.txt");
		String parameterFilePath = "./src/main/resources/inputdata/parameters.txt";

		ArrayList<List> sequenceCollection = getSequenceCollection(sequenceFile);
		
		HashMap<Integer,Float> mis_values = readParamsFile(parameterFilePath);
		printHashMap(mis_values);
		

	}

	private static ArrayList<List> getSequenceCollection(File sequenceFile) {
		ArrayList<List> sequenceCollection = new ArrayList<List>();

		//Pattern to extract item sets
		String re1=".*?";	// Non-greedy match on filler
		String re2="(\\{.*?\\})";
		Pattern p = Pattern.compile(re1+re2,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

		String temp = "";
		Scanner sc = null;
		try {
			sc = new Scanner(sequenceFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while(sc.hasNextLine()){
			List<List> sequence = new ArrayList<List>();
			temp = sc.nextLine();
			Matcher m = p.matcher(temp);
			while (m.find())
			{
				List<Integer> itemset = new ArrayList<Integer>();
				String cbraces1=m.group();

				//Pattern to extract the items
				Pattern p1 = Pattern.compile("[\\d]+");
				Matcher m1 = p1.matcher(cbraces1);
				while (m1.find()){
					itemset.add(Integer.parseInt(m1.group()));
				}
				sequence.add(itemset);
			}
			sequenceCollection.add(sequence);
		}

		for (List<List> sequence:
			sequenceCollection) {
			System.out.println("Sequence:");
			for (List<Integer> itemset:
				sequence) {
				System.out.println("\t Itemset: ");
				for (Integer item:
					itemset) {
					System.out.print("\t\t"+item+" ");
				}
				System.out.println();
			}

		}
		sc.close();
		return sequenceCollection;
	}
	
	/* Function to read the MIS values and the SDC value from the parameter.txt file
	 * and write it into hashmap. 
	 * Input :  String with the file path. 
	 * Output : HashMap of MIS values and a float with SDC value.
	 */
	
	private static HashMap<Integer, Float> readParamsFile(String parameterfilePath) {
		
		//Map containing all the MIS values given in the parameters.txt file.
		HashMap<Integer, Float> mis_values = new HashMap<Integer, Float>();
		float sdc_value;
		try
		{
			//Using FileInputStream in the draft, could be changed later. 
			FileInputStream fileReader = new FileInputStream(parameterfilePath);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileReader));

			String text = null;
			while ((text = bufferedReader.readLine()) != null) {
				//To filter those lines containing MIS keyword.
				if(text.contains("MIS(")) {
					//this pattern compiles integer/float/character datatypes.
					Pattern p_mis = Pattern.compile("MIS\\((([([A-Za-z0-9]*[.])?[0-9]+]+))\\) = ([([0-9]*[.])?[0-9]+]+)",Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
					Matcher m_mis = p_mis.matcher(text);
					if (m_mis.find())
					{
						//group(1) would take only the integer part if a decimal value were to come, so use group(2).
					mis_values.put(Integer.valueOf(m_mis.group(2)), Float.valueOf(m_mis.group(3)));
					}
				}
				//filters only lines with SDC
				else if(text.contains("SDC")){
					//Pattern matches any java float/integer value.
					Pattern p_sdc = Pattern.compile("SDC = ([([0-9]*[.])?[0-9]+]+)",Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
					Matcher m_sdc = p_sdc.matcher(text);
					if (m_sdc.find())
					{
					sdc_value = Float.valueOf(m_sdc.group(1));
					System.out.println("Printing SDC Value : " + sdc_value);
					}
				}
			}
			bufferedReader.close();
			
		}
		catch( IOException e )
		{
			System.out.print(e);
		}
		return mis_values;
	}
	
	/*just to print the values in the hash map*/
	private static void printHashMap(HashMap<Integer,Float> hashmap) {
		System.out.println("Printing HashMap Values....");
		for (Integer key:hashmap.keySet()) {
			System.out.println("Key " + key +" Value " + hashmap.get(key));
		}
	}
}
