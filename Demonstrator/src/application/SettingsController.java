package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;

public class SettingsController implements Initializable{
	
	@FXML
	private TextField clLocation;
	
	@FXML
	private TextField arffLocation;
	
	@FXML
	private TextField resultsLocationTextField;
	
	//Utility Service
	Utility util = new Utility();
	
	//String Temp Storage
	private String clasLocation;
	private String arffFolderLocation;
	private String resultsLocation;
	
	
	@FXML
	private void saveAction(ActionEvent event) {
		
		if(clLocation.getText().isEmpty()) {
			//Never can come here
			System.out.println("The properties cannot be empty initially");
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR!!!");
			alert.setHeaderText(null);
			alert.setContentText("The properties cannot be empty, please provide a location");

			alert.showAndWait();
			//Set the text back again
			clLocation.setText(clasLocation);
		}
		else {
			//There is some initial property created and that needs to change
			//Commented this property for now...
			/*if(!clLocation.getText().contains("\\") || !clLocation.getText().contains("/") || !clLocation.getText().contains("/")) {
				System.out.println("I am here!! " + clLocation.getText());
			}*/
			if(!(clasLocation.equalsIgnoreCase(clLocation.getText()))) {
				System.out.println("Came Here for Classfier Location");
				util.init(true, clLocation.getText(),Utility.CLASSIFIER_LOCATION);
			}
			if(!(arffFolderLocation.equalsIgnoreCase(arffLocation.getText()))) {
				util.init(true, arffLocation.getText(),Utility.ARFF_LOCATION);
			}
			
			if(!(resultsLocation.equalsIgnoreCase(resultsLocationTextField.getText()))) {
				util.init(true, resultsLocationTextField.getText(),Utility.RESULTS_LOCATION);
			}
			
			}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		util.init(false,null,null);
		clasLocation = util.getClassifierLocation();
		arffFolderLocation = util.getArffFolderLocation();
		resultsLocation = util.getResultLocation();
		System.out.println("Results" + resultsLocation);
		//System.out.println("Arff--> " + arffFolderLocation);
		if(clasLocation!=null) {
			System.out.println("Classifier location is set from properties");
			clLocation.setText(clasLocation);
		}
		
		if(resultsLocation!=null) {
			System.out.println("Results location is set from properties");
			resultsLocationTextField.setText(resultsLocation);
		}
		
		if(arffFolderLocation!=null) {
			System.out.println("Setting the arff Location from the properties");
			arffLocation.setText(arffFolderLocation);
		}		
	}
}
