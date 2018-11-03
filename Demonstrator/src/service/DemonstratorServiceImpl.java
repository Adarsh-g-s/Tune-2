package service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ovgu.de.classifier.boss.BOSS;
import org.ovgu.de.classifier.functions.SMO;
import org.ovgu.de.classifier.saxvsm.SAXVSM;
import org.ovgu.de.classifier.utility.ClassifierTools;
import org.ovgu.de.trial.IndividualPrediction;
import org.ovgu.de.trial.PersonDAO;
import org.ovgu.de.trial.Phase2Results;
import org.ovgu.de.trial.TrialPhaseSegregrator;
import org.ovgu.de.utils.ArffGenerator;

import Model.Person;
import Model.Results;
import application.Utility;
import weka.classifiers.meta.RotationForest;
import weka.core.Instances;

/**
 * @author subash
 *
 */

public class DemonstratorServiceImpl implements DemonstratorService {

	// locations
	private String resultsLocation;
	private String arffFolderLocation;
	private String modelClassifierLocation;

	// Static Strings
	private static final String phase1 = "phase-1";
	private static final String phase2 = "phase-2";
	
	

	@Override
	public String preprocess(List<PersonDAO> p, String arffFileName, String phase) {

		init();
		TrialPhaseSegregrator tph = new TrialPhaseSegregrator();
		String msg = null;

		if (p != null) {

			if (phase.equalsIgnoreCase(phase1)) {

				if (p.size() == 1) {
					// Single
					try {
						msg = tph.preprocessAndGenerateArffForPhase1(p.get(0).getUnisensFolderName(),
								p.get(0).getLogFileName(), arffFolderLocation + arffFileName,
								p.get(0).isStartedAfterOneMinute());
					} catch (IOException e) {

						msg = e.getMessage();
					}

				} else {
					// Multiple
					try {
						msg = tph.preprocessAndGenerateArffForMultipleForP1(arffFolderLocation + arffFileName, p);
					} catch (IOException e) {

						msg = e.getMessage();
					}

				}
			} else if (phase.equalsIgnoreCase(phase2)) {
				if (p.size() == 1) {
					// Single
					try {
						msg = tph.preprocessAndGenerateArffForPhase2(p.get(0).getUnisensFolderName(),
								p.get(0).getLogFileName(), arffFolderLocation + arffFileName,
								p.get(0).isStartedAfterOneMinute());
					} catch (IOException e) {

						msg = e.getMessage();
					}

				} else {
					// Multiple
					try {
						msg = tph.preprocessAndGenerateArffForMultipleForP2(arffFolderLocation + arffFileName, p);
					} catch (IOException e) {

						msg = e.getMessage();
					}

				}
			}
		}

		return msg;
	}

	private void init() {
		Utility util = new Utility();
		util.init(false, null, null);
		arffFolderLocation = util.getArffFolderLocation();
		resultsLocation = util.getResultLocation();
		modelClassifierLocation = util.getClassifierLocation();
	}

	@Override
	public String performClassification(String train, String test, String classifierName, int foldNum) {
		init();
		List<String> msgsList = new ArrayList<String>();
		SAXVSM vsm = new SAXVSM();
		BOSS boss = new BOSS();
		SMO svm = new SMO();
		float sumAcc = 0f;
		float meanAcc = 0f;
		float diffSumMean = 0f;
		double std = 0d;
		RotationForest rotf = new RotationForest();
		// the model will be saved as <classifierSaveLoc>+<modelName>.model
		StringBuffer messages = new StringBuffer();
		String msg = null;
		try {
			System.out.println("Model class location " + modelClassifierLocation);
			System.out.println("results location " + resultsLocation);
			System.out.println("Arff folder Location" + arffFolderLocation);

			for (int i = 1;i<=foldNum;i++) {
			
			// Ideally a case should onto the backend api on the request sent from the UI
			if (classifierName.equalsIgnoreCase("sax-vsm")) {
				msg = vsm.buildClassifierAndSave(ClassifierTools.loadData(train), ClassifierTools.loadData(test),
						modelClassifierLocation, classifierName, i, resultsLocation);

			} else if (classifierName.equalsIgnoreCase("boss")) {
				msg = boss.buildClassifierAndSave(ClassifierTools.loadData(train), ClassifierTools.loadData(test),
						modelClassifierLocation, classifierName, i, resultsLocation);
			}

			else if (classifierName.equalsIgnoreCase("rotf")) {
				msg = rotf.buildClassifierAndSave(ClassifierTools.loadData(train), ClassifierTools.loadData(test),
						modelClassifierLocation, classifierName, i, resultsLocation);
			}
			
			else if (classifierName.equalsIgnoreCase("svm-rbf")) {
				msg = svm.buildClassifierAndSave(ClassifierTools.loadData(train), ClassifierTools.loadData(test),
						modelClassifierLocation, classifierName, i, resultsLocation);
			}
			messages.append(msg);
			String[] msgs = msg.split("\n");
			if(msgs[3]!=null)
				msgsList.add(msgs[3].split(":")[1]);
			}
			
			if(!msgsList.isEmpty()) {
			for(String acc:msgsList) {
				sumAcc+= Float.valueOf(acc);
			}
			meanAcc = sumAcc/msgsList.size();
			
			}
			
			//Calc Std on accuracy
			for(String acc:msgsList) {
				diffSumMean+= Math.pow(Float.valueOf(acc) - meanAcc,2);
			}
			
			std = Math.sqrt(diffSumMean / (foldNum-1));
			
			messages.append("The average Accuracy of repeated folds " + foldNum + " is: " + meanAcc + "\n");
			messages.append("The Standard Deviation on the accuracy is  " + std + "\n");
			
		
		} catch (IOException e) {

			e.printStackTrace();
		}

		return messages.toString();
	}

	@Override
	public List<Results> testModel(String testArffFile, String modelLocation, String classifier,boolean groundTruth) {
		init();
		String msg = null;
		System.out.println("Came Here for testModel - 0");
		List<Results> msgs =new ArrayList<Results>();
		SAXVSM saxvsm = new SAXVSM();
		SMO svm = new SMO();
		BOSS boss = new BOSS();
		RotationForest rotf = new RotationForest();
		List<Phase2Results> ph2res = new ArrayList<Phase2Results>();
		List<String> tweets = new ArrayList<String>();
		File[] files = new File(testArffFile).listFiles();
		Phase2Results phr = null;
		System.out.println("Came Here for testModel");
		try {
			System.out.println("Model class location " + modelClassifierLocation);
			System.out.println("results location " + resultsLocation);
			System.out.println("Arff folder Location" + arffFolderLocation);
						
			BufferedReader br = new BufferedReader(new FileReader(testArffFile + "//" + "tweetId.txt"));
			String line = null;
			while ((line = br.readLine()) != null) {
				tweets.add(line);
			}
				 br.close();
			
			//iterate the files in a directory and execute at each and do something.
			
			for(File file:files) {
				System.out.println("File is  " + file.getName());
				if(!file.isDirectory()) {

					//String[] fNameType= file.getName().split("-");
					if(file.getName().equalsIgnoreCase("senti.arff") || file.getName().equalsIgnoreCase("rel.arff") || file.getName().equalsIgnoreCase("fact.arff")) {
						System.out.println("The Name of the file is: " + file.getName());
						
						//Make a Apply Classifier call
						if (classifier.equalsIgnoreCase("sax-vsm")) {
							try {
								phr = new Phase2Results();
								phr = saxvsm.applyClassifier(ClassifierTools.loadData(file), modelLocation, groundTruth);
								//ph2res.add(phr);
							} catch (Exception e) {
								e.printStackTrace();
							}

						} else if (classifier.equalsIgnoreCase("boss")) {
							try {
								phr = new Phase2Results();
								phr = boss.applyClassifier(ClassifierTools.loadData(file), modelLocation, groundTruth);
								//ph2res.add(phr);
							} catch (Exception e) {

								e.printStackTrace();
							}
						} else if (classifier.equalsIgnoreCase("rotf")) {
							try {
								phr = new Phase2Results();
								phr = rotf.applyClassifier(ClassifierTools.loadData(file), modelLocation, groundTruth);
								ph2res.add(phr);
							} catch (Exception e) {

								e.printStackTrace();
							}
						}
							
							else if (classifier.equalsIgnoreCase("svm-rbf")) {
								try {
									phr = new Phase2Results();
									phr = svm.applyClassifier(ClassifierTools.loadData(file), modelLocation, groundTruth);
									//ph2res.add(phr);
								} catch (Exception e) {

									e.printStackTrace();
								}
					}
						
						 Results res = generateOutput(tweets,phr,file.getName());	
						//List<String> messages = new ArrayList<String>();
						msgs.add(res);
				}
					
			}
			
				

				
			}
			
		} catch (Exception e) {

			e.printStackTrace();
		}

		return msgs;
	}
	
	private Results generateOutput(List<String> tweets,Phase2Results phr,String FileName) {
		//List<String> messages = new ArrayList<String>();
		StringBuffer msgAppender = new StringBuffer();
		Results res = new Results();
		String fileName[] = FileName.split("[.]+");
		String name = null;
		if(fileName[0]!=null) {
			name = fileName[0];
		}	
		msgAppender.append("The File being used for classification is: " + FileName + "\n\n");
		msgAppender.append("TweetId \t\t\t\t Prediction Value \t\n");
		
			List<IndividualPrediction> pred = phr.getPredictionList();
			int count=0;
			int hardCount=0;
			int easyCount=0;
			for(IndividualPrediction p:pred) {
				msgAppender.append(tweets.get(count) + "\t\t" + p.predictedValue + "\t\t\n");
				count+=1;
				
				//Getting the easy and hard Count
				if(p.predictedValue.equalsIgnoreCase("easy")) {
					easyCount+=1;
				}
				else if(p.predictedValue.equalsIgnoreCase("hard")) {
					hardCount+=1;
				}
				
				}
			
			System.out.println("Easy Count -- and -- File is: " + easyCount + "==" + name);
			System.out.println("Hard Count -- and -- File is: " + hardCount + "==" + name);
			
			//messages.add(msgAppender.toString());
			if(name!=null) {
				if(name.equalsIgnoreCase("fact")) {
					res.setFactEasy(name + "-Easy-" + easyCount);
					res.setFactHard(name + "-Hard-" + hardCount);
					
				}
				else if(name.equalsIgnoreCase("senti")) {
					
					res.setSentiEasy(name + "-Easy-" + easyCount);
					res.setSentiHard(name + "-Hard-" + hardCount);
					
				}
				else if(name.equalsIgnoreCase("rel")) {
					res.setRelEasy(name + "-Easy-" + easyCount);
					res.setRelHard(name + "-Hard-" + hardCount);
					
				}
			}
		
		res.setMsgs(msgAppender.toString());
		
		return res;
	}
	
}