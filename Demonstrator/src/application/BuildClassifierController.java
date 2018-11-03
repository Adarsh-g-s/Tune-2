package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.controlsfx.control.CheckComboBox;

import com.sun.javafx.binding.LongConstant;

import Model.Person;
import Model.Results;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import service.DemonstratorService;
import service.DemonstratorServiceImpl;


public class BuildClassifierController implements Initializable{
	
	private static final String BUILD_CLASSIFIER = "BUILD_CLASSIFIER";
	private static final String TEST_CLASSIFIER = "TEST_CLASSIFIER";

	@FXML
	private Button trainArffBtn;
	
	@FXML
	private Button execute;
	
	@FXML
	private Button visBtn;
	
	@FXML
	private Button testArffBtn;
	
	@FXML
	private Label trainArffFileName;
	
	@FXML
	private Label testArffFileName;
	
	
	@FXML
	private ComboBox<String> comboBox;
	
	/*@FXML
	private ComboBox<Integer> folds;*/
	
	@FXML
	private TextField repeatedFoldsNumber;
	
	@FXML
	private CheckBox build_classifier;
	
	@FXML
	private CheckBox testClassifier;
	
	@FXML
	private CheckBox groundTruthCheck;
	
	@FXML
	private Label foldLabel;
	
	
	@FXML
	private PieChart pieChart1;
	
	@FXML
	private PieChart pieChart2;
	
	@FXML
	private PieChart pieChart3;
	
	Utility util;
	

	@FXML
	private CheckComboBox<String> checkComboBox;
	
	@FXML
	private TextArea loggingTextOutput;
	
	
	private String trainArffFile;
	private String testArffFile;
	private String classifierName;
	private int foldNumber;
	private String msg;
	
	
	
	//Pie Chart Inputs
	private static String sentiEasy;
	private static String sentiHard;
	private static String factEasy;
	private static String factHard;
	private static String relEasy;
	private static String relHard;
	
	
	//Classifiers
	ObservableList<String> classifiers;
	//Person List
	List<Person> pList = new ArrayList<Person>();
	
	//Demonstrator Service
	DemonstratorService service;
	boolean startAfter1min;
	
	
	
	
	@FXML
	public void trainFileBtnAction(ActionEvent event) {
		
		//Write a file chooser
				FileChooser fc = new FileChooser();
				fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("ARFF Train File","*.arff"));
				File trainFile = fc.showOpenDialog(null);
				//Check if the file is selected
				if(trainFile!=null) {
					
					//lets add the file to the train label
					trainArffFileName.setText(trainFile.getName());
					loggingTextOutput.appendText("Train Arff Added is: " + trainFile.getName() + "\n");
					trainArffFile = trainFile.getAbsolutePath();
					
					//Enable TEST Button
					testArffBtn.setDisable(false);
					
					
				}
				
				else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("ERROR!!!");
					alert.setHeaderText(null);
					alert.setContentText("Looks like you have not selected any train file!!!");
					alert.showAndWait();
					loggingTextOutput.appendText("WARNING -- Looks like you have not selected any train file!!! \n");
					trainArffFile=null;
				}
		
	}
	
	
	@FXML
	public void showPieChart(ActionEvent event) {
		
		//Scene scene = new Scene(new Group());
      //  stage.setTitle("Imported Fruits");
       // stage.setWidth(500);
       // stage.setHeight(500);
		
		String[] relEasyA = relEasy.split("-");
		String[] sentEasyA = sentiEasy.split("-");
		String[] factEasyA = factEasy.split("-");
		String[] relHardA = relHard.split("-");
		String[] sentHardA = sentiHard.split("-");
		String[] factHardA = factHard.split("-");
		
		
        ObservableList<PieChart.Data> pieChartDataSent =
                FXCollections.observableArrayList(
                new PieChart.Data(sentEasyA[0] +"--" +sentEasyA[1], Double.valueOf(sentEasyA[2])),            
                new PieChart.Data(sentHardA[0] +"--" +sentHardA[1], Double.valueOf(sentHardA[2])));
    
        ObservableList<PieChart.Data> pieChartDataRel =
                FXCollections.observableArrayList(
                new PieChart.Data(relEasyA[0] +"--" +relEasyA[1], Double.valueOf(relEasyA[2])),
                new PieChart.Data(relHardA[0] +"--" +relHardA[1], Double.valueOf(relHardA[2])));
                
        ObservableList<PieChart.Data> pieChartDataFact =
                FXCollections.observableArrayList(
                		new PieChart.Data(factEasyA[0] +"--" +factEasyA[1], Double.valueOf(factEasyA[2])),
                		new PieChart.Data(factHardA[0] +"--" +factHardA[1], Double.valueOf(factHardA[2])));
        
        
        System.out.println("RelE -- " + Double.valueOf(relEasyA[2]));
        System.out.println("RelH -- " + Double.valueOf(relHardA[2]));
        System.out.println("SenE -- " + Double.valueOf(sentEasyA[2]));
        System.out.println("SenH -- " + Double.valueOf(sentHardA[2]));
        System.out.println("FactE -- " + Double.valueOf(factEasyA[2]));
        System.out.println("factH -- " + Double.valueOf(factHardA[2]));
       // pieChart = new PieChart(pieChartData);
        
        
       //Caption
        final Label caption = new Label("");
        caption.setTextFill(Color.ANTIQUEWHITE);
        caption.setStyle("-fx-font: 24 arial;");
       //Sentimental
       pieChart1.setTitle("Sentimental");
       pieChart1.setData(pieChartDataSent);
       pieChart1.setLabelLineLength(10);
       pieChart1.setLegendSide(Side.BOTTOM);
       
       //Mouse Event
       for (final PieChart.Data data : pieChart1.getData()) {
    	    data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
    	        new EventHandler<MouseEvent>() {
    	            @Override public void handle(MouseEvent e) {
    	                caption.setTranslateX(e.getSceneX());
    	                caption.setTranslateY(e.getSceneY());
    	                caption.setText(String.valueOf(data.getPieValue()) + "%");
    	             }
    	        });
    	}
       
       
       //Factual
       pieChart2.setTitle("Factual");
       pieChart2.setData(pieChartDataFact);
       pieChart2.setLabelLineLength(10);
       pieChart2.setLegendSide(Side.BOTTOM);
       
       //Mouse Event
       for (final PieChart.Data data : pieChart2.getData()) {
    	    data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
    	        new EventHandler<MouseEvent>() {
    	            @Override public void handle(MouseEvent e) {
    	                caption.setTranslateX(e.getSceneX());
    	                caption.setTranslateY(e.getSceneY());
    	                caption.setText(String.valueOf(data.getPieValue()) + "%");
    	             }
    	        });
    	}
       
       
       //Relevant
       pieChart3.setTitle("Relevant");
       pieChart3.setData(pieChartDataRel);
       pieChart3.setLabelLineLength(10);
       pieChart3.setLegendSide(Side.BOTTOM);
       
     //Mouse Event
       for (final PieChart.Data data : pieChart3.getData()) {
    	    data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED,
    	        new EventHandler<MouseEvent>() {
    	            @Override public void handle(MouseEvent e) {
    	                caption.setTranslateX(e.getSceneX());
    	                caption.setTranslateY(e.getSceneY());
    	                caption.setText(String.valueOf(data.getPieValue()) + "%");
    	             }
    	        });
    	}
       //Make it not visible
       visBtn.setVisible(false);
     /*  sentiEasy = null;
       sentiHard = null;
       relEasy = null;
       relHard = null;
       factHard = null;
       factEasy = null;*/
	}
	
	@FXML
	public void testFileBtnAction(ActionEvent event) {
		//Write a file chooser
				
				File testFile = null;
				util = new Utility();
				if(testClassifier.isSelected()) {
					//util.init(false, null, null);
					DirectoryChooser chooser = new DirectoryChooser();
					//Write a directory chooser
					chooser.setTitle("Arff Folder Location");
					//File defaultDirectory = new File(util.getArffFolderLocation());
					//chooser.setInitialDirectory(defaultDirectory);
					testFile = chooser.showDialog(null);
					if(testFile!=null)
						loggingTextOutput.appendText("Test Arff Added is: " + testFile.getName() + "\n");
				}
				else {
					FileChooser fc = new FileChooser();
					fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("ARFF Test File","*.arff"));
					testFile = fc.showOpenDialog(null);
					if(testFile!=null)
						loggingTextOutput.appendText("Test Arff Added is: " + testFile.getName() + "\n");
				}
				
				//Check if the file is selected
				if(testFile!=null) {
					
					//lets add the file to the test label
					testArffFileName.setText(testFile.getName());
					testArffFile = testFile.getAbsolutePath();
					//Default
					execute.setDisable(false);
					
					
				}
				
				else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("ERROR!!!");
					alert.setHeaderText(null);
					alert.setContentText("Looks like you have not selected anything!!!");
					alert.showAndWait();
					loggingTextOutput.appendText("WARNING -- Looks like you have not selected anything !!! \n");
					testArffFile=null;
				}
		
	}
	
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		/**
		 * Lets check and decide what to do:
		 * 1: Either to build a classifier choosing train and test arff file
		 * 
		 * 2: Choose a test file and test classifiers
		 */
		
		
		//util.init(false, null, null);
		  EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
		    	 
		        public void handle(ActionEvent e)
		        {
		            if (build_classifier.isSelected()) {
		            	msg = BUILD_CLASSIFIER;
		            	System.out.println(msg);
		            	testArffBtn.setText("Choose Test Arff File");
		            	testClassifier.setDisable(true);
		            	trainArffBtn.setDisable(false);
		            	testArffBtn.setDisable(true);
		            	comboBox.setVisible(true);
		            	groundTruthCheck.setVisible(false);
		            	repeatedFoldsNumber.setVisible(true);
		            	if(checkComboBox.isVisible())
		            		checkComboBox.setVisible(false);
		            	if(groundTruthCheck.isVisible()) {
		            		groundTruthCheck.setVisible(false);
		            	}
		            	
		            	if(!execute.isDisable())
		            		execute.setDisable(true);
		            	if(!foldLabel.isVisible()) {
	            			foldLabel.setVisible(true);
	            		}
		            	
		            }
		            	else if(testClassifier.isSelected()) {
		            		msg = TEST_CLASSIFIER;
		            		build_classifier.setDisable(true);
		            		trainArffBtn.setDisable(true);
		            		testArffBtn.setDisable(false);
		            		testArffBtn.setText("Arff Folder");
		            		checkComboBox.setVisible(true);
		            		groundTruthCheck.setVisible(true);
		            		if(comboBox.isVisible())
		            			comboBox.setVisible(false);
		            		
		            		if(repeatedFoldsNumber.isVisible())
		            			repeatedFoldsNumber.setVisible(false);
		            		
		            		if(foldLabel.isVisible()) {
		            			foldLabel.setVisible(false);
		            		}
		            		
		            		
		            		if(!execute.isDisable())
			            		execute.setDisable(true);
		            		System.out.println(msg);
		            	}
		            	else {
		            		if(msg.equalsIgnoreCase(BUILD_CLASSIFIER)) {
		            			testClassifier.setDisable(false);
		            		}
		            		else {
		            			build_classifier.setDisable(false);
		            		}
		            		
		            	}
		        }

		    };
		
		build_classifier.setOnAction(event);
		testClassifier.setOnAction(event);
		System.out.println(msg);
		util = new Utility();
		ObservableList<String> obList = util.populateList();
		comboBox.setItems(obList);
		checkComboBox.getItems().addAll(obList);	
		
		
		//Fill dropdown
		/*ObservableList<Integer> foldsList = FXCollections.observableArrayList();
		for(int i=1;i<=5;i++) {
			foldsList.add(i);
		}*/
		
		
		//Get the selected classifiers
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
	public void executeAction(ActionEvent event) {
		
		//Init Props
		util = new Utility();
		util.init(false, null, null);
		service = new DemonstratorServiceImpl();
		Alert alert = new Alert(AlertType.ERROR);
		if(build_classifier.isSelected()) {
			
			//Build a Classifier and save
			if(repeatedFoldsNumber!=null) {
				
				if(!repeatedFoldsNumber.getText().matches("[0-9]+")) {
					alert.setTitle("ERROR!!!");
					alert.setHeaderText(null);
					alert.setContentText("Please provide an integer value");
					alert.showAndWait();
					loggingTextOutput.appendText("ERROR -- Please provide an integer value \n");
				}
				
				else if(Integer.valueOf(repeatedFoldsNumber.getText()) == 1) {
					alert.setTitle("ERROR!!!");
					alert.setHeaderText(null);
					alert.setContentText("Please provide value greater than 1");
					alert.showAndWait();
					loggingTextOutput.appendText("ERROR -- Please provide value greater than 1 \n");
				}
				else {
					foldNumber = Integer.valueOf(repeatedFoldsNumber.getText());
				}
				
				
			}
			if(comboBox.getValue()!=null) {
				classifierName = comboBox.getValue();	
			}
			
			loggingTextOutput.appendText("Calling to Build Classifier now. Please wait for the results. \n");
			
			//Message
			String msg = null;
			if(trainArffFile!=null && testArffFile!=null && classifierName!=null && foldNumber!=0) {
				msg = service.performClassification(trainArffFile, testArffFile, classifierName, foldNumber);	
			}
			else {
				alert.setTitle("ERROR!!!");
				alert.setHeaderText(null);
				alert.setContentText("Looks like the required parameters for the execution is empty or missing, please reselect and execute!!");
				alert.showAndWait();
			}
			
			
			//Message Output
			if(msg!=null) {
				loggingTextOutput.appendText(msg);
			}
			
		}
		else if(testClassifier.isSelected()) {
			boolean groundTruth = groundTruthCheck.isSelected();
			//Test a classfier against arff file
			loggingTextOutput.appendText("Applying a classifier model. Please wait for the results. \n");
			//For each classifier selected to be tested go ahead and call applyClassifier()
			if(classifiers.size() > 1) {
				visBtn.setVisible(false);
				pieChart1.setVisible(false);
				pieChart2.setVisible(false);
				pieChart3.setVisible(false);
			}
			String path = null;
			for(String classifier: classifiers) {
				
				if(util.getClassifierLocation()!=null) {

					if(!util.getClassifierLocation().endsWith("//")) {
						path = util.getClassifierLocation() + "//";
						path+= classifier + ".model";
					}
					else {
						path = util.getClassifierLocation();
						path+= classifier + ".model";
					}
					
					if(path!=null) {
						System.out.println("Path --> " + path);
						boolean fileExists = util.checkFile(path);
					if(fileExists) {
						loggingTextOutput.appendText("Using --> " + classifier + " Model for classification at location: " + path.toString() + "\n");
						 List<Results> msgs = service.testModel(testArffFile, path.toString(), classifier,groundTruth);
						 loggingTextOutput.appendText("====================================================\n");
						 if(msgs!=null) {
							 for(Results msg:msgs) {
								 loggingTextOutput.appendText("\n");
								 loggingTextOutput.appendText(msg.getMsgs() + "\n");
								 
								 if(msg.getSentiEasy()!=null && msg.getSentiHard()!=null) {
									 sentiEasy = msg.getSentiEasy();
									 sentiHard = msg.getSentiHard();	 
								 }
								 if(msg.getFactEasy()!=null && msg.getFactHard()!=null) {
									 factEasy = msg.getFactEasy();
									 factHard = msg.getFactHard();	 
								 }
								 
								 if(msg.getRelEasy()!=null && msg.getRelHard()!=null) {
									 relEasy = msg.getRelEasy();
									 relHard = msg.getRelHard();	 
								 }
								 
								 loggingTextOutput.appendText("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
							 }
						 
						 }
						 loggingTextOutput.appendText("====================END================================\n");
					}
				}
				}
				
				
				
			}
		}
		
	
		
		
		//After all the action is performed lets disable again for no mess.Commented this for now
		//execute.setDisable(true);
		//comboBox.setDisable(true);
		//edaBtn.setDisable(true);
		
		//Set label back again so no confusion
		trainArffFile=null;
		testArffFile=null;
		trainArffFileName.setText("Label");
		testArffFileName.setText("Label");
		if(testClassifier.isSelected() && classifiers.size() == 1)
			visBtn.setVisible(true);
	}

}