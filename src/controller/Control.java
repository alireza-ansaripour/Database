package controller;

import java.util.ArrayList;

import model.C1Constrain;
import model.C2Constrain;
import model.FKConstrain;
import controller.commands.Command;
import controller.commands.CreateIndexCommand;
import controller.commands.CreateTableCommand;
import controller.commands.DeleteCommand;
import controller.commands.InsertCommand;
import controller.commands.SelectCommand;
import controller.commands.UpdateCommand;

public class Control {
	
	private static ArrayList<Command> commands=new ArrayList<>();
	
	public static void init(){
		commands.add(new CreateIndexCommand());
		commands.add(new CreateTableCommand());
		commands.add(new DeleteCommand());
		commands.add(new InsertCommand());
		commands.add(new SelectCommand());
		commands.add(new UpdateCommand());
	}
	
	
	public static void doAction(String command){
		
		for(int i=0;i<commands.size();i++){
			try{
				if(commands.get(i).doAction(command)==true){
					break;
				}
			}
			catch(InvalidParam exception){
				System.out.println("INVALID INPUT!");
			}
			catch(C1Constrain exception){
				System.out.println("C1 CONSTRAIT FAILED");
			}
			catch(C2Constrain exception){
				System.out.println("C2 CONSTRAIT FAILED");
			}
			catch(FKConstrain exception){
				System.out.println("FOREIGN KEY CONSTRAINT RESTRICTS");
			}
			
		}
		
	}
	
	
	
	
	
}
