import java.util.*;

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

            return candidateSequenceCollection;
    }

    public ArrayList<List<List>> prune(ArrayList<List<List>> frequentItemset) {
        List<Integer> tmp1 = new ArrayList<>();
        tmp1.add(2);
        tmp1.add(13);
        List<Integer> tmp2 = new ArrayList<>();
        tmp2.add(7);
        List<List> itemSet = new ArrayList<>();
        itemSet.add(tmp1);
        itemSet.add(tmp2);
        ArrayList<List<List>> prunedCandidateSequence = new ArrayList<>();

            for(List<List> candidateSequence: candidateSequenceCollection){
                Boolean noSubseq = false;
                if(getMininumSupportDifference(candidateSequence)) {
                    for (int j = 0; j < candidateSequence.size(); j++) {
                        for (int i = 0; i < candidateSequence.get(j).size(); i++) {
                            List<List> subsequence = new ArrayList<>();
                            subsequence.addAll(candidateSequence);
                            subsequence.remove(j);
                            if (candidateSequence.get(j).size() != 1) {
                                List<Integer> tempItemset = new ArrayList<>();
                                tempItemset.addAll(candidateSequence.get(j));
                                tempItemset.remove(i);
                                subsequence.add(j, tempItemset);
                            }
                            if (containsLowestMIS(subsequence, candidateSequence) && (!findIfSubsequence(subsequence, frequentItemset))) {
                                noSubseq = true;
                                break;
                            }
                        }
                        if (noSubseq) {
                            break;
                        }
                    }
                    if (!noSubseq) {
                        prunedCandidateSequence.add(candidateSequence);
                    }
                }
            }
         return prunedCandidateSequence;

    }

    private boolean getMininumSupportDifference(List<List> candidateSequence) {
        List<Integer> tmp1 = new ArrayList<>();
        tmp1.add(2);
        tmp1.add(13);
        List<Integer> tmp2 = new ArrayList<>();
        tmp2.add(7);
        List<List> itemSet = new ArrayList<>();
        itemSet.add(tmp1);
        itemSet.add(tmp2);

        float minSup = Float.MAX_VALUE;
        float maxSup = Float.MIN_VALUE;
        for(List<Integer> itemset: candidateSequence){
            for(Integer item: itemset){

                float support = (float)supportCount.get(item)/sequenceCollectionSize;
                if(minSup>support){
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
                    if(item.equals(candItem)){
                        return true;
                    }
            }
        }

        return false;
    }

    private boolean findIfSubsequence(List<List> subsequence, ArrayList<List<List>> frequentItemset) {
            for (List<List> sequence : frequentItemset) {
                int i = 0;
                for (List<Integer> itemset : sequence) {
                    if (i < subsequence.size()) {
                        if (itemset.containsAll(subsequence.get(i))) {
                            i++;
                        }
                    }
                }
                if (i == subsequence.size()) {
                    return true;
                }
            }

        return false;

    }

    private void joinItemsetsInIndex0(List<List> subSet1, List<List> subset2) {
        List<Integer> newItemset = new ArrayList<>();
        for(Object item: subSet1.get(0)){
            newItemset.add((Integer) item);
        }
        for(Object item: subset2.get(0)){
            if(newItemset.contains(item)) {
                return;
            }
            newItemset.add((Integer) item);

        }
        Collections.sort(newItemset);
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
        if(lastItemset.contains(s2LastItem)){
            return;
        }
        lastItemset.add(s2LastItem);
        Collections.sort(lastItemset);
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

        ind = 0 ;
        for(List itemset: subset2){
            for(Object item: itemset){
                if(ind!=ignore2){
                    itemList2.add((Integer) item);
                }
                ind++;
            }
        }

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
        return length;
    }

    public int getMinIndex(List<List> subSet1, HashMap<Integer, Float> parameters) {
        int minInd = -1;
        int i=0;
        Object minItem = 0;
        for(List itemset: subSet1){
            for(Object item: itemset){
                if(minInd ==-1){
                    minInd = i;
                    minItem =  item;
                }
                else {
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
}
