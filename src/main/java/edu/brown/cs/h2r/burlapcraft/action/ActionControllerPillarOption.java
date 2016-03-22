package edu.brown.cs.h2r.burlapcraft.action;

import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.environment.Environment;
import edu.brown.cs.h2r.burlapcraft.helper.HelperActions;

public class ActionControllerPillarOption implements ActionController {

	protected int delayMS;
	protected Environment environment;
	protected int height = 3;
	
	public ActionControllerPillarOption(int delayMS, Environment e) {
		this.delayMS = delayMS;
		this.environment = e;
	}
	
	@Override
	public int executeAction(GroundedAction ga) {
		
		System.out.println("\n*********************************************** Pillar\n");
		HelperActions.faceDown();
		for (int i = 0; i < height; i++) {
			long lStartTime = System.currentTimeMillis();
			float y = HelperActions.getPlayerPosition().y;
			HelperActions.jump();
			float yPrime = HelperActions.getPlayerPosition().y;
			while (yPrime <= y + .05) {
				yPrime = HelperActions.getPlayerPosition().y;
			}
			while(y > yPrime + .05) {
				yPrime = HelperActions.getPlayerPosition().y;
			}
			
			long lEndTime = System.currentTimeMillis();

			long difference = lEndTime - lStartTime;
			System.out.println("length of jump " + difference);
			HelperActions.placeBlock();
		}
		
		HelperActions.faceAhead();
		
		return this.delayMS;
	}

}
