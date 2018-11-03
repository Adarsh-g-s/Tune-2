package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import org.controlsfx.control.CheckComboBox;

import Model.Person;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import service.DemonstratorService;
import service.DemonstratorServiceImpl;

public class SingleUserController implements Initializable{
	@FXML
	private Button edaBtn;
	
	@FXML
	private Button execute;
	
	@FXML
	private Button logFileBtn;
	
	@FXML
	private Label edaFilename;
	
	@FXML
	private Label logFileName;
	
	@FXML
	private CheckBox checkAfter1min;
	
	/*@FXML
	private ComboBox<String> comboBox;*/
	
	Utility util;


	@FXML
	private CheckComboBox<String> checkComboBox;
	
	@FXML
	private TextArea loggingTextOutput;
	
	
	private String edaFileAbPath;
	private String logFileAbPath;
	private String classifierName;
	
	//Classifiers
	ObservableList<String> classifiers;
	//Person List
	List<Person> pList = new ArrayList<Person>();
	
	//Demonstrator Service
	DemonstratorService service;
	boolean startAfter1min;
	
	
	@FXML
	public void edaBtnAction(ActionEvent event) {
		//Write a directory chooser
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("EDA Folder Location");
		//File defaultDirectory = new File("c://");
		//chooser.setInitialDirectory(defaultDirectory);
		File edaFile = chooser.showDialog(null);
		
		//Write a file chooser
		/*FileChooser fc = new FileChooser();
		fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("EDA Binary File","*.bin"));
		File edaFile = fc.showOpenDialog(null);*/
		//Check if the file is selected
		if(edaFile!=null) {
			//lets add the file to the eda label
			edaFilename.setText(edaFile.getName());
			loggingTextOutput.appendText("EDA File Added is: " + edaFile.getName() + "\n");
			edaFileAbPath = edaFile.getAbsolutePath();
		
			if(logFileAbPath!=null){
				//May be there are both items to be added
				Person p = new Person();
				p.setEdaFileAbsPath(edaFileAbPath);
				p.setLogFileAbsPath(logFileAbPath);
				pList.add(p);
				
			}	
			
			//Enable Combo Box
			checkComboBox.setDisable(false);
			execute.setDisable(false);
		}
		
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR!!!");
			alert.setHeaderText(null);
			alert.setContentText("Looks like you have not selected any EDA file!!!");
			alert.showAndWait();
			loggingTextOutput.appendText("WARNING -- Looks like you have not selected any EDA file!!!\n");
			
		}
		
		
		
	}
	
	@FXML
	public void logFileBtnAction(ActionEvent event) {
		//Write a file chooser
				FileChooser fc = new FileChooser();
				fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Log File","*.*"));
				File logFile = fc.showOpenDialog(null);
				//Check if the file is selected
				if(logFile!=null) {
					
					//lets add the file to the eda label
					logFileName.setText(logFile.getName());
					loggingTextOutput.appendText("Log File Added is: " + logFile.getName() + "\n");
					logFileAbPath = logFile.getAbsolutePath();

					if(edaFileAbPath!=null){
						//May be there are both items to be added
						Person p = new Person();
						p.setEdaFileAbsPath(edaFileAbPath);
						p.setLogFileAbsPath(logFileAbPath);
						pList.add(p);						
					}
					
					//Enable EDA Button
					edaBtn.setDisable(false);
					
					
				}
				
				else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("ERROR!!!");
					alert.setHeaderText(null);
					alert.setContentText("Looks like you have not selected any Log file!!!");
					alert.showAndWait();
					loggingTextOutput.appendText("WARNING -- Looks like you have not selected any Log file!!! \n");
					
				}
		
	}
	
	
	ObservableList<String> obList;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		util = new Utility();
		obList = util.populateList();
		//comboBox.setItems(obList);
		checkComboBox.getItems().addAll(obList);
		 checkComboBox.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
	            @SuppressWarnings("unchecked")
				@Override public void onChanged(ListChangeListener.Change<? extends String> change) {
	                
	                while (change.next()) {
	                    System.out.println("============================================");
	                    System.out.println("Change: " + change);
	                    System.out.println("Added sublist " + change.getAddedSubList());
	                    System.out.println("Removed sublist " + change.getRemoved());
	                    System.out.println("List " + change.getList());
	                    System.out.println("Added " + change.wasAdded() + " Permutated " + change.wasPermutated() + " Removed " + change.wasRemoved() + " Replaced "
	                            + change.wasReplaced() + " Updated " + change.wasUpdated());
	                    System.out.println("============================================");
	                }
	             classifiers  =  (ObservableList<String>) change.getList();
	            }
	        });
	}
	
	@FXML
	public void comboAction(ActionEvent event) {
		//classifierName = comboBox.getValue();
		System.out.println("Classifier Selected is: " + classifierName);
		loggingTextOutput.appendText("You have selected - " + classifierName + "\n");
		
		//Enable Execute By a check
		if(obList.contains(classifierName) && (edaFileAbPath!=null) && (logFileAbPath!=null)) {
			execute.setDisable(false);
		}
	}
	
	@FXML
	public void executeAction(ActionEvent event) {
		
		for(Person p : pList) {
			System.out.println("Selected Items are: " + " LogFile -- " + p.getLogFileAbsPath() + " EDAFolderLocation -- " + p.getEdaFileAbsPath());
		}
		//Call a service to call the preprocess and the classifier HERE!!!
		System.out.println("The val is : " + startAfter1min);
		if(checkAfter1min.isSelected()) {
			System.out.println("I came here !!!");
			startAfter1min = true;
		}
		else {
			startAfter1min = false;
		}
		
		System.out.println("The val is : " + startAfter1min);
		
		//Perform Preprocess
		service = new DemonstratorServiceImpl();
		//boolean rc = service.processAndGenerateArffSingle(pList, startAfter1min, false);
		/*if(rc) {
			loggingTextOutput.appendText("Arff is generated Successfully");
		}
		else {
			loggingTextOutput.appendText("There is an error please check it");
		}*/
		
		if(classifiers!=null) {
		for(String classifier:classifiers) {
			System.out.println(classifier);
		}
		}
		//Error on Text area if no classifier is choosen
		if(null == classifiers) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR!!!");
			alert.setHeaderText(null);
			alert.setContentText("Classifier is not choosen!!!");
			alert.showAndWait();
			loggingTextOutput.appendText("Classifier is not choosen!!!\n");
		}
		else if(classifiers.size() == 0) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR!!!");
			alert.setHeaderText(null);
			alert.setContentText("Classifier is not choosen!!!");
			alert.showAndWait();
			loggingTextOutput.appendText("Classifier is not choosen!!!\n");
		}
		
		
		//After all the action is performed lets disable again for no mess.Commented this for now
		//execute.setDisable(true);
		//comboBox.setDisable(true);
		//edaBtn.setDisable(true);
		
		
	}

	public CheckComboBox<String> getCheckComboBox() {
		return checkComboBox;
	}

	public void setCheckComboBox(CheckComboBox<String> checkComboBox) {
		this.checkComboBox = checkComboBox;
	}
	
}
