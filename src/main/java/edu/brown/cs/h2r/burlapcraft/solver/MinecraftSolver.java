package edu.brown.cs.h2r.burlapcraft.solver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import burlap.oomdp.singleagent.Action;
import net.minecraft.block.Block;
import burlap.behavior.singleagent.EpisodeAnalysis;
import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.modellearning.rmax.PotentialShapedRMax;
import burlap.oomdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.behavior.singleagent.planning.deterministic.DDPlannerPolicy;
import burlap.behavior.singleagent.planning.deterministic.DeterministicPlanner;
import burlap.behavior.singleagent.planning.deterministic.SDPlannerPolicy;
import burlap.behavior.singleagent.planning.deterministic.informed.Heuristic;
import burlap.behavior.singleagent.planning.deterministic.informed.NullHeuristic;
import burlap.behavior.singleagent.planning.deterministic.informed.astar.AStar;
import burlap.behavior.singleagent.planning.deterministic.uninformed.bfs.BFS;
import burlap.behavior.singleagent.planning.stochastic.sparsesampling.SparseSampling;
import burlap.behavior.singleagent.pomdp.wrappedmdpalgs.BeliefSparseSampling;
import burlap.debugtools.MyTimer;
import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;
import edu.brown.cs.h2r.burlapcraft.BurlapCraft;
import edu.brown.cs.h2r.burlapcraft.action.ActionChangeItemSimulated;
import edu.brown.cs.h2r.burlapcraft.action.ActionPillarParameterizedOptionLearnedSimulated;
import edu.brown.cs.h2r.burlapcraft.action.ActionPillarParameterizedOptionSimulated;
import edu.brown.cs.h2r.burlapcraft.domaingenerator.MinecraftDomainGenerator;
import edu.brown.cs.h2r.burlapcraft.dungeongenerator.Dungeon;
import edu.brown.cs.h2r.burlapcraft.dungeongenerator.ReadDungeon;
import edu.brown.cs.h2r.burlapcraft.environment.MinecraftEnvironment;
import edu.brown.cs.h2r.burlapcraft.helper.HelperActions;
import edu.brown.cs.h2r.burlapcraft.helper.HelperGeometry;
import edu.brown.cs.h2r.burlapcraft.helper.HelperNameSpace;
import edu.brown.cs.h2r.burlapcraft.helper.HelperGeometry.Pose;
import edu.brown.cs.h2r.burlapcraft.stategenerator.StateGenerator;
import machinelearning.PillarWekaClassifierWrapper;

/**
 * @author James MacGlashan.
 */
public class MinecraftSolver {

	static PotentialShapedRMax lastLearningAgent = null;
	static Dungeon lastDungeon;
	static Domain lastDomain;

	static RewardFunction rf = new GotoRF();
	static TerminalFunction tf = new GotoTF();
	static StateConditionTest gc = new GotoGoalCondition();
	static MyTimer newTimer = new MyTimer();

	/**
	 * Runs planning for the current dungeon. Note that that the player needs to have first teleported to a dungeon
	 * before this will work.
	 * @param plannerToUse 0: BFS; 1: A*
	 * @param closedLoop if true then a closed loop policy will be followed; if false, then open loop.
	 */
	public static PillarWekaClassifierWrapper.DungeonTrainExample plan(Dungeon d, int plannerToUse, boolean closedLoop, boolean place, String[] params){

		int [][][] map = StateGenerator.getMap(d);

		MinecraftDomainGenerator simdg = new MinecraftDomainGenerator(map);

		//if (!place) {
		simdg.setActionWhiteListToNavigationAndDestroy();
		//}
		
		Domain domain = simdg.generateDomain();

		State initialState = StateGenerator.getCurrentState(domain, d);

		DeterministicPlanner planner = null;
		if(plannerToUse == 0){
			planner = new BFS(domain, gc, new SimpleHashableStateFactory(false));
		}
		else if(plannerToUse == 1){
			Heuristic mdistHeuristic = new Heuristic() {
				
				@Override
				public double h(State s) {
					
					ObjectInstance agent = s.getFirstObjectOfClass(HelperNameSpace.CLASSAGENT);
					int ax = agent.getIntValForAttribute(HelperNameSpace.ATX);
					int ay = agent.getIntValForAttribute(HelperNameSpace.ATY);
					int az = agent.getIntValForAttribute(HelperNameSpace.ATZ);

					HelperGeometry.Pose goalPose = getGoalPose(s);
					
					int gx = (int) goalPose.getX();
					int gy = (int) goalPose.getY();
					int gz = (int) goalPose.getZ();
					
					//compute Manhattan distance
					double mdist = Math.abs(ax-gx) + Math.abs(ay-gy) + Math.abs(az-gz);
					
					return -mdist;
				}
			};
			planner = new AStar(domain, rf, gc, new SimpleHashableStateFactory(false), mdistHeuristic);
		}
		else{
			throw new RuntimeException("Error: planner type is " + planner + "; use 0 for BFS or 1 for A*");
		}
//		planner.setTf(tf);
		
		
		PillarWekaClassifierWrapper classifier;
		
		//to run with learned do, /runDungeons run learned
		try {
			classifier = new PillarWekaClassifierWrapper("../trainingRound2/complete.dat", new NaiveBayes());
			ActionPillarParameterizedOptionLearnedSimulated pillarActionLearned = new ActionPillarParameterizedOptionLearnedSimulated(
					HelperNameSpace.ACTIONPILLAR, domain, map, classifier);
			if (params[1].equals("learned")) {
				planner.addNonDomainReferencedAction(pillarActionLearned);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int minHeight = 0;
		int maxHeight = 15;
		if (params.length >= 4) {
			try {
				minHeight = Integer.valueOf(params[2]);
				System.out.println("Pillar minHeight param " + minHeight);
				maxHeight = Integer.valueOf(params[3]);
				System.out.println("Pillar maxHeight param " + maxHeight);
			} catch (NumberFormatException e) {
				System.out.println("invalid params arguments, need two integers for minPillar and MaxPillar");
			}
		}
		ActionPillarParameterizedOptionSimulated pillarAction = new ActionPillarParameterizedOptionSimulated(
				HelperNameSpace.ACTIONPILLAR, domain, map, minHeight, maxHeight);
		
		//to run with parameters do /runDungeons run normal 0 14
		if (params[1].equals("normal")) {
			planner.addNonDomainReferencedAction(pillarAction);
		}
		planner.planFromState(initialState);

		Policy p = closedLoop ? new DDPlannerPolicy(planner) : new SDPlannerPolicy(planner);

		MinecraftEnvironment me = new MinecraftEnvironment(domain);
		me.setTerminalFunction(tf);
		
		// do we only want to generate a state instance if the "run" option is set?
		if (params[0].equals("run")) {
			
			EpisodeAnalysis analysis = p.evaluateBehavior(me);
			List<State> states = analysis.stateSequence;
			Iterator<State> stateIter = states.iterator();
			
			List<State> pillarStates = new ArrayList<State>();
			List<Integer> pillarHeights = new ArrayList<Integer>();
			for (GroundedAction a : analysis.actionSequence) {
				System.out.println(a);
				System.out.println(a.action.getName());
				if (a.toString().contains("pillar") && a.toString().charAt(12) == '0') {
					pillarStates.add(stateIter.next());
					pillarHeights.add(Integer.valueOf(a.toString().split(" ")[2]));
				} else {
					stateIter.next();
				}
			}
			return new PillarWekaClassifierWrapper.DungeonTrainExample(map, pillarStates, pillarHeights);//, 20, map.length);
		} return null;
	}

	

	public static void learn(Dungeon d) {

		if(d != lastDungeon || lastLearningAgent == null){
			int [][][] map = StateGenerator.getMap(d);
			MinecraftDomainGenerator mdg = new MinecraftDomainGenerator(map);
			mdg.setActionWhiteListToNavigationOnly();
			
			lastDomain = mdg.generateDomain();
			lastLearningAgent = new PotentialShapedRMax(lastDomain, 0.99, new SimpleHashableStateFactory(false), 0, 1, 0.01, 200);
			lastDungeon = d;
			
			System.out.println("Starting new RMax");
		}

		State initialState = StateGenerator.getCurrentState(lastDomain, d);
		MinecraftEnvironment me = new MinecraftEnvironment(lastDomain);
		me.setRewardFunction(rf);
		me.setTerminalFunction(tf);
		
		newTimer.start();
		lastLearningAgent.runLearningEpisode(me);
		newTimer.stop();
		
		System.out.println(newTimer.getTotalTime());
	}

	public static class GotoRF implements RewardFunction {

		@Override
		public double reward(State s, GroundedAction a, State sprime) {

			//get location of agent in next state
			ObjectInstance agent = sprime.getFirstObjectOfClass(HelperNameSpace.CLASSAGENT);
			int ax = agent.getIntValForAttribute(HelperNameSpace.ATX);
			int ay = agent.getIntValForAttribute(HelperNameSpace.ATY);
			int az = agent.getIntValForAttribute(HelperNameSpace.ATZ);
			String[] params = a.getParametersAsString();
			if (params != null && params.length == 1) {
				if (Integer.valueOf(params[0]) > 0) {
					return -1;
				} else {
					return -2;
				}
			}

			return -1.0;
		}
	}
	
	
	
	public static class WalkWallsRF implements RewardFunction {

		@Override
		public double reward(State s, GroundedAction a, State sprime) {
			//get location of agent in next state
			ObjectInstance agent = sprime.getFirstObjectOfClass(HelperNameSpace.CLASSAGENT);
			int ax = agent.getIntValForAttribute(HelperNameSpace.ATX);
			int ay = agent.getIntValForAttribute(HelperNameSpace.ATY);
			int az = agent.getIntValForAttribute(HelperNameSpace.ATZ);
			String[] params = a.getParametersAsString();
			if (params != null && params.length == 1) {
				if (Integer.valueOf(params[0]) > 0) {
					return 0.;
				}
			}

			return -1.0;
		}
	}


	/**
	 * Find the gold block and return its pose.
	 * @param s the state
	 * @return the pose of the agent being one unit above the gold block.
	 */
	public static HelperGeometry.Pose getGoalPose(State s) {
		List<ObjectInstance> blocks = s.getObjectsOfClass(HelperNameSpace.CLASSBLOCK);
		//System.out.println(s);
		for (ObjectInstance block : blocks) {
			if (block.getIntValForAttribute(HelperNameSpace.ATBTYPE) == 41) {
				int goalX = block.getIntValForAttribute(HelperNameSpace.ATX);
				int goalY = block.getIntValForAttribute(HelperNameSpace.ATY);
				int goalZ = block.getIntValForAttribute(HelperNameSpace.ATZ);

				return HelperGeometry.Pose.fromXyz(goalX, goalY + 1, goalZ);
			}
		}
		return null;
	}

	public static class GotoTF implements TerminalFunction {

		@Override
		public boolean isTerminal(State s) {
			ObjectInstance agent = s.getFirstObjectOfClass(HelperNameSpace.CLASSAGENT);
			int ax = agent.getIntValForAttribute(HelperNameSpace.ATX);
			int ay = agent.getIntValForAttribute(HelperNameSpace.ATY);
			int az = agent.getIntValForAttribute(HelperNameSpace.ATZ);

			HelperGeometry.Pose agentPose = HelperGeometry.Pose.fromXyz(ax, ay, az);
			int rotDir = agent.getIntValForAttribute(HelperNameSpace.ATROTDIR);
			int vertDir = agent.getIntValForAttribute(HelperNameSpace.ATVERTDIR);

			HelperGeometry.Pose goalPose = getGoalPose(s);

			//are they at goal location or dead
			double distance = goalPose.distance(agentPose);
			//System.out.println("Distance: " + distance + " goal at: " + goalPose);

			if (goalPose.distance(agentPose) < 0.5) {
				return true;
			} else {
				return false;
			}
		}

	}

	public static class GotoGoalCondition implements StateConditionTest {

		@Override
		public boolean satisfies(State s) {
			ObjectInstance agent = s.getFirstObjectOfClass(HelperNameSpace.CLASSAGENT);
			int ax = agent.getIntValForAttribute(HelperNameSpace.ATX);
			int ay = agent.getIntValForAttribute(HelperNameSpace.ATY);
			int az = agent.getIntValForAttribute(HelperNameSpace.ATZ);


			HelperGeometry.Pose agentPose = HelperGeometry.Pose.fromXyz(ax, ay, az);
			int rotDir = agent.getIntValForAttribute(HelperNameSpace.ATROTDIR);
			int vertDir = agent.getIntValForAttribute(HelperNameSpace.ATVERTDIR);

			HelperGeometry.Pose goalPose = getGoalPose(s);

			//are they at goal location or dead
			double distance = goalPose.distance(agentPose);
			//System.out.println("Distance: " + distance + " goal at: " + goalPose);

			if (goalPose.distance(agentPose) < 0.5) {
				return true;
			} else {
				return false;
			}

		}

	}
}
