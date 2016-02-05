package controller;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import controller.commands.OrException;


public class ClauseNode {
	
	
	private boolean isLeaf;
	
	public int operator;//0:AND, 1:OR.	this Node can not be leaf.
	private ClauseNode leftNode;
	private ClauseNode rightNode;
	
	private int aggregationFunction;//0:MAX, 1:MIN, 2:SUM, 3:AVE.
	private String aggregationVariable;//a variable name like MAX(aggregationVariable).
	private int aggregationValue;//value of aggregation function.
	
	private int singleOperator;//4:> ,3:>= ,2:= ,1:<= ,1:< .this Node must be leaf.
	
	private String variableName;//if this Node is a leaf must has an operand.
	private String variableValue;//if this Node is a leaf its variable must has a value. 
	public boolean not = false;//if this Node is a leaf can has a not operator.if true its operator is != else =. 
	
	private boolean isConst;
	private boolean constant;
	
	private boolean isFull;//if this node has two children or this node is leaf this variable is true.
	
	
	/**
	 * constructor.
	 * @param	operator	0:AND, 1:OR.
	 */
	public ClauseNode(int operator){
		this.isLeaf=false;
		this.isConst=false;
		this.isFull=false;
		this.operator=operator;
	}
	
	
	/**
	 * constructor.
	 * called when condition is a leaf.(does not contain AND or OR)
	 * @param	condition	if condition does not contains AND or OR or NOT then we can send it to this constructor.
	 */
	public ClauseNode(String condition){
		
		this.isLeaf=true;
		this.isFull=true;
		
		
		if(condition.equals("TRUE")){
			this.isConst=true;
			this.constant=true;
		}
		else if(condition.equals("FALSE")){
			this.isConst=true;
			this.constant=false;
		}
		else{
			
			this.isConst=false;
			
			int operator;
			String temp;
			if(condition.contains(">=")==true){
				operator=3;
				temp=">=";
			}
			else if(condition.contains("<=")==true){
				operator=1;
				temp="<=";
			}
			else if(condition.contains(">")==true){
				operator=4;
				temp=">";
			}
			else if(condition.contains("=")==true){
				operator=2;
				temp="=";
			}
			//condition.contains("<")==true
			else{
				operator=0;
				temp="<";
			}
			
			String[] result=condition.split(temp);
			
			
			this.singleOperator=operator;
			this.variableName=clearSpace(result[0]);
			this.variableValue=clearSpace(result[1]);
			
			
		}
		
	}
	
	
	
	
	/**
	 * gets a string and remove free space of start and end of it.
	 * @param str
	 * @return
	 */
	private String clearSpace(String str){
		int index1=0;
		int index2=str.length()-1;
		for(;str.charAt(index1)==' ';index1++){
			
		}
		for(;str.charAt(index2)==' ';index2--){
			
		}
		return str.substring(index1, index2+1);
	}
	
	
	
	
	
	
	/**
	 * this constructor is used when all operator in WHERE clause are AND and there is no constant 
	 * or OR operator.
	 * @param variables an array: array[][0] is variable name, array[][1] is variable value,
	 * array[][2] is operator.
	 */
	public ClauseNode(String[][] variables){
		
		if(variables.length==1){
			
			this.isLeaf=true;
			this.isConst=false;
			
			this.variableName=variables[0][0];
			this.variableValue=variables[0][1];
			if(variables[0][2].equals(">")==true){
				this.singleOperator=4;
			}
			else if(variables[0][2].equals(">=")==true){
				this.singleOperator=3;
			}
			else if(variables[0][2].equals("=")==true){
				this.singleOperator=2;
			}
			else if(variables[0][2].equals("<=")==true){
				this.singleOperator=1;
			}
			// variables[0][2].equals("<")==true
			else{
				this.singleOperator=0;
			}
			
			return;
		}
		
		this.isLeaf=false;
		this.isConst=false;
		this.isFull=true;
		
		String[][] left=new String[variables.length-1][2];
		String[][] right=new String[1][2];
		
		for(int i=0;i<left.length;i++){
			left[i]=variables[i];
		}
		
		right[0]=variables[variables.length-1];
		
		this.rightNode=new ClauseNode(right);
		this.leftNode=new ClauseNode(left);
		
		this.operator=0;
		
		return;
	}
	
	
	
	
	
	/**
	 * constructor just used for having condition.
	 * @param condition single aggregation condition like "MAX(id)>12" or "MIN ( id) <= 10".
	 * @param havingCondition it is not important.
	 * @throws InvalidParam if condition does not match condition string.
	 */
	public ClauseNode(String condition,boolean havingCondition){
		
		
		String matchingString="\\s*([A-Za-z]*)\\s+([A-Za-z]*)\\s+(.*)\\s*";
		Pattern pattern=Pattern.compile(matchingString);
		Matcher matcher=pattern.matcher(condition);
		
		if(matcher.find()==true){
			String[] functions={"MAX","MIN","SUM","AVE"};
			boolean founded=false;
			for(int i=0;i<functions.length;i++){
				if(matcher.group(1).equals(functions[i])){
					this.aggregationFunction=i;
					founded=true;
					break;
				}
			}
			
			
			
			this.aggregationVariable=matcher.group(2);
			
			
			int operatorLength;
			String operatorAndValue=matcher.group(3);
			//4:>, 3:>=, 2:=, 1:<=, 0:<.
			if(operatorAndValue.startsWith(">=")==true){
				this.singleOperator=3;
				operatorLength=2;
			}
			else if(operatorAndValue.startsWith("<=")==true){
				this.singleOperator=1;
				operatorLength=2;
			}
			else if(operatorAndValue.startsWith(">")==true){
				this.singleOperator=4;
				operatorLength=1;
			}
			else if(operatorAndValue.startsWith("=")==true){
				this.singleOperator=2;
				operatorLength=1;
			}
			//<
			else{
				this.singleOperator=0;
				operatorLength=1;
			}
			
			
			
			for(;;operatorLength++){
				if(operatorAndValue.charAt(operatorLength)!=' '){
					break;
				}
			}
			
			int lastSpace=operatorAndValue.length()-1;
			for(;operatorAndValue.charAt(lastSpace)==' ';lastSpace--){
				
			}
			
			String val=operatorAndValue.substring(operatorLength,lastSpace+1);
			this.aggregationValue=Integer.parseInt(val);
		}
		
		this.isLeaf=true;
		this.isConst=false;
		
		
		
		
		
	}
	
	
	
	
	
	
	/**
	 * gets two ClauseNode and set them to this object. used for ClauseNode that has a compare operator.
	 * @param	rightNode	one of the ClauseNode objects.
	 * @param	leftNode	one of the ClauseNode objects.
	 * 
	 */
	public void addChild(ClauseNode rightNode,ClauseNode leftNode){
		this.rightNode=rightNode;
		this.leftNode=leftNode;
		this.isFull=true;
	}
	
	
	
	
	
	
	
	
	/**
	 * checks condition of this Node(if this Node is leaf) or its subtree(if this Node is not leaf) 
	 * then returns result.
	 * @param	columnsName		a HashMap<column name, index of this column in table> to get variable with its name
	 * 							and get its column index then get its value in values array.
	 * @param	values		row of a table that is values of a record.
	 * 
	 */
	public boolean checkCondition(HashMap<String,Integer> columnNames,String[] values)
			throws Exception,NoVariable{
		
		
		if(this.isLeaf==true){
			
			if(this.isConst==true){
				if(this.not==true){
					return !this.constant;
				}
				else{
					return this.constant;
				}
			}
			
			Integer index=(Integer)columnNames.get(this.variableName);
			if(index==null){
				throw new NoVariable();
			}
			
			
			
			String value=values[index.intValue()];
			
			
			
			try{
				boolean result;
				String checkValue=InputHandler.getValue(this.variableValue, columnNames, values);
				
				
//				single operator is >
				if(this.singleOperator==4){
//					result=Integer.parseInt(value)>Integer.parseInt(this.variableValue);
					result=Integer.parseInt(value)>Integer.parseInt(checkValue);
				}
//				single operator is >=
				else if(this.singleOperator==3){
//					result=Integer.parseInt(value)>=Integer.parseInt(this.variableValue);
					result=Integer.parseInt(value)>=Integer.parseInt(checkValue);
				}
//				single operator is =
				else if(this.singleOperator==2){
//					result=this.variableValue.equals(value);
					result=checkValue.equals(value);
				}
//				single operator is <=
				else if(this.singleOperator==1){
//					result=Integer.parseInt(value)<=Integer.parseInt(this.variableValue);
					result=Integer.parseInt(value)<=Integer.parseInt(checkValue);
				}
//				single operator is <. this.singleOperator==0
				else{
//					result=Integer.parseInt(value)<Integer.parseInt(this.variableValue);
					result=Integer.parseInt(value)<Integer.parseInt(checkValue);
				}
				
				
				
				if(this.not==true){
					return !(result);
				}
				//this.not==false
				else{
					return result;
				}
			}
			//value==null
			catch(NullPointerException exception){
				return false;
			}
			
		}
		//this.isLeaf==false.
		else{
			
			boolean leftResult;
			boolean rightResult;
			
			//this.operator is AND.
			if(this.operator==0){
				
				
				if(this.leftNode!=null){
					leftResult=this.leftNode.checkCondition(columnNames, values);
				}
				else{
					throw new Exception();
				}
				
				//in AND if one of operands is false result become false.
				if(leftResult==false){
					if(this.not==true){
						return true;
					}
					else{
						return false;
					}
				}
				//leftResult == true
				else{
					if(this.rightNode!=null){
						rightResult=this.rightNode.checkCondition(columnNames, values);
					}
					else{
						throw new Exception();
					}
				}
				
				if(this.not==false){
					return leftResult && rightResult;
				}
				else{
					return !(leftResult&&rightResult);
				}
				
			}
			//this.operator is OR.
			else if(this.operator==1){
				
				if(this.leftNode!=null){
					leftResult=this.leftNode.checkCondition(columnNames, values);
				}
				else{
					throw new Exception();
				}
				
				//in OR if one of operands is true result become true.
				if(leftResult==true){
					if(this.not==true){
						return false;
					}
					else{
						return true;
					}
				}
				//leftResult==false
				else{
					if(this.rightNode!=null){
						if(this.not==true){
							return !this.rightNode.checkCondition(columnNames, values);
						}
						else{
							return this.rightNode.checkCondition(columnNames, values);
						}
					}
					else{
						throw new Exception();
					}
				}
				
			}
			
		}
		
		throw new Exception();
		
	}
	
	
	
	
	/**
	 * gets values and names of a group of variables then checks this clauseNode condition is valid 
	 * on this group of variables.
	 * @param values values of this variable group. order of this variables must be same by order of names.
	 * @param names names of this variable group.
	 * @return if this condition is satisfied by this variable group returns true else returns false.
	 */
	public boolean checkAggregationCondition(String[][] values,String[] names){
		
		for(int i=0;i<values.length;i++){
			for(int j=0;j<values[0].length;j++){
				System.out.print(values[i][j]+" ");
			}
			System.out.println();
		}
		
		System.out.println("begin");
		System.out.println("aggFunc: "+this.aggregationFunction);
		System.out.println("aggVari: "+this.aggregationVariable);
		System.out.println("aggVal: "+this.aggregationValue);
		System.out.println("single: "+this.singleOperator);
		System.out.println("after");
		
		
		boolean result = false;
		
		//if this clauseNode does not contains AND, OR.
		if(this.isLeaf==true){
			int variableIndex=0;
			for(int i=0;i<names.length;i++){
				if(names[i].equals(this.aggregationVariable)){
					variableIndex=i;
					break;
				}
			}
			
			
			int calculateValue=0;
			
			//MAX.
			if(this.aggregationFunction==0){
				calculateValue=Integer.parseInt(values[0][variableIndex]);
				int temp=0;
				for(int i=0;i<values.length;i++){
					temp=Integer.parseInt(values[i][variableIndex]);
					if(calculateValue<temp){
						calculateValue=temp;
					}
				}
				System.out.println("cal: "+calculateValue);
			}
			//MIN.
			else if(this.aggregationFunction==1){
				calculateValue=Integer.parseInt(values[0][variableIndex]);
				int temp=0;
				for(int i=0;i<values.length;i++){
					temp=Integer.parseInt(values[i][variableIndex]);
					if(temp<calculateValue){
						calculateValue=temp;
					}
				}
				
			}
			//SUM.
			else if(this.aggregationFunction==2){
				calculateValue=0;
				int temp=0;
				for(int i=0;i<values.length;i++){
					temp=Integer.parseInt(values[i][variableIndex]);
					calculateValue+=temp;
				}
			}
			//AVE.
			else{
				calculateValue=0;
				int temp=0;
				for(int i=0;i<values.length;i++){
					temp=Integer.parseInt(values[i][variableIndex]);
					calculateValue+=temp;
				}
				calculateValue=calculateValue/values.length;
			}
			
			
			//>
			if(this.singleOperator==4&&calculateValue>this.aggregationValue){
				result=true;
			}
			//>=
			else if(this.singleOperator==3&&calculateValue>=this.aggregationValue){
				result=true;
			}
			//==
			else if(this.singleOperator==2&&calculateValue==this.aggregationValue){
				result=true;
			}
			//<=
			else if(this.singleOperator==1&&calculateValue<=this.aggregationValue){
				result=true;
			}
			//<
			else if(this.singleOperator==0&&calculateValue<this.aggregationValue){
				result=true;
			}
			else{
				result=false;
			}
			
		}
		//this is not a leaf in condition tree. contains AND, OR.
		else{
			boolean right=this.rightNode.checkAggregationCondition(values, names);
			boolean left=this.leftNode.checkAggregationCondition(values, names);
			
			//AND.
			if(this.operator==0){
				result=right&&left;
			}
			//OR.
			else if(this.operator==1){
				result=right||left;
			}
		}
		
		
		if(this.not==false){
			return result;
		}
		else{
			return !result;
		}
	}
	
	
	
	
	
	
	/**
	 * before a condition is there is a NOT this function reverse its sign.
	 */
	public void inverseSign(){
		if(not==true){
			not=false;
		}
		else{
			not=true;
		}
	}
	
	
	
	/**
	 * @return	if this node is leaf or if has binary operator and has two children returns true else returns false.
	 */
	public boolean isFull(){
		return this.isFull;
	}
	
	
	
	
	
	
	/**
	 * @return if this clause does not contains AND,OR and is not TRUE,FALSE
	 *  returns an array: array[][0]:variable name , array[][1]:variable value, array[][2]:operator.
	 * @throws Exception if this node is TRUE,FALSE or contains AND,OR.
	 */
	public String[][] getVariable()throws OrException,TrueCondition,FalseCondition{
		String[][] result = null;
		if(this.isLeaf==true&&this.isConst==false){
			String operator;
			if(this.singleOperator==4){
				operator=">";
			}
			else if(this.singleOperator==3){
				operator=">=";
			}
			else if(this.singleOperator==2){
				operator="=";
			}
			else if(this.singleOperator==1){
				operator="<=";
			}
			else{
				operator="<";
			}
			result=new String[1][3];
			result[0]=new String[]{this.variableName,this.variableValue,operator};
		}
		else if(this.isLeaf==false){
			
			if(this.operator==1){
				throw new OrException();
			}
			
			String[][] left;
			try{
				left=this.leftNode.getVariable();
			}
			catch(TrueCondition exception){
				left=new String[0][0];
			}
			
			String[][] right;
			try{
				right=this.rightNode.getVariable();
			}
			catch(TrueCondition exception){
				right=new String[0][0];
			}
			
			result=new String[right.length+left.length][3];
			
			
			int index=0;
			
			for(index=0;index<left.length;index++){
				result[index]=left[index];
			}
			
			for(int i=0;i<right.length;i++,index++){
				result[index]=right[i];
			}
			
			
			
		}
		else if(this.isConst==true){
			if(this.not==false){
				if(this.constant==true){
					throw new TrueCondition();
				}
				else{
					throw new FalseCondition();
				}
			}
			else{
				if(this.constant==true){
					throw new FalseCondition();
				}
				else{
					throw new TrueCondition();
				}
			}
		}
		
		return result;
	}
	
	
	
	@Override
	public String toString(){
		
		String result="";
		if(this.isLeaf==true){
			if(this.isConst==true){
				result=this.constant+"";
			}
			else{
				String temp;
//				">"
				if(this.singleOperator==4){
					temp=">";
				}
//				">="
				else if(this.singleOperator==3){
					temp=">=";
				}
//				"="
				else if(this.singleOperator==2){
					temp="=";
				}
//				"<="
				else if(this.singleOperator==1){
					temp="<=";
				}
//				"<"
				else{
					temp="<";
				}
				result=this.variableName+" "+temp+" "+this.variableValue;
			}
		}
//		binary operators
		else{
			String temp;
			
			if(this.not==true){
				result+="NOT ";
			}
			
			if(this.operator==0){
				temp="AND";
			}
			else{
				temp="OR";
			}
			
			result+=this.leftNode.toString()+" "+temp+" "+this.rightNode.toString();
		}
		
		return result;
		
	}
	
	
	
}
























