import java.util.HashMap;

/**
 * Created by alireza on 15/10/15.
 */
public class Record {
    private HashMap<String ,String> values = new HashMap<String, String>();
    public void addValues(String key, String value){
        values.put(key,value);
    }
    public HashMap<String ,String > returnValues(){
        return values;
    }

}
