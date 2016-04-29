package edu.brown.cs.h2r.burlapcraft.action;

import java.util.ArrayList;
import java.util.List;

import machinelearning.PillarWekaClassifierWrapper;
import edu.brown.cs.h2r.burlapcraft.action.ActionPillarParameterizedOptionSimulated.SimpleParameterizedGroundedAction;
import edu.brown.cs.h2r.burlapcraft.helper.HelperNameSpace;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.GroundedAction;

public class ActionPillarParameterizedOptionLearnedSimulated extends ActionPillarParameterizedOptionSimulated {

	private PillarWekaClassifierWrapper classifier;
	private int[][][] map;
	
	public ActionPillarParameterizedOptionLearnedSimulated(String name,
			Domain domain, int[][][] map, PillarWekaClassifierWrapper classifier) {
		super(name, domain, map);
		this.map = map;
		this.classifier = classifier;
	}
	
	@Override
	public List<GroundedAction> getAllApplicableGroundedActions(State s) {
		List<GroundedAction> actions = new ArrayList<GroundedAction>();
		
		ObjectInstance agent = s.getFirstObjectOfClass(HelperNameSpace.CLASSAGENT);
		int currentItemID = agent.getIntValForAttribute(HelperNameSpace.ATSELECTEDITEMID);
//		if (currentItemID != 4) { //cobblestone ID = 4
//			return actions;
//		}
		//get inventoryBlocks
		int numBlocks = 0;
		List<ObjectInstance> invBlocks = s.getObjectsOfClass(HelperNameSpace.CLASSINVENTORYBLOCK);
		for (ObjectInstance invBlock : invBlocks) {
			if (invBlock.getIntValForAttribute(HelperNameSpace.ATBTYPE) == currentItemID) {
				numBlocks++;
			}
		}
		
		int optPillarHeight = -1;
		try {
			Instance instance = classifier.getInstanceFromData(map, s, 0, classifier.getFeatLength(), classifier.getAttrs());
			Instances testInstances = new Instances("TestInstances", classifier.getAttrs(), 1);
			testInstances.add(instance);
			instance.setDataset(testInstances);
			double optPillarHeightD = classifier.predict(instance);
			
			optPillarHeight = (int) optPillarHeightD;
			System.out.println(optPillarHeight);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int minPillarHeight = optPillarHeight;
		int maxPillarHeight = optPillarHeight;
		
		int curY = agent.getIntValForAttribute(HelperNameSpace.ATY);
		//for (int i = minPillarHeight; i < Math.min(maxPillarHeight, numBlocks); i++) {
			GroundedAction a = new SimpleParameterizedGroundedAction(this, 
					new String[]{
						new Integer(optPillarHeight).toString(),  //height of pillar
						new Integer(curY).toString(), //current height
						new Integer(0).toString() //number of iterations attempted
					});
			actions.add(a);
		//}
		
		return actions;
	}

}
