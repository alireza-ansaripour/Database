package controller.commands;

import java.util.regex.Pattern;
import model.TableManager;
import controller.InvalidParam;

public class CreateTableCommand extends Command{
	
	
	
	
	@Override
	public boolean doAction(String command) throws InvalidParam{
		
		if(command.startsWith("CREATE TABLE")==false){
			return false;
		}
		
		String[] component=getComponents(command);
		String name=component[0];
		String[] colNames=new String[(int)((component.length-1)/2)];
		String[] colTypes=new String[colNames.length];
		
		for(int i=0;i<colNames.length;i++){
			colNames[i]=component[2*i+1];
			colTypes[i]=component[2*i+2];
		}
		
		TableManager.createTable(name, colNames, colTypes);
		
		this.print();
		
		return true;
	}
	
	
	
	/**
	 * @return array[0]:table name, array[oddNumbers]:column name, array[evenNumbers]: column type.
	 */
	@Override
	protected String[] getComponents(String command) throws InvalidParam{
		matchingString="CREATE TABLE\\s(\\w+)[(](.*)[)]";
		pattern=Pattern.compile(matchingString);
		matcher=pattern.matcher(command);
		if(matcher.find()==true){
			String[] columns=matcher.group(2).split(",");
			result=new String[columns.length*2+1];
			result[0]=matcher.group(1);//table name.
			
			String temp[];
			for(int i=0;i<columns.length;i++){
				temp=columns[i].split(" ");
				result[2*i+1]=temp[0];//column name.
				result[2*i+2]=temp[1];//column type.
			}
			
			return result;
		}
		else{
			throw new InvalidParam();
		}
	}
	
	
	
	
	@Override
	public void print() {
		System.out.println("TABLE CREATED");
	}
	
	
	
}
