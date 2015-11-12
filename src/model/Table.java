package model;

import controller.*;
import java.util.*;

/**
 * Created by Alireza on 15/10/15.
 */
public class Table {
    
	private int[] types;//0:INT, 1:VARCHAR.
	private String[] columns;
    private String name;
    private TreeMap<String, ArrayList<Record>> indexes = new TreeMap<String, ArrayList<Record>>();
    private String index = "";
    private Record head= null, root= null;
    
    
    /**
     * constructor.
     * @param name	name of this table.
     * @param columns		name of each column in this table.
     * @param types	type of each column in this table.just can be INT or VARCHAR. INT:integer, VARCHAR:string.
     */
    public Table(String name,String[] columns,int[] types) {
        // the name of the table and all of the columns must be given
    	this.name = name;
        this.columns = columns;
        this.types=types;
    }
    
    
    
    /**
     * creates index for table then puts all of records from LinkedList to TreeMap.
     * if there is no column name equals to columnName throws Exception.
     * @param index		name of index column in this table.
     */
    public void addIndex(String columnName)throws Exception{
    	
    	//checking is there a column with this name or not.
    	boolean isColumnName=false;
    	for(int i=0;i<this.columns.length;i++){
    		if(this.columns[i].equals(columnName)==true){
    			isColumnName=true;
    		}
    	}
    	if(isColumnName==false){
    		throw new Exception();
    	}
    	
    	//setting value of this column to index.
        this.index = columnName;
        Record record = root;
        // puts all the records in a tree
        while (record != null){
            String indexValue = record.returnValues().get(this.index);
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
     * if new record is not satisfied by length and type throws exception(InvalidRecord).
     * @param values	values of a record.
     */
    public void addRecord(String[] values)throws InvalidRecord{
    	
    	//satisfying record by length and type.
    	if(values.length!=this.types.length){
    		throw new InvalidRecord();
    	}
    	for(int i=0;i<values.length;i++){
    		//if type is INT and value is not an integer number.
    		if(this.types[i]==0 && values[i].equals("NULL")==false && values[i].startsWith("\"")==true){
    			throw new InvalidRecord();
    		}
    		//if type is VARCHAR and value is not a string.
    		else if(this.types[i]==1 && values[i].equals("NULL")==false && values[i].startsWith("\"")==false){
    			throw new InvalidRecord();
    		}
    	}
    	//end checking.
    	
    	
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
                ArrayList<Record> records = new ArrayList<Record>();
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
     * gets a ClauseNode then returns all of records that satisfied by ClauseNode condition.
     * @param condition	a ClauseNode that its function gets each records and checks that record by condition.
     * @return an ArrayList of satisfied records. 
     */
    public Record[] getRecords(ClauseNode condition){
        
    	
    	ArrayList<Record> result=new ArrayList<>();
        Record record = root;
        while (record != null){
        	
            
        	String[] values = new String[columns.length];
            HashMap<String, Integer> column=new HashMap<String, Integer>();
            for (int j = 0; j < columns.length; j++) {
                column.put(columns[j], j);
                values[j] = record.returnValues().get(columns[j]);
            }
            
            //**********************************************
//            ClauseNode root= InputHandler.createClauseTree(condition);
            boolean check=false;
            try{
            	check=condition.checkCondition(column, values);
            }
            catch(Exception exception){
            	check=false;
            }
            
            if (check==true) { // if the record satisfies the condition
                result.add(record);
//            	data.add(record.returnValues());
            }
            
            record = record.getNext();
        }
        
        Record[] res=new Record[result.size()];
        return result.toArray(res);
    }
    
    
    
    /**
     * gets name of column and new value to update it. then gets records by condition and 
     * updates all of them.
     * @param columnName	name of column that must be updated.
     * @param newValue		new value to update. can be compute value.
     * @param condition		gets records by this condition.
     */
    public void updateRecords(String columnName,String newValue,ClauseNode condition){
    	Record[] records=this.getRecords(condition);
    	HashMap<String, Integer> hash=new HashMap<String, Integer>();
    	
    	for(int i=0;i<this.columns.length;i++){
    		hash.put(this.columns[i], new Integer(i));
    	}
    	
    	String temp;
    	for(int i=0;i<records.length;i++){
    		temp=InputHandler.getValue(newValue, hash, records[i].getValues());
    		records[i].update(columnName, temp);
    	}
    }
    
    
    
    /**
     * gets array of name of variables and a condition then returns value of that variables from all satisfied
     * records by condition.
     * @param variableNames an array of name of variables that user wants to see.
     * @param condition returns variables of satisfied records by this condition.
     * @return first dimension of array[][] is a record and second dimension of array[][] is variables of that
     * record.
     */
    public String[][] select(String[] variableNames,ClauseNode condition){
    	String[][] result;
    	Record[] records=this.getRecords(condition);
    	result=new String[records.length][variableNames.length];
    	
    	
    	for(int i=0;i<result.length;i++){
    		for(int j=0;j<result[0].length;j++){
    			result[i][j]=records[i].getValue(variableNames[j]);
    		}
    	}
    	
    	return result;
    }
    
    
    
    
    /**
     * gets a ClauseNode as condition then removes all of satisfied record with that condition.
     * @param condition	a ClauseNode that its function satisfies records to delete.
     */
    public void removeRecords(ClauseNode condition){
    	
    	
        Record record = root;
        while (record != null){
            String[] values = new String[columns.length];
            HashMap<String, Integer> column=new HashMap<String, Integer>();
            for (int j = 0; j < columns.length; j++) {
                column.put(columns[j], j);
                values[j] = record.returnValues().get(columns[j]);
            }
            
//            ClauseNode r= InputHandler.createClauseTree(condition);
            boolean check=false;
            try{
            	check=condition.checkCondition(column, values);
            }
            catch(Exception exception){
            	check=false;
            }
            
            
            if (check==true) { // the data should be deleted
            	
            	try{
	                ArrayList<Record> records = indexes.get(record.returnValues().get(index)); // gets the arrayList which the data is in it
	                records.remove(record); // removes it form arrayList
            	}
            	catch(NullPointerException exception){
            		
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
     * gets an index then returns all of records with that index in an ArrayList.
     * @param index	index of records that must be given.
     * @return an ArrayList of record with this index.
     */
    public ArrayList<Record> returnOnIndex(String index){
        return indexes.get(index);
    }
    
    
    
    public String getName(){
    	return this.name;
    }

    
    
//    public static void main(String[] args) {
//        String[] c = {"fname","lname","year","age"};
//        Table t = new Table("ali",c);
//        t.addIndex("year");
//        String[] j = {"ali","ansari","1392","he"};
//        t.addRecord(j);
//        j = new String[]{"alireza","ansaripour","1392","it"};
//        t.addRecord(j);
//        j = new String[]{"hamid","miri","1392","se"};
//        t.addRecord(j);
//        j = new String[]{"ali","rezaie","1394","se"};
//        t.addRecord(j);
//        j = new String[]{"Ahmad","Ahmadi","1393","se"};
//        t.addRecord(j);
//        try {
////            t.deleteRecords("fname = ali");
////            ArrayList<Record> records = t.returnOnIndex("1392");
////
////            System.out.println(records);
//            System.out.println("result: " + t.getRecords("year = 1395 AND fname = ali OR TRUE"));
//
//
//        }catch (Exception e){
//            System.out.println("error: "+e.getMessage());
//        }
//    }
}
