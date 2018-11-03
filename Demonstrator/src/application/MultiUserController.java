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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import service.DemonstratorService;

public class MultiUserController implements Initializable{
	
	@FXML
	private Button multiEdaBtn;
	
	@FXML
	private Button multiExecute;
	
	@FXML
	private Button multiLogFileBtn;
	
	@FXML
	private Label multiEdaFilename1;
	
	@FXML
	private Label multiEdaFilename2;
	
	@FXML
	private Label multiEdaFilename3;
	
	@FXML
	private Label multiLogFileName1;
	
	@FXML
	private Label multiLogFileName2;
	
	@FXML
	private Label multiLogFileName3;
	
	/*@FXML
	private ComboBox<String> comboBox;*/
	
	@FXML
	private CheckComboBox<String> checkComboBox1;
	
	@FXML
	private TextArea multiLoggingTextOutput;
	
	private String edaFileAbPath;
	private String logFileAbPath;
	
	SingleUserController singleUserController;
	
	
	//Classifiers
	ObservableList<String> classifiers;
	//Person List
	List<Person> pList = new ArrayList<Person>();
	
	//Demonstrator Service
	DemonstratorService service;
	
	//Utility
	Utility util;
	
	@FXML
	public void edaBtnAction(ActionEvent event) {
		int buttonNumber=0;
		String buttonEDA = ((Button) event.getSource()).getText();
		//System.out.println("Button --> " + buttonEDA);
		if(buttonEDA.split("-")[1]!=null) {
			buttonNumber = Integer.valueOf(buttonEDA.split("-")[1]);	
			//System.out.println("Button " + buttonNumber);
		}
		
		
		
		//Write a file chooser
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("EDA Binary File","*.bin"));
		File edaFile = fc.showOpenDialog(null);
		//Check if the file is selected
		if(edaFile!=null) {
			//lets add the file to the eda label
			checkAndAddLabel(buttonNumber,edaFile.getName(),true);
			multiLoggingTextOutput.appendText("EDA File Added is: " + edaFile.getName() + "\n");
			edaFileAbPath = edaFile.getAbsolutePath();
		
			if(logFileAbPath!=null){
				//May be there are both items to be added
				Person p = new Person();
				p.setEdaFileAbsPath(edaFileAbPath);
				p.setLogFileAbsPath(logFileAbPath);
				if(!pList.contains(p)) {
					pList.add(p);
				}
				
				
			}	
			
			//Enable Combo Box
			checkComboBox1.setDisable(false);
			multiExecute.setDisable(false);
		}
		
		else {
			multiLoggingTextOutput.appendText("WARNING -- Looks like you have not selected any EDA file!!!\n");
			
		}
		
		
		
	}
	
	@FXML
	public void logFileBtnAction(ActionEvent event) {
		//Write a file chooser
				FileChooser fc = new FileChooser();
				fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Log File","*.*"));
				File logFile = fc.showOpenDialog(null);
				int buttonNumber=0;
				String buttonEDA = ((Button) event.getSource()).getText();
				//System.out.println("Button --> " + buttonEDA);
				if(buttonEDA.split("-")[1]!=null) {
					buttonNumber = Integer.valueOf(buttonEDA.split("-")[1]);	
					//System.out.println("Button " + buttonNumber);
				}
				
				//Check if the file is selected
				if(logFile!=null) {
					checkAndAddLabel(buttonNumber,logFile.getName(),false);
					//lets add the file to the eda label
					//logFileName.setText(logFile.getName());
					multiLoggingTextOutput.appendText("Log File Added is: " + logFile.getName() + "\n");
					logFileAbPath = logFile.getAbsolutePath();

					if(edaFileAbPath!=null){
						//May be there are both items to be added
						Person p = new Person();
						p.setEdaFileAbsPath(edaFileAbPath);
						p.setLogFileAbsPath(logFileAbPath);
						if(!pList.contains(p)) {
							pList.add(p);
						}						
					}
					
					//Enable EDA Button
					//edaBtn.setDisable(false);
					
					
				}
				
				else {
					multiLoggingTextOutput.appendText("WARNING -- Looks like you have not selected any Log file!!! \n");
					
				}
		
	}
	
	
	
	
	@FXML
	public void executeAction(ActionEvent event) {
		
		for(Person p : pList) {
			System.out.println("Selected Items are: " + " LogFile -- " + p.getLogFileAbsPath() + " EDAFile -- " + p.getEdaFileAbsPath());
		}
		//Call a service to call the preprocess and the classifier HERE!!!
		
		if(classifiers!=null) {
		for(String classifier:classifiers) {
			System.out.println(classifier);
		}
		}
		//Error on Text area if no classifier is choosen
		if(null == classifiers) {
			multiLoggingTextOutput.appendText("Classifier is not choosen!!!\n");
		}
		else if(classifiers.size() == 0) {
			multiLoggingTextOutput.appendText("Classifier is not choosen!!!\n");
		}
		
		
		//After all the action is performed lets disable again for no mess.Commented this for now
		//execute.setDisable(true);
		//comboBox.setDisable(true);
		//edaBtn.setDisable(true);
		
		
	}

	ObservableList<String> checkList;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		util = new Utility();
		checkList = util.populateList();
		checkComboBox1.getItems().addAll(checkList);
		
		//Get the selected classifiers
		checkComboBox1.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
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
	
	
	private void checkAndAddLabel(int buttonNumber,String fileName,boolean isEDA) {
		if(isEDA) {
			switch(buttonNumber) {
				case 1: multiEdaFilename1.setText(fileName);break;
				case 2: multiEdaFilename2.setText(fileName);break;
				case 3: multiEdaFilename3.setText(fileName);break;
				default: System.out.println("There is nothing to add");break;
			}
		}
		else {
			switch(buttonNumber) {
			case 1: multiLogFileName1.setText(fileName);break;
			case 2: multiLogFileName2.setText(fileName);break;
			case 3: multiLogFileName3.setText(fileName);break;
			default: System.out.println("There is nothing to add");break;
		}
			
		}
	}
}
