//import java.util.ArrayList;
//import java.util.Hashtable;
//import java.util.Set;
//
//public class temp {
//    public double function1(String input){
//        String []arr2=input.split("[()]")[1].split("[,|=]");
//        double numerator=0,subSum=1,denominator=0;
//        ArrayList<String> keys=new ArrayList<>();
//        Set<String> setKeys= BN.keySet();//make arraylist of keys (by making a set and converting)
//        ArrayList<Integer> indexes=new ArrayList<>(),list;
//        boolean isFirst=true;
//        Hashtable<String,ArrayList<Integer>> tableOfLists=new Hashtable<>();
//        for (String key:setKeys) {
//            keys.add(key);
//            indexes.add(0);
//            int size =this.BN.get(key).getVars().size();
//            list=new ArrayList<>();
//            for(int i=1;i<size+1;i++){
//                list.add(i);
//            }
//            if(isFirst){
//                list.add(null);
//                isFirst=false;
//            }
//            tableOfLists.put(key,list);
//        }
//        for (int i=0;i<arr2.length;i=i+2){//change the list of all known variables
//            list=new ArrayList<>();
//            list.add(BN.get(arr2[i]).getVars().indexOf(arr2[i+1])+1);
//            tableOfLists.replace(arr2[i],list);
//        }
//        while(tableOfLists.get(keys.get(0)).get(indexes.get(0))!=null) {
//
//
//            for (int i = 0; i < keys.size(); i++) {//goes over the keys
//                list = new ArrayList<>();
//                for (BayesianNode key : BN.get(keys.get(i)).getParents()) {
//                    list.add(tableOfLists.get(key.getName()).get(indexes.get(keys.indexOf(key.getName()))));//adds to list the parents wanted value
//                }
//                list.add(tableOfLists.get(keys.get(i)).get(indexes.get(i)));
//                subSum *= BN.get(keys.get(i)).getCpt().getProb(list);
//            }
//            numerator+=subSum;
//            subSum=1;
//            //need to add to the last index
//            for(int i=indexes.size()-1;i>=0;i--){
//                indexes.set(i,indexes.get(i)+1);
//                if(indexes.get(i)<tableOfLists.get(keys.get(i)).size()){
//                    break;
//                }
//                indexes.set(i,0);
//            }
//        }
//
//
//        return numerator;
//    }
//}
