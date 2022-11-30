import jdk.internal.util.xml.impl.Pair;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.PriorityQueue;
import java.util.Set;

public class BayesianNetwork {
    private Hashtable<String,BayesianNode> BN;
    //private ArrayList<ArrayList<Pair<String, Float>>> factors;


    public BayesianNetwork(Hashtable<String, BayesianNode> BN) {
        this.BN = BN;
    }
    public void function1(String input){
        String []arr2=input.split("[()]")[1].split("[,|=]");
        double numerator=0,subSum=1,denominator=0;
        int times=0,adds=0;
        ArrayList<String> keys=new ArrayList<>();
        Set<String> setKeys= BN.keySet();//make arraylist of keys (by making a set and converting)
        ArrayList<Integer> indexes=new ArrayList<>(),list;
        boolean isNumerator=true;
        Hashtable<String,ArrayList<Integer>> tableOfLists=new Hashtable<>();
        for (String key:setKeys) {
            keys.add(key);
            indexes.add(0);
            int size =this.BN.get(key).getVars().size();
            list=new ArrayList<>();
            for(int i=1;i<size+1;i++){
                 list.add(i);
                }
            tableOfLists.put(key,list);
        }
        keys.remove(arr2[0]);
        keys.add(0,arr2[0]);
        tableOfLists.get(arr2[0]).add(null);
        int index=tableOfLists.get(arr2[0]).indexOf(BN.get(arr2[0]).getVars().indexOf(arr2[1])+1);
        tableOfLists.get(arr2[0]).remove(index);
        tableOfLists.get(arr2[0]).add(0,BN.get(arr2[0]).getVars().indexOf(arr2[1])+1);
        for (int i=2;i<arr2.length;i=i+2){//change the list of all known variables
            list=new ArrayList<>();
            list.add(BN.get(arr2[i]).getVars().indexOf(arr2[i+1])+1);
            tableOfLists.replace(arr2[i],list);
        }
        while(tableOfLists.get(keys.get(0)).get(indexes.get(0))!=null) {


            for (int i = 0; i < keys.size(); i++) {//goes over the keys
                list = new ArrayList<>();
                for (BayesianNode key : BN.get(keys.get(i)).getParents()) {
                    list.add(tableOfLists.get(key.getName()).get(indexes.get(keys.indexOf(key.getName()))));//adds to list the parents wanted value
                }
                list.add(tableOfLists.get(keys.get(i)).get(indexes.get(i)));
                subSum *= BN.get(keys.get(i)).getCpt().getProb(list);
                times++;
            }
            times--;//we added the first multiplication that wasn't part of our actual multiplication because we multiplied by 1;
            if (isNumerator) {
                numerator+=subSum;
            }else{
                denominator+=subSum;
            }
            adds++;
            subSum=1;
            //need to add to the last index
            for(int i=indexes.size()-1;i>=0;i--){
                indexes.set(i,indexes.get(i)+1);
                if(indexes.get(i)<tableOfLists.get(keys.get(i)).size()){
                    if (i == 0 && indexes.get(i) >= 1 && isNumerator) {
                        denominator += numerator;
                        isNumerator = false;
                    }
                    break;

                }
                indexes.set(i,0);
            }
        }
        adds--;//we added the first added that wasn't part of our actual addition because we started from numerator=0 and added;


        System.out.println(numerator/denominator+","+adds+","+times);
    }

    public Hashtable<String, BayesianNode> getBN() {
        return BN;
    }

    public void setBN(Hashtable<String, BayesianNode> BN) {
        this.BN = BN;
    }

    @Override
    public String toString() {
        return "BayesianNetwork{" +
                "BN=" + BN +
                '}';
    }

//    public void bla() {
//        PriorityQueue<[BayesianNode,String]>root = new PriorityQueue();
//        root.add(table)
//    }
}
