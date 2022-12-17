import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class Ex1 {
    public static String mySplit(String str){
        String[] strArr=str.split(">",2);
        String[] strArr2=strArr[1].split("<",2);
        return strArr2[0];
    }
    public static Hashtable<String,BayesianNode> makeNetwork(String filename){
        Hashtable<String,BayesianNode>BN= new Hashtable<>();
        try {
            URL file = Ex1.class.getResource(filename);
            assert file != null;
            File URI = new File(file.toURI());
            Scanner sc = new Scanner(URI);
            String name="",data2,data,ParentName;
            ArrayList<String> arr;
            //go over xml line by line
            while (sc.hasNextLine()) {
                data2=sc.nextLine();
                arr=new ArrayList<>();
                //if it's a variable do this
                if (data2.contains("VAR")) {
                    while (sc.hasNextLine()) {
                        data = sc.nextLine();
                        //if it's the end of the variable break
                        if(data.contains("VAR")){
                            break;
                        }
                        //if it's the name update var name to add to node
                        if (data.contains("<NAME>")) {
                            name=(mySplit(data));
                        }
                        //if its outcomes add the variables to list to add to node
                        if (data.contains("<OUT")) {
                            arr.add(mySplit(data));
                        }
                    }
                    BayesianNode node = new BayesianNode(name,arr);
                    BN.put(node.getName(),node);
                    //if it's a definition do this
                }if(data2.contains("DEF")){
                    //update name and variables
                    data=sc.nextLine();
                    name=(mySplit(data));
                    BayesianNode node =BN.get(name);
                    while (sc.hasNextLine()) {
                        data = sc.nextLine();
                        //if it's the end of the definition break
                        if (data.contains("DEF")) {
                            break;
                            //update parents
                        }if(data.contains("GIV")){
                            ParentName=mySplit(data);
                            BayesianNode parent=BN.get(ParentName);
                            node.addParent(parent);
                            parent.addChild(node);
                        }
                        //update probabilities
                        if(data.contains("TAB")){
                            data=mySplit(data);
                            String[] arr2=data.split(" ");
                            node.setCptTable(arr2);
                            node.setCptParents();
                            node.setCptVars();
                            node.setCptName();
                        }
                    }
                }
            }
        } catch (FileNotFoundException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return BN;
    }
    public static void main(String []argv) {
        try {
            String input="input.txt";
            ArrayList<String> evidence;
            URL file = Ex1.class.getResource(input);
            assert file != null;
            File filename = new File(file.toURI());
            Scanner sc=new Scanner(filename);
            String net,line,query,finalAns;
            int functionType;
            double[] ans;
            String [] wholeLine;
            net = sc.nextLine();
            BayesianNetwork BN =new BayesianNetwork(makeNetwork(net));
            VariableElimination ve;
            FileWriter fw = new FileWriter(new File("src", "output.txt"));
            while (sc.hasNextLine()){
                ans=new double[3];
                line= sc.nextLine();
                evidence=new ArrayList<>();
                wholeLine=line.split("[()]")[1].split("[,|]");
                functionType=Integer.parseInt(line.split(",")[line.split(",").length-1]);
                query=wholeLine[0];
                for (int i = 1; i < wholeLine.length; i++) {
                    evidence.add(wholeLine[i]);
                }
                switch (functionType){
                    case 1:
                        BN.function1(line,ans);
                        finalAns=ans[2]+","+(int)ans[1]+","+(int)ans[0]+"\n";
                        fw.write(finalAns);
                        break;
                    case 2:
                        ve = new VariableElimination(BN, query, evidence);
                        ve.function2(ans);
                        finalAns=ans[2]+","+(int)ans[1]+","+(int)ans[0]+"\n";
                        BN =new BayesianNetwork(makeNetwork(net));
                        fw.write(finalAns);
                        break;
                    case 3:
                        ve = new VariableElimination(BN, query, evidence);
                        ve.function3(ans);
                        finalAns=ans[2]+","+(int)ans[1]+","+(int)ans[0]+"\n";
                        BN =new BayesianNetwork(makeNetwork(net));
                        fw.write(finalAns);
                        break;
                }

            }
            fw.close();

        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
