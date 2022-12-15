import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class ReadXMLFile {
    public static String mySplit(String str){
        String[] strArr=str.split(">",2);
        String[] strArr2=strArr[1].split("<",2);
        return strArr2[0];
    }
    public static Hashtable<String,BayesianNode> makeNetwork(String filename){
        Hashtable<String,BayesianNode>BN= new Hashtable<>();
        try {
            File xml= new File(filename);
            Scanner sc = new Scanner(xml);
            String name="",data2,data,ParentName;
            ArrayList<String> arr;
            while (sc.hasNextLine()) {
                data2=sc.nextLine();
                arr=new ArrayList<>();
                if (data2.contains("VAR")) {
                    while (sc.hasNextLine()) {
                        data = sc.nextLine();
                        if(data.contains("VAR")){
                            break;
                        }
                        if (data.contains("<NAME>")) {
                            name=(mySplit(data));
                        }
                        if (data.contains("<OUT")) {
                            arr.add(mySplit(data));
                        }
                    }
                    BayesianNode node = new BayesianNode(name,arr);
                    BN.put(node.getName(),node);
//                    System.out.println(arr);
                }if(data2.contains("DEF")){
                    data=sc.nextLine();
                    name=(mySplit(data));
                    BayesianNode node =BN.get(name);
                    while (sc.hasNextLine()) {
                        data = sc.nextLine();
                        if (data.contains("DEF")) {
                            break;
                        }if(data.contains("GIV")){
                            ParentName=mySplit(data);
                            BayesianNode parent=BN.get(ParentName);
                            node.addParent(parent);
                            parent.addChild(node);
                        }
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
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return BN;
    }

    //P(B=T|J=T,M=T)
    public static void main(String []argv) {
//

        try {
            String input="input.txt";
            File file=new File(input);
            Scanner sc= new Scanner(file);
            while(sc.hasNextLine()){
                String b=sc.nextLine();
                System.out.println(b);
                sc.nextLine();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


        String filename="alarm_net.xml";
        String filename2="big_net.xml";
        String input="input.txt";


        BayesianNetwork BN =new BayesianNetwork(makeNetwork(filename));
        ArrayList<String> evidence=new ArrayList<>();
        evidence.add("B=T");
//        evidence.add("B2=F");
//        evidence.add("C3=F");
        VariableElimination ve = new VariableElimination(BN, "J=T", evidence);
        ve.function2();
        BN =new BayesianNetwork(makeNetwork(filename));
//        TODO round 5th digit for function 2 and 3
//        TODO add switch for each function

//        BN.function1("P(B0=v3|C3=T,B2=F,C2=v3),1)");
        BN.function1("P(J=T|B=T),1)");
//        filename="/home/avi/IdeaProjects/Algorithms_And_AI_Proj/src/big_net.xml";
//        BayesianNetwork BnBig =new BayesianNetwork(makeNetwork(filename));
        //off by a little
//        BN.function1("P(J=T|B=T),1");
//        System.out.println(ans2);
        //Scanner sc=new Scanner();
//        String t="alarm_net.xml\n" +
//                "P(B=T|J=T,M=T),1\n" +
//                "P(B=T|J=T,M=T),2\n" +
//                "P(B=T|J=T,M=T),3\n" +
//                "P(J=T|B=T),1\n" +
//                "P(J=T|B=T),2\n" +
//                "P(J=T|B=T),3",v,w;
//        Scanner scr = new Scanner(t),sc;
//        v=scr.nextLine();
//        v=scr.nextLine();



    }
}
