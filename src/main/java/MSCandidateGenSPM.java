import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MSCandidateGenSPM {


    ArrayList<List<List>> candidateSequenceCollection = new ArrayList<>();
    HashMap<Integer, Float> parameters = new HashMap<>();
    Map<Integer,Integer> supportCount = new HashMap<>();
    float sdc_value;
    int sequenceCollectionSize;

    MSCandidateGenSPM(HashMap<Integer, Float> parameters, Map<Integer, Integer> supportCount, float sdc_value, int sequenceCollectionSize){
        this.parameters = parameters;
        this.supportCount = supportCount;
        this.sdc_value = sdc_value;
        this.sequenceCollectionSize = sequenceCollectionSize;
    }


    public ArrayList<List<List>> join(ArrayList<List<List>> frequentItemSet){

            for(List<List> subSet1: frequentItemSet){
                for(List<List> subset2: frequentItemSet){
                    //if the MIS value of the first item in a sequence (denoted by s1) is less than (<) the MIS value of every other item in s1
                    boolean isS1FirstItemMISMin = isFirstItemMISMin(subSet1, parameters);
                    boolean isS2LastItemMISMin = isLastItemMISMin(subset2,parameters);
                    int s2LastItemIndex = getLength(subset2)-1;
                    boolean isS1EqualS2 = isEqual(subSet1,subset2,1,s2LastItemIndex);
                    Float misS1FirstItem  = parameters.get(subSet1.get(0).get(0));
                    Integer s2LastItem = getItem(subset2,s2LastItemIndex);
                    Float misS2LastItem  = parameters.get(s2LastItem);


                    //Sequence s1 joins with s2
                    // if (1) the subsequences obtained by dropping the second item of s1 and the last item of s2 are the same, and
                    // (2) the MIS value of the last item of s2 is greater than that of the first item of s1.
                    if(isS1FirstItemMISMin && isS1EqualS2 && (misS1FirstItem < misS2LastItem) ){

                        int s1Length = getLength(subSet1);
                        int s1size = subSet1.size();
                        Integer s1LastItem = getItem(subSet1,s1Length-1);
                              //if last item in l in s2 is a separate element
                              if(isItemSeperate(subset2,subset2.size()-1)){
                                    getCSeqWithLastItemSeperate(subSet1, subset2);

                                    //(the length and the size of s1 are both 2) AND (the last item of s2 is greater than the last item of s1)
                                    if((s1Length==2 && s1size ==2 && (s2LastItem > s1LastItem))){// maintain lexicographic order
                                        //l is added at the end of the last element of s1 to form another candidate sequence c2.
                                        getCSeqAddingItemInLastItemset(subSet1, s2LastItem, s1size);
                                    }
                              }

                              else if(((s1Length==2 && s1size ==1)&&(s2LastItem>s1LastItem)) || s1Length>2){
                                  getCSeqAddingItemInLastItemset(subSet1, s2LastItem, s1size);
                              }


                    }
                    //the MIS value of the last item in a sequence (denoted by s2) is less than (<) the MIS value of every other item in s2
                    else if(isS2LastItemMISMin && isEqual(subSet1,subset2,0,s2LastItemIndex-1) && (misS1FirstItem > misS2LastItem)){

                        int s2Length = getLength(subset2);
                        int s2size = subset2.size();
                        if(isItemSeperate(subSet1,0)){
                            List<List> newSequenceC1 = new ArrayList<>();
                            newSequenceC1.add(subSet1.get(0));
                            newSequenceC1.addAll(subset2);
                            if(!candidateSequenceCollection.contains(newSequenceC1)) {
                                        candidateSequenceCollection.add(newSequenceC1);
                            }


                            if((s2Length==2 && s2size ==2 && (getItem(subSet1,0) < getItem(subset2,0)))){// maintain lexicographic order
                                joinItemsetsInIndex0(subSet1, subset2);

                            }
                        }

                        else if(((s2Length==2 && s2size ==1)&&(getItem(subSet1,0) < getItem(subset2,0))) || s2Length>2){
                            joinItemsetsInIndex0(subSet1,subset2);
                        }

                    }
                    else {
                        //Join step of GSP
                        //int s2LastItemIndex = getLength(subset2)-1;
                        boolean isS1EqualS2Cond = isEqual(subSet1,subset2,0,s2LastItemIndex);
                        if(isS1EqualS2Cond){
                            if(isItemSeperate(subset2,subset2.size()-1)){
                                 getCSeqWithLastItemSeperate(subSet1, subset2);
                            }else{
                                 getCSeqAddingItemInLastItemset(subSet1, s2LastItem, subSet1.size());
                            }

                        }

                    }


                }
            }

            for(List sequence:candidateSequenceCollection) {
                System.out.println(sequence);
            }

            return candidateSequenceCollection;
    }

    public ArrayList<List<List>> prune(ArrayList<List<List>> frequentItemset) {
        ArrayList<List<List>> prunedCandidateSequence = new ArrayList<>();

            for(List<List> candidateSequence: candidateSequenceCollection){
                Boolean noSubseq = false;
                System.out.println("candidate seq = "+candidateSequence);
                System.out.println("Inital nosubseq = "+noSubseq);
                if(getMininumSupportDifference(candidateSequence)) {
                    for (int j = 0; j < candidateSequence.size(); j++) {
                        for (int i = 0; i < candidateSequence.get(j).size(); i++) {
                            //System.out.println("Item = "+candidateSequence.get(j).get(i));
                            List<List> subsequence = new ArrayList<>();
                            subsequence.addAll(candidateSequence);
                            subsequence.remove(j);
                            if (candidateSequence.get(j).size() != 1) {
                                System.out.println();
                                List<Integer> tempItemset = new ArrayList<>();
                                tempItemset.addAll(candidateSequence.get(j));
                                tempItemset.remove(i);
                                subsequence.add(j, tempItemset);
                            }
                            //System.out.println("Candidate seq = "+candidateSequence);
                            // System.out.println("subsequ = "+subsequence);
                            if (containsLowestMIS(subsequence, candidateSequence) && (!findIfSubsequence(subsequence, frequentItemset))) {
                                System.out.println("Does not contain subseq " + subsequence);
                                noSubseq = true;
                                break;
                            }
                        }
                        if (noSubseq) {
                            System.out.println("Does not contain subseq");
                            break;
                        }
                    }
                    System.out.println("For candidate seq NoSubseq = " + noSubseq);
                    if (!noSubseq) {
                        System.out.println("Adding seq");
                        prunedCandidateSequence.add(candidateSequence);
                    }
                }
               // noSubseq =false;
            }

            System.out.println(prunedCandidateSequence);
            return prunedCandidateSequence;

    }

    private boolean getMininumSupportDifference(List<List> candidateSequence) {
        float minSup = (float) 0.0;
        float maxSup = (float) 0.0;
        for(List<Integer> itemset: candidateSequence){
            for(Integer item: itemset){
                float support = supportCount.get(item)/sequenceCollectionSize;
                if(support<minSup){
                    minSup = support;
                }
                if(support>maxSup){
                    maxSup = support;
                }
            }
        }
        if(Math.abs(maxSup-minSup)<=sdc_value){
            return true;
        }
        return false;
    }

    private boolean containsLowestMIS(List<List> subsequence, List<List> candidateSequence) {
        int ind = getMinIndex(candidateSequence,parameters);
        Integer candItem = getItem(candidateSequence,ind);
        for(List<Integer> itemset: subsequence){
            for(Integer item: itemset){
               // for(Integer item: itemset){
                    if(item.equals(candItem)){
                        return true;
                    }
              //  }
            }
        }

        return false;
    }

    private boolean findIfSubsequence(List<List> subsequence, ArrayList<List<List>> frequentItemset) {
        HashMap<List<List>, Integer> supportCount = new HashMap<>();

       // for(List<List> candidateSequence: candidateSequenceList) {
            for (List<List> sequence : frequentItemset) {
                int i = 0;
                //List<Integer> candidateItem =
                for (List<Integer> itemset : sequence) {
                    if (i < subsequence.size()) {
                        // System.out.println(itemset + "---" + candidateSequence.get(i));
                        if (itemset.containsAll(subsequence.get(i))) {
                            i++;
                        }
                    }
                }
                // System.out.println("i == " + i);
                if (i == subsequence.size()) {
                    return true;
                }
            }
       // }


        return false;

    }

    private void joinItemsetsInIndex0(List<List> subSet1, List<List> subset2) {
        List<Integer> newItemset = new ArrayList<>();
        newItemset.addAll(subSet1.get(0));
        newItemset.addAll(subset2.get(0));
        List<List> newSequenceC2 = new ArrayList<>();
        newSequenceC2.add(newItemset);
        for(int i=1;i<subset2.size();i++) {
            newSequenceC2.add(subset2.get(i));
        }
        if(!candidateSequenceCollection.contains(newSequenceC2)) {
            candidateSequenceCollection.add(newSequenceC2);
        }
    }

    private void getCSeqWithLastItemSeperate(List<List> subSet1, List<List> subset2) {
        List<List> newSequence = new ArrayList<>();
        newSequence.addAll(subSet1);
        newSequence.add(subset2.get(subset2.size()-1));
        if(!candidateSequenceCollection.contains(newSequence)) {
            candidateSequenceCollection.add(newSequence);
        }
    }

    private void getCSeqAddingItemInLastItemset(List<List> subSet1, Integer s2LastItem, int s1size) {
        List<Integer> lastItemset = new ArrayList<>();
        //Copying the items from last itemset of s1 to a new itemset
        int s1lastIndex=s1size-1;
        lastItemset.addAll(subSet1.get(s1lastIndex));
        //Adding last item of s2 to the new itemset
        lastItemset.add(s2LastItem);

        //Create new sequence
        List<List> newSequence = new ArrayList<>();
        for(int i=0; i<(subSet1.size()-1);i++){
            newSequence.add(subSet1.get(i));
        }
        newSequence.add(lastItemset);
        if(!candidateSequenceCollection.contains(newSequence)) {
            candidateSequenceCollection.add(newSequence);
        }
    }

    private boolean isItemSeperate(List<List> subset2, int index) {
            //int size = subset2.size();
            if(subset2.get(index).size()==1){
                return true;
            }
            return false;
    }

    public Integer getItem(List<List> subset, int index) {
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

    public int getMinIndex(List<List> subSet1, HashMap<Integer, Float> parameters) {
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


    //Testing MSCandidateGenSPM
   /* public static void main(String args[]){
            //Main main = new Main();
            HashMap<Integer, Float> parameter = Main.readParamsFile("./src/main/resources/inputdata/parameters.txt");

        //    MSCandidateGenSPM msCandidateGenSPM = new MSCandidateGenSPM(parameter, supportCount, sdc_value, sequenceCollection.size());

        //Example to check finding support count of a list of candidate sequences
        //Candidate sequence <{30}{40,70}>
        List<Integer> tmp1 = new ArrayList<>();
        tmp1.add(1);
       // tmp1.add(4);
        List<Integer> tmp2 = new ArrayList<>();
         tmp2.add(4);
        List<Integer> tmp3 = new ArrayList<>();
        tmp3.add(5);
       // tmp2.add(70);
        List<List> itemSet = new ArrayList<>();
        itemSet.add(tmp1);
        itemSet.add(tmp2);
        itemSet.add(tmp3);

        //Candidate sequence <{10, 20}>
        List<Integer> tmp4 = new ArrayList<>();
        tmp4.add(1);
        // tmp3.add(4);
        List<Integer> tmp5 = new ArrayList<>();
        tmp5.add(4);

        // tmp3.add(4);
        List<Integer> tmp6= new ArrayList<>();
        tmp6.add(6);
        List<List> itemSet2 = new ArrayList<>();
        itemSet2.add(tmp4);
        itemSet2.add(tmp5);
        itemSet2.add(tmp6);


        //Candidate sequence <{10, 20}>
        List<Integer> tmp7 = new ArrayList<>();
        tmp7.add(1);
        // tmp3.add(4);
        List<Integer> tmp8 = new ArrayList<>();
        tmp8.add(5);

        // tmp3.add(4);
        List<Integer> tmp9= new ArrayList<>();
        tmp9.add(6);
        List<List> itemSet3 = new ArrayList<>();
        itemSet3.add(tmp7);
        itemSet3.add(tmp8);
        itemSet3.add(tmp9);

        //Candidate sequence <{10, 20}>
        List<Integer> tmp10 = new ArrayList<>();
        tmp10.add(1);
        //tmp10.add(5);

        // tmp3.add(4);
        List<Integer> tmp11= new ArrayList<>();
        tmp11.add(5);
        tmp11.add(6);
        List<List> itemSet4 = new ArrayList<>();
        itemSet4.add(tmp10);
        itemSet4.add(tmp11);


        //Candidate sequence <{10, 20}>
        List<Integer> tmp12 = new ArrayList<>();
        tmp12.add(1);
        List<Integer> tmp13 = new ArrayList<>();
        tmp13.add(6);
        List<Integer> tmp14= new ArrayList<>();
        // tmp11.add(5);
        tmp14.add(3);
        List<List> itemSet5 = new ArrayList<>();
        itemSet5.add(tmp12);
        itemSet5.add(tmp13);
        itemSet5.add(tmp14);

        //Candidate sequence <{10, 20}>
        List<Integer> tmp15 = new ArrayList<>();
        tmp15.add(6);
        List<Integer> tmp16 = new ArrayList<>();
        tmp16.add(3);
        List<Integer> tmp17= new ArrayList<>();
        tmp17.add(6);
        List<List> itemSet6 = new ArrayList<>();
        itemSet6.add(tmp15);
        itemSet6.add(tmp16);
        itemSet6.add(tmp17);

        //Candidate sequence <{10, 20}>
        List<Integer> tmp18 = new ArrayList<>();
        tmp18.add(5);
        tmp18.add(6);
        List<Integer> tmp19= new ArrayList<>();
        tmp19.add(3);
        List<List> itemSet7 = new ArrayList<>();
        itemSet7.add(tmp18);
        itemSet7.add(tmp19);

        //Candidate sequence <{5}{4}{3}>
        List<Integer> tmp20 = new ArrayList<>();
        tmp20.add(5);
        List<Integer> tmp21 = new ArrayList<>();
        tmp21.add(4);
        List<Integer> tmp22= new ArrayList<>();
        tmp22.add(3);
        List<List> itemSet8 = new ArrayList<>();
        itemSet8.add(tmp20);
        itemSet8.add(tmp21);
        itemSet8.add(tmp22);

        //Candidate sequence <{4}{5}{3}>
        List<Integer> tmp23 = new ArrayList<>();
        tmp23.add(4);
        List<Integer> tmp24 = new ArrayList<>();
        tmp24.add(5);
        List<Integer> tmp25= new ArrayList<>();
        tmp25.add(3);
        List<List> itemSet9 = new ArrayList<>();
        itemSet9.add(tmp23);
        itemSet9.add(tmp24);
        itemSet9.add(tmp25);

        ArrayList<List<List>> frequentItemSet = new ArrayList<>();
        frequentItemSet.add(itemSet);
        frequentItemSet.add(itemSet2);
        frequentItemSet.add(itemSet3);
        frequentItemSet.add(itemSet4);
        frequentItemSet.add(itemSet5);
        frequentItemSet.add(itemSet6);
        frequentItemSet.add(itemSet7);
        frequentItemSet.add(itemSet8);
        frequentItemSet.add(itemSet9);


        msCandidateGenSPM.join(frequentItemSet);
        msCandidateGenSPM.prune(frequentItemSet);*/
       // System.out.println(msCandidateGenSPM.isEqual(itemSet,itemSet2,1,msCandidateGenSPM.getLength(itemSet2)-1));
       // System.out.println(msCandidateGenSPM.isLastItemMISMin(itemSet,parameter));
    //}
}
