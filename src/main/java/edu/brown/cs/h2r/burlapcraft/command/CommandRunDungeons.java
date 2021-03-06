package edu.brown.cs.h2r.burlapcraft.command;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import edu.brown.cs.h2r.burlapcraft.BurlapCraft;
import edu.brown.cs.h2r.burlapcraft.dungeongenerator.Dungeon;
import edu.brown.cs.h2r.burlapcraft.handler.HandlerEvents;
import edu.brown.cs.h2r.burlapcraft.helper.HelperActions;
import edu.brown.cs.h2r.burlapcraft.helper.HelperGeometry.Pose;
import edu.brown.cs.h2r.burlapcraft.solver.MinecraftSolver;
import edu.brown.cs.h2r.burlapcraft.stategenerator.StateGenerator;
import machinelearning.PillarWekaClassifierWrapper;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class CommandRunDungeons implements ICommand {
	
	private final List aliases;
	
	final List<PillarWekaClassifierWrapper.DungeonTrainExample> training;
	
	public CommandRunDungeons(List<PillarWekaClassifierWrapper.DungeonTrainExample> training) {
		aliases = new ArrayList();
		aliases.add("runDungeons");
		
		this.training = training;
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

	//Usage:
	// Step 1: /runDungeons run normal 0 14     /runDunegons run learned
	// Step 2: once step 1 finishes run /runDungeons nameOfFile
	
	@Override
	public void processCommand(ICommandSender sender, final String[] args) {
		
		Classifier classifier = new NaiveBayes();
		
		if (args.length == 1) {
			PillarWekaClassifierWrapper wrapper = new PillarWekaClassifierWrapper(15, this.training, classifier);
			ObjectOutputStream out;
			try {
				out = new ObjectOutputStream(new FileOutputStream(args[0] + ".dat"));
				out.writeObject(wrapper.getTrainingInstances());
				out.flush();
				out.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Instances training;
			
			try {
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(args[0] + ".dat"));
				training = (Instances) in.readObject();
				in.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			wrapper.runClassifier(wrapper.getTrainingInstances());
		} else {
		
		World world = sender.getEntityWorld();
		if (!world.isRemote) {
			
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
					int count = 1;
					
					EntityPlayer player = HandlerEvents.player;
					
					for (Dungeon d : DUNGEONS) {						
						System.out.println("DUNGEON CALL #" + count);
						count++;
						
						StateGenerator.blockNameMap.clear();
						StateGenerator.invBlockNameMap.clear();
						
						Pose offset = d.getPlayerStartOffset();
						Pose playerPose = d.getPose().add(offset);
						
						HelperActions.setPlayerPosition(player, playerPose);
						
						BurlapCraft.currentDungeon = d;
						try {
							Thread.sleep(200);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						System.out.println(d.getName());
						// @bug I don't know why, but the player position gets off on the second teleport
						PillarWekaClassifierWrapper.DungeonTrainExample example = MinecraftSolver.plan(d, PLANNER, true, true, args);
						if (example != null) { training.add(example); }
						System.out.println(example);
						//break;
					}
					for (PillarWekaClassifierWrapper.DungeonTrainExample e : training) {
						System.out.println("TRAINING:" + e);
					}
					// @note make instance of WekaClassifierWrapper, do computation and recording here?
					// otherwise, there'll need to be polling, right?
				}
			});
			bthread.start();
		}
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
