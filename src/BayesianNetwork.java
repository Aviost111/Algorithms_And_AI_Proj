import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

public class BayesianNetwork {
    private Hashtable<String, BayesianNode> BN;
    private ArrayList<CPT> factors;

    public boolean inACpt(String query, String[] evidence) {
        ArrayList<BayesianNode> parents = this.BN.get(query).getParents();
        if (parents.size()==0){
            return false;
        }
        int count=0;
        BayesianNode node;
        if (parents.size() > evidence.length) {
            return false;
        }
        for (int i = 0; i < evidence.length; i++) {
            node = this.BN.get(evidence[i]);
            if (parents.contains(node)) {
                count++;
            }
        }
        if(count== parents.size()) {
            return true;
        }else {
            return false;
        }
    }


    public BayesianNetwork(Hashtable<String, BayesianNode> BN) {
        this.BN = BN;
    }

    public int valueToNumber(String name, String value) {
        return this.BN.get(name).getVars().indexOf(value) + 1;
    }

    public void function1(String input,double[] arr) {
        String[] arr2 = input.split("[()]")[1].split("[,|=]");
        String query = arr2[0];
        //get names of evidence
        int w = 0;
        String[] evidence = new String[(arr2.length - 2) / 2];
        for (int i = 2; i < arr2.length; i = i + 2) {
            evidence[w] = arr2[i];
            w++;
        }
        //checks if the query we want is in a factor and if it is gets the probability
        boolean isAFactor;
        isAFactor = inACpt(query, evidence);
        if (isAFactor) {
            ArrayList<Integer> ans = new ArrayList<>();
            BayesianNode queryNode=this.BN.get(query);
            for (int i = 0; i < queryNode.getParents().size(); i++) {
                for (int j = 2; j < arr2.length-1; j++) {
                    if(arr2[j].contains(queryNode.getParents().get(i).getName())){
                        ans.add(valueToNumber(arr2[j], arr2[j + 1]));
                    }
                }
            }
            ans.add(valueToNumber(arr2[0], arr2[1]));
            arr[2] = this.getBN().get(arr2[0]).getCpt().getProb(ans);
            return;
        }

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
        keys.remove(arr2[0]);
        keys.add(0, arr2[0]);
        tableOfLists.get(arr2[0]).add(null);
        int index = tableOfLists.get(arr2[0]).indexOf(BN.get(arr2[0]).getVars().indexOf(arr2[1]) + 1);
        tableOfLists.get(arr2[0]).remove(index);
        tableOfLists.get(arr2[0]).add(0, BN.get(arr2[0]).getVars().indexOf(arr2[1]) + 1);
        //changes the list of all known variables
        for (int i = 2; i < arr2.length; i = i + 2) {
            list = new ArrayList<>();
            list.add(valueToNumber(arr2[i], arr2[i + 1]));
            tableOfLists.replace(arr2[i], list);
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
