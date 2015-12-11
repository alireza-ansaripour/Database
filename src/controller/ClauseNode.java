package controller;

import java.util.HashMap;


public class ClauseNode {
	
	
	private boolean isLeaf;
	
	public int operator;//0:AND, 1:OR.	this Node can not be leaf.
	private ClauseNode leftNode;
	private ClauseNode rightNode;
	
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
	public boolean checkCondition(HashMap<String,Integer> columnNames,String[] values)throws Exception,NoVariable{
		
		
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
	 *  returns an array: array[0]:variable name , array[1]:variable value, array[2]:operator.
	 * @throws Exception if this node is TRUE,FALSE or contains AND,OR.
	 */
	public String[] getVariable()throws Exception{
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
			return new String[]{this.variableName,this.variableValue,operator};
		}
		else{
			throw new Exception();
		}
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