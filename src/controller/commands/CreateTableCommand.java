package controller.commands;

import java.util.regex.Pattern;

import model.C1Constrain;
import model.C2Constrain;
import model.TableManager;
import controller.InvalidParam;

public class CreateTableCommand extends Command{
	
	
	
	
	@Override
	public boolean doAction(String command) throws InvalidParam,C1Constrain,C2Constrain{
		
		if(command.startsWith("CREATE TABLE")==false){
			return false;
		}
		
		String[] component=getComponents(command);
		String name=component[0];
		String information=component[1];
		String[] colNames=new String[(int)((component.length-2)/2)];
		String[] colTypes=new String[colNames.length];
		
		
		for(int i=0;i<colNames.length;i++){
			colNames[i]=component[2*i+2];
			colTypes[i]=component[2*i+3];
		}
		
		TableManager.createTable(name, colNames, colTypes, information);
		
		this.print();
		
		return true;
	}
	
	
	
	/**
	 * @return array[0]:table name, array[1]:information after variables, 
	 * 			array[evenNumbers]:column name, array[oddNumbers]: column type.
	 */
	@Override
	protected String[] getComponents(String command) throws InvalidParam{
		matchingString="CREATE TABLE\\s(\\w+)\\s*[(](.*)[)]\\s*(.*);";
		pattern=Pattern.compile(matchingString);
		matcher=pattern.matcher(command);
		if(matcher.find()==true){
			String[] columns=matcher.group(2).split(",");
			result=new String[columns.length*2+2];
			result[0]=matcher.group(1);//table name.
			result[1]=matcher.group(3);
			
			
			String temp[];
			for(int i=0;i<columns.length;i++){
				temp=columns[i].split(" ");
				result[2*i+2]=temp[0];//column name.
				result[2*i+3]=temp[1];//column type.
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

