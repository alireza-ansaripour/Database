package controller.commands;

import java.util.regex.Pattern;

import model.TableManager;
import controller.InvalidParam;

public class InsertCommand extends Command {

	
	
	@Override
	public boolean doAction(String command) throws InvalidParam {
		
		if(command.startsWith("INSERT")==false){
			return false;
		}
		
		String[] components=getComponents(command);
		String tableName=components[0];
		String[] recordValues=new String[components.length-1];
		
		for(int i=0;i<recordValues.length;i++){
			recordValues[i]=components[i+1];
		}
		TableManager.addRecord(tableName, recordValues);
		
		this.print();
		
		return true;
	}
	
	
	
	/**
	 * @return array[0]:table name, array[1..n-1]:values of records.
	 */
	@Override
	protected String[] getComponents(String command) throws InvalidParam {
		matchingString="INSERT INTO (.*) VALUES[(](.*)[)]";
		pattern=Pattern.compile(matchingString);
		matcher=pattern.matcher(command);
		
		if(matcher.find()==true){
			String[] temp=matcher.group(2).split(",");
			result=new String[temp.length+1];
			result[0]=matcher.group(1);
			for(int i=0;i<temp.length;i++){
				result[i+1]=temp[i];
			}
			return result;
			
		}
		else{
			throw new InvalidParam();
		}
	}

	@Override
	public void print() {
		System.out.println("RECORD INSERTED");
	}

}
