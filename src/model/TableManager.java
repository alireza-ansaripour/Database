package model;

import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import controller.ClauseNode;
import controller.InvalidParam;

public class TableManager {
	
	
	private static HashMap<String, Table> tables=new HashMap<String, Table>();
	
	
	/**
	 * 
	 * @param name
	 * @param columnNames
	 * @param types
	 * @throws InvalidParam
	 */
	public static void createTable(String name,String[] columnNames,String[] types,String information)
			throws InvalidParam{
		
		
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
		
		Pattern pattern;
		Matcher matcher;
		String copy=information;
		String primary="PRIMARY KEY\\s+([A-Za-z0-9]+)\\s*(.*)";
		String foreign=
			"FOREIGN KEY\\s+([A-Za-z0-9]+)\\s+REFERENCES\\s+([A-Za-z0-9]+)\\s+ON DELETE\\s+([A-Za-z0-9]+)\\s+ON UPDATE\\s+([A-Za-z0-9]+)\\s*(.*)";
		
		if(copy.startsWith("PRIMARY")==true){
			pattern=Pattern.compile(primary);
			matcher=pattern.matcher(copy);
			if(matcher.find()==true){
				String pKey=matcher.group(1);
				table.setPrimaryKeyName(pKey);
				copy=matcher.group(2);
			}
			else{
				throw new InvalidParam();
			}
		}
		
		for(;;){
			if(copy.startsWith("FOREIGN")==true){
				pattern=Pattern.compile(foreign);
				matcher=pattern.matcher(copy);
				if(matcher.find()==true){
					String fKeyName=matcher.group(1);
					String fKeyTableName=matcher.group(2);
					
					boolean onDelete;
					if(matcher.group(3).equals("RESTRICT")){
						onDelete=false;
					}
					else if(matcher.group(3).equals("CASCADE")){
						onDelete=true;
					}
					else{
						throw new InvalidParam();
					}
					
					boolean onUpdate;
					if(matcher.group(4).equals("RESTRICT")){
						onUpdate=false;
					}
					else if(matcher.group(4).equals("CASCADE")){
						onUpdate=true;
					}
					else{
						throw new InvalidParam();
					}
					
					Table fkTable=null;
					fkTable=tables.get(fKeyTableName);
					
					if(fkTable==null){
						throw new InvalidParam();
					}
					
					table.addForeignKey(fkTable, fKeyName, onDelete, onUpdate);
					copy=matcher.group(5);
					
				}
				else{
					throw new InvalidParam();
				}
			}
			else{
				break;
			}
		}
		
		if(tables.containsKey(name)==false){
			tables.put(name, table);
		}
		else{
			throw new InvalidParam();
		}
		
//		System.out.println("primary key: "+table.primaryKey);
//		for(Map.Entry<Table, String> pair:table.foreignKeys.entrySet()){
//			System.out.println("foreignKey: "+pair.getKey().name+" "+pair.getValue().toString());
//		}
//		
//		for(Map.Entry<Table, Boolean> pair:table.onDelete.entrySet()){
//			System.out.println("onDelete: "+pair.getKey().name+" "+pair.getValue().toString());
//		}
//		
//		for(Map.Entry<Table, Boolean> pair:table.onUpdate.entrySet()){
//			System.out.println("onUpdate: "+pair.getKey().name+" "+pair.getValue().toString());
//		}
		
		
	}
	
	
	
	
	
	public static void createIndex(String tableName,String indexName,String columnName){
		
		Table t = tables.get(tableName.trim());
		try {
			t.addIndex(columnName, indexName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	public static String[][] select(String tableName,String[] variables,ClauseNode condition){
		Table table;
		
		boolean MorJ=true;
		
		Table[] tablesArray=null;
		if(tableName.contains(",")==true){
			String[] temp=tableName.split(",");
			tablesArray=new Table[temp.length];
			
			table=tables.get(temp[0]);
			tablesArray[0]=table;
			
			Table tempTable;
			for(int i=1;i<temp.length;i++){
				tempTable=tables.get(temp[i]);
				table=table.times(tables.get(temp[i]));
				tablesArray[i]=tempTable;
			}
		}
		else if(tableName.contains("JOIN")==true){
			String[] temp=tableName.split(" JOIN ");
			tablesArray=new Table[temp.length];
			
			table=tables.get(temp[0]);
			tablesArray[0]=table;
			
			Table tempTable;
			for(int i=1;i<temp.length;i++){
				tempTable=tables.get(temp[i]);
				table=table.join(tables.get(temp[i]));
				tablesArray[i]=tempTable;
			}
		}
		else{
			MorJ=false;
			table=tables.get(tableName);
		}
		
		String[] newVar=variables;
		if(MorJ==true){
			int index=0;
			int[] indexes=new int[variables.length];
			String[] array=new String[variables.length];
			for(int i=0;i<variables.length;i++){
				if(variables[i].contains(".")!=true){
					array[index]=variables[i];
					indexes[index]=i;
					index++;
				}
			}
			
			String[] dotLess=new String[index];
			for(int i=0;i<index;i++){
				dotLess[i]=array[i];
			}
			
			dotLess=refreshNames(tablesArray, dotLess);
			
			for(int i=0;i<index;i++){
				newVar[indexes[i]]=dotLess[i];
			}
		}
		
		String[][] result=table.select(newVar, condition);
		
		return result;
	}
	
	
	
	
	private static String[] refreshNames(Table[] tables,String[] variables){
		
		for(int i=0;i<tables.length;i++){
			for(int j=0;j<variables.length;j++){
				for(int k=0;k<tables[i].columns.length;k++){
					if(tables[i].columns[k].equals(variables[j])){
						variables[j]=tables[i].name+"."+variables[j];
					}
				}
			}
		}
		
		return variables;
	}
	
	
	
	
	
	public static Record[] getRecords(String tableName,ClauseNode condition){
		Table table=tables.get(tableName);
		Record[] result=table.getRecords(condition);
		return result;
	}
	
	
	
	
	
//	public static void updateRecords(String tableName,String columnName,String newValue,ClauseNode condition){
//		Table table=tables.get(tableName);
//		table.updateRecords(columnName, newValue, condition);
//	}
	
	
	public static void updateRecords(String tableName,String columnName,String newValue,ClauseNode condition)
				throws C1Constrain,C2Constrain,FKConstrain
	{
		Table table=tables.get(tableName);
		try {
			table.updateRecords(columnName, newValue, condition);
		} catch (InvalidRecord e) {
			e.printStackTrace();
		}
	}
	
	
	
//	public static void addRecord(String tableName,String[] variables) throws InvalidParam{
//		
//		Table table=tables.get(tableName);
//		try {
//			table.addRecord(variables);
//		} catch (InvalidRecord e) {
//			throw new InvalidParam();
//		}
//		
//	}
	
	
	
	
	public static void addRecord(String tableName,String[] variables)
			throws InvalidParam,C1Constrain,C2Constrain{
		
		Table table=tables.get(tableName);
		try {
			table.addRecord(variables);
		} catch (InvalidRecord e) {
			throw new InvalidParam();
		}
		
	}
	
	
	
	
	
	
//	public static void removeRecords(String tableName,ClauseNode condition){
//		
//		Table table=tables.get(tableName);
//		table.removeRecords(condition);
//		
//	}
	
	
	
	public static void removeRecords(String tableName,ClauseNode condition)throws C2Constrain{
		
		Table table=tables.get(tableName);
		try {
			table.removeRecords(condition);
		} catch (InvalidRecord e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	
	
	
}
