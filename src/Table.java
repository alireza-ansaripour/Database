import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alireza on 15/10/15.
 */
public class Table {
    private ArrayList<String> colums = new ArrayList<String>();
    private String name;
    private ArrayList<Record> records = new ArrayList<Record>();

    public Table(String name,ArrayList<String> colums) {
        // the name of the table and all of the colums must be given
        this.colums = colums;
        this.name = name;
    }

    /**
     * adds new record to the table records
     * @param values
     */
    public void addRecord(ArrayList<String> values){
        // creates a new record and places all the values given from user to record
        Record record = new Record();
        for (int i = 0; i < values.size(); i++) {
            record.addValues(colums.get(i), values.get(i));
        }
        records.add(record);// adds the record to table
    }

    /**
     * returns all records
     * @return
     */
    public ArrayList<HashMap<String,String>> getTableData(){
        ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
        for (int i = 0; i < records.size(); i++) {
            data.add(records.get(i).returnValues());
        }
        return data;
    }

    /**
     * deletes the i'th record given
     * @param number
     */
    public void deleteRecord(int number){
        records.remove(number);
    }
    public static void main(String[] args) {
        ArrayList<String >vals = new ArrayList<String>();
        vals.add("id");
        vals.add("first name");
        vals.add("last name");
        Table t1 = new Table("t1",vals);
        ArrayList<String >record = new ArrayList<String>();
        record.add("1");
        record.add("alireza");
        record.add("ansaripour");
        t1.addRecord(record);
        record = new ArrayList<String>();
        record.add("2");
        record.add("hamid");
        record.add("miri");
        t1.addRecord(record);
        System.out.println(t1.getTableData());
        t1.deleteRecord(1);
        System.out.println(t1.getTableData());
    }
}
