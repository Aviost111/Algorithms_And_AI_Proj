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
        boolean contains=false;
        for (String key : setKeys) {
            contains = false;
            for (String ev : this.evidence) {
                if (ev.contains(key)) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                hiddenVars.add(key);
            }
        }
        hiddenVars.remove(query.split("=")[0]);
        return hiddenVars;
    }
    public ArrayList<Integer> iterate(ArrayList<Integer> arr, CPT factor,int hidden) {
        for (int i = arr.size() - 1; i >= 0; i--) {
            if(i==hidden){
                continue;
            }
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
                if (toAscii(arr.get(j))>toAscii(arr.get(j+1))) {
                    // swap arr[j+1] and arr[j]
                    String temp = arr.get(j);
                    arr.set(j,arr.get(j+1));
                    arr.set(j+1,temp);
                }
            }
        }
    }
    public ArrayList<Double> timesFactorTable(CPT factor1 ,CPT factor2,double [] arr,boolean nameFromFac1,CPT newF){
        ArrayList<Double> table=new ArrayList<>();
        ArrayList<Integer> iterationNewF=new ArrayList<>(),iterationFac1,iterationFac2;
        double probOfTimes;
        int indexForFact2,count=0,indexForFact1;
        //create iteration for new factor
        for (int i=0;i<newF.getParents().size()+1;i++){
            iterationNewF.add(1);
        }
        //finds new length of factor table
        int sizeOfNew=1;
        for (BayesianNode parent: newF.getParents()) {
            sizeOfNew *= parent.getVars().size();
        }
        sizeOfNew*=this.network.getBN().get(newF.getName()).getVars().size();
        //make table by multiplying factor 1 and factor 2
        for(int i=0;i<sizeOfNew;i++){
            count++;
            iterationFac1=new ArrayList<>();
            iterationFac2=new ArrayList<>();
            //get iterator for factor 1 to know where to take the probability from
            for(int j=0;j<factor1.getParents().size();j++){
//                iterationFac1.add(iterationNewF.get(j));
                indexForFact1=newF.getParents().indexOf(factor1.getParents().get(j));//gets the index for where parent at
                // index j in old the factor is in the iterator of the new factor
                iterationFac1.add(iterationNewF.get(indexForFact1));
            }
            //if query of fac1 isn't new query add him to the iterator of 1 after the last parent.
            if(!nameFromFac1) {
//                iterationFac1.add(iterationNewF.get(factor1.getParents().size()));
                indexForFact1=newF.getParents().indexOf(this.network.getBN().get(factor1.getName()));
                iterationFac1.add(iterationNewF.get(indexForFact1));
            }else {//otherwise from the end of the iterator because that's where the query is represented
                iterationFac1.add(iterationNewF.get(iterationNewF.size()-1));
            }
            //now for factor2
            for(int j=0;j<factor2.getParents().size();j++){
                indexForFact2=newF.getParents().indexOf(factor2.getParents().get(j));//gets the index for where parent at
                // index j in old the factor is in the iterator of the new factor
                iterationFac2.add(iterationNewF.get(indexForFact2));
            }
            if(factor2.getName().contains(newF.getName())){
                iterationFac2.add(iterationNewF.get(iterationNewF.size()-1));
            }else{
                indexForFact2=newF.getParents().indexOf(this.network.getBN().get(factor2.getName()));
                iterationFac2.add(iterationNewF.get(indexForFact2));
            }
            //compute the new prob
            probOfTimes=factor1.getProb(iterationFac1)*factor2.getProb(iterationFac2);
            //add to times 1
            arr[0]+=1;
            table.add(probOfTimes);
            iterate(iterationNewF,newF);
        }
        return table;
    }
    public CPT eliminateHidden(CPT factor,String hiddenName,double[] arr ){
        CPT newFactor=new CPT();
        ArrayList<BayesianNode> newParents=new ArrayList<>();
        BayesianNode node,hiddenNode;
        hiddenNode=network.getBN().get(hiddenName);
        double prob=0;
        ArrayList<Double> table=new ArrayList<>();
        int newTableSize,indexOfHidden;
        boolean isName;
        //make iterations
        ArrayList<Integer>iterator=new ArrayList<>();
        for(int i=0;i<factor.getParents().size()+1;i++){
            iterator.add(1);
        }
        newTableSize=factor.getFactorSize()/hiddenNode.getVars().size();
        //if the hidden is the query, make last parent the query to keep order correct
        if(factor.getName().contains(hiddenName)){
            isName=true;
            node=factor.getParents().get(factor.getParents().size()-1);
            newFactor.setName(node.getName());
            newFactor.setVars(node.getVars());
            for (BayesianNode p: factor.getParents()) {
                newFactor.getParents().add(p);
            }
            newFactor.getParents().remove(newFactor.getParents().size()-1);
        }else {
            isName=false;
            newFactor.setName(factor.getName());
            newFactor.setVars(factor.getVars());
            for (BayesianNode p: factor.getParents()) {
                newFactor.getParents().add(p);
            }
            newFactor.getParents().remove(hiddenNode);
        }
        //update table of new factor
        for (int i=0;i<newTableSize;i++){
            //find the index of the hidden.
            if(isName){
                indexOfHidden=iterator.size()-1;
            }else{
                indexOfHidden=factor.getParents().indexOf(hiddenNode);
            }
            //add together all probabilities of hidden var for each combo of the factor without it.
            for(int j=0;j<hiddenNode.getVars().size();j++){
                iterator.set(indexOfHidden,j+1);
                prob+=factor.getProb(iterator);
                if(j==0){
                    continue;
                }
                arr[1]+=1;
            }
            table.add(prob);
            prob=0;
            iterate(iterator,factor,indexOfHidden);
        }
        newFactor.setTable(table);
        return newFactor;
    }
    public int toAscii(String params) {
        int sum = 0;
        for (int i = 0; i < params.length(); i++) {
            sum += params.charAt(i);
        }
        return sum;
    }
    public CPT timesFactor(CPT factor1 ,CPT factor2,double [] arr,String hiddenName){
        CPT newFactor=new CPT();
        ArrayList<BayesianNode> newParents=new ArrayList<>();
        boolean nameIsFirstFac = false;
        //if factor 2 has a different name than query than make name be from factor2 otherwise make name from factor1
        if(!factor2.getName().contains(hiddenName)){
            newFactor.setName(factor2.getName());
        }else {
            newFactor.setName(factor1.getName());
            nameIsFirstFac=true;
        }
        //add the variables of your name
        newFactor.setVars(this.network.getBN().get(newFactor.getName()).getVars());
        //update parents
        for (BayesianNode p: factor1.getParents()) {
            newParents.add(p);
        }
        //TODO h
        if(!nameIsFirstFac){
            newParents.add(this.network.getBN().get(factor1.getName()));
        }
        for (BayesianNode parent: factor2.getParents()) {
            if(newParents.contains(parent)){
                continue;
            }
            newParents.add(parent);
        }
        newFactor.setParents(newParents);
        //TODO bbb
        //factor multiplying for the new table
        ArrayList<Double>table=timesFactorTable(factor1,factor2,arr,nameIsFirstFac,newFactor);
        //now add it to the new factor
        newFactor.setTable(table);
        return newFactor;
    }
    public CPT join(ArrayList<CPT> _factors,double[] arr,String hiddenName){
        CPT factor1,factor2,newFactor;
        String query=this.query.split("=")[0];
        System.out.println(hiddenName+" is the hidden");
        //join all factors in that contain hidden
        while (_factors.size()>1){
            //remove the two first factors and join them
            factor1=_factors.remove(0);
            System.out.print("f1: "+factor1.getFactorParams()+" ");
            System.out.println(factor1.getTable());
            factor2=_factors.remove(0);
            System.out.print("f2: "+factor2.getFactorParams()+" ");
            System.out.println(factor2.getTable());
            newFactor=timesFactor(factor1,factor2,arr,hiddenName);
            System.out.print("new: "+newFactor.getFactorParams()+" ");
            System.out.println(newFactor.getTable());
            _factors.add(newFactor);
            //sort factors
            sortBy1(_factors);
        }
        newFactor=_factors.get(0);
        //if the factor is the query, and it has only its variables than it's after the final join
        if((newFactor.getName().contains(query))&&(newFactor.getFactorSize()<=newFactor.getVars().size())){
            return newFactor;
        }
        if(newFactor.getFactorSize()<=newFactor.getVars().size()){
            newFactor.getTable().remove(0);
            return newFactor;
        }
        //eliminate the hidden
        newFactor=eliminateHidden(newFactor,hiddenName,arr);
        System.out.println("after elimination "+newFactor.getFactorParams()+" ");
        System.out.println(newFactor.getTable());

        return newFactor;
    }
    public ArrayList<String> sortHidden(ArrayList<String> hidden){
        int size=hidden.get(0).length(),n= hidden.size();
        for (int k=size-1;k>=0;k--){
            for (int i = 0; i < n - 1; i++){
                for (int j = 0; j < n - i - 1; j++){
                    if (hidden.get(j).charAt(k)>hidden.get(j+1).charAt(k)) {
                        // swap arr[j+1] and arr[j]
                        String temp = hidden.get(j);
                        hidden.set(j,hidden.get(j+1));
                        hidden.set(j+1,temp);
                    }
                }
            }
        }
        return hidden;
    }
    public void function2(){
        double [] arr=new double[3];
        String hiddenName;
        double sumOfFinal=0;
        CPT afterJoin,finalFactor;
        ArrayList<CPT> hiddenFactors;
        //remove all leafs from the graph until you're left only with ancestors
        removeLeafs();
        this.hidden = getHidden();
        //update your factors by evidence
        updateFactorsByEvidence();
        sortHidden(this.hidden);
        //go over all hidden variables
        while(!this.hidden.isEmpty()){
            hiddenFactors=new ArrayList<>();
            hiddenName=this.hidden.remove(0);
            for (CPT fact:this.factors) {
                if(fact.getFactorParams().contains(hiddenName)){
                    hiddenFactors.add(fact);

                }
            }
            for (CPT fact:hiddenFactors) {
                if(this.factors.contains(fact)){
                    this.factors.remove(fact);
                }
            }
            if(hiddenFactors.size()==0){
                continue;
            }
            sortBy1(hiddenFactors);
            afterJoin=join(hiddenFactors,arr,hiddenName);
            if(afterJoin.getFactorSize()<2){
                continue;
            }
            this.factors.add(afterJoin);
        }
        sortBy1(this.factors);
        finalFactor=join(this.factors,arr,this.factors.get(0).getName());
        for(int i=0;i<finalFactor.getFactorSize();i++){
            sumOfFinal+=finalFactor.getTable().get(i);
            arr[1]+=1;
        }
        arr[1]-=1;
        for(int i=0;i<finalFactor.getFactorSize();i++){
            finalFactor.getTable().set(i,(finalFactor.getTable().get(i))/sumOfFinal);
        }
        String [] wanted=this.query.split("=");
        arr[2]=finalFactor.getTable().get(this.network.valueToNumber(wanted[0],wanted[1])-1);
        arr[2]=Math.round(arr[2]*100000)/100000.0d;
        System.out.println(String.format("%.5f", arr[2])+","+(int)arr[1]+","+(int)arr[0]);

        //TODO finish pls

    }
    public ArrayList<Double> updateProbTable(String name, int value, CPT factor, Boolean isNode) {
        ArrayList<Double> arr = new ArrayList<>();
        ArrayList<Integer> indexes = new ArrayList<>();
        int size;
        for (int i = 0; i < factor.getParents().size() + 1; i++) {//makes indexes
            indexes.add(1);
        }
        if (isNode) {
            size=factor.getFactorSize();
            for (int i = 0; i < size; i++) {
                if (indexes.get(indexes.size() - 1) == value) {
                    arr.add(factor.getProb(indexes));
                }
                indexes = iterate(indexes, factor);

            }
        } else {
            size= factor.getFactorSize();
            for (int i = 0; i < size; i++) {
                if (indexes.get(factor.getParentIndex(name)) == value) {
                    arr.add(factor.getProb(indexes));
                }
                indexes = iterate(indexes, factor);
            }

        }
        return arr;
    }

    public void removeLeafs() {
        ArrayList<String> remove = new ArrayList<>();
        int size = this.network.getBN().size();
        Set<String> setKeys = this.network.getBN().keySet();
        String query = this.query.split("=")[0];
        boolean inEvidence = false, finished = false;
        BayesianNode node;

        while (!finished) {
            remove = new ArrayList<>();
            for (String var : setKeys) {
                inEvidence = false;
                for (int j = 0; j < this.evidence.size(); j++) {
                    if (this.evidence.get(j).contains(var)) {
                        inEvidence = true;
                        break;
                    }
                }

                if ((this.network.getBN().get(var).getChildren().size() == 0) && (!inEvidence) && (!query.contains(var))) {
                    remove.add(var);
                }
            }
            if (remove.size() == 0) {
                finished = true;
            }
            for (String name : remove) {
                node = this.network.getBN().get(name);
                for (BayesianNode parent : node.getParents()) {
                    parent.getChildren().remove(node);
                }
                this.network.getBN().remove(name);
            }
        }
        setFactors();
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
                    if(factor.getParents().size()>0) {
                        factor.setName(factor.getParents().remove(factor.getParents().size() - 1).getName());
                        factor.setVars(this.network.getBN().get(factor.getName()).getVars());
                    }
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