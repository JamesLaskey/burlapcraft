package edu.brown.cs.h2r.burlapcraft.action;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.core.TransitionProbability;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.common.SimpleAction.SimpleDeterministicAction;
import edu.brown.cs.h2r.burlapcraft.helper.HelperActions;
import edu.brown.cs.h2r.burlapcraft.helper.HelperNameSpace;
import edu.brown.cs.h2r.burlapcraft.helper.HelperPos;
import edu.brown.cs.h2r.burlapcraft.stategenerator.StateGenerator;

public class ActionMoveForwardSimulated extends SimpleDeterministicAction {
	
	private int[][][] map;

	public ActionMoveForwardSimulated(String name, Domain domain, int[][][] map) {
		super(name, domain);
		this.map = map;
	}

	@Override
	protected State performActionHelper(State s, GroundedAction groundedAction) {
		
		StateGenerator.validate(s);
		//get agent and current position
		ObjectInstance agent = s.getFirstObjectOfClass(HelperNameSpace.CLASSAGENT);
		int curX = agent.getIntValForAttribute(HelperNameSpace.ATX);
		int curY = agent.getIntValForAttribute(HelperNameSpace.ATY);
		int curZ = agent.getIntValForAttribute(HelperNameSpace.ATZ);
		int rotDir = agent.getIntValForAttribute(HelperNameSpace.ATROTDIR);
		//get objects and their positions
		List<ObjectInstance> blocks = s.getObjectsOfClass(HelperNameSpace.CLASSBLOCK);
		List<HelperPos> coords = new ArrayList<HelperPos>();
		for (ObjectInstance block : blocks) {
			int blockX = block.getIntValForAttribute(HelperNameSpace.ATX);
			int blockY = block.getIntValForAttribute(HelperNameSpace.ATY);
			int blockZ = block.getIntValForAttribute(HelperNameSpace.ATZ);
			coords.add(new HelperPos(blockX, blockY, blockZ));
		}
		
		//get resulting position
		HelperPos newPos = this.moveResult(s, curX, curY, curZ, rotDir, coords);
		
		//set the new position
		agent.setValue(HelperNameSpace.ATX, newPos.x);
		agent.setValue(HelperNameSpace.ATY, newPos.y);
		agent.setValue(HelperNameSpace.ATZ, newPos.z);
		StateGenerator.validate(s);
		//return the state we just modified
		return s;
		
	}
	
	protected HelperPos moveResult(State s, int curX, int curY, int curZ, int direction, List<HelperPos> coords) {
		
		//first get change in x and z from direction using 0: south; 1: west; 2: north; 3: east
		int xdelta = 0;
		int zdelta = 0;
		if(direction == 0){
			zdelta = 1;
		}
		else if(direction == 1){
			xdelta = -1;
		}
		else if(direction == 2){
			zdelta = -1;
		}
		else{
			xdelta = 1;
		}
		
		int nx = curX + xdelta;
		int nz = curZ + zdelta;
		
		int length = this.map[curY].length;
		int width = this.map[curY][curX].length;
		
		//   :(
		ObjectInstance nearestBlockUnder = null;
		int nearestBlockUnderY = Integer.MIN_VALUE;
		boolean blockingBlock = false;
		List<ObjectInstance> blocks = s.getObjectsOfClass(HelperNameSpace.CLASSBLOCK);
		for (ObjectInstance block : blocks) {
			int blockX = block.getIntValForAttribute(HelperNameSpace.ATX);
			int blockY = block.getIntValForAttribute(HelperNameSpace.ATY);
			int blockZ = block.getIntValForAttribute(HelperNameSpace.ATZ);
			
			if (nx == blockX && nz == blockZ) {
				//in the column we will walk into
				if (blockY == curY || blockY == curY + 1) {
					//would smack head or feet into block
					blockingBlock = true;
				}
				
				if (blockY <= curY && (nearestBlockUnder == null || blockY > nearestBlockUnderY)) {
					nearestBlockUnder = block;
					nearestBlockUnderY = blockY;
				}
			}
		}
		
		if (nx < 0 || nx >= length || nz < 0 || nz >= width ) {
			return new HelperPos(curX, curY, curZ);
		}
		
		for (int y = 0; y < map.length; y++) {
			if (map[y][nx][nz] >= 1 && y <= curY) {
				nearestBlockUnderY = y;
			}
		}
		int ny = nearestBlockUnderY;
		
		
		
		//make sure new position is valid (not a wall or off bounds)
		if(blockingBlock || map[curY][nx][nz] >= 1 || curY + 1 >= map.length || map[curY + 1][nx][nz] >= 1){
			nx = curX;
			nz = curZ;
			ny = curY;
		}
		
		for(HelperPos coord : coords) {
			if (nx == coord.x && (curY == coord.y || curY + 1 == coord.y) && nz == coord.z) {
				nx = curX;
				nz = curZ;
			}
		}
		
		return new HelperPos(nx, ny, nz);
		
	}
	
	@Override
	public boolean applicableInState(State s, GroundedAction groundedAction) {
		ObjectInstance agent = s.getFirstObjectOfClass(HelperNameSpace.CLASSAGENT);
		int ax = agent.getIntValForAttribute(HelperNameSpace.ATX);
		int ay = agent.getIntValForAttribute(HelperNameSpace.ATY);
		int az = agent.getIntValForAttribute(HelperNameSpace.ATZ);
		List<ObjectInstance> blocks = s.getObjectsOfClass(HelperNameSpace.CLASSBLOCK);
		for (ObjectInstance block : blocks) {
			if (HelperActions.blockIsOneOf(Block.getBlockById(block.getIntValForAttribute(HelperNameSpace.ATBTYPE)), HelperActions.dangerBlocks)) {
				int dangerX = block.getIntValForAttribute(HelperNameSpace.ATX);
				int dangerY = block.getIntValForAttribute(HelperNameSpace.ATY);
				int dangerZ = block.getIntValForAttribute(HelperNameSpace.ATZ);
				if ((ax == dangerX) && (ay - 1 == dangerY) && (az == dangerZ) || (ax == dangerX) && (ay == dangerY) && (az == dangerZ)) {
					return false;
				}
			}
		}
		return true;
	}

}
