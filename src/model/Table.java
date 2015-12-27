package model;

import controller.*;

import java.util.*;

/**
 * Created by Alireza on 15/10/15.
 */
public class Table {
    
	public int[] types;//0:INT, 1:VARCHAR.
	public String[] columns;
    public String name;
    public ArrayList<String>indexes=new ArrayList<String>();
    public String index = "";
    public Record root= new Record(),head = root;
    public String primaryKey ="";
    private boolean onDelete = true,onUpdate = true;
	public int pK;
    public ArrayList<Table>refrences= new ArrayList<Table>();
    public HashMap<String,TreeMap<String, ArrayList<Record>>>treePair = new HashMap<String, TreeMap<String,ArrayList<Record>>>(); // <columnName,Treemap>
    public HashMap<Table, String>foreignKeys= new HashMap<Table, String>();
    
    
    
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
    
    public Table(String name,String[] columns){
    	this(name, columns, null);
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
     * gets a name and set it as this table primary key.
     * @param name name of one of this table columns.
     * @throws InvalidParam if name is not name of this table columns.
     */
    public void setPrimaryKeyName(String name)throws InvalidParam{
    	boolean found=false;
    	for(int i=0;i<this.columns.length;i++){
    		if(this.columns[i].equals(name)==true){
    			found=true;
    			break;
    		}
    	}
    	
    	if(found==false){
    		throw new InvalidParam();
    	}
    	
    	this.primaryKey=name;
    	try {
			this.addIndex(primaryKey, "PK");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    /**
     * adds foreignKey to the table
     * @param t   the referenced table
     * @param cloumn	the name of the column in table
     */
    public void addForeignKey(Table t,String cloumn, Boolean Delete, Boolean Update){
    	foreignKeys.put(t, cloumn);
    	t.refrences.add(this);
    	if(!Delete)
    		t.onDelete = false;
    	if(!Update)
    		t.onUpdate = false;
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
     * @throws C1Constrain if another record exist with that pk or the pk is null
     * @throws C2Constrain checks whether the fk exists in the referenced table
     */
    public void addRecord(String[] values)throws InvalidRecord, C1Constrain, C2Constrain{
    	
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
        		throw new C1Constrain();
        	// checking if the primary key is repeated or not
    		if(returnOnIndex(getPrimaryKeyName(), record.getValue(primaryKey)) != null && returnOnIndex(getPrimaryKeyName(), record.getValue(primaryKey)).size() != 0)
    			throw new C1Constrain();
    		//if the primary key is null
	    	if(record.getValue(primaryKey).equals("NULL"))
	    		throw new C1Constrain();
        }
        // checking c2
        // for each foreign key we should check that is there any record in the referenced table 
        for(Map.Entry<Table, String> e: foreignKeys.entrySet()){
        	Table table = e.getKey();
        	String column = e.getValue();
        	if(record.getValue(column)!= null &&(table.returnOnIndex(table.getPrimaryKeyName(), record.getValue(column)) == null || table.returnOnIndex(table.getPrimaryKeyName(), record.getValue(column)).size() == 0))
        		throw new C2Constrain();
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
        System.err.println(head.getValue(primaryKey));
        head.setNext(record);
        record.setBefore(head);
        head = record; // update the head
        System.err.println(head.getValue(primaryKey));
        
    }

    
    
    /**
     * gets a ClauseNode then returns all of records that satisfied by ClauseNode condition.
     * @param condition	a ClauseNode that its function gets each records and checks that record by condition.
     * @return an ArrayList of satisfied records. 
     */
    public Record[] getRecords(ClauseNode condition){
        try {
			String[] parts = condition.getVariable();
			
			String temp = InputHandler.getValue(parts[1],null, null);
			if (treePair.containsKey(parts[0])){
				if(parts[2].equals("=")){
					ArrayList<Record>records = returnOnIndex(parts[0], temp);
					Record[] res = new Record[records.size()];
					records.toArray(res);
					return res;
				}
			}
		} catch (Exception e) {
			System.err.println("SFDF");
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
            
            boolean check=false;
            try{
            	check=condition.checkCondition(column, values);
            }
            catch(Exception exception){
            	check=false;
            }
            
            if (check==true) { // if the record satisfies the condition
                result.add(record);
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
     * @throws C1Constrain  if another record is with that pk  
     * @throws FKConstrain  if onDelete is false
     */
    public void updateRecords(String columnName,String newValue,ClauseNode condition) throws InvalidRecord, C2Constrain, C1Constrain, FKConstrain  {
    	// if the onUpdate is false then this method will throw an exception
    	if(columnName.equals(primaryKey))
    		if (!onUpdate)
    			throw new FKConstrain();
    	
    	
    	Record[] records=this.getRecords(condition);
    	HashMap<String, Integer> hash=new HashMap<String, Integer>();
    	for(int i=0;i<this.columns.length;i++){
    		hash.put(this.columns[i], new Integer(i));
    	}
    	
    	String temp;
    	for(int i=0;i<records.length;i++){
    		temp=InputHandler.getValue(newValue, hash, records[i].getValues(this.columns));
    		TreeMap<String, ArrayList<Record>>t = treePair.get(primaryKey); // the primary key tree
    		if (t != null){ // if there is a primary key
    			ArrayList<Record>records2 = t.get(temp);
        		// if there is a record with that primary key
        		if(records2 != null && records2.size() != 0)
        			throw new C1Constrain();
    		}
    		// it should check all the referenced table 
    		for(Map.Entry<Table, String> e: foreignKeys.entrySet()){
            	Table table = e.getKey();
            	String column = e.getValue();
            	if(column.equals(columnName)){
            		if(table.returnOnIndex(table.getPrimaryKeyName(), temp) == null)
            			throw new C2Constrain();
            		if(table.returnOnIndex(table.getPrimaryKeyName(), temp).size() == 0)
            			throw new C2Constrain();
            	}
            	
            }
    		if (indexes.contains(columnName)){ // if the update is on a column name
    			ArrayList<Record> r = returnOnIndex(columnName, records[i].getValue(columnName));
    			r.remove(records[i]);
    			TreeMap<String,ArrayList<Record>>tree = treePair.get(columnName);
    			ArrayList<Record> r2 = tree.get(temp);
    			if (r2== null){
    				tree.put(temp, new ArrayList<Record>());
    				r2 = tree.get(temp);
    			}
    			r2.add(records[i]);
    		}
    		records[i].update(columnName, temp);
    		for (Table table : refrences) {
				table.updateByForeignKey(this, records[i].getValue(primaryKey));
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
     * @throws C2Constrain if the onDelete is false
     * @throws FKConstrain 
     */
    public void removeRecords(ClauseNode condition) throws  C2Constrain, FKConstrain{
    	if(!onDelete)
    		throw new C2Constrain();
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
            	for (Table table : refrences) {
					table.removeByForeignKey(this, record.getValue(primaryKey));
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
     * when the onDelete is cascade the referenced table removes the records
     * @param t	     :The referenced table
     * @param value  :The value of the fk
     * @throws FKConstrain if onDelete is false
     */
    private void removeByForeignKey(Table t,String value) throws FKConstrain{
    	if(! onDelete)
    		throw new FKConstrain();
    	Record[] records = getAllRecords();
    	String key = "";
    	for(Map.Entry<Table, String>e:foreignKeys.entrySet()){
    		Table table = e.getKey();
    		if (table.name == t.name)
    			key = foreignKeys.get(table);
    	}
    	for (Record record : records) {
			if(record.getValue(key).equals(value)){
				if(record.getValue(primaryKey)!=null){
					String val = record.getValue(primaryKey);
					for (Table table : refrences) {
						table.removeByForeignKey(this, val);
					}
				}
				Record next = record.getNext();
				Record before = record.getBefore();
				if(next != null)
					next.setBefore(before);
				else
					head = record.getBefore();
				before.setNext(next);
			}
		}
    }
    /**
     * like deleteOnFK
     * @param t     :the ref table	
     * @param value : the value to be deleted
     */
    private void updateByForeignKey(Table t,String value){
    	String key = foreignKeys.get(t);
    	Record[] records = getAllRecords();
    	for (Record record : records) {
			record.update(key, value);
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

    
    /**
     * returns all records of this table in an array.
     * @return all records of this table.
     */
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
    	String tableName = table.getName();
    	String[] destTableHeader = new String[tableHeaders.length+columns.length];
    	String[] copy = new String[destTableHeader.length];
    	for (int i = 0; i < destTableHeader.length; i++) {
			if(i < columns.length){
				destTableHeader[i] = columns[i];
				copy[i] = name+"."+destTableHeader[i];
			}else{
				destTableHeader[i] = tableHeaders[i-columns.length];
				copy[i] = tableName+"."+destTableHeader[i];
			}
		}
    	Record[] firstTableRecords = getAllRecords();
    	Record[] secondTableRecord = table.getAllRecords();
    	Table dest = new Table("times", copy);
    	for (int i = 0; i < firstTableRecords.length; i++) {
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
				} catch (C1Constrain e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (C2Constrain e) {
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
    	String tableName = table.getName();
    	Record[] firstTableRecords = getAllRecords();
    	String[] tableHeaders = table.getHeaders();
    	String[] destTableHeader = new String[tableHeaders.length+columns.length];
    	String[] copy = new String[destTableHeader.length];
    	for (int i = 0; i < destTableHeader.length; i++) {
			if(i < columns.length){
				destTableHeader[i] = columns[i];
				copy[i] = name+"."+destTableHeader[i];
			}else{
				destTableHeader[i] = tableHeaders[i-columns.length];
				copy[i] = tableName+"."+destTableHeader[i];
			}
		}
    	Table dest = new Table("join", copy);
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
				} catch (C1Constrain e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (C2Constrain e) {
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
    
    
    
    
    
    
}