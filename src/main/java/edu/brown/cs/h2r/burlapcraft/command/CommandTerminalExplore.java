package edu.brown.cs.h2r.burlapcraft.command;

import java.util.ArrayList;
import java.util.List;

import burlap.oomdp.auxiliary.common.NullTermination;
import burlap.oomdp.core.Domain;
import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.common.NullRewardFunction;
import burlap.oomdp.singleagent.environment.Environment;
import burlap.oomdp.singleagent.environment.SimulatedEnvironment;
import burlap.oomdp.singleagent.explorer.TerminalExplorer;
import edu.brown.cs.h2r.burlapcraft.BurlapCraft;
import edu.brown.cs.h2r.burlapcraft.domaingenerator.MinecraftDomainGenerator;
import edu.brown.cs.h2r.burlapcraft.stategenerator.StateGenerator;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

public class CommandTerminalExplore implements ICommand {

	private final List aliases;
	Domain domain;
	
	public CommandTerminalExplore() {
		aliases = new ArrayList();
		aliases.add(getCommandName());
	}
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandName() {
		return "terminalExplore";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "terminalExplore";
	}

	@Override
	public List getCommandAliases() {
		return aliases;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		
		MinecraftDomainGenerator mdg = new MinecraftDomainGenerator(StateGenerator.getMap(BurlapCraft.currentDungeon));
		domain = mdg.generateDomain();
		
		Environment env = new SimulatedEnvironment(domain, new NullRewardFunction(), new NullTermination(), 
				StateGenerator.getCurrentState(domain, BurlapCraft.currentDungeon));
		String actionName = args[0];
		Action action = domain.getAction(actionName);
		if(action == null){
			System.out.println("Unknown action: " + actionName + "; nothing changed");
		}
		else{
			GroundedAction ga = action.getAssociatedGroundedAction();
			ga.initParamsWithStringRep(new String[0]);
			if(action.applicableInState(env.getCurrentObservation(), ga)) {
				ga.executeIn(env);
				System.out.println("executed action");
			}
			else{
				System.out.println(ga.toString() + " is not applicable in the current state; nothing changed");
			}
		}
		
		//TerminalExplorer exp = new TerminalExplorer(domain, StateGenerator.getCurrentState(domain, BurlapCraft.currentDungeon)); 
		//exp.explore();
		
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender p_71516_1_,
			String[] p_71516_2_) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
		return false;
	}
	
}
