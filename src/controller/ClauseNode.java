package controller;

import java.util.HashMap;

public class ClauseNode {
	
	
	private boolean isLeaf;
	
	private int operator;//0:AND, 1:OR.	this Node can not be leaf.
	private ClauseNode leftNode;
	private ClauseNode rightNode;
	
	private int singleOperator;//4:> ,3:>= ,2:= ,1:<= ,1:< .this Node must be leaf.
	private String variableName;//if this Node is a leaf must has an operator.
	private String variableValue;//if this Node is a leaf its variable must has a value. 
	private boolean not;//if this Node is a leaf can has a not operator.if true its operator is != else =. 
	
	private boolean isConst;
	private boolean constant;
	
	/*
	 * constructor if this Node is not leaf this constructor must be called.
	 * operation:		can be 0:AND and 1:OR.
	 * leftNode:		if this node has operator must has left child.
	 * rightNode:		if this node has operator must has right child.
	 */
	public ClauseNode(int operator,ClauseNode leftNode,ClauseNode rightNode){
		
		this.isLeaf=false;
		this.isConst=false;
		this.operator=operator;
		this.leftNode=leftNode;
		this.rightNode=rightNode;
		
	}
	
	
	
	/*
	 * constructor if this Node is a leaf this constructor must be called.
	 * variableName:	name of column that we want to check its value.
	 * variableValue:	value of that variable that must be check.
	 * not:				if is true in checking returns opposite of result if is false returns result.
	 */
	public ClauseNode(int singleOperator,String variableName,String variableValue,boolean not){
		
		this.isLeaf=true;
		this.isConst=false;
		this.singleOperator=singleOperator;
		this.variableName=variableName;
		this.variableValue=variableValue;
		this.not=not;
		
	}
	
	
	/*
	 * constructor. if this Node is a leaf and is a constant TRUE or FALSE.
	 * constant:	if true value of this Node is true and if is false value of this Node is false in checking.
	 */
	public ClauseNode(boolean constant,boolean not){
		
		this.isLeaf=true;
		this.isConst=true;
		this.constant=constant;
		this.not=not;
		
	}
	
	
	
	
	/*
	 * checks condition of this Node(if this Node is leaf) or its subtree(if this Node is not leaf) 
	 * then returns result.
	 * 
	 * columnsName:		a HashMap<column name, index of this column in table> to get variable with its name
	 * 					and get its column index then get its value in values array.
	 * values:			row of a table that is values of a record.
	 * 
	 */
	public boolean checkCondition(HashMap<String,Integer> columnNames,String[] values)throws Exception,NoVariable{
		
		if(this.isLeaf==true){
			
			if(this.isConst==true){
				if(this.not==true){
					return !this.constant;
				}
				//this.not == false
				else{
					return this.constant;
				}
			}
			
			Integer index=(Integer)columnNames.get(this.variableName);
			if(index==null){
				throw new NoVariable();
			}
			
			String value=values[index.intValue()];
			
			if(value!=null){
				boolean result;
				
//				single operator is >
				if(this.singleOperator==4){
					result=Integer.parseInt(value)>Integer.parseInt(this.variableValue);
				}
//				single operator is >=
				else if(this.singleOperator==3){
					result=Integer.parseInt(value)>=Integer.parseInt(this.variableValue);
				}
//				single operator is =
				else if(this.singleOperator==2){
					result=this.variableValue.equals(value);
				}
//				single operator is <=
				else if(this.singleOperator==1){
					result=Integer.parseInt(value)<=Integer.parseInt(this.variableValue);
				}
//				single operator is <. this.singleOperator==0
				else{
					result=Integer.parseInt(value)<Integer.parseInt(this.variableValue);
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
			else{
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
					return false;
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
				
				return leftResult && rightResult;
				
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
					return true;
				}
				//leftResult==false
				else{
					if(this.rightNode!=null){
						return this.rightNode.checkCondition(columnNames, values);
					}
					else{
						throw new Exception();
					}
				}
				
				
			}
			
		}
		
		throw new Exception();
		
	}
	
	
	
	
}
























