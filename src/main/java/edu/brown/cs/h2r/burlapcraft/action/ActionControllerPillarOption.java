package edu.brown.cs.h2r.burlapcraft.action;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.util.MovementInput;
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

		final Timer timer = new Timer();
		final ArrayList<Integer> iters = new ArrayList<Integer>(1);
		iters.add(0);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				MovementInput movement = new MovementInput();
				movement.jump = true;

				HelperActions.overrideMovement(movement);
				
				//if (HelperActions.getMinecraft().thePlayer.inventory.getCurrentItem() != null) {
					HelperActions.overrideUseItem();
				//}
				int val = iters.get(0);
				iters.set(0, val + 1);
				if (val > 5) {
					HelperActions.resetAllInputs();
					
					HelperActions.faceAhead();
					
					timer.cancel();
				}
			}
		}, 700, 400);
//		for (int i = 0; i < height; i++) {
//			long lStartTime = System.currentTimeMillis();
//			float y = HelperActions.getPlayerPosition().y;
//			HelperActions.jump();
//			float yPrime = HelperActions.getPlayerPosition().y;
//			while (yPrime <= y + .05) {
//				yPrime = HelperActions.getPlayerPosition().y;
//			}
//			while(y > yPrime + .05) {
//				yPrime = HelperActions.getPlayerPosition().y;
//			}
//			
//			long lEndTime = System.currentTimeMillis();
//
//			long difference = lEndTime - lStartTime;
//			System.out.println("length of jump " + difference);
//			HelperActions.placeBlock();
//		}
		
		return this.delayMS;
	}
	
	@Override
	public String toString() {
		return "pillaring action controller";
	}

}
