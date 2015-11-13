package controller;


import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Stack;


/**
 * this class has input handler functions.
 */


public class InputHandler {
	
	
	private static String quoteSpace="#";
	
	
	/**
	 * get a string as condition then deletes parentheses and free spaces.
	 * then creates clause tree and returns its root.
	 * @condition condition of a command.
	 * @return root of clause tree.
	 */
	public static ClauseNode createClauseTree(String condition){
		
		
		String copy=condition;
		
		copy=preparingToPostfix(copy);
		
		
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
		
		String copy=postfixCondition.replace(" 0 ", " ");
		
		copy=removeSpaces(copy, new String[]{"+","-","*","/"});
		
		String[] array=copy.split(" ");
		
//		for(int i=0;i<array.length;i++){
//			System.out.println("hay: "+array[i]);
//		}
		
		array=fixOps(array);
		
		for(int i=0;i<array.length;i++){
			array[i]=array[i].replace(quoteSpace, " ");
		}
		
		
//		for(int i=0;i<array.length;i++){
//			System.out.println("111: "+array[i]);
//		}
		
		Stack<ClauseNode> stack=new Stack<>();
		
		ClauseNode temp;
		for(int i=0;i<array.length;i++){
			
			if(array[i].length()==0){
				continue;
			}
			
			if(array[i].equals("!")){//!:NOT.
				temp=stack.pop();
				temp.inverseSign();
				stack.push(temp);
			}
			else{
				temp=convertToClauseNode(array[i]);
				if(temp.isFull()==true){
					stack.push(temp);
				}
				else{
					ClauseNode c1=stack.pop();
					ClauseNode c2=stack.pop();
					temp.addChild(c1, c2);
					stack.push(temp);
				}
			}
		}
		
		return stack.pop();
		
		
	}
	
	
	
	
	
	
	/**
	 * gets a str and array of operators then remove all free spaces around operators and replace all free space
	 * in double quote with #. 
	 * @param str computed_value.
	 * @param operators array of operators in computed_value.
	 * @return fixed str.
	 */
	public static String removeSpaces(String str,String[] operators){
		String result="";
		String copy=str;
		boolean qoute=false;
		for(int i=0;i<copy.length();i++){
			if(copy.charAt(i)=='\"'){
				if(qoute==false){
					qoute=true;
				}
				else{
					qoute=false;
				}
			}
			
			if(copy.charAt(i)==' '){
				if(qoute==true){
					result=result+quoteSpace;
					continue;
				}
				else{
					if(isCharAt(str, i-1, operators)==true||isCharAt(str, i+1, operators)==true){
						continue;
					}
					else{
						result=result+" ";
					}
				}
			}
			else{
				result=result+copy.charAt(i);
			}
		}
		
		return result;
	}
	
	
	
	/**
	 * gets a string and an index then if in that index is an operator returns true else return false.
	 * @param str compute value.
	 * @param index checking index.
	 * @param operators operators to check.
	 * @return if at str.CharAt(index) is an operator returns true else returns false.
	 */
	private static boolean isCharAt(String str,int index,String[] operators){
		try{
			for(int i=0;i<operators.length;i++){
				if(str.charAt(index)==operators[i].charAt(0)){
					return true;
				}
			}
		}
		catch(Exception exception){
			return false;
		}
		return false;
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
		
//		if(operand1.startsWith("\"")==true){
//			operand1=operand1.substring(1, operand1.length()-1);
//		}
		
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























