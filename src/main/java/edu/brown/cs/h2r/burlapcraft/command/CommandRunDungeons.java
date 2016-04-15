package edu.brown.cs.h2r.burlapcraft.command;

import java.util.ArrayList;
import java.util.List;

import edu.brown.cs.h2r.burlapcraft.BurlapCraft;
import edu.brown.cs.h2r.burlapcraft.dungeongenerator.Dungeon;
import edu.brown.cs.h2r.burlapcraft.handler.HandlerEvents;
import edu.brown.cs.h2r.burlapcraft.helper.HelperActions;
import edu.brown.cs.h2r.burlapcraft.helper.HelperGeometry.Pose;
import edu.brown.cs.h2r.burlapcraft.solver.MinecraftSolver;
import edu.brown.cs.h2r.burlapcraft.stategenerator.StateGenerator;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class CommandRunDungeons implements ICommand {
	
	private final List aliases;
	
	private Thread bthread;
	
	public CommandRunDungeons() {
		aliases = new ArrayList();
		aliases.add("runDungeons");
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public String getCommandName() {
		return "runDungeons";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "runDungeons";
	}

	@Override
	public List getCommandAliases() {
		return this.aliases;
	}

	@Override
	public void processCommand(ICommandSender sender, final String[] args) {
		World world = sender.getEntityWorld();
		if (!world.isRemote) {
			if (args.length > 0) {
				sender.addChatMessage(new ChatComponentText("This command doesn't take any arguments. Ignoring..."));
			}
			
			final List<Dungeon> DUNGEONS = BurlapCraft.dungeons;
			final ICommandSender SENDER = sender;
			
			if (DUNGEONS.isEmpty()) {
				sender.addChatMessage(new ChatComponentText("There are no dungeons."));
				return;
			}
			
			final int PLANNER = 1;
			
			Thread bthread = new Thread(new Runnable() {
				@Override
				public void run() {
					for (Dungeon d : DUNGEONS) {
						EntityPlayer player = HandlerEvents.player;
						
						StateGenerator.blockNameMap.clear();
						StateGenerator.invBlockNameMap.clear();
						
						Pose offset = d.getPlayerStartOffset();
						Pose playerPose = d.getPose().add(offset);
						
						HelperActions.setPlayerPosition(player, playerPose);

						BurlapCraft.currentDungeon = d;
						MinecraftSolver.plan(d, PLANNER, true, true, args);
						//break;
					}
				}
			});

			bthread.start();
		}

	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
		// TODO Auto-generated method stub
		return false;
	}

}
