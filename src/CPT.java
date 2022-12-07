import java.util.ArrayList;


public class CPT {
    private String name;
    private ArrayList<BayesianNode> parents;
    private ArrayList<String> vars;
    private ArrayList<Double> table;

    public CPT(ArrayList<BayesianNode> parents, ArrayList<String> vars, ArrayList<Double> table, String name) {
        this.parents = parents;
        this.name = name;
        this.vars = vars;
        this.table = table;
    }

    public CPT() {
        this.parents = new ArrayList<>();
        this.vars = new ArrayList<>();
        this.table = new ArrayList<>();
        this.name = "";
    }

    //    public CPT copy(){
//        CPT copy=new CPT(this.parents,this.vars,this.table,this.name);
//        return copy;
//    }
    public String getFactorParams() {
        String params = this.name;
        for (BayesianNode node : this.parents) {
            params += node.getName();
        }
        return params;
    }

    public int getFactorSize() {
        return this.table.size();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void arrToArrL(String[] arr) {
        for (String s : arr) {
            this.table.add(Double.parseDouble(s));
        }
    }

    public ArrayList<BayesianNode> getParents() {
        return parents;
    }

    public void setParents(ArrayList<BayesianNode> parents) {
        this.parents = parents;
    }

    public boolean containsParents(String name) {
        for (BayesianNode node : this.parents) {
            if (node.getName().contains(name)) {
                return true;
            }
        }
        return false;
    }

    public int getParentIndex(String name) {
        for (int i = 0; i < this.parents.size(); i++) {
            if (this.parents.get(i).getName().contains(name)) {
                return i;
            }
        }
        return -1;
    }

    public void removeParent(String name) {
        for (BayesianNode node : this.parents) {
            if (node.getName().contains(name)) {
                this.parents.remove(node);
                break;
            }
        }
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

    //gets an arraylist containing the value that i want(for example a=t,b=t,c=t) and returns the probability.
    public double getProb(ArrayList<Integer> arr) {
        int size = arr.size(), varsSize, sumOfVars, sum = 0;
        varsSize = this.vars.size();
        for (int i = 0; i < size - 1; i++) {
            sumOfVars = 1;
            for (int j = i + 1; j < size - 1; j++) {
                int parentSize = this.parents.get(j).getVars().size();
                sumOfVars *= parentSize;
            }
            sumOfVars *= varsSize;
            sum += (arr.get(i) - 1) * sumOfVars;
        }
        sum += arr.get(arr.size() - 1);
        return this.table.get(sum - 1);
    }

    @Override
    public String toString() {
        return "CPT{" + "name=" + name +
                ", vars=" + vars +
                ", table=" + table +
                '}';
    }

    public int toAscii(String params) {
        int sum = 0;
        for (int i = 0; i < params.length(); i++) {
            sum += params.charAt(i);
        }
        return sum;
    }

    public boolean big1(CPT cpt) {
        if (this.table.size() > cpt.getFactorSize()) {
            return true;
        } else if (this.table.size() < cpt.getFactorSize()) {
            return false;
        } else if (toAscii(getFactorParams()) > cpt.toAscii(cpt.getFactorParams())) {
            return true;
        } else if(toAscii(getFactorParams()) < cpt.toAscii(cpt.getFactorParams())){
            return false;
        }
        return false;
    }
}
