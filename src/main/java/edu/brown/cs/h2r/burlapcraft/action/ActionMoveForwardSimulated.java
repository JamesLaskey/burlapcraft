package edu.brown.cs.h2r.burlapcraft.action;

import java.util.ArrayList;
import java.util.List;

import edu.brown.cs.h2r.burlapcraft.domaingenerator.DomainGeneratorSimulated;
import edu.brown.cs.h2r.burlapcraft.helper.HelperNameSpace;
import edu.brown.cs.h2r.burlapcraft.helper.HelperPos;
import edu.brown.cs.h2r.burlapcraft.stategenerator.StateGenerator;

import burlap.oomdp.core.Domain;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.State;
import burlap.oomdp.core.TransitionProbability;

public class ActionMoveForwardSimulated extends ActionAgentSimulated {
	
	private int[][][] map;

	public ActionMoveForwardSimulated(String name, Domain domain, int[][][] map) {
		super(name, domain);
		this.map = map;
		System.out.println(map.length);
	}

	@Override
	State doAction(State s) {
		
		//get agent and current position
		ObjectInstance agent = s.getFirstObjectOfClass(HelperNameSpace.CLASSAGENT);
		int curX = agent.getIntValForAttribute(HelperNameSpace.ATX);
		int curY = agent.getIntValForAttribute(HelperNameSpace.ATY);
//		System.out.println(this.map.length);
//		System.out.println(curY);
		int curZ = agent.getIntValForAttribute(HelperNameSpace.ATZ);
		int rotDir = agent.getIntValForAttribute(HelperNameSpace.ATROTDIR);
		
		//get objects and their positions
		List<ObjectInstance> blocks = s.getObjectsOfTrueClass(HelperNameSpace.CLASSBLOCK);
		List<HelperPos> coords = new ArrayList<HelperPos>();
		for (ObjectInstance block : blocks) {
			int blockX = block.getIntValForAttribute(HelperNameSpace.ATX);
			int blockY = block.getIntValForAttribute(HelperNameSpace.ATY);
			int blockZ = block.getIntValForAttribute(HelperNameSpace.ATZ);
			coords.add(new HelperPos(blockX, blockY, blockZ));
		}
		
		//get resulting position
		HelperPos newPos = this.moveResult(curX, curY, curZ, rotDir, coords);
		
		//set the new position
		agent.setValue(HelperNameSpace.ATX, newPos.x);
		agent.setValue(HelperNameSpace.ATY, newPos.y);
		agent.setValue(HelperNameSpace.ATZ, newPos.z);
		
		//return the state we just modified
		return s;

	}
	
	protected HelperPos moveResult(int curX, int curY, int curZ, int direction, List<HelperPos> coords) {
		
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
		
		//make sure new position is valid (not a wall or off bounds)
		if(nx < 0 || nx >= length || nz < 0 || nz >= width ||  
			map[curY][nx][nz] >= 1){
			nx = curX;
			nz = curZ;
		}
		
		for(HelperPos coord : coords) {
			if (nx == coord.x && curY == coord.y && nz == coord.z) {
				nx = curX;
				nz = curZ;
			}
		}
		
		return new HelperPos(nx, curY, nz);
		
	}
	
	@Override
	public List<TransitionProbability> getTransitions(State s, String [] params){
		
		return this.deterministicTransition(s, params);
		
	}

}