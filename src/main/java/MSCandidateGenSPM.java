import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MSCandidateGenSPM {
    ArrayList<List<List>> candidateSequenceCollection = new ArrayList<>();
        public ArrayList<List<List>> join(ArrayList<List<List>> frequentItemSet, ArrayList<List> sequenceCollection, HashMap<Integer, Float> parameters){

            for(List<List> subSet1: frequentItemSet){
                for(List<List> subset2: frequentItemSet){
                    System.out.println("Subset1: "+subSet1);
                    System.out.println("Subset2:"+subset2);
                    //if the MIS value of the first item in a sequence (denoted by s1) is less than (<) the MIS value of every other item in s1
                    boolean isS1FirstItemMISMin = isFirstItemMISMin(subSet1, parameters);
                    boolean isS2LastItemMISMin = isLastItemMISMin(subset2,parameters);
                    if(isS1FirstItemMISMin){
                        int s2LastItemIndex = getLength(subset2)-1;
                        boolean isS1EqualS2 = isEqual(subSet1,subset2,1,s2LastItemIndex);
                        Float misS1FirstItem  = parameters.get(subSet1.get(0).get(0));
                        Integer s2LastItem = getItem(subset2,s2LastItemIndex);
                        Float misS2LastItem  = parameters.get(s2LastItem);
                        int s1Length = getLength(subSet1);
                        int s1size = subSet1.size();
                        Integer s1LastItem = getItem(subSet1,s1Length-1);

                        //Sequence s1 joins with s2
                        // if (1) the subsequences obtained by dropping the second item of s1 and the last item of s2 are the same, and
                        // (2) the MIS value of the last item of s2 is greater than that of the first item of s1.
                        if(isS1EqualS2 && (misS1FirstItem < misS2LastItem) ){
                              //if last item in l in s2 is a separate element
                              if(isLastItemSeperate(subset2)){
                                    List<List> newSequenceC1 = getCSeqWithLastItemSeperate(subSet1, subset2);
                                    candidateSequenceCollection.add(newSequenceC1);
                                    System.out.println("c1 ="+newSequenceC1);

                                    //(the length and the size of s1 are both 2) AND (the last item of s2 is greater than the last item of s1)
                                    /*int s1Length = getLength(subSet1);
                                    int s1size = subSet1.size();
                                    Integer s1LastItem = getItem(subSet1,s1Length-1);*/

                                    if((s1Length==2 && s1size ==2 && (s2LastItem > s1LastItem))){// maintain lexicographic order
                                        //l is added at the end of the last element of s1 to form another candidate sequence c2.


                                        List<List> newSequenceC2 = getCSeqWithItemToLastItemset(subSet1, s2LastItem, s1size);
                                        candidateSequenceCollection.add(newSequenceC2);
                                        System.out.println("C2="+newSequenceC2);

                                    }
                              }

                              else if(((s1Length==2 && s1size ==1)&&(s2LastItem>s1LastItem)) || s1Length>2){
                                  List<List> newSequence = getCSeqWithItemToLastItemset(subSet1, s2LastItem, s1size);
                                  candidateSequenceCollection.add(newSequence);
                                  System.out.println("C2="+newSequence);
                              }
                        }

                    }

                    else if(isS2LastItemMISMin){
                        //write the logic for this
                    }
                    else {
                        //Join step of GSP
                        int s2LastItemIndex = getLength(subset2)-1;
                        boolean isS1EqualS2 = isEqual(subSet1,subset2,0,s2LastItemIndex);
                        if(isS1EqualS2){
                            if(isLastItemSeperate(subset2)){
                                List<List> newSequenceC1 = getCSeqWithLastItemSeperate(subSet1, subset2);
                                candidateSequenceCollection.add(newSequenceC1);
                                System.out.println("c1 ="+newSequenceC1);
                            }else{
                                Integer s2LastItem = getItem(subset2,s2LastItemIndex);
                                List<List> newSequence = getCSeqWithItemToLastItemset(subSet1, s2LastItem, subSet1.size());
                                candidateSequenceCollection.add(newSequence);
                                System.out.println("c1 ="+newSequence);
                            }

                        }

                    }


                }
            }


            //Prune the candidate sequence list
            prune(candidateSequenceCollection,sequenceCollection);

            return candidateSequenceCollection;
        }

    private void prune(ArrayList<List<List>> candidateSequenceCollection, ArrayList<List> sequenceCollection) {

    }

    private List<List> getCSeqWithLastItemSeperate(List<List> subSet1, List<List> subset2) {
        List<List> newSequence = new ArrayList<>();
        newSequence.addAll(subSet1);
        newSequence.add(subset2.get(subset2.size()-1));
        return newSequence;
    }

    private List<List> getCSeqWithItemToLastItemset(List<List> subSet1, Integer s2LastItem, int s1size) {
        List<Integer> lastItemset = new ArrayList<>();
        //Copying the items from last itemset of s1 to a new itemset
        int s1lastIndex=s1size-1;
        for(int i=0;i<subSet1.get(s1lastIndex).size();i++){
            lastItemset.add((Integer) subSet1.get(s1lastIndex).get(i));
        }
        //Adding last item of s2 to the new itemset
        lastItemset.add(s2LastItem);

        //Create new sequence
        List<List> newSequence = new ArrayList<>();
        for(int i=0; i<(subSet1.size()-1);i++){
            newSequence.add(subSet1.get(i));
        }
        newSequence.add(lastItemset);
        return newSequence;
    }

    private boolean isLastItemSeperate(List<List> subset2) {
            int size = subset2.size();
            if(subset2.get(size-1).size()==1){
                return true;
            }
            return false;
    }

    private Integer getItem(List<List> subset, int index) {
        Integer it = null;
        int ind=0;
        for(List itemset: subset){
            for(Object item: itemset){
                if(ind==index) {
                    return (Integer) item;
                }
                ind++;
            }
        }
        return it;
    }

    private boolean isEqual(List<List> subSet1, List<List> subset2, int ignore1, int ignore2) {
        List<Integer> itemList1 = new ArrayList();
        List<Integer> itemList2 = new ArrayList<>();
        int ind=0;
        for(List itemset: subSet1){
            for(Object item: itemset){
                if(ind!=ignore1){
                    itemList1.add((Integer) item);
                }
                ind++;
            }
        }
        //System.out.println(itemList1);

        ind = 0 ;
        for(List itemset: subset2){
            for(Object item: itemset){
                if(ind!=ignore2){
                    itemList2.add((Integer) item);
                }
                ind++;
            }
        }

        //System.out.println(itemList2);
        if(itemList1.containsAll(itemList2)){
            return true;
        }
        return false;
    }

    private boolean isFirstItemMISMin(List<List> subSet1, HashMap<Integer, Float> parameters) {
        int minInd = getMinIndex(subSet1, parameters);

            if(minInd==0){
                return true;
            }
            return false;
    }

    private boolean isLastItemMISMin(List<List> subSet1, HashMap<Integer, Float> parameters) {
        int minInd = getMinIndex(subSet1, parameters);
        int length = getLength(subSet1);

        if(minInd==length-1){
            return true;
        }
        return false;
    }

    private int getLength(List<List> subSet1) {
            int length = 0;
        for(List itemset: subSet1){
            for(Object item: itemset){
               length++;
            }
        }
       // System.out.println(length);
        return length;
    }

    private int getMinIndex(List<List> subSet1, HashMap<Integer, Float> parameters) {
        int minInd = -1;
        int i=0;
        Object minItem = 0;
        for(List itemset: subSet1){
            for(Object item: itemset){
                //System.out.println(i);
                if(minInd ==-1){
                    minInd = i;
                    minItem =  item;
                    //System.out.println(minItem);
                }
                else {
                    //System.out.println(parameters.get(item)+" ?? "+parameters.get(minItem));
                    if(parameters.get(item)<parameters.get(minItem)){
                        minInd = i;
                        minItem =  item;
                    }
                }
                i++;
            }
        }
        return minInd;
    }

    public static void main(String args[]){
            //Main main = new Main();
            HashMap<Integer, Float> parameter = Main.readParamsFile("./src/main/resources/inputdata/parameters.txt");
            MSCandidateGenSPM msCandidateGenSPM = new MSCandidateGenSPM();

        //Example to check finding support count of a list of candidate sequences
        //Candidate sequence <{30}{40,70}>
        List<Integer> tmp1 = new ArrayList<>();
        tmp1.add(30);
        tmp1.add(40);
        List<Integer> tmp2 = new ArrayList<>();
       // tmp2.add(40);
       // tmp2.add(70);
        List<List> itemSet = new ArrayList<>();
        itemSet.add(tmp1);
      //  itemSet.add(tmp2);

        //Candidate sequence <{10, 20}>
        List<Integer> tmp3 = new ArrayList<>();
        tmp3.add(30);
         tmp3.add(70);
        List<Integer> tmp4 = new ArrayList<>();
       // tmp4.add(70);
        List<List> itemSet2 = new ArrayList<>();
        itemSet2.add(tmp3);
      //  itemSet2.add(tmp4);
        ArrayList<List<List>> candidateSequenceList = new ArrayList<>();
        candidateSequenceList.add(itemSet);
        candidateSequenceList.add(itemSet2);

        msCandidateGenSPM.join(candidateSequenceList,parameter);
       // System.out.println(msCandidateGenSPM.isEqual(itemSet,itemSet2,1,msCandidateGenSPM.getLength(itemSet2)-1));
       // System.out.println(msCandidateGenSPM.isLastItemMISMin(itemSet,parameter));
    }
}
