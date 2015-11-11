package controller;


import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


/**
 * this class has input handler functions.
 */


public class InputHandler {
	
	
	
	public static String[] handleCommand(String command){
		
		String[] result;
		String matchingString;
		Pattern pattern;
		Matcher matcher;
		
		
		if(command.startsWith("CREATE TABLE")){
			matchingString="CREATE TABLE\\s(\\w+)[(](.*)[)]";
			pattern=Pattern.compile(matchingString);
			matcher=pattern.matcher(command);
			if(matcher.find()==true){
				String[] columns=matcher.group(2).split(",");
				result=new String[columns.length*2+1];
				result[0]=matcher.group(1);
				
				
				String temp[];
				for(int i=0;i<columns.length;i++){
					temp=columns[i].split(" ");
					result[2*i+1]=temp[0];
					result[2*i+2]=temp[1];
				}
				
				return result;
			}
			else{
				System.out.println("CREATE TABLE not found");
			}
		}
		else if(command.startsWith("CREATE INDEX")){
			matchingString="CREATE INDEX (.+) ON (.+)[(](.+)[)]";
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
			
			
		}
		else if(command.startsWith("INSERT")){
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
				System.out.println("INSERT not found");
			}
			
			
		}
		else if(command.startsWith("UPDATE")){
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
				System.out.println("UPDATE not found");
			}
			
			
		}
		else if(command.startsWith("DELETE")){
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
				System.out.println("DELETE not found");
			}
			
		}
		else if(command.startsWith("SELECT")){
			matchingString="SELECT (.*) FROM (.*) WHERE (.*)";
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
				System.out.println("SELECT not found");
			}
			
		}
		
		return null;
		
		
	}
	
	
	
	
	
	/**
	 * get a string as condition then deletes parentheses and free spaces.
	 * then creates clause tree and returns its root.
	 * @condition condition of a command.
	 * @return root of clause tree.
	 */
	public static ClauseNode createClauseTree(String condition){
		
		
		String copy=condition;
		
		copy=preparingToPostfix(copy);
		
		System.out.println("copy: "+copy);
		
		InToPost post=new InToPost(copy);
		copy=post.doTrans();
		
		
		return createTree(copy);
		
	}
	
	
	
	/**
	 * gets a condition then replace all of last operators with new operators.
	 * "AND":" ^ " - "OR":" @ " - "NOT":" 0 ! ".
	 * @param conidtion	a string that must be prepared to convert to postfix.
	 * @return prepared string to convert to postfix.
	 */
	private static String preparingToPostfix(String condition){
		String copy=condition;
		String[] operators={"AND","OR"};
		String[] newOperators={" ^ "," @ "};
		
		copy=copy.replace("("," ( ");
		copy=copy.replace(")"," ) ");
		
		for(int i=0;i<operators.length;i++){
			copy=copy.replace(" "+operators[i]+" ", newOperators[i]);
			copy=copy.replace(" "+operators+"(", newOperators[i]+"(");
			copy=copy.replace(")"+operators[i]+" ",")"+newOperators[i]);
			copy=copy.replace(")"+operators[i]+"(",")"+newOperators+"(");
		}
		
		copy=" "+copy+" ";
		copy=copy.replace(")NOT ",") NOT ");
		copy=copy.replace(" NOT(", " NOT (");
		copy=copy.replace(")NOT(",") NOT (");
		
		
		int parentheses=0;
		for(;;){
			int index=copy.indexOf(" NOT ");
			if(index==-1){
				break;
			}
			copy=copy.substring(0, index)+"( 0 ! "+copy.substring(index+5,copy.length());
			parentheses=0;
			
			boolean notReplaced=false;
			for(int i=index+5;i<copy.length();i++){
				if(copy.charAt(i)=='('){
					parentheses++;
				}
				else if(copy.charAt(i)==')'){
					parentheses--;
				}
				
				if(parentheses==0&&(copy.charAt(i)=='^'||copy.charAt(i)=='@')){//^:AND	@:OR.
					copy=copy.substring(0, i)+" ) "+copy.substring(i, copy.length());
					notReplaced=true;
					break;
				}
			}
			
			if(notReplaced==false){
				copy=copy+")";
			}
			
		}
		
		return copy;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * get a string as condition and creates clause tree recursively then returns its root.
	 * condition:	condition of a command. note condition can not contains parentheses or free spaces.
	 * @param postfixCondition	postFix of condition in type of String. 
	 * @return root of clause tree.
	 */
	private static ClauseNode createTree(String postfixCondition){
		System.out.println(postfixCondition+"  ****************");
		
		String copy=postfixCondition.replace(" 0 ", " ");
		
		System.out.println("jijsd: "+copy);
		
		String[] array=copy.split(" ");
		
		for(int i=0;i<array.length;i++){
			System.out.println("hay: "+array[i]);
		}
		
		array=fixOps(array);
		
		for(int i=0;i<array.length;i++){
			System.out.println("111: "+array[i]);
		}
		
		Stack<ClauseNode> tempStack=new Stack<>();
		Stack<ClauseNode> stack=new Stack<>();
		
		ClauseNode temp;
		int sign=0;
		for(int i=0;i<array.length;i++){
			
			if(array[i].length()==0){
				continue;
			}
			
			if(array[i].equals("!")){//!:NOT.
				temp=stack.pop();
				temp.inverseSign();
				System.out.println("not "+temp.not);
				stack.push(temp);
			}
			else{
				stack.push(convertToClauseNode(array[i]));
			}
		}
		
		
		
		boolean oneLastLeaf=false;
		for(;stack.size()>1||tempStack.size()>0;){
			
			temp=stack.pop();
			if(oneLastLeaf==true){
				if(temp.isFull()==true){
					ClauseNode child=tempStack.pop();
					ClauseNode parent;
					try{
						parent=tempStack.pop();
					}
					catch(EmptyStackException exception){
						break;
					}
					parent.addChild(child, temp);
					stack.push(parent);
				}
				else{
					tempStack.push(temp);
					oneLastLeaf=false;
				}
			}
			else{
				oneLastLeaf=temp.isFull();
				tempStack.push(temp);
			}
		}
		
		return stack.pop();
		
	}
	
	
	
	
	
	/**
	 * gets an array and fixes its operators and operands then returns fixed array.
	 * @param array	not fixed operands and operators.
	 * @return fixed operands and operators.
	 */
	private static String[] fixOps(String[] array){
		
		String[] list=new String[array.length];
		int index=0;
		
		
		int i=0;
		for(i=0;array[i].length()==0;i++){
			
		}
		
		list[0]=array[i++];
		
		
		boolean lastOperator=false;
		
		if(list[0].contains("^")==true||list[0].contains("@")==true||list[0].contains("!")==true){
			lastOperator=true;
		}
		
		for(;i<array.length;i++){
			
			if(array[i].length()==0||array[i].equals(" ")){
				continue;
			}
			
			//^:AND		@:OR	!:NOT.
			if(array[i].contains("^")==true||array[i].contains("@")==true||array[i].contains("!")==true){
				lastOperator=true;
				for(int j=0;j<array[i].length();j++){
					index++;
					list[index]=array[i].charAt(j)+"";
				}
			}
			else{
				if(lastOperator==true){
					System.out.println("lastop");
					index++;
					list[index]=array[i];
					lastOperator=false;
				}
				else{
					if((list[index].contains("=")||list[index].contains("<")||list[index].contains(">"))==true
							&&(list[index].endsWith("=")||list[index].endsWith("<")||list[index].endsWith(">"))==false){
						index++;
					}
					else if(list[index].equals("TRUE")==true||list[index].equals("FALSE")==true){
						index++;
					}
					
					if(list[index]==null){
						list[index]="";
					}
					list[index]+=array[i];
					
				}
			}
		}
		
		String[] result=new String[index+1];
		
		for(index=0;index<result.length;index++){
			result[index]=list[index];
		}
		
		return result;
	}
	
	
	
	
	
	/**
	 * gets a string then returns a ClauseNode according to it.
	 * @param string	can be "*" , "+" , "TRUE" , "FALSE" , condition with compare operator.
	 * @return a ClauseNode object.
	 */
	private static ClauseNode convertToClauseNode(String string){
		
		//AND
		if(string.contains("^")==true){
			return new ClauseNode(0);
		}
		//OR
		else if(string.contains("@")==true){
			return new ClauseNode(1);
		}
		//constant like true or false, condition with compare operator.
		else{
			return new ClauseNode(string);
		}
		
	}
	
	
	
	/**
	 * 
	 * @param computeValue
	 * @param columns
	 * @param variables
	 * @return
	 */
	public static String getValue(String computeValue,HashMap<String, Integer> columns,String[] variables){

		String[] operators={"\"","+","-","*","/"};
		String copy=computeValue;
		int[] operatorIndex=getFirstOperator(computeValue, operators);
		
		String operand1;
		
		if(operatorIndex==null){
			return getCorrectValue(copy, columns, variables);
		}
		
		// "
		if(operatorIndex[1]==0){
			int tempIndex=copy.indexOf("\"", 1);
			operand1=copy.substring(0, tempIndex+1);
			copy=copy.substring(tempIndex+1,copy.length());
		}
		else{
			operand1=copy.substring(0, operatorIndex[0]);
			operand1=getCorrectValue(operand1, columns, variables);
			operand1=setQuote(operand1);
			copy=copy.substring(operatorIndex[0], copy.length());
		}
		
		
		String operand2;
		String operator;
		for(;;){
			try{
				operator=copy.substring(0, 1);
			}
			catch(StringIndexOutOfBoundsException exception){
				break;
			}
			
			copy=copy.substring(1, copy.length());
			
			operatorIndex=getFirstOperator(copy, operators);
			// "
			if(operatorIndex==null){
				operand2=getCorrectValue(copy, columns, variables);
				
				String string=null;
				try{
					string=calculateValue(operand1, operand2, operator);
				}
				catch(InvalidParam exception){
					exception.printStackTrace();
				}
				
				if(string!=null){
					operand1=string;
				}
				
				break;
			}
			if(operatorIndex[1]==0){
				int tempIndex=copy.indexOf("\"", 1);
				operand2=copy.substring(0, tempIndex+1);
				copy=copy.substring(tempIndex+1, copy.length());
			}
			else{
				operand2=copy.substring(0, operatorIndex[0]);
				operand2=getCorrectValue(operand2, columns, variables);
				copy=copy.substring(operatorIndex[0], copy.length());
			}
			
			String string=null;
			try {
				string=calculateValue(operand1, operand2, operator);
			} catch (InvalidParam e) {
				e.printStackTrace();
			}
			
			if(operand1!=null){
				operand1=string;				
			}
			
		}
		
		if(operand1.startsWith("\"")==true){
			operand1=operand1.substring(1, operand1.length()-1);
		}
		
		return operand1;
	}
	
    
	
	
	
	
	
	
	/**
	 * if string can not be converted to number checks if it is name of a variable returns value of that variable.
	 * @param string value can be a number or name of variable.
	 * @param columns each pair key is name of variable and value id index of it variable in variables array.
	 * @param variables array of value of variables.
	 * @return if string is name of a variable return it else return string.
	 */
	private static String getCorrectValue(String string,HashMap<String, Integer> columns,String[] variables){
		
		try{
			Integer.parseInt(string);
		}
		catch(NumberFormatException exception){
			string=variables[columns.get(string)];
		}
		
		return string;	
	}
	
	
	
	
	
	
	/**
	 * gets a string and if it can not convert to a number sets it in double quotes.
	 * @param string 
	 * @return
	 */
	private static String setQuote(String string){
		try{
			Integer.parseInt(string);
		}
		catch(NumberFormatException exception){
			return "\""+string+"\"";
		}
		return string;
	}
	
	
	
	
	

	/**
	 * gets an operator and two operand as operand1,operand2 then calculates result of this operation. 
	 * @param operand1 one of operands. if starts with " this is a string else it is an Integer number.
	 * @param operand2 like operand1.
	 * @param operator can be +, -, *, /
	 * @return result of operation. if result is a string it starts with ".
	 * @throws InvalidParam if one of operators is a string and operator is not +.
	 */
	private static String calculateValue(String operand1,String operand2,String operator)
			throws InvalidParam{
		
		
		int op1=0;
		int op2=0;
		boolean isDigit=true;
		
		try{
			op1=Integer.parseInt(operand1);
		}
		catch(NumberFormatException exception){
			isDigit=false;
		}
		
		try{
			op2=Integer.parseInt(operand2);
		}
		catch(NumberFormatException exception){
			isDigit=false;
		}
		
		
		if(operand1.startsWith("\"")==true&&operand1.endsWith("\"")==true){
			operand1=operand1.substring(1, operand1.length()-1);
		}
		
		if(operand2.startsWith("\"")==true&&operand2.endsWith("\"")==true){
			operand2=operand2.substring(1, operand2.length()-1);
		}
		
		if(operator.equals("+")){
			if(isDigit==true){
				return String.valueOf(op1+op2);
			}
			else{
				return "\""+operand1+operand2+"\"";
			}
		}
		else if(isDigit==false){
			throw new InvalidParam();
		}
		
		else if(operator.equals("-")){
			return String.valueOf(op1-op2);
		}
		else if(operator.equals("*")){
			return String.valueOf(op1*op2);
		}
		else if(operator.equals("/")){
			return String.valueOf(op1/op2);
		}
		
		throw new InvalidParam();
		
	}
	
	
	
	
	
	
	

	/**
	 * gets array of operators and a string as compute value then gets first index of each operator 
	 * and returns minimum of them.
	 * @param computeValue a string as compute value that searches operators in that.
	 * @param operators array of operators.
	 * @return array[0] is index of first operator in computeValue. 
	 * 			array[1] is index of first operator in operators array.
	 */
	private static int[] getFirstOperator(String computeValue,String[] operators){
		
		int[] result=new int[2];
		
		int[] index=new int[operators.length];
		
		for(int i=0;i<operators.length;i++){
			index[i]=computeValue.indexOf(operators[i]);
			if(index[i]==-1){
				index[i]=computeValue.length();
			}
		}
		
		result[0]=index[0];
		result[1]=0;
		for(int i=1;i<operators.length;i++){
			if(index[i]<result[0]){
				result[0]=index[i];
				result[1]=i;
			}
		}
		
		if(result[0]==computeValue.length()){
			return null;
		}
		
		return result;
		
	}
	
	
	
	
	
	
	
}























