package controller.commands;

import java.util.regex.Pattern;

import model.C1Constrain;
import model.C2Constrain;
import model.TableManager;
import controller.ClauseNode;
import controller.InputHandler;
import controller.InvalidParam;

public class DeleteCommand extends Command{

	@Override
	public boolean doAction(String command) throws InvalidParam,C1Constrain,C2Constrain {
		
		if(command.startsWith("DELETE")==false){
			return false;
		}
		
		String[] components=getComponents(command);
		String tableName=components[0];
		String condition=components[1];
		
		ClauseNode node=InputHandler.createClauseTree(condition);
		
		TableManager.removeRecords(tableName, node);
		
		this.print();
		
		return true;
	}

	
	
	/**
	 * @return array[0]:table name, array[1]:condition.
	 */
	@Override
	protected String[] getComponents(String command) throws InvalidParam {
		matchingString="DELETE FROM (.*) WHERE (.*);";
		pattern=Pattern.compile(matchingString);
		matcher=pattern.matcher(command);
		
		if(matcher.find()==true){
			result=new String[2];
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
	public void print() {
		System.out.println("RECORD DELETED");
	}

}
