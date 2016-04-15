package edu.brown.cs.h2r.burlapcraft.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

public class CommandKill implements ICommand {
	private final List aliases;
	private List<Thread> threads;

	public CommandKill(List<Thread> threads) {
		aliases = new ArrayList();
		aliases.add(getCommandName());
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public String getCommandName() {
		return "kill";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "kill <process_name>";
	}

	@Override
	public List getCommandAliases() {
		return null;
	}

	@Override
	public void processCommand(ICommandSender p_71515_1_, String[] p_71515_2_) {
		if (threads != null) {
			Iterator<Thread> iter = threads.iterator();
			while (iter.hasNext()) {
				Thread t = iter.next();
				System.out.println(t);
				t.stop();
				iter.remove();
			}
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
		return false;
	}
}
