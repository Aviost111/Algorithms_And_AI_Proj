import jdk.jfr.internal.tool.Main;

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
            System.out.println("hi");
            URL file = Ex1.class.getResource(filename);
            assert file != null;
            File URI = new File(file.toURI());
            Scanner sc = new Scanner(URI);
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
//                        System.out.println(data);
                    }
                }
            }
//            System.out.println(BN);
//            System.out.println(BN.get("A").getCpt());
        } catch (FileNotFoundException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return BN;
    }

    //P(B=T|J=T,M=T)
    public static void main(String []argv) {
//

//        try {
//            String input="input.txt";
//            File file=new File(input);
//            Scanner sc= new Scanner(file);
//            while(sc.hasNextLine()){
//                String b=sc.nextLine();
//                System.out.println(b);
//                sc.nextLine();
//            }
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
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
            System.out.println(net);
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
                        finalAns=ans[2]+","+(int)ans[0]+","+(int)ans[1]+"\n";
                        fw.write(finalAns);
                        break;
                    case 2:
                        ve = new VariableElimination(BN, query, evidence);
                        ve.function2(ans);
                        finalAns=ans[2]+","+(int)ans[0]+","+(int)ans[1]+"\n";
                        BN =new BayesianNetwork(makeNetwork(net));
                        fw.write(finalAns);
                        break;
                    case 3:
                        ve = new VariableElimination(BN, query, evidence);
                        ve.function2(ans);
                        finalAns=ans[2]+","+(int)ans[0]+","+(int)ans[1]+"\n";
                        System.out.println("do case 3");
                        BN =new BayesianNetwork(makeNetwork(net));
                        fw.write(finalAns);
                        break;
                }

            }
            fw.close();

        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
//        try {
//            int arr[]=new int[3];
//            arr[0]=5;
//            arr[1]=3;
//            arr[2]=6;
//            String str=arr[0]+","+arr[1]+","+arr[2]+"";
////            URL out = Ex1.class.getResource("output.txt");
//            FileWriter fw = new FileWriter(new File("src", "output.txt"));
//            fw.write("hello world\n");
//            fw.write(str);
//            fw.close();
//            PrintWriter outs = new PrintWriter(fw);
//            outs.println(str);
//            System.out.println("Successfully wrote to the file.");
//        } catch (IOException e) {
//            System.out.println("An error occurred.");
//            e.printStackTrace();
//
//        }
//        TODO round 5th digit for function 2 and 3



    }
}
