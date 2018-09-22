import java.util.*;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

public class MSGSP {
    ArrayList<List> sequenceCollection = new ArrayList<>();
    HashMap<Integer, Float> parameters = new HashMap<>();
    float sdc_value;

    MSGSP(ArrayList<List> sequenceCollection, HashMap<Integer, Float> parameters, float sdc_value){
         this.sequenceCollection = sequenceCollection;
         this.parameters = sortByValue(parameters);
         this.sdc_value = sdc_value;

         //Sorting MIS values
        // parameters = sortByValue(parameters);
        // printHashMap(parameters);

        //Finding 1-itemsets
        List<Integer> itemSetCollection = find_1_ItemSet(sequenceCollection);

        //Get support count for each itemset
        Map<Integer, Integer> supportCount = findSupportCount(itemSetCollection, sequenceCollection);

        //Finding L
        List<Integer> l = findLSet(supportCount);

        //Finding F1
        findF1(l, supportCount);
    }

    private void findF1(List<Integer> l, Map<Integer, Integer> supportCount) {
        List<Integer> f1 = new ArrayList<>();
        for(Integer i: l){
            float support = (float)supportCount.get(i)/sequenceCollection.size();
            if(parameters.get(i)<=support)
                f1.add(i);
        }
        Collections.sort(f1);
        System.out.println("The number of 1 sequential patterns is "+f1.size());
        for(Integer i: f1){
            System.out.println("Pattern: <{"+i+"}>: Count = "+supportCount.get(i));
        }

    }

    private List<Integer> findLSet(Map<Integer, Integer> supportCount) {
        List<Integer> theLSet = new ArrayList<>();
        Integer item1 = 0;
        boolean firstItem  = false;
       // printHashMap(parameters);
        for(Integer key: parameters.keySet()){
            float support = (float)supportCount.get(key)/sequenceCollection.size();
           // System.out.println(key+" support = "+support);
            if(parameters.get(key)<=support && !firstItem) {
              //  System.out.println("First item: "+key);
                theLSet.add(key);
                item1 = key;
                firstItem = true;
            }
            else if(firstItem && parameters.get(item1)<=support){
               // System.out.println(parameters.get(item1)+" ?? "+support);
                theLSet.add(key);
            }

        }

        System.out.println("L = "+theLSet);

        return theLSet;
    }

    private List<Integer> find_1_ItemSet(ArrayList<List> sequenceCollection) {
        List<Integer> newItemSet = new ArrayList<>();

        for (List<List> sequence:
                sequenceCollection) {
            for (List<Integer> itemset:
                    sequence) {
                for (Integer item:
                        itemset) {
                    if (!newItemSet.contains(item)) {
                        newItemSet.add(item);
                    }
                }
            }

        }
        return newItemSet;

    }

    private Map<Integer, Integer> findSupportCount(List<Integer> itemSetCollection, ArrayList<List> sequenceCollection) {
        HashMap<Integer, Integer> supportCount = new HashMap<>();
            for(Integer newItem: itemSetCollection){
                for (List<List> sequence:
                        sequenceCollection) {
                    boolean sequenceContainsItem = false;
                    for (List<Integer> itemSet:
                            sequence) {
                       for(Object item: itemSet){
                                if (item.equals(newItem)) {
                                    sequenceContainsItem = true;
                                    if (supportCount.containsKey(newItem)) {
                                        supportCount.put(newItem, supportCount.get(newItem) + 1);
                                    } else {
                                        supportCount.put(newItem, 1);

                                    }
                                    break;

                                }
                       }
                        if(sequenceContainsItem){
                            break;
                        }

                    }

            }

        }

        Map<Integer, Integer> map = new TreeMap<>(supportCount);

      /*  for (Integer itemSet:map.keySet()) {
            System.out.println(itemSet+" -- "+supportCount.get(itemSet));
        }*/
        return map;

    }

    private HashMap<Integer,Float> sortByValue(HashMap<Integer, Float> parameters) {
        HashMap<Integer, Float> sortedParameters = parameters
                .entrySet()
                .stream()
                .sorted(comparingByValue())
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));

        return sortedParameters;
    }



}
