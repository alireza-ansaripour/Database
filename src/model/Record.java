package model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alireza on 15/10/15.
 */
public class Record {
    // example {"name":"ali","id":"1","lname":"ahmad"}
    private HashMap<String ,String> values = new HashMap<String, String>();
    
    public void addValues(String key, String value){
    	if (value == null){
    		values.put(key, null);
    		return;
    	}
    	if(value.equals("NULL")==true){
    		values.put(key,null);
    	}
    	else{
    		values.put(key, value);
    	}
    	
    	
    }
    
    public HashMap<String ,String > returnValues(){
        return values;
    }
    
    private Record next = null;
    private Record before = null;

    public Record getBefore() {
        return before;
    }

    public void setBefore(Record before) {
        this.before = before;
    }

    public Record getNext(){
        return next;
    }

    public void setNext(Record next){
        this.next = next;
    }
    
    
    public String[] getValues(){
    	ArrayList<String> list=new ArrayList<>();
    	for(String value: values.values()){
    		list.add(value);
    	}
    	String[] result=new String[list.size()];
    	return list.toArray(result);
    }
    
    
    /**
     * gets name of a variable then returns value of this variable.
     * @param name name of a variable in this record.
     * @return value of variable.
     */
    public String getValue(String name){
    	return this.values.get(name);
    }
    
    
    public String toString(){
        return values.toString();
    }
    
    public void update(String name,String value){
    	values.put(name, value);
    }
    
    public void remove(){
        Record before = this.getBefore();
        Record next = this.getNext();
        if (before != null)
            before.setNext(next);
        if (next != null)
            next.setBefore(before);
    }
    protected void finalize(){
    	if(this.next != null){
    		next.setBefore(before);
    	}
    	before.setNext(next);
    }
}
