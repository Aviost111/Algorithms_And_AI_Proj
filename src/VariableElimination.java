import java.util.ArrayList;
import java.util.Set;

public class VariableElimination {
    private BayesianNetwork network;
    private ArrayList<CPT> factors;
    private String query;
    private ArrayList<String> evidence;
    private ArrayList<String> hidden;

    public VariableElimination(BayesianNetwork network, String query, ArrayList<String> evidence) {
        this.network = network;
        setFactors();
        this.query = query;
        this.evidence = evidence;
        this.hidden = getHidden();
    }

    public ArrayList<String> getHidden() {
        Set<String> setKeys = network.getBN().keySet();
        ArrayList<String> hiddenVars = new ArrayList<>();
        for (String key : setKeys) {
            if (!evidence.contains(key))
                hiddenVars.add(key);
        }
        hiddenVars.remove(query);
        return hiddenVars;
    }

    public ArrayList<Integer> iterate(ArrayList<Integer> arr, CPT factor) {
        for (int i = arr.size() - 1; i >= 0; i--) {
            arr.set(i, arr.get(i) + 1);
            if ((i == arr.size() - 1) && (arr.get(i) < factor.getVars().size() + 1)) {
                break;
            }
            if ((i != arr.size() - 1) && (arr.get(i) < factor.getParents().get(i).getVars().size() + 1)) {
                break;
            }
            arr.set(i, 1);
        }
        return arr;
    }
    public void sortByAscii(ArrayList<String> arr){
        int n = arr.size();
        for (int i = 0; i < n - 1; i++){
            for (int j = 0; j < n - i - 1; j++){
                if (arr.get(j).charAt(0)>arr.get(j+1).charAt(0)) {
                    // swap arr[j+1] and arr[j]
                    String temp = arr.get(j);
                    arr.set(j,arr.get(j+1));
                    arr.set(j+1,temp);
                    }
            }
        }
    }
    public CPT timesFactor(CPT factor1 ,CPT factor2){

    }
    public CPT join(ArrayList<CPT> _factors,int[] arr){
        CPT factor1,factor2,newFactor;
        while (_factors.size()>1){
            factor1=_factors.remove(0);
            factor2=_factors.remove(0);
            newFactor=timesFactor(factor1,factor2);

        }
    }
    public void function2(){
        int [] arr=new int[2];
        String hiddenName;
        CPT afterJoin;
        ArrayList<CPT> hiddenFactors;
        sortByAscii(this.hidden);
        while(!this.hidden.isEmpty()){
            hiddenFactors=new ArrayList<>();
            hiddenName=this.hidden.remove(0);
            for (CPT fact:this.factors) {
                if(fact.getFactorParams().contains(hiddenName)){
                    hiddenFactors.add(fact);
                }
            }
            sortBy1(hiddenFactors);
            afterJoin=join(hiddenFactors,arr);
            this.factors.add(afterJoin);
        }
    }
    public ArrayList<Double> updateProbTable(String name, int value, CPT factor, Boolean isNode) {
        ArrayList<Double> arr = new ArrayList<>();
        ArrayList<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < factor.getParents().size() + 1; i++) {//makes indexes
            indexes.add(1);
        }
        if (isNode) {
            for (int i = 0; i < factor.getTable().size(); i++) {
                if (indexes.get(indexes.size() - 1) == value) {
                    arr.add(factor.getProb(indexes));
                }
                indexes = iterate(indexes, factor);//TODO finish is the node

            }
        } else {
            for (int i = 0; i < factor.getTable().size(); i++) {
                if (indexes.get(factor.getParentIndex(name)) == value) {
                    arr.add(factor.getProb(indexes));
                }
                indexes = iterate(indexes, factor);
            }

        }
        return arr;
    }

    public void updateFactorsByEvidence() {
        ArrayList<Double> prob;
        ArrayList<CPT> oneValue = new ArrayList<>();
        for (String e : this.evidence) {
            String[] arr = e.split("=");
            String eName = arr[0];
            int eVal = this.network.valueToNumber(arr[0], arr[1]);
            for (CPT factor : this.factors) {
                if (factor.getName().contains(eName)) {
                    prob = updateProbTable(eName, eVal, factor, true);
                    this.factors.get(this.factors.indexOf(factor)).setTable(prob);
                    ArrayList<String> vars = new ArrayList<>();
                    vars.add(arr[1]);
                    this.factors.get(this.factors.indexOf(factor)).setVars(vars);

                }
                if (factor.containsParents(eName)) {
                    prob = updateProbTable(eName, eVal, factor, false);
                    this.factors.get(this.factors.indexOf(factor)).setTable(prob);
                    this.factors.get(this.factors.indexOf(factor)).removeParent(eName);
                }
                if (factor.getTable().size() == 1) {//finds all factors with 1 variable
                    oneValue.add(factor);
                }
            }
            for (CPT factor : oneValue) {//and deletes them
                this.factors.remove(factor);
            }

        }
    }

    public void setFactors() {
        this.factors = new ArrayList<>();
        Set<String> setKeys = this.network.getBN().keySet();
        for (String key : setKeys) {
            this.factors.add(this.network.getBN().get(key).getCpt());
        }
    }
    public void sortBy1(ArrayList<CPT> arr){
        int n = arr.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr.get(j).big1(arr.get(j + 1))) {
                    // swap arr[j+1] and arr[j]
                    CPT temp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, temp);
                }
            }
        }
    }
    public BayesianNetwork getNetwork() {
        return network;
    }

    public void setNetwork(BayesianNetwork network) {
        this.network = network;
    }

    public ArrayList<CPT> getFactors() {
        return factors;
    }

    public void setFactors(ArrayList<CPT> factors) {
        this.factors = factors;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public ArrayList<String> getEvidence() {
        return evidence;
    }

    public void setEvidence(ArrayList<String> evidence) {
        this.evidence = evidence;
    }
}
