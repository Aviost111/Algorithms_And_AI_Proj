import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

public class BayesianNetwork {
    private Hashtable<String, BayesianNode> BN;
    private ArrayList<CPT> factors;

    public boolean inACpt(String query, String[] evidence) {
        boolean isInCpt;
        ArrayList<BayesianNode> parents = this.BN.get(query).getParents();
        if(parents.size()!= evidence.length){
            return false;
        }
        for (BayesianNode parent:parents) {
            isInCpt=false;
            for (int i = 0; i <evidence.length; i++) {
                if(parent.getName().equals(evidence[i])){
                    isInCpt=true;
                    break;
                }
            }
            if(!isInCpt){
                return false;
            }
        }
        return true;
    }

    public BayesianNetwork(Hashtable<String, BayesianNode> BN) {
        this.BN = BN;
    }

    public int valueToNumber(String name, String value) {
        return this.BN.get(name).getVars().indexOf(value) + 1;
    }

    public void function1(String input,double[] arr) {
        String[] fullEvidence = input.split("[()]")[1].split("[,|=]");
        String query = fullEvidence[0];
        //get names of evidence
        int w = 0;
        String[] evidence = new String[(fullEvidence.length - 2) / 2];
        for (int i = 2; i < fullEvidence.length; i = i + 2) {
            evidence[w] = fullEvidence[i];
            w++;
        }
        //checks if the query we want is in a factor and if it is gets the probability
        boolean isAFactor;
        isAFactor = inACpt(query, evidence);
        if(isAFactor){
            ArrayList<Integer> ans = new ArrayList<>();
            BayesianNode queryNode=this.BN.get(query);
            for (BayesianNode parent:queryNode.getParents()) {
                for (int i = 2; i <fullEvidence.length; i=i+2) {
                    if(fullEvidence[i].equals(parent.getName())){
                        ans.add(valueToNumber(fullEvidence[i],fullEvidence[i+1]));
                        break;
                    }
                }
            }
            ans.add(valueToNumber(fullEvidence[0],fullEvidence[1]));
            arr[2]= queryNode.getCpt().getProb(ans);
            return;
        }
//        if (isAFactor) {
//            ArrayList<Integer> ans = new ArrayList<>();
//            BayesianNode queryNode=this.BN.get(query);
//            for (int i = 0; i < queryNode.getParents().size(); i++) {
//                for (int j = 2; j < fullEvidence.length-1; j=j+2) {
//                    if(fullEvidence[j].contains(queryNode.getParents().get(i).getName())){
//                        ans.add(valueToNumber(fullEvidence[j], fullEvidence[j + 1]));
//                    }
//                }
//            }
//            ans.add(valueToNumber(fullEvidence[0], fullEvidence[1]));
//            arr[2] = this.getBN().get(fullEvidence[0]).getCpt().getProb(ans);
//            return;
//        }

        double numerator = 0, subSum = 1, denominator = 0;
        int times = 0, adds = 0;
        ArrayList<String> keys = new ArrayList<>();
        Set<String> setKeys = BN.keySet();//make arraylist of keys (by making a set and converting)
        ArrayList<Integer> indexes = new ArrayList<>(), list;
        boolean isNumerator = true;//add to the numerator while true
        //makes the array lists and hashtable with array lists
        Hashtable<String, ArrayList<Integer>> tableOfLists = new Hashtable<>();
        for (String key : setKeys) {
            keys.add(key);
            indexes.add(0);
            int size = this.BN.get(key).getVars().size();
            list = new ArrayList<>();
            for (int i = 1; i < size + 1; i++) {
                list.add(i);
            }
            tableOfLists.put(key, list);
        }
        //makes the first name in array list the query and makes the array list in hashtable have null and arranges vars
        keys.remove(fullEvidence[0]);
        keys.add(0, fullEvidence[0]);
        tableOfLists.get(fullEvidence[0]).add(null);
        int index = tableOfLists.get(fullEvidence[0]).indexOf(BN.get(fullEvidence[0]).getVars().indexOf(fullEvidence[1]) + 1);
        tableOfLists.get(fullEvidence[0]).remove(index);
        tableOfLists.get(fullEvidence[0]).add(0, BN.get(fullEvidence[0]).getVars().indexOf(fullEvidence[1]) + 1);
        //changes the list of all known variables
        for (int i = 2; i < fullEvidence.length; i = i + 2) {
            list = new ArrayList<>();
            list.add(valueToNumber(fullEvidence[i], fullEvidence[i + 1]));
            tableOfLists.replace(fullEvidence[i], list);
        }
        while (tableOfLists.get(keys.get(0)).get(indexes.get(0)) != null) {//does the addition while not every option has been done
            for (int i = 0; i < keys.size(); i++) {//goes over the keys and does the multiplication
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
                numerator += subSum;
            } else {//changes to denominator
                denominator += subSum;
            }
            adds++;
            subSum = 1;
            //need to add to the last index to iterate
            for (int i = indexes.size() - 1; i >= 0; i--) {
                indexes.set(i, indexes.get(i) + 1);
                if (indexes.get(i) < tableOfLists.get(keys.get(i)).size()) {
                    if (i == 0 && indexes.get(i) >= 1 && isNumerator) {//checks if we've moved to denominator
                        denominator += numerator;
                        isNumerator = false;
                    }
                    break;

                }
                indexes.set(i, 0);
            }
        }
        adds--;//we added the first added that wasn't part of our actual addition because we started from numerator=0 and added;
        double ans = numerator / denominator;
        ans = Math.round(ans * 100000) / 100000.0d;
        arr[0] = times;
        arr[1] = adds;
        arr[2] = ans;
    }

    public Hashtable<String, BayesianNode> getBN() {
        return BN;
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
