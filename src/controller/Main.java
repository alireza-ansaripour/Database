package controller;

import java.util.HashMap;
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
				
//		int number=scanner.nextInt();
//		scanner.nextLine();
//		
//		String[] record=new String[number];
//		HashMap<String, Integer> column=new HashMap<String, Integer>();
//		
//		String temp;
//		for(int i=0;i<number;i++){
//			temp=scanner.next();
//			column.put(temp, i);
//			temp=scanner.next();
//			record[i]=temp;
//		}
//		
//		scanner.nextLine();
//		
//		String condition=scanner.nextLine();
//		
//		ClauseNode clause=InputHandler.createClauseTree(condition);
//		boolean result=false;
//		try{
//			result=clause.checkCondition(column, record);
//		}
//		catch(NoVariable noVariable){
//			result=false;
//		}
//		catch(Exception exception){
//			exception.printStackTrace();
//		}
//		
//		System.out.println(result);
		
//		***************************************************************************
		
//		String computeValue=scanner.nextLine();
//		
//		HashMap<String, Integer> hash=new HashMap<String, Integer>();
//		hash.put("name", 0);
//		hash.put("lastName", 1);
//		String[] variables={"hamid","miri"};
//		
//		String string=InputHandler.getValue(computeValue,hash,variables);
//		
//		System.out.println(string);
		
//		****************************************************************************
		
		Control.init();
		
		for(;;){
			String command=scanner.nextLine();
			Control.doAction(command);
		}
		
		
		
		
	}
	
}








