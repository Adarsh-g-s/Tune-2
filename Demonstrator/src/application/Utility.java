package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.sun.glass.ui.ClipboardAssistance;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Utility {
	public static String ARFF_LOCATION = "ARFF_LOCATION";
	public static String CLASSIFIER_LOCATION = "CLASSIFIER_LOCATION";
	public static String RESULTS_LOCATION = "RESULTS_LOCATION";
	private String classifierLocation;
	private String arffFolderLocation;
	private String resultLocation;
	private String classifiers;
	private String tempLocation;
	
	
	
	
	
	public String getTempLocation() {
		return tempLocation;
	}



	public void setTempLocation(String tempLocation) {
		this.tempLocation = tempLocation;
	}



	public String getClassifierLocation() {
		return classifierLocation;
	}



	public void setClassifierLocation(String classifierLocation) {
		this.classifierLocation = classifierLocation;
	}



	public String getClassifiers() {
		return classifiers;
	}



	public void setClassifiers(String classifiers) {
		this.classifiers = classifiers;
	}


	
	
	
	public String getArffFolderLocation() {
		return arffFolderLocation;
	}



	public void setArffFolderLocation(String arffFolderLocation) {
		this.arffFolderLocation = arffFolderLocation;
	}
	
	
	

	public String getResultLocation() {
		return resultLocation;
	}



	public void setResultLocation(String resultLocation) {
		this.resultLocation = resultLocation;
	}
	

	public void init(boolean writeToProp,String propVal,String lbl) {
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream("resources/demonstrator.properties");

			// load a properties file
			prop.load(input);
			System.out.println("classifiers: " + prop.getProperty("classifiers"));
			System.out.println("limit: " + prop.getProperty("limit"));
			System.out.println("Arff Location" + prop.getProperty("arffFolderLocation"));
			//Bug Here to Fix it
			if(writeToProp && propVal!=null && lbl!=null) {
			FileOutputStream out = new FileOutputStream("resources/demonstrator.properties");
			if(lbl.equalsIgnoreCase(CLASSIFIER_LOCATION))
				prop.setProperty("classifiersLocation", propVal);
			else if(lbl.equalsIgnoreCase(ARFF_LOCATION))
				prop.setProperty("arffFolderLocation", propVal);
			else if(lbl.equalsIgnoreCase(RESULTS_LOCATION)) {
				prop.setProperty("resultsLocation", propVal);
			}
			prop.store(out, null);
			out.close();
			}
			
			
			if(prop.getProperty("classifiersLocation")!=null) {
				setClassifierLocation(prop.getProperty("classifiersLocation"));
			}
			else {
				setClassifierLocation("");
			}
			
			if(prop.getProperty("resultsLocation")!=null) {
				setResultLocation(prop.getProperty("resultsLocation"));
			}
			else {
				setResultLocation("");
			}
			
			if(prop.getProperty("arffFolderLocation")!=null) {
				setArffFolderLocation(prop.getProperty("arffFolderLocation"));
			}
			else {
				setArffFolderLocation("");
			}
			setClassifiers(prop.getProperty("classifiers"));
			
			
			setTempLocation(prop.getProperty("tempDir"));
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
		//Populates the combo box list dynamically through properties
		public ObservableList<String> populateList() {
				init(false,null,null);
				String[] classifiers = getClassifiers().split(",");
				System.out.println(classifiers.toString());
				//obList = FXCollections.observableArrayList(classifiers);
				return FXCollections.observableArrayList(classifiers);
		}
	
		//Check file existence
		public boolean checkFile(String filePath) {
			File f = new File(filePath);
			if(f.exists() && !f.isDirectory()) { 
			    return true;
			}
			return false;
		}
		
}
