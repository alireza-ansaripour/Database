package controller.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.C1Constrain;
import model.C2Constrain;
import model.FKConstrain;
import model.TableManager;
import controller.InvalidParam;

public class CreateViewCommand extends Command{

	@Override
	public boolean doAction(String command) throws InvalidParam, C1Constrain,
			C2Constrain, FKConstrain {
		
		if(command.startsWith("CREATE VIEW")==false){
			return false;
		}
		
		String[] components=getComponents(command);
		String viewName=components[0];
		String selectCommand=components[1];
		String[] selectedVariables=new String[components.length-2];
		
		for(int i=0;i<selectedVariables.length;i++){
			selectedVariables[i]=components[i+2];
		}
		
		TableManager.createView(viewName, selectedVariables, selectCommand);
		
		this.print();
		
		return true;
	}

	
	
	
	
	@Override
	/**
	 * 
	 * @return array[0] name of view, array[1] select command, array[2...] is name of variables.
	 */
	protected String[] getComponents(String command) throws InvalidParam {
		matchingString="CREATE VIEW\\s+(.*)\\s+AS\\s+(.*);";
		pattern=Pattern.compile(matchingString);
		matcher=pattern.matcher(command);
		
		if(matcher.find()==true){
			String[] result=new String[2];
			result[0]=matcher.group(1);
			result[1]=matcher.group(2);
			
			String selectString="SELECT\\s+(.*)\\s+(.*)";
			Pattern selectPattern=Pattern.compile(selectString);
			Matcher selectMatcher=selectPattern.matcher(result[1]);
			
			if(selectMatcher.find()==true){
				String[] temp=selectMatcher.group(1).split(",");
				for(int i=0;i<temp.length;i++){
					result[i+2]=temp[i];
				}
			}
			else{
				throw new InvalidParam();
			}
			
			return result;
			
		}
		else{
			throw new InvalidParam();
		}
	}

	@Override
	public void print() {
		System.out.println("VIEW CREATED");
	}

}
