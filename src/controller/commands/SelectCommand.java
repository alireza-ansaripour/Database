package controller.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.C1Constrain;
import model.C2Constrain;
import model.TableManager;
import controller.ClauseNode;
import controller.InputHandler;
import controller.InvalidParam;

public class SelectCommand extends Command{
	
	private String[] selectedVariables;
	private String[][] actionResult;
	
	@Override
	public boolean doAction(String command) throws InvalidParam,C1Constrain,C2Constrain {
		
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
		
		String[][] components2=getGroupByComponents(condition);
		String whereCondition=components2[0][0];
		String[] groupVriables=components2[1];
		String havingCondition=components2[2][0];
		
		ClauseNode whereNode=InputHandler.createClauseTree(whereCondition);
		ClauseNode havingNode=null;
		if(havingCondition!=null){
			havingNode=InputHandler.createHavingClauseNode(havingCondition);
		}
		
		if(groupVriables[0]!=null){
			actionResult=TableManager.selectGroup(tableName, selectedVariables,groupVriables,
					whereNode, havingNode);
			System.out.println("group");
		}
		else{
			actionResult=TableManager.select(tableName, selectedVariables, whereNode);
			System.out.println("not");
		}
		
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
	
	
	/**
	 * gets a string as condition then first checks it contains "GROUP BY" or not.
	 * if yes then checks it contains "HAVING" or not. if yes separates components of condition
	 * and returns them. if not just separates WHERE condition and GROUP BY condition. 
	 * @param condition a string that come after WHERE such as  WHERE condition, GROUP BY condition, 
	 * HAVING condition.
	 * @return array[][] first row is WHERE condition string, if condition contains GROUP BY second row is
	 * name of variables in GROUP BY, if condition contains HAVING the first element of third row
	 * is HAVING condition string.
	 */
	private String[][] getGroupByComponents(String condition){
		String[][] result=new String[3][1];
		String matchingString="(.*)\\s+GROUP BY\\s+(.*)";
		Pattern groupPattern=Pattern.compile(matchingString);
		Matcher groupMatcher=groupPattern.matcher(condition);
		
		if(groupMatcher.find()==true){
			String whereCondition=groupMatcher.group(1);
			result[0][0]=whereCondition;
			
			String havingString="(.*) HAVING (.*)";
			Pattern havingPattern=Pattern.compile(havingString);
			Matcher havingMatcher=havingPattern.matcher(groupMatcher.group(2));
			
			if(havingMatcher.find()==true){
				result[2][0]=havingMatcher.group(2);
				String columns=havingMatcher.group(1);
				result[1]=columns.split(",");
			}
			else{
				result[1]=groupMatcher.group(2).split(",");
			}
		}
		else{
			result[0][0]=condition;
		}
		
		return result;
		
	}
	
	
	
	/**
	 * created by alireza
	 * this method will return all the record resulting from the select command
	 * this method is used for views
	 * @param command	the input command
	 * @return the list of records
	 * @throws InvalidParam
	 * @throws C1Constrain
	 * @throws C2Constrain
	 */
	public String[][] returnResult(String command) throws InvalidParam, C1Constrain, C2Constrain{
		doAction(command);
		return actionResult;
	}
	public String[] getHeaders(){
		return selectedVariables;
	}
	
	
	
	@Override
	public void print() {
		
		
		if(actionResult.length==0){
			System.out.println("NO RESULTS");
			return;
		}
		
		String temp=selectedVariables[0];
		String[] tempArray;
		for(int i=1;i<selectedVariables.length;i++){
			if(selectedVariables[i].contains(".")==true){
				tempArray=selectedVariables[i].split(".");
				selectedVariables[i]=tempArray[1];
			}
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





