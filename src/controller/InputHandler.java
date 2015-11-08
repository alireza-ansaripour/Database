package controller;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


/*
 * summary:
 * this class has input handler functions.
 */


public class InputHandler {

	
	public static void handleCommand(String command){
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = null;
        if(engine == null)
            engine = manager.getEngineByName("js");
        String matchingString;
		Pattern pattern;
		Matcher matcher;
		
		
		if(command.startsWith("CREATE TABLE")){
			matchingString="CREATE TABLE\\s(\\w+)[(](.*)[)]";
			pattern=Pattern.compile(matchingString);
			matcher=pattern.matcher(command);
			if(matcher.find()){
				String[] columns=matcher.group(2).split(",");
                String tName = matcher.group(1);
                Table table = new Table(columns,engine);
                Table.addTable(tName,table);
                System.out.println("Table created");
			}
			else{
				System.out.println("CREATE TABLE not found");
			}
			
		}
		else if(command.startsWith("CREATE INDEX")){
			matchingString="CREATE INDEX (.+) ON (.+)[(](.+)[)] ;";
			pattern=Pattern.compile(matchingString);
			matcher=pattern.matcher(command);
			if(matcher.find()){
                String tName = matcher.group(2);
                Table table = Table.getTable(tName);
                table.addIndex(matcher.group(1).trim() ,matcher.group(3).trim());
			}
			else{
				System.out.println("CREATE INDEX not found");
			}
			
			
		}
		else if(command.startsWith("INSERT")){
			matchingString="INSERT INTO (.*) VALUES[(](.*)[)]";
			pattern=Pattern.compile(matchingString);
			matcher=pattern.matcher(command);
			
			if(matcher.find()){
				String[] values = matcher.group(2).split(",");
				String tName = matcher.group(1);
                Table table = Table.getTable(tName);
                table.addRecord(values);
			}
			else{
				System.out.println("INSERT not found");
			}
			
			
		}
		else if(command.startsWith("UPDATE")){
			matchingString="UPDATE (.*) SET (.*)=(.*) WHERE (.*) ;";
			pattern=Pattern.compile(matchingString);
			matcher=pattern.matcher(command);
			
			if(matcher.find()){
				String tName = matcher.group(1);
                Table table = Table.getTable(tName);
                try {


                    ArrayList<Record> records = table.getRecords(matcher.group(4));
                    String column = matcher.group(2);
                    String value = matcher.group(3);
                    for(Record record : records){
                        HashMap<String,String > r = record.returnValues();
                        r.put(column.trim(), value.trim());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
			else{
				System.out.println("UPDATE not found");
			}
			
			
		}
		else if(command.startsWith("DELETE")){
			matchingString="DELETE FROM (.*) WHERE (.*);";
			pattern=Pattern.compile(matchingString);
			matcher=pattern.matcher(command);
			
			if(matcher.find()){
				String tName = matcher.group(1);
                Table table = Table.getTable(tName);
                try {
                    table.deleteRecords(matcher.group(2));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
			else{
				System.out.println("DELETE not found");
			}
			
		}
		else if(command.startsWith("SELECT")){
			matchingString="SELECT (.*) FROM (.*) WHERE (.*) ;";
			pattern=Pattern.compile(matchingString);
			matcher=pattern.matcher(command);
			
			if(matcher.find()){
				String[] values = matcher.group(1).split(",");
				String tName = matcher.group(2);
                String condition = matcher.group(3);
                Table table = Table.getTable(tName);
                try {
                    String index = table.getIndex();
                    ArrayList<Record>records = null;
                    // if the condition is on index
                    if ( !index.equals("") && condition.contains(index) && !condition.contains("NOT")){
                        if (condition.contains("=")){
                            records = table.returnOnIndex(condition.substring(condition.indexOf("=")+1));
                        }
                        if (condition.contains("=>")){
                            records = table.returnOnIndex(condition.substring(condition.indexOf("=>")+1));
                            records.addAll(table.returnOnIndex(condition.substring(condition.indexOf("=")+1)));

                        }
                        if (condition.contains(">")){
                            table.returnOnIndexHigher(condition.substring(condition.indexOf(">")+1));

                        }
                        if (condition.contains("<")){
                            records = table.returnOnIndexLower(condition.substring(condition.indexOf("<")+1));

                        }
                        if (condition.contains("<=")){
                            records = table.returnOnIndex(condition.substring(condition.indexOf("<=")+1));
                            records.addAll(table.returnOnIndex(condition.substring(condition.indexOf("=")+1)));

                        }
                    }else
                        records = table.getRecords(condition);
                    assert records != null;
                    for (Record record : records){
                        HashMap<String , String> value = record.returnValues();
                        String output = "";
                        for (String val : values) {
                            output += value.get(val)+",";
                        }
                        output = output.substring(0,output.length()-1);
                        System.out.println(output);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

				
			}
			else{
				System.out.println("SELECT not found");
			}
			
		}
		
		
		
	}

    public static void main(String[] args) {
        InputHandler.handleCommand("CREATE TABLE ALIREZA(fname,lname,age)");
        InputHandler.handleCommand("INSERT INTO ALIREZA VALUES(\"alireza\",\"Ansaripur\",12) ;");
        InputHandler.handleCommand("INSERT INTO ALIREZA VALUES(\"ali\",\"Ansari\",13) ;");
        InputHandler.handleCommand("INSERT INTO ALIREZA VALUES(\"reza\",\"A\",14) ;");
        long m = System.currentTimeMillis();
        InputHandler.handleCommand("SELECT fname,lname,age FROM ALIREZA WHERE NOT age = 12 OR fname = \"alireza\" ;");
        long n = System.currentTimeMillis();
        InputHandler.handleCommand("SELECT fname,lname,age FROM ALIREZA WHERE NOT age = 12 OR fname = \"alireza\" ;");
        InputHandler.handleCommand("SELECT fname,lname,age FROM ALIREZA WHERE NOT age = 12 OR fname = \"alireza\" ;");
        InputHandler.handleCommand("SELECT fname,lname,age FROM ALIREZA WHERE NOT age = 12 OR fname = \"alireza\" ;");
        InputHandler.handleCommand("SELECT fname,lname,age FROM ALIREZA WHERE NOT age = 12 OR fname = \"alireza\" ;");
        InputHandler.handleCommand("SELECT fname,lname,age FROM ALIREZA WHERE NOT age = 12 OR fname = \"alireza\" ;");
        System.out.println(n-m);
    }


    /*
     * get a string as condition then deletes parentheses and free spaces.
     * then creates clause tree and returns its root.
     * condition:	condition of a command.
     *
     * returns:		root of clause tree.
     */
	public static ClauseNode createClauseTree(String condition){

		String copy=condition;

		copy=copy.replace(" ", "");
		copy=copy.replace("(", "");
		copy=copy.replace(")", "");

		return createTree(copy);

	}



	/*
	 * get a string as condition and creates clause tree recursively then returns its root.
	 * condition:	condition of a command. note condition can not contains parentheses or free spaces.
	 *
	 * returns:		root of clause tree.
	 */
	private static ClauseNode createTree(String condition){


		int andIndex=condition.indexOf("AND");
		int orIndex=condition.indexOf("OR");


//		this condition is a single condition.
		if(andIndex==-1&&orIndex==-1){


			boolean not=condition.startsWith("NOT");
			if(not){
				condition=condition.substring(3,condition.length());
			}


			if(condition.equals("TRUE")){
				return new ClauseNode(true,not);
			}
			else if(condition.equals("FALSE")){
				return new ClauseNode(false,not);
			}

			String[] name_value;
			int singleOperator;
			if(condition.contains(">")==true){
				singleOperator=4;
				name_value=condition.split(">");
			}
			else if(condition.contains(">=")==true){
				singleOperator=3;
				name_value=condition.split(">=");
			}
			else if(condition.contains("=")==true){
				singleOperator=2;
				name_value=condition.split("=");
			}
			else if(condition.contains("<=")==true){
				singleOperator=1;
				name_value=condition.split("<=");
			}
			//condition.contains("<")==true
			else{
				singleOperator=0;
				name_value=condition.split("<");
			}

			return new ClauseNode(singleOperator,name_value[0],name_value[1],not);
		}
		//index != -1. condition has OR or AND.
		else{

			String condition1;
			String condition2;
			int operator;

			if((andIndex!=-1 && orIndex!=-1 && andIndex<=orIndex) || orIndex==-1){
				condition1=condition.substring(0, andIndex);
				operator=0;//AND
				condition2=condition.substring(andIndex+3, condition.length());
			}
			else{
				condition1=condition.substring(0, orIndex);
				operator=1;//OR
				condition2=condition.substring(orIndex+2, condition.length());
			}

			ClauseNode CN1=createTree(condition1);
			ClauseNode CN2=createTree(condition2);
			return new ClauseNode(operator, CN1, CN2);

		}


	}





//	public static String calculatedValue(String calculatingValue){
//
//		String copy=calculatingValue;
//		Stack<String> stack=new Stack<String>();
//		ArrayList<String> list=new ArrayList<String>();
//
//		String operator;
//		for(;copy.length()>0;){
//
//			int index=min(new int[]{copy.indexOf("+"), copy.indexOf("-"), copy.indexOf("*"), copy.indexOf("/")});
//			list.add(copy.substring(0, index));
//
//			operator=copy.substring(index, index+1);
//			for(;;){
//				String temp;
//				try{
//					temp=stack.pop();
//				}
//				catch(EmptyStackException exception){
//					stack.push(operator);
//					break;
//				}
//
//				firstOperatorPriority(temp, operator);
//
//
//
//			}
//
//
//
//			copy=copy.substring(0, index+1);
//
//
//
//		}
//
//
//
//	    return copy;
//	}




	/*
	 * gets first operator as op1 and second operator as op2 then returns a integer number.
	 * if returns 0 stack must pop and new operator must be pushed else if returns 1
	 * stack does not pop and new operator must be pushed.
	 * op1:		first operator.
	 * op2:		second operator.
	 *
	 * returns:	if 0 first pop then push. if 1 just push.
	 */
	private static int firstOperatorPriority(String op1,String op2){

		if(((op1=="+"||op1=="-")&&(op2=="+"||op2=="-"))||
				(op1=="*"||op1=="/")){
			return 0;
		}
		else if((op1=="+"||op1=="-")&&(op2=="*"||op2=="/")){
			return 1;
		}

		return 0;

	}




	/*
	 * gets an array of number and returns minimum of them.
	 * numbers:		array of numbers.
	 *
	 * returns:		minimum of numbers.
	 */
	private static int min(int[] numbers){
		int min=numbers[0];
		for(int i=0;i<numbers.length;i++){
			if(numbers[i]<min){
				min=numbers[i];
			}
		}
		return min;
	}


	
}























