import java.util.ArrayList;
import java.util.Set;

public class VariableElimination {
    private BayesianNetwork network;
    private ArrayList<CPT> factors;
    private String query;
    private ArrayList<String> evidence;
    private ArrayList<String> hidden;

    //constructor
    public VariableElimination(BayesianNetwork network, String query, ArrayList<String> evidence) {
        this.network = network;
        setFactors();
        this.query = query;
        this.evidence = evidence;
        this.hidden = getHidden();
    }

    //finds all hidden variables in network
    public ArrayList<String> getHidden() {
        Set<String> setKeys = network.getBN().keySet();
        ArrayList<String> hiddenVars = new ArrayList<>();
        boolean contains;
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

    //gets an array reflecting a combination (for example A=T,B=T,C=F) and updates it to the next combination
    // without changing the hidden variable in index "hidden"
    // in this case if B is hidden A=F,B=T,C=T
    public void iterate(ArrayList<Integer> arr, CPT factor, int hidden) {
        for (int i = arr.size() - 1; i >= 0; i--) {
            if (i == hidden) {
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
    }

    //gets an array reflecting a combination (for example A=T,B=T,C=F) and updates it to the next combination
    //in this case A=T,B=F,C=T
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
    //computes and returns the table for the new factor
    public ArrayList<Double> timesFactorTable(CPT factor1, CPT factor2, double[] arr, boolean nameFromFac1, CPT newF) {
        ArrayList<Double> table = new ArrayList<>();
        ArrayList<Integer> iterationNewF = new ArrayList<>(), iterationFac1, iterationFac2;
        double probOfTimes;
        int indexForFact2, indexForFact1;
        //create iteration for new factor
        for (int i = 0; i < newF.getParents().size() + 1; i++) {
            iterationNewF.add(1);
        }
        //finds new length of factor table
        int sizeOfNew = 1;
        for (BayesianNode parent : newF.getParents()) {
            sizeOfNew *= parent.getVars().size();
        }
        sizeOfNew *= this.network.getBN().get(newF.getName()).getVars().size();
        //make table by multiplying factor 1 and factor 2
        for (int i = 0; i < sizeOfNew; i++) {
            iterationFac1 = new ArrayList<>();
            iterationFac2 = new ArrayList<>();
            //get iterator for factor 1 to know where to take the probability from
            for (int j = 0; j < factor1.getParents().size(); j++) {
//                iterationFac1.add(iterationNewF.get(j));
                if (factor1.getParents().get(j).getName().contains(newF.getName())) {
                    iterationFac1.add(iterationNewF.get(iterationNewF.size() - 1));
                    continue;
                }
                indexForFact1 = newF.getParents().indexOf(factor1.getParents().get(j));//gets the index for where parent at
                // index j in old the factor is in the iterator of the new factor
                iterationFac1.add(iterationNewF.get(indexForFact1));
            }
            //if query of fac1 isn't new query add him to the iterator of 1 after the last parent.
            if (!nameFromFac1) {
//                iterationFac1.add(iterationNewF.get(factor1.getParents().size()));
                indexForFact1 = newF.getParents().indexOf(this.network.getBN().get(factor1.getName()));
                iterationFac1.add(iterationNewF.get(indexForFact1));
            } else {//otherwise from the end of the iterator because that's where the query is represented
                iterationFac1.add(iterationNewF.get(iterationNewF.size() - 1));
            }
            //now for factor2
            for (int j = 0; j < factor2.getParents().size(); j++) {
                if (factor2.getParents().get(j).getName().contains(newF.getName())) {
                    iterationFac2.add(iterationNewF.get(iterationNewF.size() - 1));
                    continue;
                }
                indexForFact2 = newF.getParents().indexOf(factor2.getParents().get(j));//gets the index for where parent at
                // index j in old the factor is in the iterator of the new factor
                iterationFac2.add(iterationNewF.get(indexForFact2));
            }
            if (factor2.getName().contains(newF.getName())) {
                iterationFac2.add(iterationNewF.get(iterationNewF.size() - 1));
            } else {
                indexForFact2 = newF.getParents().indexOf(this.network.getBN().get(factor2.getName()));
                iterationFac2.add(iterationNewF.get(indexForFact2));
            }
            //compute the new prob
            probOfTimes = factor1.getProb(iterationFac1) * factor2.getProb(iterationFac2);
            //add to times 1
            arr[0] += 1;
            table.add(probOfTimes);
            iterate(iterationNewF, newF);
        }
        return table;
    }
    //eliminates the hidden
    public CPT eliminateHidden(CPT factor, String hiddenName, double[] arr) {
        CPT newFactor = new CPT();
        BayesianNode node, hiddenNode;
        hiddenNode = network.getBN().get(hiddenName);
        double prob = 0;
        ArrayList<Double> table = new ArrayList<>();
        int newTableSize, indexOfHidden;
        boolean isName;
        //make iterations
        ArrayList<Integer> iterator = new ArrayList<>();
        for (int i = 0; i < factor.getParents().size() + 1; i++) {
            iterator.add(1);
        }
        newTableSize = factor.getFactorSize() / hiddenNode.getVars().size();
        //if the hidden is the query, make last parent the query to keep order correct
        if (factor.getName().contains(hiddenName)) {
            isName = true;
            node = factor.getParents().get(factor.getParents().size() - 1);
            newFactor.setName(node.getName());
            newFactor.setVars(node.getVars());
            for (BayesianNode p : factor.getParents()) {
                newFactor.getParents().add(p);
            }
            newFactor.getParents().remove(newFactor.getParents().size() - 1);
        } else {
            isName = false;
            newFactor.setName(factor.getName());
            newFactor.setVars(factor.getVars());
            for (BayesianNode p : factor.getParents()) {
                newFactor.getParents().add(p);
            }
            newFactor.getParents().remove(hiddenNode);
        }
        //update table of new factor
        for (int i = 0; i < newTableSize; i++) {
            //find the index of the hidden.
            if (isName) {
                indexOfHidden = iterator.size() - 1;
            } else {
                indexOfHidden = factor.getParents().indexOf(hiddenNode);
            }
            //add together all probabilities of hidden var for each combo of the factor without it.
            for (int j = 0; j < hiddenNode.getVars().size(); j++) {
                iterator.set(indexOfHidden, j + 1);
                prob += factor.getProb(iterator);
                if (j == 0) {
                    continue;
                }
                arr[1] += 1;
            }
            table.add(prob);
            prob = 0;
            iterate(iterator, factor, indexOfHidden);
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

    //creates the new factor and joins the other two into it
    public CPT timesFactor(CPT factor1, CPT factor2, double[] arr, String hiddenName) {
        CPT newFactor = new CPT();
        ArrayList<BayesianNode> newParents = new ArrayList<>();
        boolean nameIsFirstFac = false;
        //if factor 2 has a different name than query than make name be from factor2 otherwise make name from factor1
        if (!factor2.getName().contains(hiddenName)) {
            newFactor.setName(factor2.getName());
        } else {
            newFactor.setName(factor1.getName());
            nameIsFirstFac = true;
        }
        //add the variables of your name
        newFactor.setVars(this.network.getBN().get(newFactor.getName()).getVars());
        //update parents
        for (BayesianNode p : factor1.getParents()) {
            if (p.getName().contains(newFactor.getName())) {
                continue;
            }
            newParents.add(p);
        }
        if (!nameIsFirstFac) {
            newParents.add(this.network.getBN().get(factor1.getName()));
        }
        for (BayesianNode parent : factor2.getParents()) {
            if (newParents.contains(parent)) {
                continue;
            }
            newParents.add(parent);
        }
        newFactor.setParents(newParents);
        //factor multiplying for the new table
        ArrayList<Double> table = timesFactorTable(factor1, factor2, arr, nameIsFirstFac, newFactor);
        //now add it to the new factor
        newFactor.setTable(table);
        return newFactor;
    }

    //joins factors
    public CPT join(ArrayList<CPT> _factors, double[] arr, String hiddenName) {
        CPT factor1, factor2, newFactor;
        String query = this.query.split("=")[0];
        //join all factors in that contain hidden
        while (_factors.size() > 1) {
            //remove the two first factors and join them
            factor1 = _factors.remove(0);
            factor2 = _factors.remove(0);
            newFactor = timesFactor(factor1, factor2, arr, hiddenName);
            _factors.add(newFactor);
            //sort factors
            sortBy1(_factors);
        }
        newFactor = _factors.get(0);
        //if the factor is the query, and it has only its variables than it's in the final join
        if ((newFactor.getName().contains(query)) && (newFactor.getFactorSize() <= newFactor.getVars().size())) {
            return newFactor;
        }
        //if it's a factor that will become a one variable and isn't query,don't do elimination
        //we'll throw it out soon
        if (newFactor.getFactorSize() <= newFactor.getVars().size()) {
            newFactor.getTable().remove(0);
            return newFactor;
        }
        //eliminate the hidden
        newFactor = eliminateHidden(newFactor, hiddenName, arr);

        return newFactor;
    }

    //sorts hidden factors by ABC with advanced bubble sort
    public void sortHidden(ArrayList<String> hidden) {
        int size = hidden.get(0).length(), n = hidden.size();
        for (int k = size - 1; k >= 0; k--) {
            for (int i = 0; i < n - 1; i++) {
                for (int j = 0; j < n - i - 1; j++) {
                    if (Character.toUpperCase(hidden.get(j).charAt(k)) > Character.toUpperCase(hidden.get(j + 1).charAt(k))) {
                        // swap arr[j+1] and arr[j]
                        String temp = hidden.get(j);
                        hidden.set(j, hidden.get(j + 1));
                        hidden.set(j + 1, temp);
                    }
                }
            }
        }
    }

    public void function2(double[] arr) {
        //array containing: multiplication,addition and final answer
        String[] wanted = this.query.split("="), e = new String[evidence.size()];
        String[] arr2 = new String[evidence.size()*2],query = this.query.split("=");
        String hiddenName;
        double sumOfFinal = 0;
        CPT afterJoin, finalFactor;
        ArrayList<CPT> hiddenFactors;
        boolean inCpt;
        //create an array of evidence names to check if the query that was given is in a cpt already.
        for (int i = 0; i < e.length; i++) {
            e[i] = this.evidence.get(i).split("=")[0];
        }
        //creates array of evidence with name and value
        int w=0;
        for (int i = 0; i < arr2.length; i=i+2) {
            arr2[i]=this.evidence.get(w).split("=")[0];
            arr2[i+1]=this.evidence.get(w).split("=")[1];
            w++;
        }
        //check if the query that was given is in a cpt already.
        inCpt = this.network.inACpt(wanted[0], e);
        //if it is get the probability
        if (inCpt) {
            ArrayList<Integer> ans = new ArrayList<>();
            for (int i = 0; i < arr2.length - 1; i = i + 2) {
                ans.add(this.network.valueToNumber(arr2[i], arr2[i + 1]));
            }
            ans.add(this.network.valueToNumber(query[0], query[1]));
            arr[0] = this.network.getBN().get(query[0]).getCpt().getProb(ans);
            System.out.println(arr[0] + " ," + arr[1] + " " + arr[2] + " ");
            return;
        }
        //remove all leafs from the graph until you're left only with ancestors
        removeLeafs();
        this.hidden = getHidden();
        //update your factors by evidence
        updateFactorsByEvidence();
        //sort the hidden variables by ABC
        sortHidden(this.hidden);
        //go over all hidden variables and eliminate them one by one
        while (!this.hidden.isEmpty()) {
            hiddenFactors = new ArrayList<>();
            hiddenName = this.hidden.remove(0);
            //add all factors containing the hidden variable into an array
            for (CPT fact : this.factors) {
                if (fact.getFactorParams().contains(hiddenName)) {
                    hiddenFactors.add(fact);

                }
            }
            //remove the factors from the original factor list
            for (CPT fact : hiddenFactors) {
                this.factors.remove(fact);
            }
            //if it's a factor that was removed previously (most likely because it wasn't an ancestor)
            //move on to the next one
            if (hiddenFactors.size() == 0) {
                continue;
            }
            //sort factors by size and ascii
            sortBy1(hiddenFactors);
            //join the factors
            afterJoin = join(hiddenFactors, arr, hiddenName);
            //if factor after join is the size of 1 remove it
            if (afterJoin.getFactorSize() < afterJoin.getVars().size()) {
                continue;
            }
            //add the factor back to lists of factors
            this.factors.add(afterJoin);
        }
        sortBy1(this.factors);
        //do the final join with query
        finalFactor = join(this.factors, arr, this.factors.get(0).getName());
        //find sum of variables to normalize.
        for (int i = 0; i < finalFactor.getFactorSize(); i++) {
            sumOfFinal += finalFactor.getTable().get(i);
            //add to addition
            arr[1] += 1;
        }
        //subtract from addition because we started from sumOfFinal=0
        arr[1] -= 1;
        //normalize
        for (int i = 0; i < finalFactor.getFactorSize(); i++) {
            finalFactor.getTable().set(i, (finalFactor.getTable().get(i)) / sumOfFinal);
        }
        //put results int array
        arr[2] = finalFactor.getTable().get(this.network.valueToNumber(wanted[0], wanted[1]) - 1);
        arr[2] = Math.round(arr[2] * 100000) / 100000.0d;
        System.out.println(String.format("%.5f", arr[2]) + "," + (int) arr[1] + "," + (int) arr[0]);
    }
    //gets the factor and name of evidence and value of evidence and if the evidence is this factor
    //and removes all probabilities that aren't the correct variable
    public ArrayList<Double> updateProbTable(String name, int value, CPT factor, Boolean isNode) {
        ArrayList<Double> arr = new ArrayList<>();
        ArrayList<Integer> indexes = new ArrayList<>();
        int size;
        for (int i = 0; i < factor.getParents().size() + 1; i++) {//makes indexes
            indexes.add(1);
        }
        //removes unwanted probabilities if evidence is the factor
        if (isNode) {
            size = factor.getFactorSize();
            for (int i = 0; i < size; i++) {
                if (indexes.get(indexes.size() - 1) == value) {
                    arr.add(factor.getProb(indexes));
                }
                indexes = iterate(indexes, factor);

            }
            //removes  unwanted probabilities if evidence is a parent
        } else {
            size = factor.getFactorSize();
            for (int i = 0; i < size; i++) {
                if (indexes.get(factor.getParentIndex(name)) == value) {
                    arr.add(factor.getProb(indexes));
                }
                indexes = iterate(indexes, factor);
            }

        }
        return arr;
    }
    //removes all non ancestors
    public void removeLeafs() {
        ArrayList<String> remove;
        Set<String> setKeys = this.network.getBN().keySet();
        String query = this.query.split("=")[0];
        boolean inEvidence, finished = false;
        BayesianNode node;
        //every iteration removes all leafs until an iteration that it doesn't remove anymore
        while (!finished) {
            remove = new ArrayList<>();
            for (String var : setKeys) {
                inEvidence = false;
                for (String s : this.evidence) {
                    if (s.contains(var)) {
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
    //goes to every factor that contains the evidence in it and removes the evidence.
    public void updateFactorsByEvidence() {
        ArrayList<Double> prob;
        ArrayList<CPT> oneValue = new ArrayList<>();
        //goes through each evidence
        for (String e : this.evidence) {
            String[] arr = e.split("=");
            String eName = arr[0];
            int eVal = this.network.valueToNumber(arr[0], arr[1]);
            //goes through each factor
            for (CPT factor : this.factors) {
                //removes the evidence if it's located in name.
                if (factor.getName().contains(eName)) {
                    prob = updateProbTable(eName, eVal, factor, true);
                    this.factors.get(this.factors.indexOf(factor)).setTable(prob);
                    if (factor.getParents().size() > 0) {
                        factor.setName(factor.getParents().remove(factor.getParents().size() - 1).getName());
                        factor.setVars(this.network.getBN().get(factor.getName()).getVars());
                    }
                }
                //removes the evidence if it's located in parents.
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
    //sets the list factors
    public void setFactors() {
        this.factors = new ArrayList<>();
        Set<String> setKeys = this.network.getBN().keySet();
        for (String key : setKeys) {
            this.factors.add(this.network.getBN().get(key).getCpt());
        }
    }

    //sorts by size and ascii
    public void sortBy1(ArrayList<CPT> arr) {
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
}