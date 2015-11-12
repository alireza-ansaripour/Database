package controller.commands;

import java.util.regex.Pattern;

import model.TableManager;
import controller.InvalidParam;



public class CreateIndexCommand extends Command{
	
	
	
	
	@Override
	public boolean doAction(String command) throws InvalidParam {
		
		if(command.startsWith("CREATE INDEX")==false){
			return false;
		}
		
		String[] components=getComponents(command);
		String tableName=components[1];
		String indexName=components[0];
		String columnName=components[2];
		
		TableManager.createIndex(tableName, indexName, columnName);
		
		this.print();
		
		return true;
	}
	
	
	
	/**
	 * @return array[0]: index name, array[1]: table name, array[2]: column name.
	 */
	@Override
	protected String[] getComponents(String command) throws InvalidParam {
		matchingString="CREATE INDEX (.+) ON (.+)[(](.+)[)];";
		pattern=Pattern.compile(matchingString);
		matcher=pattern.matcher(command);
		if(matcher.find()==true){
			result=new String[3];
			for(int i=0;i<result.length;i++){
				result[i]=matcher.group(i+1);
			}
			
			return result;
		}
		else{
			System.out.println("CREATE INDEX not found");
		}
		
		return null;
	}
	
	
	
	@Override
	public void print() {
		System.out.println("INDEX CREATED");
	}







	

}
