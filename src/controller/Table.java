/**
 * Created by alireza on 15/10/15.
 */
package controller;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.*;
public class Table {
    private String[] columns;
    private static HashMap<String,Table> tableHashMap = new HashMap<String, Table>();
    private TreeMap<String, ArrayList<Record>> indexes = new TreeMap<String, ArrayList<Record>>();
    private String index = "",indexName = "";
    private Record head= null, root= null;
    private ScriptEngine engine ;

    /**
     * Adds the given table to the tableHashmap
     * @param name : the name of the table
     * @param table : the Table object
     */
    public static void addTable(String name,Table table){
        tableHashMap.put(name,table);
    }

    /**
     * returns the table with the given name
     * @param name : the name of the table
     * @return the table object
     */
    public static Table getTable(String name){
        return tableHashMap.get(name);
    }
    public Table(String[] columns, ScriptEngine engine) {
        // the name of the table and all of the columns must be given
        this.columns = columns;
        this.engine = engine;
    }
    /**
     * create index for table
     * @param index : the name of the column in which table is indexed
     */
    public void addIndex(String indexName ,String index){
        this.index = index;
        this.indexName = indexName;
        Record record = root;
        // puts all the records in a tree
        while (record != null){
            String indexValue = record.returnValues().get(index).trim();
            if(indexes.get(indexValue) == null){ // if there is no record with this index the it creates a new array:ist and adds the record there
                ArrayList<Record>records = new ArrayList<Record>();
                records.add(record);
                indexes.put(indexValue,records );
            }else { // gets the arrayList and adds the record to arrayList
                 ArrayList<Record> i = indexes.get(indexValue);
                i.add(record);
            }
            record = record.getNext();
        }
    }

    /**
     * adds new record to the table records
     * @param values : values of the new record
     */
    public void addRecord(String[] values){
        // creates a new record and places all the values given from user to record
        Record record = new Record();
        for (int i = 0; i < values.length; i++) {
            record.addValues(columns[i], values[i].trim());
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
    private boolean parseCondition(String condition, Record record) throws ScriptException {
        return true;
    }
    /**
     *
     * @param condition : the condition string from query
     * @return : all matching records
     * @throws Exception : condition is wrong
     */
    public ArrayList<Record> getRecords(String condition) throws Exception {
        ArrayList<Record> data = new ArrayList<Record>();
        Record record = root;
        while (record != null){
            boolean result = parseCondition(condition,record);
            if (result) { // if the record satisfies the condition
                data.add(record);
            }
            record = record.getNext();
        }
        return data;
    }

    /**
     * deletes on condition
     * @param condition :  the condition string from query
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
                if(!index.equals("")){
                    ArrayList<Record> records = indexes.get(record.returnValues().get(index)); // gets the arrayList which the data is in it
                    records.remove(record); // removes it form arrayList
                }

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

    /**
     *
     * @param condition :  the condition string from query
     * @return : returns records which are equal to index
     */
    public ArrayList<Record> returnOnIndex(String condition){
        if (indexes.get(condition.trim())!= null)
            return indexes.get(condition.trim());
        else
            return new ArrayList<Record>();
    }

    /**
     *
     * @param condition : the condition string from query
     * @return : returns records which are higher than index
     */
    public ArrayList<Record> returnOnIndexHigher(String condition){
        ArrayList<Record> records = new ArrayList<Record>();
        if (indexes.get(condition.trim()) == null)
            return records;
        TreeMap<String,Record> treeMap = (TreeMap<String, Record>) indexes.higherEntry(condition.trim());
        System.out.println(treeMap.entrySet());
        return records;
    }
    /**
     *
     * @param condition : the condition string from query
     * @return : returns records which are lower than index
     */
    public ArrayList<Record> returnOnIndexLower(String condition){
        if (indexes.lowerKey(condition.trim())!= null)
            return indexes.get(condition.trim());
        else
            return new ArrayList<Record>();
    }
    public String getIndex(){
        return indexName;
    }

}
