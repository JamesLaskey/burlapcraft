package edu.brown.cs.h2r.burlapcraft.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import edu.brown.cs.h2r.burlapcraft.BurlapCraft;
import edu.brown.cs.h2r.burlapcraft.dungeongenerator.Dungeon;
import edu.brown.cs.h2r.burlapcraft.solver.MinecraftSolver;
import edu.brown.cs.h2r.burlapcraft.stategenerator.StateGenerator;

public class CommandAStar implements ICommand {
	
	private final List aliases;
	private List<Thread> threads;
	
	public CommandAStar(List<Thread> threads) {
		aliases = new ArrayList();
		aliases.add("astar");
		this.threads = threads;
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public String getCommandName() {
		return "astar";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "astar [closed|open] [all|noplace]\nIf closed/open not specified, closed is used.\nIf all/noplace not specified, all is used.";
	}

	@Override
	public List getCommandAliases() {
		return this.aliases;
	}

	@Override
	public void processCommand(ICommandSender sender, final String[] args) {
		World world = sender.getEntityWorld();
		if (!world.isRemote) {
			
			
			Dungeon dungeon = BurlapCraft.currentDungeon;

			
			if (dungeon == null) {
				sender.addChatMessage(new ChatComponentText("You are not inside a dungeon"));
				return;
			}

			
			
			boolean closed = true;
			boolean place = true;
			if(args.length == 1){
				if(args[0].equals("open")){
					closed = false;
				}
			}
			
			if(args.length == 2){
				if(args[0].equals("open")){
					closed = false;
				}
				if(args[1].equals("noplace")){
					place = false;
				}
			}

			final boolean fclosed = closed;
			final boolean fplace = place;
			
			Thread bthread = new Thread(new Runnable() {
				@Override
				public void run() {
					MinecraftSolver.plan(BurlapCraft.currentDungeon, 1, fclosed, fplace, args);
				}
			});
			threads.add(bthread);
			bthread.start();
		}
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
