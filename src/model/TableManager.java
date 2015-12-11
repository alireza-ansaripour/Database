package model;

import java.util.HashMap;

import controller.ClauseNode;
import controller.InvalidParam;

public class TableManager {
	
	
	private static HashMap<String, Table> tables=new HashMap<String, Table>();
	
	
	
	/**
	 * 
	 */
	public static void createTable(String name,String[] columnNames,String[] types)throws InvalidParam{
		
		//0:integer, 1:varChar.
		int[] intTypes=new int[types.length];
		for(int i=0;i<types.length;i++){
			if(types[i].equals("INT")==true){
				intTypes[i]=0;
			}
			else if(types[i].equals("VARCHAR")==true){
				intTypes[i]=1;
			}
			else{
				throw new InvalidParam();
			}
		}
		
		Table table=new Table(name,columnNames, intTypes);
		
		if(tables.containsKey(name)==false){
			tables.put(name, table);
		}
		else{
			throw new InvalidParam();
		}
		
	}
	
	
	
	
	public static void createIndex(String tableName,String indexName,String columnName){
		
		Table t = tables.get(tableName.trim());
		try {
			t.addIndex(columnName, indexName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public static String[][] select(String tableName,String[] variables,ClauseNode condition){
		Table table=tables.get(tableName);
		String[][] result=table.select(variables, condition);
		return result;
	}
	
	
	
	public static Record[] getRecords(String tableName,ClauseNode condition){
		Table table=tables.get(tableName);
		Record[] result=table.getRecords(condition);
		return result;
	}
	
	
	
	
	
	public static void updateRecords(String tableName,String columnName,String newValue,ClauseNode condition){
		Table table=tables.get(tableName);
		try {
			table.updateRecords(columnName, newValue, condition);
		} catch (InvalidRecord e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
	public static void addRecord(String tableName,String[] variables) throws InvalidParam{
		
		Table table=tables.get(tableName);
		try {
			table.addRecord(variables);
		} catch (InvalidRecord e) {
			throw new InvalidParam();
		}
		
	}
	
	
	
	
	public static void removeRecords(String tableName,ClauseNode condition){
		
		Table table=tables.get(tableName);
		try {
			table.removeRecords(condition);
		} catch (InvalidRecord e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
