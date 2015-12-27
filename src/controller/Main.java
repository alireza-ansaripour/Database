package controller;

import java.util.Scanner;




/**
 * provides main functions.
 */
public class Main {
	
	
	/**
	 * main.
	 */
	public static void main(String[] args) {
		Scanner scanner=new Scanner(System.in);
		
		Control.init();
		
		for(;;){
			String command=scanner.nextLine();
			Control.doAction(command);
		}
		
//		String c=scanner.nextLine();
//		ClauseNode clauseNode=InputHandler.createClauseTree(c);
//		
//		String[][] variables=null;
//		try{
//			variables=clauseNode.getVariable();
//			
//			for(int i=0;i<variables.length;i++){
//				System.out.println(variables[i][0]+" "+variables[i][2]+" "+variables[i][1]);
//			}
//			
//		}
//		catch(Exception exception){
//			exception.printStackTrace();
//		}
//		
//		
//		ClauseNode result=new ClauseNode(variables);
//		System.out.println("result: "+result);
		
	}
	
}













