package model.table;

import controller.*;

import java.util.*;

import model.InvalidRecord;
/**
 * Created by Alireza on 15/10/15.
 */
public class Table {
    private static HashMap<Table, Boolean> onDelete = new HashMap<Table, Boolean>(), onUpdate= new HashMap<Table, Boolean>();
	private int[] types;//0:INT, 1:VARCHAR.
	private String[] columns;
    private String name;
    private String primaryKey ="";
    private int pK;
    private ArrayList<Table>refrences= new ArrayList<Table>();
    private ArrayList<String>indexes=new ArrayList<String>();
    private HashMap<String,TreeMap<String, ArrayList<Record>>>treePair = new HashMap<String, TreeMap<String,ArrayList<Record>>>(); // <columnName,Treemap>
    private Record root= new Record(),head = root;
    private HashMap<Table, String>foreignKeys= new HashMap<Table, String>();
    
    
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
        onDelete.put(this, true);
        onUpdate.put(this, true);
    }
    public Table(String name,String[] columns){
    	this(name, columns, null);
    }
    /**
     * 
     * @param name   name of the table	
     * @param columns	columns name	
     * @param types   columns type
     * @param primaryKey	primary keys of table
     */
    public Table(String name,String[]columns,int[] types, String primaryKey){
    	this(name, columns, types);
    	this.primaryKey = primaryKey;
    	try {
			this.addIndex(primaryKey, "PK");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    /**
     * returns table headers
     * @return table columns name
     */
    public String[] getHeaders(){
    	return columns;
    }
    /**
     * returns the name of the primary key
     * @return	primary key name of the table
     */
    public String getPrimaryKeyName(){
    	return primaryKey;
    }
    /**
     * adds foreignKey to the table
     * @param t   the referenced table
     * @param cloumn	the name of the column in table
     */
    public void addForeignKey(Table t,String cloumn, Boolean onDelete, Boolean onUpdate){
    	foreignKeys.put(t, cloumn);
    	this.onDelete.put(this, onDelete);
    	this.onUpdate.put(this, onUpdate);
    	
    	
    }
    /**
     * creates index for table then puts all of records from LinkedList to TreeMap.
     * if there is no column name equals to columnName throws Exception.
     * @param index		name of index column in this table.
     */
    public void addIndex(String columnName,String indexName)throws Exception{
    	
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
        
        indexes.add(columnName);
        TreeMap<String, ArrayList<Record>> indexes = new TreeMap<String, ArrayList<Record>>();
        Record record = root.getNext();
        // puts all the records in a tree
        while (record != null){
            String indexValue = record.returnValues().get(columnName);
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
        treePair.put(columnName, indexes);
    }    
    /**
     * adds new record to the table records
     * if new record is not satisfied by length and type throws exception(InvalidRecord).
     * @param values	values of a record.
     */
    public void addRecord(String[] values)throws InvalidRecord{
    	
    	//satisfying record by length and type.
    	if(types != null){
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
    	}
    	//end checking.
    
    	
        // creates a new record and places all the values given from user to record
        Record record = new Record();
        for (int i = 0; i < values.length; i++) {
            record.addValues(columns[i], values[i]);
        }
        
        // checking c1
        if (primaryKey != ""){
        	if (record.getValue(primaryKey) == null)
        		throw new InvalidRecord();
        	// checking if the primary key is repeated or not
    		if(returnOnIndex(getPrimaryKeyName(), record.getValue(primaryKey)) != null && returnOnIndex(getPrimaryKeyName(), record.getValue(primaryKey)).size() != 0)
    			throw new InvalidRecord();
    		//if the primary key is null
	    	if(record.getValue(primaryKey).equals("NULL"))
	    		throw new InvalidRecord();
        }
        // checking c2
        // for each foreign key we should check that is there any record in the referenced table 
        for(Map.Entry<Table, String> e: foreignKeys.entrySet()){
        	Table table = e.getKey();
        	String column = e.getValue();
        	if(record.getValue(column)!= null &&(table.returnOnIndex(table.getPrimaryKeyName(), record.getValue(column)) == null || table.returnOnIndex(table.getPrimaryKeyName(), record.getValue(column)).size() == 0))
        		throw new InvalidRecord();
        }
        
        
        for(Map.Entry<String, TreeMap<String, ArrayList<Record>> > e:treePair.entrySet()){
        	TreeMap<String, ArrayList<Record>> indexes = e.getValue();
        	String indexValue = record.returnValues().get(e.getKey());
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
        String command = condition.toString();
        String[] parts = command.split(" ");
        if( indexes.contains(parts[0]) != false){
        	String index = parts[0];
        	try {
        		String val_inString = InputHandler.getValue(parts[2], null, null);
        		ArrayList<Record> result = returnOnIndex(index,val_inString);
	           	 if (result == null){
	           		 result = new ArrayList<Record>();
	           	 }
           	 	Record[] res=new Record[result.size()];
                return result.toArray(res);
			} catch (Exception e) {
				System.out.println("INVALID INPUT");
			}
        }
    	ArrayList<Record> result=new ArrayList<>();
    	
        Record record = root.getNext();
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
     * @throws InvalidRecord 
     */
    public void updateRecords(String columnName,String newValue,ClauseNode condition) throws InvalidRecord{
    	if(columnName.equals(primaryKey))
    		if (! onUpdate.get(this))
    			throw new InvalidRecord();
    	
    	
    	Record[] records=this.getRecords(condition);
    	HashMap<String, Integer> hash=new HashMap<String, Integer>();
    	
    	for(int i=0;i<this.columns.length;i++){
    		hash.put(this.columns[i], new Integer(i));
    	}
    	
    	String temp;
    	for(int i=0;i<records.length;i++){
    		temp=InputHandler.getValue(newValue, hash, records[i].getValues());
    		records[i].update(columnName, temp);
    		if (indexes.contains(columnName)){
    			TreeMap<String,ArrayList<Record>>tree = treePair.get(columnName);
    			String command = condition.toString();
    			if(command.equalsIgnoreCase("false"))
    				return;
    			if(command.equalsIgnoreCase("TRUE")){
    				tree = new TreeMap<String, ArrayList<Record>>();
    				tree.put(temp, new ArrayList<Record>(Arrays.asList(records)));
    				treePair.put(columnName, tree);
    				return;
    			}
    			String[] parts = command.split(" ");
    			String val = InputHandler.getValue(parts[2], null, null);
    			tree.put(val, null);
    			ArrayList<Record>recs = tree.get(temp);
    			recs.addAll(new ArrayList<Record>(Arrays.asList(records)));
    		}
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
     * @throws InvalidRecord 
     */
    public void removeRecords(ClauseNode condition) throws InvalidRecord{
    	if(! onDelete.get(this))
    		throw new InvalidRecord();
    	
        Record record = root.getNext();
        while (record != null){
            String[] values = new String[columns.length];
            HashMap<String, Integer> column=new HashMap<String, Integer>();
            for (int j = 0; j < columns.length; j++) {
                column.put(columns[j], j);
                values[j] = record.returnValues().get(columns[j]);
            }
            
            boolean check=false;
            try{
            	check=condition.checkCondition(column, values);
            }
            catch(Exception exception){
            	check=false;
            }
            Record next = record.getNext();
            if (check==true) { // the data should be deleted
            	for(Map.Entry<String, TreeMap<String, ArrayList<Record>> > e:treePair.entrySet()){
                	String indexValue = record.returnValues().get(e.getKey());
                	try{
    	                ArrayList<Record> records =returnOnIndex(e.getKey(),indexValue); // gets the arrayList which the data is in it
    	                records.remove(record); // removes it form arrayList
                	}catch(Exception ex){
                		System.err.println(ex.getMessage());
                	}
                }
            	if(next == null){
            		head = record.getBefore();
            	}else{
            		next.setBefore(record.getBefore());
            	}
            	Record before = record.getBefore();
            	before.setNext(next);
            }
            record = next;
        }

    }
    
    /**
     * gets an index then returns all of records with that index in an ArrayList.
     * @param index	index of records that must be given.
     * @return an ArrayList of record with this index.
     */
    public ArrayList<Record> returnOnIndex(String index,String value){
    	TreeMap<String, ArrayList<Record>> indexes = treePair.get(index);
    	if (value == null)
    		return null;
    	ArrayList<Record> resArrayList = indexes.get(value);
    	return resArrayList;
    }
    
    public String getName(){
    	return this.name;
    }
    
    
    public Record[] getAllRecords(){
    	ArrayList<Record>records = new ArrayList<Record>();
    	Record record = root.getNext();
    	while(record != null){
    		records.add(record);
    		record = record.getNext();
    	}
    	Record[] res=new Record[records.size()];
        return records.toArray(res);
    }
    
    
    public Table times(Table table){
    	String[] tableHeaders = table.getHeaders();
    	String[] destTableHeader = new String[tableHeaders.length+columns.length];
    	for (int i = 0; i < destTableHeader.length; i++) {
			if(i < columns.length){
				destTableHeader[i] = columns[i];
			}else{
				destTableHeader[i] = tableHeaders[i-columns.length];
			}
		}
    	Record[] firstTableRecords = getAllRecords();
    	Record[] secondTableRecord = table.getAllRecords();
    	Table dest = new Table("times", destTableHeader);
    	for (int i = 0; i < firstTableRecords.length; i++) {
			for (int j = 0; j < secondTableRecord.length; j++) {
				Record record = new Record();
				for (int k = 0; k < destTableHeader.length; k++) {
					if(k < columns.length){
						record.addValues(destTableHeader[k], firstTableRecords[i].getValue(destTableHeader[k]));
					}else{
						record.addValues(destTableHeader[k], secondTableRecord[j].getValue(destTableHeader[k]));
					}
				}
				try {
					dest.addRecord(record.getValues());
				} catch (InvalidRecord e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
    	
    	return dest;
    }
    
    public Table join(Table table){
    	String sharedKey = foreignKeys.get(table);
    	if (sharedKey == null)
    		return table.join(this);
    	Record[] firstTableRecords = getAllRecords();
    	String[] tableHeaders = table.getHeaders();
    	String[] destTableHeader = new String[tableHeaders.length+columns.length];
    	for (int i = 0; i < destTableHeader.length; i++) {
			if(i < columns.length){
				destTableHeader[i] = columns[i];
			}else{
				destTableHeader[i] = tableHeaders[i-columns.length];
			}
		}
    	Table dest = new Table("join", destTableHeader);
    	for (int i = 0; i < firstTableRecords.length; i++) {
			String value = firstTableRecords[i].getValue(sharedKey);
			if(value == null)
				continue;
			ArrayList<Record>records = table.returnOnIndex(table.getPrimaryKeyName(), value);
			Record[] secondTableRecord = new Record[records.size()];
			records.toArray(secondTableRecord);
			for (int j = 0; j < secondTableRecord.length; j++) {
				String[] values = new String[destTableHeader.length];
				for (int k = 0; k < destTableHeader.length; k++) {
					if(k < columns.length){
						String val = firstTableRecords[i].getValue(destTableHeader[k]);
						values[k] = val;
					}else{
						String val = secondTableRecord[j].getValue(destTableHeader[k]);
						if (val == null)
							val = "NULL";
						values[k] = val;
					}
				}
				try {
					dest.addRecord(values);
				} catch (InvalidRecord e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}	
    	return dest;
    }
    
    
    public boolean equals(Object table){
    	Table t = (Table) table;
    	if (t.getName() == this.getName())
    		return true;
    	return false;
    }
    
    public static void main(String[] args) throws InvalidRecord {
    	Table t1 = new Table("ali", new String[]{"name","pk1"},null,"pk1");
    	Table t2 = new Table("reza", new String[]{"pk2","fk","n2"},null,"pk2");
    	t2.addForeignKey(t1, "fk",true,true);
    	t1.addRecord(new String[]{"ali","12"});
    	ClauseNode c = InputHandler.createClauseTree("TRUE");
    	t1.addRecord(new String[]{"ahmad","13"});
    	t1.updateRecords("name", "jj", c);
    	t2.addRecord(new String[]{"2","12","j"});
    	t2.addRecord(new String[]{"3","13","k"});
    	t2.addRecord(new String[]{"4","13","l"});
    	Record[] records = t1.times(t2).getAllRecords();
    	for (int i = 0; i < records.length; i++) {
			System.out.println(records[i]);
		}
    }
    
}
