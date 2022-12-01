import java.util.ArrayList;
import java.util.PriorityQueue;



public class CPT implements Comparable{
    private String name;
    private ArrayList<BayesianNode> parents;
    private ArrayList<String> vars;
    private ArrayList<Double> table;

    public CPT(ArrayList<BayesianNode> parents, ArrayList<String> vars, ArrayList<Double> table,String name) {
        this.parents = parents;
        this.name=name;
        this.vars = vars;
        this.table = table;
    }

    public CPT() {
        this.parents = new ArrayList<BayesianNode>();
        this.vars = new ArrayList<String>();
        this.table = new ArrayList<Double>();
        this.name="";
    }
    public CPT copy(){
        CPT copy=new CPT(this.parents,this.vars,this.table,this.name);
        return copy;
    }
    public int getFactorSize(){
        return this.table.size();
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public double getProb(ArrayList<Integer> arr){
        int size=arr.size(),varsSize,sumOfVars=1,sum=0;
        varsSize=this.vars.size();
        for (int i=0;i<size-1;i++) {
            sumOfVars=1;
            for (int j = i+1; j < size-1; j++) {
                int parentSize = this.parents.get(j).getVars().size();
                sumOfVars *= parentSize;
            }
            sumOfVars*=varsSize;
            sum+=(arr.get(i)-1)*sumOfVars;
        }
        sum+=arr.get(arr.size()-1);
        return this.table.get(sum-1);
    }

    @Override
    public String toString() {
        return "CPT{" +"name="+name+
                ", vars=" + vars +
                ", table=" + table +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
