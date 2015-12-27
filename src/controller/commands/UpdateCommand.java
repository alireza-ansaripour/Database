package controller.commands;

import java.util.regex.Pattern;

import sun.nio.ch.FileKey;
import model.C1Constrain;
import model.C2Constrain;
import model.FKConstrain;
import model.TableManager;
import controller.ClauseNode;
import controller.InputHandler;
import controller.InvalidParam;

public class UpdateCommand extends Command{

	@Override
	public boolean doAction(String command) throws InvalidParam,C1Constrain,C2Constrain,FKConstrain {
		
		if(command.startsWith("UPDATE")==false){
			return false;
		}
		
		String[] components=getComponents(command);
		String tableName=components[0];
		String colName=components[1];
		String newValue=components[2];
		String condition=components[3];
		
		ClauseNode node=InputHandler.createClauseTree(condition);
		
		TableManager.updateRecords(tableName, colName, newValue, node);
		
		this.print();
		
		return true;
	}

	
	/**
	 * @return array[0]:table name, array[1]:column name, array[2]:new value, array[3]:condition.
	 */
	@Override
	protected String[] getComponents(String command) throws InvalidParam {
		matchingString="UPDATE (.*) SET (.*)=(.*) WHERE (.*);";
		pattern=Pattern.compile(matchingString);
		matcher=pattern.matcher(command);
		
		if(matcher.find()==true){
			result=new String[4];
			for(int i=0;i<result.length;i++){
				result[i]=matcher.group(i+1);
			}
			return result;
		}
		else{
			throw new InvalidParam();
		}
	}

	@Override
	public void print(){
		//TODO print the output.
	}

}
