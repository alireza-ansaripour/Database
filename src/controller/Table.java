package controller;

import controller.ClauseNode;
import controller.InputHandler;

import java.util.*;

/**
 * Created by alireza on 15/10/15.
 */
public class Table {
    private String[] columns;
    private String name;
    private TreeMap<String, ArrayList<Record>> indexes = new TreeMap<String, ArrayList<Record>>();
    private String index = "";
    private Record head= null, root= null;
    public Table(String name,String[] columns) {
        // the name of the table and all of the columns must be given
        this.columns = columns;
        this.name = name;
    }
    /**
     * create index for table
     * @param index : index of selected record
     */
    public void addIndex(String index){
        this.index = index;
        Record record = root;
        // puts all the records in a tree
        while (record != null){
            String indexValue = record.returnValues().get(index);
            if(indexes.get(indexValue) == null){
                ArrayList<Record>records = new ArrayList<Record>();
                records.add(record);
                indexes.put(indexValue,records );
            }else {
                 ArrayList<Record> i = indexes.get(indexValue);
                i.add(record);
            }
            record = record.getNext();
        }
    }

    /**
     * adds new record to the table records
     * @param values
     */
    public void addRecord(String[] values){
        // creates a new record and places all the values given from user to record
        Record record = new Record();
        for (int i = 0; i < values.length; i++) {
            record.addValues(columns[i], values[i]);
        }
        if (head == null){
            head = record;
        }
        if (root == null){
            root = record;
            return;
        }
        if (!index.equals("")){ // update the index tree
            String indexValue = record.returnValues().get(index);
            if(indexes.get(indexValue) == null){ // if there is no record with the index
                ArrayList<Record>records = new ArrayList<Record>();
                records.add(record);
                indexes.put(indexValue,records );
            }else {
                ArrayList<Record> i = indexes.get(indexValue);
                i.add(record);
            }
        }
        // sets the next and before of the record
        head.setNext(record);
        record.setBefore(head);
        head = record; // update the head
    }

    /**
     * returns all records
     * @return
     */
    public ArrayList<HashMap<String,String>> getRecords(String condition) throws Exception {
        ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
        Record record = root;
        while (record != null){
            // Hamids logic
            String[] values = new String[columns.length];
            HashMap<String, Integer> column=new HashMap<String, Integer>();
            for (int j = 0; j < columns.length; j++) {
                column.put(columns[j], j);
                values[j] = record.returnValues().get(columns[j]);
            }
            ClauseNode root= InputHandler.createClauseTree(condition);
            if (root.checkCondition(column, values)) { // if the record satisfies the condition
                data.add(record.returnValues());
            }
            record = record.getNext();
        }
        return data;
    }

    /**
     * deletes on condition
     * @param condition
     */
    public void deleteRecords(String condition) throws Exception {
        Record record = root;
        while (record != null){
            String[] values = new String[columns.length];
            HashMap<String, Integer> column=new HashMap<String, Integer>();
            for (int j = 0; j < columns.length; j++) {
                column.put(columns[j], j);
                values[j] = record.returnValues().get(columns[j]);
            }
            ClauseNode r= InputHandler.createClauseTree(condition);
            if (r.checkCondition(column, values)) { // the data should be deleted

                ArrayList<Record> records = indexes.get(record.returnValues().get(index)); // gets the arrayList which the data is in it
                records.remove(record); // removes it form arrayList

                // removes it from linkList
                Record before = record.getBefore();
                Record next = record.getNext();
                if (before != null){
                    before.setNext(next);
                }else {
                    root = next;
                    root.setBefore(null);
                }
                if(next != null){
                    next.setBefore(before);
                }else {
                    before.setNext(null);
                    head = before;
                }

            }
            record = record.getNext();
        }

    }
    public ArrayList<Record> returnOnIndex(String condition){
        return indexes.get(condition);
    }

    public static void main(String[] args) {
        String[] c = {"fname","lname","year","age"};
        Table t = new Table("ali",c);
        t.addIndex("year");
        String[] j = {"ali","ansari","1392","he"};
        t.addRecord(j);
        j = new String[]{"alireza","ansaripour","1392","it"};
        t.addRecord(j);
        j = new String[]{"hamid","miri","1392","se"};
        t.addRecord(j);
        j = new String[]{"ali","rezaie","1394","se"};
        t.addRecord(j);
        j = new String[]{"Ahmad","Ahmadi","1393","se"};
        t.addRecord(j);
        try {
//            t.deleteRecords("fname = ali");
//            ArrayList<Record> records = t.returnOnIndex("1392");
//
//            System.out.println(records);
            System.out.println("result: " + t.getRecords("year = 1395 AND fname = ali OR TRUE"));


        }catch (Exception e){
            System.out.println("error: "+e.getMessage());
        }
    }
}
