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
		
//		String string=scanner.nextLine();
//		System.out.println(InputHandler.removeSpaces(string, new String[]{"+","-","*","/"}));
		
		
		Control.init();
		
		for(;;){
			String command=scanner.nextLine();
			Control.doAction(command);
		}
		
		
		
		
	}
	
}








