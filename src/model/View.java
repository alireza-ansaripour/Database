package model;

import controller.InvalidParam;
import controller.commands.SelectCommand;

public class View extends Table{
	private String command = "SELECT ID FROM T WHERE TRUE;";
	public View(String name, String[] columns,String command) {
		super(name, columns);
		//this.command = command;
		// TODO Auto-generated constructor stub
	}
	public Record[] getAllRecords(){
		// TODO : execute the command on the views table(s)
		SelectCommand sc = new SelectCommand();
		try {
			String[][] result = sc.returnResult(command);
			System.out.println(result[0][0]);
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
	

}
