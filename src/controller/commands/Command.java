package controller.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.C1Constrain;
import model.C2Constrain;
import model.FKConstrain;
import controller.InvalidParam;

/**
 * 
 * @author hamid.m
 * this class is parent of all other commands class.
 */
public abstract class Command {
	
	protected String[] result;
	protected String matchingString;
	protected Pattern pattern;
	protected Matcher matcher;
	
	
	
	
	/**
	 * gets input command then does its action.
	 * @param command string that user has written.
	 * @return if command is relative to its action first does action then returns true else returns false.
	 * @throws InvalidParam if this command is relative to its action but is incompatible with its form.
	 */
	public abstract boolean doAction(String command)throws InvalidParam,C1Constrain,C2Constrain,FKConstrain;
	
	
	/**
	 * gets a command then separates all of its components then returns them.
	 * @param command input command by user.
	 * @return it depends on type of command.
	 * @throws InvalidParam if command does not match with its pattern.
	 */
	protected abstract String[] getComponents(String command)throws InvalidParam;
	
	
	
	/**
	 * prints result of action.
	 */
	public abstract void print();
	
}
