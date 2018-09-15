import javax.sound.midi.Soundbank;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String args[]){
        File sequenceFile = new File("./src/main/resources/inputdata/sequences.txt");
        File parameterFile = new File("./src/main/resources/inputdata/parameters.txt");

        ArrayList<List> sequenceCollection = getSequenceCollection(sequenceFile);

    }

    private static ArrayList<List> getSequenceCollection(File sequenceFile) {
        ArrayList<List> sequenceCollection = new ArrayList<>();

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
            List<List> sequence = new ArrayList<>();
            temp = sc.nextLine();
            Matcher m = p.matcher(temp);
            while (m.find())
            {
                List<Integer> itemset = new ArrayList<>();
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
}
