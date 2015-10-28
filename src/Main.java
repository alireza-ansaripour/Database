import controller.Table;

import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Created by alireza on 26/10/15.
 */
public class Main {
    public static void main(String[] args) {
        TreeMap<Integer,String> map = new TreeMap<Integer, String>();
        map.put(1,"alireza");
        map.put(1,"reza");
        System.out.println(map.get(1));
//        String input = "";
//        Scanner scanner = new Scanner(System.in);
//        HashMap<String, Table>tablesHashMap = new HashMap<String, Table>(); // hashmap for tables
//        while (false){ // start getting input
//            input = scanner.nextLine().toLowerCase();
//            String firstPart = input.substring(0,input.indexOf(" "));
//
//
//
//            if (firstPart.equals("create")){
//                input = input .replace("create ","");
//                String type = input.substring(0,input.indexOf(" "));
//                if(type.equals("table")){
//                    input = input .replace("table ","");
//                    String tName = input.substring(0, input.indexOf(" ")); // the name of the table
//                    String columsString = input.substring(input.indexOf("(")+1,input.indexOf(")"));
//                    String[] colums = columsString.split(",");
//                    for (int i = 0; i < colums.length; i++) {
//                        colums[i] = colums[i].substring(0,colums[i].indexOf(" "));
//                    }
//                    tablesHashMap.put(tName,new Table(tName,colums));
//                }else{ // create index
//                    //TODO: create index
//                }
//            }else
//
//
//            if (firstPart.equals("insert")){
//                input = input.replace("insert into ","");
//                String tName = input.substring(0,input.indexOf(" "));
//                Table table = tablesHashMap.get(tName);
//                String valuesString = input.substring(input.indexOf("(")+1,input.indexOf(")"));
//                String[] values = valuesString.split(",");
//                table.addRecord(values);
//            }else
//
//            if(firstPart.equals("select")){
//                input = input.replace("select ","");
//                String tName = input.substring(input.indexOf("from ")+5);
//                Table table = tablesHashMap.get(tName.trim());
//                String condition = input.substring(input.indexOf("where ")+6);
//                try {
//                    System.out.println(table.getRecords(condition));
//                }catch (Exception e){
//
//                }
//            }
//
//        }
    }
}
