package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.controlsfx.control.CheckComboBox;
import org.ovgu.de.trial.PersonDAO;

import Model.Person;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import service.DemonstratorService;
import service.DemonstratorServiceImpl;

public class PreprocessController implements Initializable{
	
	@FXML
	private Button multiEdaBtn;
	
	@FXML
	private Button multiExecute;
	
	/*@FXML 
	RadioButton phase1;
	
	@FXML 
	RadioButton phase2;*/
	
	@FXML
	private Button multiLogFileBtn;
	
	@FXML
	private TextField arffName;
	
	@FXML
	private CheckBox checkAfter1min1;
	
	@FXML
	private CheckBox checkAfter1min2;
	
	@FXML
	private CheckBox checkAfter1min3;
	
	@FXML
	private CheckBox checkAfter1min4;
	
	@FXML
	private CheckBox checkAfter1min5;
	
	@FXML
	private CheckBox checkAfter1min6;
	
	@FXML
	private CheckBox checkAfter1min7;
	
	@FXML
	private CheckBox checkAfter1min8;
	
	@FXML
	private CheckBox checkAfter1min9;
	
	@FXML
	private CheckBox checkAfter1min10;
	
	/*
	 * If Label is required uncomment and use
	 * 
	 * 
	 * List<Label> labels;
	 * 
	*/
	/*@FXML
	private ComboBox<String> comboBox;*/
	
	@FXML
	private TextArea multiLoggingTextOutput;
	
	@FXML
	private CheckBox phase1;
	
	@FXML
	private CheckBox phase2;
	
	private String edaFileAbPath;
	private String logFileAbPath;
	private boolean startAfter1min;
	
	SingleUserController singleUserController;
	
	private int edaBtnNum;
	private int logBtnNum;
	
	private String msg;
	
	private static final String PHASE1 = "phase-1";
	private static final String PHASE2 = "phase-2";
	
	
	//Classifiers
	ObservableList<String> classifiers;
	//Person List
	List<Person> pList;
	Map<Person,Boolean> pMap = new HashMap<Person,Boolean>();
	//Demonstrator Service
	DemonstratorService service;
	
	//Utility
	Utility util;
	
	@FXML
	public void edaBtnAction(ActionEvent event) {
		//int buttonNumber=0;
		String buttonEDA = ((Button) event.getSource()).getText(); 
		//System.out.println("Button --> " + buttonEDA);
		if(buttonEDA.split("-")[1]!=null) {
			edaBtnNum = Integer.valueOf(buttonEDA.split("-")[1]);	
			//System.out.println("Button " + buttonNumber);
		}
		
		//Write a directory chooser
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("EDA Folder Location");
		//File defaultDirectory = new File("c://");
		//chooser.setInitialDirectory(defaultDirectory);
		File edaFile = chooser.showDialog(null);
		
		
		/*//Write a file chooser
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("EDA Binary File","*.bin"));
		File edaFile = fc.showOpenDialog(null);*/
		//Check if the file is selected
		if(edaFile!=null) {
			//lets add the file to the eda label
			//checkAndAddLabel(buttonNumber,edaFile.getName(),true);
			multiLoggingTextOutput.appendText("EDA File Added is: " + edaFile.getName() + "\n");
			edaFileAbPath = edaFile.getAbsolutePath();
		
			if(logFileAbPath!=null){
				if(edaBtnNum!=logBtnNum) {
					//Alert an error on execution
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("ERROR!!!");
					alert.setHeaderText(null);
					alert.setContentText("Looks like EDA file and log File are not Same, Please rechoose them");
					alert.showAndWait();
					edaBtnNum=0;
				}
				else {
				
				//May be there are both items to be added
				Person p = new Person();
				p.setEdaFileAbsPath(edaFileAbPath);
				p.setLogFileAbsPath(logFileAbPath);
				if(!pList.contains(p)) {
					pList.add(p);
				}
				edaBtnNum=0;
				logBtnNum=0;
				
			}
				edaFileAbPath=null;logFileAbPath=null;
			}
		}
		
		else {
			multiLoggingTextOutput.appendText("WARNING -- Looks like you have not selected any EDA file!!!\n");
			
		}
		
		
		
	}
	
	@FXML
	public void logFileBtnAction(ActionEvent event) {
		//Write a file chooser
				pList = new ArrayList<Person>();
				FileChooser fc = new FileChooser();
				fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Log File","*.*"));
				File logFile = fc.showOpenDialog(null);
				//int buttonNumber=0;
				String buttonEDA = ((Button) event.getSource()).getText();
				//System.out.println("Button --> " + buttonEDA);
				if(buttonEDA.split("-")[1]!=null) {
					logBtnNum = Integer.valueOf(buttonEDA.split("-")[1]);	
					//System.out.println("Button " + buttonNumber);
				}
				
				//Check if the file is selected
				if(logFile!=null) {
					//checkAndAddLabel(buttonNumber,logFile.getName(),false);
					//lets add the file to the eda label
					//logFileName.setText(logFile.getName());
					multiLoggingTextOutput.appendText("Log File Added is: " + logFile.getName() + "\n");
					logFileAbPath = logFile.getAbsolutePath();

					if(edaFileAbPath!=null){
						if(edaBtnNum!=logBtnNum) {
							//Alert an error on execution
							Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("ERROR!!!");
							alert.setHeaderText(null);
							alert.setContentText("Looks like EDA file and log File are not Same, Please rechoose them");
							alert.showAndWait();
							logBtnNum=0;
						}
						else {
						//May be there are both items to be added
						Person p = new Person();
						p.setEdaFileAbsPath(edaFileAbPath);
						p.setLogFileAbsPath(logFileAbPath);
						if(!pList.contains(p)) {
							pList.add(p);
						}
						edaBtnNum=0;
						logBtnNum=0;
						}
						edaFileAbPath=null;logFileAbPath=null;
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
		
		util = new Utility();
		
		for(Person p:pList) {
			System.out.println(p.getEdaFileAbsPath() + " === " + p.getLogFileAbsPath());
		}
		
	
		//check pList Size
		List<PersonDAO> personsList = populatePersonsList();
		

		for(PersonDAO p:personsList) {
			System.out.println(p.getLogFileName() + " === " + p.getUnisensFolderName());
		}
		
		
		service = new DemonstratorServiceImpl();
		String arffFileName = arffName.getText();
		System.out.println(msg);
		if(personsList!=null && !personsList.isEmpty() && msg!=null) {
			String message = service.preprocess(personsList,arffFileName,msg);
			if(message!=null) {
				multiLoggingTextOutput.appendText(message);
			}
			
		}
		
		//Finally empty the person list of the first execution.
		pList.clear();personsList.clear();
		
	}
		
		
	private List<PersonDAO> populatePersonsList(){
		List<PersonDAO> persons = new ArrayList<PersonDAO>();
		int count=0;
		for(Person p:pList) {
			persons.add(new PersonDAO(p.getEdaFileAbsPath(),p.getLogFileAbsPath(), checkAndFetchDelay(count+1)));
			count+=1;
		}
		return persons;
	}
	
	
	
	private boolean checkAndFetchDelay(int count) {
		
			switch(count) {
				case 1: return checkAfter1min1.isSelected();
				case 2: return checkAfter1min2.isSelected();
				case 3: return checkAfter1min2.isSelected();
				case 4: return checkAfter1min2.isSelected();
				case 5: return checkAfter1min2.isSelected();
				case 6: return checkAfter1min2.isSelected();
				case 7: return checkAfter1min2.isSelected();
				case 8: return checkAfter1min2.isSelected();
				case 9: return checkAfter1min2.isSelected();
				case 10: return checkAfter1min2.isSelected();
				default: return false;
			
		}
	}
	
	
	
	/*private void checkAndAddLabel(int buttonNumber,String fileName,boolean isEDA) {
		if(isEDA) {
			switch(buttonNumber) {
				case 1: multiEdaFilename1.setText(fileName);break;
				case 2: multiEdaFilename2.setText(fileName);break;
				case 3: multiEdaFilename3.setText(fileName);break;
				case 4: multiEdaFilename4.setText(fileName);break;
				case 5: multiEdaFilename5.setText(fileName);break;
				case 6: multiEdaFilename6.setText(fileName);break;
				case 7: multiEdaFilename7.setText(fileName);break;
				case 8: multiEdaFilename8.setText(fileName);break;
				case 9: multiEdaFilename9.setText(fileName);break;
				case 10: multiEdaFilename10.setText(fileName);break;
				default: System.out.println("There is nothing to add");break;
			}
		}
		else {
			switch(buttonNumber) {
			case 1: multiLogFileName1.setText(fileName);break;
			case 2: multiLogFileName2.setText(fileName);break;
			case 3: multiLogFileName3.setText(fileName);break;
			case 4: multiLogFileName4.setText(fileName);break;
			case 5: multiLogFileName5.setText(fileName);break;
			case 6: multiLogFileName6.setText(fileName);break;
			case 7: multiLogFileName7.setText(fileName);break;
			case 8: multiLogFileName8.setText(fileName);break;
			case 9: multiLogFileName9.setText(fileName);break;
			case 10: multiLogFileName10.setText(fileName);break;
			default: System.out.println("There is nothing to add");break;
		}
			
		}
	}*/

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// If any initialization required write the code here!!
		
		 EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
	    	 
		        public void handle(ActionEvent e)
		        {
		        	if(phase1.isSelected()) {
		        		phase2.setDisable(true);
		        		msg = PHASE1;
		        	}
		        	else if(phase2.isSelected()) {
		        		phase1.setDisable(true);
		        		msg = PHASE2;
		        	}
		        	else {
		        		if(msg.equalsIgnoreCase(PHASE1)) {
		        			phase2.setDisable(false);
		        		}
		        		else if(msg.equalsIgnoreCase(PHASE2)) {
		        			phase1.setDisable(false);
		        		}
		        	}
		        }
		
	};
	
	phase1.setOnAction(event);
	phase2.setOnAction(event);
	
	
	}
}
