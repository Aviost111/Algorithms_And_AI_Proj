import java.util.ArrayList;

public class CPT {
    private ArrayList<BayesianNode> parents;
    private ArrayList<String> vars;
    private ArrayList<Double> table;

    public CPT(ArrayList<BayesianNode> parents, ArrayList<String> vars, ArrayList<Double> table) {
        this.parents = parents;
        this.vars = vars;
        this.table = table;
    }

    public CPT() {
        this.parents = new ArrayList<BayesianNode>();
        this.vars = new ArrayList<String>();
        this.table = new ArrayList<Double>();
    }

    public void arrToArrL(String [] arr){
        for (int i=0;i<arr.length;i++){
            this.table.add(Double.parseDouble(arr[i]));
        }
    }
    public ArrayList<BayesianNode> getParents() {
        return parents;
    }

    public void setParents(ArrayList<BayesianNode> parents) {
        this.parents = parents;
    }

    public ArrayList<String> getVars() {
        return vars;
    }

    public void setVars(ArrayList<String> vars) {
        this.vars = vars;
    }

    public ArrayList<Double> getTable() {
        return table;
    }

    public void setTable(ArrayList<Double> table) {
        this.table = table;
    }

    @Override
    public String toString() {
        return "CPT{" +
                "vars=" + vars +
                ", table=" + table +
                '}';
    }
}
