package machinelearning;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import edu.brown.cs.h2r.burlapcraft.helper.HelperNameSpace;

public class WekaClassifierWrapper {
	
	public static class DungeonTrainExample {
		
		public int[][][] map;
		public List<State> states;
		public List<Integer> pillarHeights;
		
		public DungeonTrainExample(int[][][] map, List<State> states, List<Integer> pillarHeights) {
			this.map = map;
			this.states = states;
			this.pillarHeights = pillarHeights;
		}
	}
	
	
	
	private int maxDungeonHeight = 0;
	private int maxPillarHeight = 0;
	private int dataSetSize = 0;
	private List<DungeonTrainExample> rawTrainExamples = new ArrayList<DungeonTrainExample>();
	
	private Instances training;
	private FastVector attrs;
	private int featLength;
	
	private Classifier classifier;
	
	public WekaClassifierWrapper(int maxPillarHeight, List<DungeonTrainExample> training, Classifier classifier) {
		this.maxPillarHeight = maxPillarHeight;
		this.classifier = classifier;
		for (DungeonTrainExample example : training) {
			addDungeonTrainExample(example);
		}
		try {
			buildClassifier();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addDungeonTrainExample(DungeonTrainExample example) {
		if (example.map.length > maxDungeonHeight) {
			maxDungeonHeight = example.map.length;
		}
		dataSetSize += example.states.size();
		rawTrainExamples.add(example);
	}
	
	private static void addMapFeat(Instance instance, FastVector attrs, int featno, int[][][] map, int x, int y, int z) {
		if (y < 0 || y >= map.length || x < 0 || x >= map[y].length || z < 0 || z >= map[y][x].length) {
			instance.setValue((Attribute) attrs.elementAt(featno), 0);
		} else {
			instance.setValue((Attribute) attrs.elementAt(featno), map[y][x][z]);
		}
	}
	
	public static Instance getInstanceFromData(int[][][] map, State s, int pillarHeight, int featLength,
			FastVector attrs) {
		SparseInstance instance = new SparseInstance(featLength);
		
		ObjectInstance agent = s.getFirstObjectOfClass(HelperNameSpace.CLASSAGENT);
		int curX = agent.getIntValForAttribute(HelperNameSpace.ATX);
		int curY = agent.getIntValForAttribute(HelperNameSpace.ATY);
		int curZ = agent.getIntValForAttribute(HelperNameSpace.ATZ);
		int rotDir = agent.getIntValForAttribute(HelperNameSpace.ATROTDIR);
		
		int featno = 0;
		instance.setValue((Attribute) attrs.elementAt(featno++), curY);
		for (int y = 0; y < map.length; y++) {
			addMapFeat(instance, attrs, featno++, map, y, curX, curZ);
			if(rotDir == 0){
				addMapFeat(instance, attrs, featno++, map, y, curX, curZ+1);
			}
			else if(rotDir == 1){
				addMapFeat(instance, attrs, featno++, map, y, curX-1, curZ);
			}
			else if(rotDir == 2){
				addMapFeat(instance, attrs, featno++, map, y, curX, curZ-1);
			}
			else{
				addMapFeat(instance, attrs, featno++, map, y, curX+1, curZ);
			}
		}
		
		instance.setValue((Attribute) attrs.elementAt(featno), pillarHeight);
		System.out.println(featno);
		
		return instance;
	}
	
	private Instances generateTrainingInstances() {
		attrs = new FastVector();
		
		int numFeats = (2 * maxDungeonHeight) + 1; //3 by 3 by dungeonheight patch of the map
		for (int i = 0; i < numFeats; i++) { 
			Attribute attr = new Attribute(i + "Numeric");
			attrs.addElement(attr);
		}
		System.out.println(numFeats);
		FastVector classTypes = new FastVector();
		for (int i = 1; i < maxPillarHeight; i++) {
			classTypes.addElement(new Integer(i).toString());
		}
		Attribute classAttribute = new Attribute("theClass", classTypes);
		attrs.addElement(classAttribute);
		
		training = new Instances("TrainingData", attrs, dataSetSize);
		training.setClassIndex(numFeats);
		
		for (DungeonTrainExample dungeon : rawTrainExamples) {
			Iterator<State> stateIter = dungeon.states.iterator();
			Iterator<Integer> heightIter = dungeon.pillarHeights.iterator();
	
			for (int i = 0; i < dataSetSize; i++) {
				training.add(getInstanceFromData(dungeon.map, stateIter.next(), 
						heightIter.next(), numFeats, attrs));
			}
		}

		return training;
	}
	
	public void buildClassifier() throws Exception {
		classifier.buildClassifier(generateTrainingInstances());
	}
	
	public void runClassifier(Instances test) {
		try {
			Evaluation e = new Evaluation(training);
			e.evaluateModel(classifier, test);
			System.out.println(e.toSummaryString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
