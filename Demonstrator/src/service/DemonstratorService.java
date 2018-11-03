package service;

import java.util.List;

import org.ovgu.de.trial.PersonDAO;

import Model.Person;
import Model.Results;

/**
 * @author subash
 *
 */
public interface DemonstratorService {

	//All the required method declaration will be written here...
	
	
	//Person object contains the respective Classifier required items like the eda file and log file required for preprocess
	//Note: I am not sure if the same methods with may as overload can be used directly. Yet to discover.
	
	/**
	 * @param p
	 * @param arffFileName
	 * @return
	 */
	public String preprocess(List<PersonDAO> p, String arffFileName, String phase);
	
	//Subjected to modifications
	//Needed items: The location of the arff file which is generated, the classifier that has come from the front end to use
	
	
	/**
	 * @param train
	 * @param test
	 * @param ClassifierName
	 * @param foldNum
	 * @return
	 */
	public String performClassification(String train,String test,String classifierName,int foldNum);
	
	
	//Testing an existing model for accurracy and stuffs
	
	
	/**
	 * @param testArffFile
	 * @param modelLocation
	 * @param classifier
	 * @return
	 */
	public List<Results> testModel(String testArffFile, String modelLocation,String classifier,boolean groundTruth);
	
}
