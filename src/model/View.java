package model;

import java.util.ArrayList;

import controller.InvalidParam;
import controller.commands.CreateTableCommand;
import controller.commands.InsertCommand;
import controller.commands.SelectCommand;

public class View extends Table{
	private String command = "SELECT ID FROM T WHERE TRUE;";
	/**
	 * Creates a new view 
	 * @param name 		the name of the view
	 * @param columns	columns list that we should get from the query that creates the view 
	 * @param command	the command that creates the view
	 */
	public View(String name, String[] columns,String command) {
		super(name, columns);
		this.command = command;
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * gets all the record in the view
	 */
	public Record[] getAllRecords(){
		// TODO : execute the command on the views table(s)
		SelectCommand sc = new SelectCommand();
		try {
			String[][] result = sc.returnResult(command); // first we should execute the view command to get the records
			String[] headers = sc.getHeaders(); // then we get the headers of the table(s)
			ArrayList<Record>records = new ArrayList<Record>();
			for (String[] record : result) { // convert each record from string[] to record and adds it to the arrayList
				Record r = new Record();
				for (int i = 0; i < record.length; i++) {
					r.addValues(headers[i], record[i]);
				}
				records.add(r);
			}
			return records.toArray(new Record[records.size()]); // returns the result
		} catch (InvalidParam e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (C1Constrain e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (C2Constrain e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
//	public static void main(String[] args) {
//		String cmd = "CREATE TABLE T (ID INT);";
//		String add = "INSERT INTO T VALUES (1);";
//		String cmd2 = "CREATE TABLE T2 (ID INT);";
//		String add2 = "INSERT INTO T2 VALUES (2);";
//		CreateTableCommand ctc = new CreateTableCommand();
//		InsertCommand is = new InsertCommand();
//		try {
//			ctc.doAction(cmd);
//			ctc.doAction(cmd2);
//			is.doAction(add);
//			is.doAction(add2);
//		} catch (InvalidParam | C1Constrain | C2Constrain e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		View v = new View("cmd", new String[]{"ID"}, "");
//		View v2 = new View("cmd2",new String[]{"Reza"},"");
//		Record[] records =v.times(v2).getAllRecords();
//		for (Record record : records) {
//			System.out.println(record);
//		}
//	}

}
