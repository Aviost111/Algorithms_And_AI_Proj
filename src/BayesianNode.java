import java.util.ArrayList;

public class BayesianNode {
    private String name;
    private ArrayList<String> vars;
    private CPT cpt;
    private ArrayList<BayesianNode> parents= new ArrayList<BayesianNode>();
    private ArrayList<BayesianNode> children= new ArrayList<BayesianNode>();

    public BayesianNode(String name, ArrayList<String> vars, CPT cpt, ArrayList<BayesianNode> parents, ArrayList<BayesianNode> children) {
        this.name = name;
        this.vars = vars;
        this.cpt = cpt;
        this.parents = parents;
        this.children = children;
    }
    public BayesianNode(String name, ArrayList<String> vars) {
        this.name = name;
        this.vars = vars;
        this.cpt=new CPT();

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getVars() {
        return vars;
    }
    public void setCptName(){
        this.cpt.setName(this.name);
    }
    public void setVars(ArrayList<String> vars) {
        this.vars = vars;
    }

    public CPT getCpt() {
        return cpt;
    }
    public void setCptTable(String[] arr){
        this.cpt.arrToArrL(arr);
    }
    public void setCptParents(){
        this.cpt.setParents(this.parents);
    }
    public void setCptVars(){
        this.cpt.setVars(this.vars);
    }

    public ArrayList<BayesianNode> getParents() {
        return parents;
    }

    public void setParents(ArrayList<BayesianNode> parents) {
        this.parents = parents;
    }
    public void addParent(BayesianNode parent){
        this.parents.add(parent);
    }
    public ArrayList<BayesianNode> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<BayesianNode> children) {
        this.children = children;
    }
    public void addChild(BayesianNode child) {
        this.children.add(child);
    }
    public String printChild(){
        String names="";
        for(int i=0;i<this.children.size();i++){
            names=names+this.children.get(i).name+" ";
        }
        return names;
    }
    public String printParent(){
        String names="";
        for(int i=0;i<this.parents.size();i++){
            names=names+this.parents.get(i).name+" ";
        }
        return names;
    }

    @Override
    public String toString() {
        return "BayesianNode{" +
                "name='" + name + '\n' +
                ", vars=" + vars +'\n'+
                ", cpt=" + cpt +'\n'+
                ", parents=" + printParent() +'\n'+
                ", children=" + printChild() +'\n'+
                '}';
    }
}
