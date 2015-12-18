package controller.commands;

import java.util.regex.Pattern;
import model.TableManager;
import controller.ClauseNode;
import controller.InputHandler;
import controller.InvalidParam;

public class SelectCommand extends Command{
	
	private String[] selectedVariables;
	private String[][] actionResult;
	
	@Override
	public boolean doAction(String command) throws InvalidParam {
		
		if(command.startsWith("SELECT")==false){
			return false;
		}
		
		String[] components=getComponents(command);
		String tableName=components[0];
		String condition=components[1];
		selectedVariables=new String[components.length-2];
		
		for(int i=0;i<selectedVariables.length;i++){
			selectedVariables[i]=components[i+2];
		}
		
		
		
		ClauseNode node=InputHandler.createClauseTree(condition);
		
		actionResult=TableManager.select(tableName, selectedVariables, node);
		
		this.print();
		
		return true;
	}

	/**
	 * @return array[0]:table name, array[1]:condition, array[2..n-1]:selected variables.
	 */
	@Override
	protected String[] getComponents(String command) throws InvalidParam {
		matchingString="SELECT (.*) FROM (.*) WHERE (.*);";
		pattern=Pattern.compile(matchingString);
		matcher=pattern.matcher(command);
		
		if(matcher.find()==true){
			String[] temp=matcher.group(1).split(",");
			result=new String[temp.length+2];
			result[0]=matcher.group(2);
			result[1]=matcher.group(3);
			
			for(int i=0;i<temp.length;i++){
				result[i+2]=temp[i];
			}
			return result;
		}
		else{
			throw new InvalidParam();
		}
	}
	
	
	
	
	@Override
	public void print() {
		
		if(actionResult.length==0){
			System.out.println("NO RESULTS");
			return;
		}
		
		String temp=selectedVariables[0];
		for(int i=1;i<selectedVariables.length;i++){
			temp+=","+selectedVariables[i];
		}
		System.out.println(temp);
		for(int i=0;i<actionResult.length;i++){
			for(int j=0;j<actionResult[0].length;j++){
				String temp1=actionResult[i][j];
				if(temp1.startsWith("\"")&&temp1.endsWith("\"")){
					temp1=temp1.substring(1,temp1.length()-1);
				}
				
				System.out.print(temp1);
				if(j<actionResult[0].length-1){
					System.out.print(",");
				}
			}
			System.out.println();
		}
	}

}





