package controller;

import java.util.HashMap;

/**
 * Created by alireza on 15/10/15.
 */
public class Record {
    // example {"name":"ali","id":"1","lname":"ahmad"}
    private HashMap<String ,String> values = new HashMap<String, String>();
    public void addValues(String key, String value){
        values.put(key,value);
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
    public String toString(){
        return values.toString();
    }
    public void remove(){
//        Record before = record.getBefore();
//        Record next = record.getNext();
//        if (before != null)
//            before.setNext(next);
//        if (next != null)
//            next.setBefore(before);
    }
}
