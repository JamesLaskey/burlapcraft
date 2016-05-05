package machinelearning;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.functions.Logistic;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import edu.brown.cs.h2r.burlapcraft.helper.HelperNameSpace;

public class PillarWekaClassifierWrapper {
	
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
	
	
	
	public int maxDungeonHeight = 15;
	private int maxPillarHeight = 0;
	private int dataSetSize = 0;
	private List<DungeonTrainExample> rawTrainExamples = new ArrayList<DungeonTrainExample>();
	
	private Instances training;
	private FastVector attrs;
	private int featLength;
	
	public Classifier classifier;
	
	public PillarWekaClassifierWrapper(int maxPillarHeight, List<DungeonTrainExample> training, Classifier classifier) {
		this.maxPillarHeight = maxPillarHeight;
		this.classifier = classifier;
		for (DungeonTrainExample example : training) {
			addDungeonTrainExample(example);
		}
		try {
			setupAttrs();
			buildClassifier();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public PillarWekaClassifierWrapper(String trainingString, Classifier classifier, int dungeonHeight) throws Exception {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(trainingString));
		this.training = (Instances) in.readObject();
		this.maxDungeonHeight = dungeonHeight;
		System.out.println("class index: " + this.training.classIndex());
		System.out.println("attribute length: " + this.training.numAttributes());
		in.close();
		setupAttrs();
		classifier.buildClassifier(this.training);
		this.classifier = classifier;
	}
	
	public PillarWekaClassifierWrapper(Instances training, Classifier classifier) {
		this.classifier = classifier;
		this.training = training;
		setupAttrs();
		try {
			classifier.buildClassifier(training);
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
	
	private static void addMapFeat(Instance instance, FastVector attrs, int featno, int[][][] map, int y, int x, int z) {
		if (y < 0 || y >= map.length || x < 0 || x >= map[y].length || z < 0 || z >= map[y][x].length) {
			instance.setValue((Attribute) attrs.elementAt(featno), 0);
		} else {
			instance.setValue((Attribute) attrs.elementAt(featno), map[y][x][z] == 0 ? 0 : 1);
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
		for (int y = curY-1; y < curY + 16; y++) {
			addMapFeat(instance, attrs, featno++, map, y, curX, curZ);
			//cardinal
			addMapFeat(instance, attrs, featno++, map, y, curX, curZ+1);
			addMapFeat(instance, attrs, featno++, map, y, curX+1, curZ);
			addMapFeat(instance, attrs, featno++, map, y, curX, curZ-1);
			addMapFeat(instance, attrs, featno++, map, y, curX-1, curZ);
			//diagonal
			addMapFeat(instance, attrs, featno++, map, y, curX+1, curZ+1);
			addMapFeat(instance, attrs, featno++, map, y, curX-1, curZ-1);
			addMapFeat(instance, attrs, featno++, map, y, curX+1, curZ-1);
			addMapFeat(instance, attrs, featno++, map, y, curX-1, curZ+1);
		}
		
		instance.setValue((Attribute) attrs.elementAt(featno), Integer.toString(pillarHeight));
		
		return instance;
	}
	
	public void setupAttrs() {
		attrs = new FastVector();
		
		featLength = (3 * 3 * 17) + 1; //3 by 3 by dungeonheight patch of the map
		for (int i = 0; i < featLength; i++) { 
			Attribute attr = new Attribute(i + "Numeric");
			attrs.addElement(attr);
		}
		FastVector classTypes = new FastVector();
		for (int i = 0; i < 15; i++) {
			classTypes.addElement(new Integer(i).toString());
		}
		Attribute classAttribute = new Attribute("theClass", classTypes);
		attrs.addElement(classAttribute);
	}
	
	private Instances generateTrainingInstances() {
		
		training = new Instances("TrainingData", attrs, dataSetSize);
		training.setClassIndex(featLength);
		
		for (DungeonTrainExample dungeon : rawTrainExamples) {
			Iterator<State> stateIter = dungeon.states.iterator();
			Iterator<Integer> heightIter = dungeon.pillarHeights.iterator();
			
			int dungeonExampleSize = dungeon.states.size();
			for (int i = 0; i < dungeonExampleSize; i++) {
				training.add(getInstanceFromData(dungeon.map, stateIter.next(), 
						heightIter.next(), featLength, attrs));
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
			System.out.println(e.toMatrixString());
			System.out.println(e.toSummaryString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public double predict(Instance test) throws Exception {
		return classifier.classifyInstance(test);
	}
	
	public Instances getTrainingInstances() {
		return this.training;
	}
	
	public static void main(String[] args) {
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(args[0]));
			Instances training = (Instances) in.readObject();
			in.close();

			//System.out.println(training);
			for (int i = 0; i < training.numInstances(); i++) {
				Instance inst = training.instance(i);
				if (inst.value(154) == 8) {
					System.out.println(inst);
				}
			}
			PillarWekaClassifierWrapper wrapper = new PillarWekaClassifierWrapper(training, new Logistic());
			
			try {
				in = new ObjectInputStream(new FileInputStream(args[1]));
				Instances test = (Instances) in.readObject();
				in.close();
			
				wrapper.runClassifier(test);
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
	}

	public int getFeatLength() {
		return featLength;
	}

	public FastVector getAttrs() {
		return attrs;
	}
}
