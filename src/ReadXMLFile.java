import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

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
                    System.out.println(arr);
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
                        }
                        System.out.println(data);
                    }
                }
            }
            System.out.println(BN);
            System.out.println(BN.get("A").getCpt());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return BN;
    }

    //P(B=T|J=T,M=T)
    public static void main(String []argv) {
        String filename="/home/avi/IdeaProjects/Algorithims_And_AI_Proj/src/alarm_net.xml";
        BayesianNetwork BN =new BayesianNetwork(makeNetwork(filename));

    }
}
