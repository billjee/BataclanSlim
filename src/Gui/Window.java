/**
 * 
 */
package Gui;

import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.geotools.data.store.ContentState;
import org.geotools.feature.SchemaException;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import ActivityChoiceModel.BiogemeControlFileGenerator;
import ActivityChoiceModel.BiogemeSimulator;
import ActivityChoiceModel.CensusPreparator;
import ActivityChoiceModel.TravelSurveyPreparator;
import Controlers.Button;
import Controlers.ComboBox;
import Controlers.PromptStringInformation;
import Controlers.TextField;
import SimulationObjects.World;
import Smartcard.GTFSRoute;
import Smartcard.PublicTransitSystem;
import Smartcard.SmartcardDataManager;
import Smartcard.UtilsSM;
import Utils.*;

/**
 * @author Antoine
 *
 */
public class Window extends JFrame implements ActionListener {
	
	JPanel container = new JPanel();
	PaneContent content;
    InformationPane informationPane = new InformationPane();
    PaneButtons paneButtons = new PaneButtons();

	public Window() throws IOException, SchemaException{
		content = new PaneContent();
		this.setTitle("BataclanGUI");
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		//this.setUndecorated(true);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.setLocationRelativeTo(null);
	    
	    container.setBackground(Color.white);
	    container.setLayout(new BorderLayout());
	    //container.add(content, BorderLayout.CENTER);
	    
	    paneButtons.buttonGeneral.myButton.addMouseListener(new HelpListenerButton());
	    paneButtons.buttonPopulationSynthesis.myButton.addMouseListener(new HelpListenerButton());
	    paneButtons.buttonShowMap.myButton.addMouseListener(new HelpListenerButton());
	    paneButtons.buttonModelCalibration.myButton.addMouseListener(new HelpListenerButton());
	    paneButtons.buttonDestinationInference.myButton.addMouseListener(new HelpListenerButton());
	    paneButtons.buttonSocioDemoInference.myButton.addMouseListener(new HelpListenerButton());
	    
	    paneButtons.buttonGeneral.myButton.addActionListener(new ButtonListener());
	    paneButtons.buttonPopulationSynthesis.myButton.addActionListener(new ButtonListener());
	    paneButtons.buttonShowMap.myButton.addActionListener(new ButtonListener());
	    paneButtons.buttonModelCalibration.myButton.addActionListener(new ButtonListener());
	    paneButtons.buttonDestinationInference.myButton.addActionListener(new ButtonListener());
	    paneButtons.buttonSocioDemoInference.myButton.addActionListener(new ButtonListener());
	    
	    content.paneGeneral.line3.myButton.addActionListener(new UpdateWorkingDirectory());
	    
	    content.panePopulationSynthesis.tabPreparePopulationSynthesis.line4.myButton.addMouseListener(new HelpListenerButton());
	    content.panePopulationSynthesis.tabPreparePopulationSynthesis.line4.myButton.addActionListener(new PrepareGlobalPopulationSynthesisButton());
	    content.panePopulationSynthesis.tabPreparePopulationSynthesis.line6.myButton.addMouseListener(new HelpListenerButton());
	    content.panePopulationSynthesis.tabPreparePopulationSynthesis.line6.myButton.addActionListener(new PrepareLocalPopulationSynthesisButton());
	    
	    content.panePopulationSynthesis.tabRunPopulationSynthesis.line5.myComboBox.addMouseListener(new HelpListenerComboBox());
	    content.panePopulationSynthesis.tabRunPopulationSynthesis.line11.myButton.addMouseListener(new HelpListenerButton());
	    content.panePopulationSynthesis.tabRunPopulationSynthesis.line11.myButton.addActionListener(new RunPopulationSynthesisButton());
	    
	    content.paneModelCalibration.tabPrepareBiogemeCalibration.line4.myButton.addMouseListener(new HelpListenerButton());
	    content.paneModelCalibration.tabPrepareBiogemeCalibration.line4.myButton.addActionListener(new PrepareBiogemeCtrlFile());
	    content.paneModelCalibration.tabPrepareBiogemeCalibration.line8.myComboBox.addMouseListener(new HelpListenerComboBox());
	    content.paneModelCalibration.tabPrepareBiogemeCalibration.line11.myButton.addMouseListener(new HelpListenerButton());
	    content.paneModelCalibration.tabPrepareBiogemeCalibration.line11.myButton.addActionListener(new PrepareTravelSurvey());
	    
	    content.paneModelCalibration.tabRunModelValidation.line5.myComboBox.addMouseListener(new HelpListenerComboBox());
	    content.paneModelCalibration.tabRunModelValidation.line7.myButton.addMouseListener(new HelpListenerButton());
	    content.paneModelCalibration.tabRunModelValidation.line7.myButton.addActionListener(new RunModelValidation());
	    
	    content.paneDestinationInference.tabRunDestinationInference.line9.myButton.addMouseListener(new HelpListenerButton());
	    content.paneDestinationInference.tabRunDestinationInference.line9.myButton.addActionListener(new RunDestinationInference());
	    
	    content.paneSocioDemographicInference.tabRunSocioDemographicInference.line15.myButton.addMouseListener(new HelpListenerButton());
	    content.paneSocioDemographicInference.tabRunSocioDemographicInference.line15.myButton.addActionListener(new RunSocioDemographicInference());
	    
	    ArrayList<PromptStringInformation> toListen = new ArrayList<PromptStringInformation>();
	    toListen = content.panePopulationSynthesis.tabPreparePopulationSynthesis.myStringPrompts;
	    listenText(toListen);
	    toListen = content.panePopulationSynthesis.tabRunPopulationSynthesis.myStringPrompts;
	    listenText(toListen);
	    toListen = content.paneModelCalibration.tabPrepareBiogemeCalibration.myStringPrompts;
	    listenText(toListen);
	    toListen = content.paneModelCalibration.tabRunModelValidation.myStringPrompts;
	    listenText(toListen);
	    toListen = content.paneDestinationInference.tabRunDestinationInference.myStringPrompts;
	    listenText(toListen);
	    toListen = content.paneSocioDemographicInference.tabRunSocioDemographicInference.myStringPrompts;
	    listenText(toListen);
	    
	    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, paneButtons, content);
	    JSplitPane split2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, split, informationPane);
	    
	    
	    
	    //this.getContentPane().add(split, BorderLayout.WEST);
	    this.getContentPane().add(split2, BorderLayout.CENTER);
	    this.setVisible(true);
	    
	    
	}
	
	private void listenText(ArrayList<PromptStringInformation> toListen) {
		// TODO Auto-generated method stub
		for(PromptStringInformation str: toListen){
	    	str.myText.addMouseListener(new HelpListenerText());
	    }
	}

	class ButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent arg0){
			int index = ((Button)arg0.getSource()).id;
			content.cl.show(content, content.listContent[index]);
		}
	}
	
	class UpdateWorkingDirectory implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String newWD = content.paneGeneral.line2.myText.getText();
			Utils.DATA_DIR = newWD;
			System.out.println("--new project directory = " + Utils.DATA_DIR);
			update(content);
		}

		private void update(Component compo) {
			// TODO Auto-generated method stub
			 if (compo instanceof PromptStringInformation) {
				 ((PromptStringInformation)compo).updateText();
				 }
				Component[] insideCompo = ((Container) compo).getComponents();
				for(Component curComp: insideCompo){
					update(curComp);
				}
		}

		
	}
	
	class RunDestinationInference implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e){
			// TODO Auto-generated method stub
			try {
				PublicTransitSystem myPublicTransitSystem = new PublicTransitSystem();
	
				String pathGTFSTrips = content.paneDestinationInference.tabRunDestinationInference.line1.myText.getText();
				String pathGTFSStops = content.paneDestinationInference.tabRunDestinationInference.line2.myText.getText();
				String pathGTFSStopTimes =content.paneDestinationInference.tabRunDestinationInference.line3.myText.getText();
				String pathGTFSRoutes = content.paneDestinationInference.tabRunDestinationInference.line4.myText.getText();
				
				myPublicTransitSystem.initializePTsystemForDestinationInference(pathGTFSTrips, pathGTFSStops, pathGTFSStopTimes, pathGTFSRoutes);
			
				System.out.println("-- public transportation system loaded");

				String pathSmartcards = content.paneDestinationInference.tabRunDestinationInference.line5.myText.getText();
				String pathOutput = content.paneDestinationInference.tabRunDestinationInference.line6.myText.getText();
				String walkingDistance = content.paneDestinationInference.tabRunDestinationInference.line7.myText.getText();
				String activityThreshold = content.paneDestinationInference.tabRunDestinationInference.line8.myText.getText();
				UtilsSM.distanceThreshold = Double.parseDouble(walkingDistance);
				UtilsSM.timeThreshold = Double.parseDouble(activityThreshold);
				
				
				SmartcardDataManager mySmartcardManager = new SmartcardDataManager();
				mySmartcardManager.prepareSmartcards(pathSmartcards);
				mySmartcardManager.inferDestinations();
				System.out.println("--destination infered");
				mySmartcardManager.printSmartcards(pathOutput);
			
			} 
			
			
			catch (IOException | ParseException | NumberFormatException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1.getMessage());
				e1.printStackTrace();
			}
		}
		
	}
	
	
	class RunPopulationSynthesisButton implements ActionListener{

		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			System.out.println("<html>--population synthesis is running");
			try{
				String configFile = content.panePopulationSynthesis.tabRunPopulationSynthesis.line0.myText.getText();
				ConfigFile.InitializeImportance(configFile);
				
				String censusData = content.panePopulationSynthesis.tabRunPopulationSynthesis.line1.myText.getText();
				String seedData = content.panePopulationSynthesis.tabRunPopulationSynthesis.line2.myText.getText();
				String distributionsDirectory = content.panePopulationSynthesis.tabRunPopulationSynthesis.line3.myText.getText();
				String nBtch = content.panePopulationSynthesis.tabRunPopulationSynthesis.line4.myText.getText();
				int nBatch = Integer.parseInt(nBtch.trim());
				int	nThreads = (int) content.panePopulationSynthesis.tabRunPopulationSynthesis.line5.myComboBox.getSelectedItem();
				String nSkp = content.panePopulationSynthesis.tabRunPopulationSynthesis.line6.myText.getText();
				int nSkips = Integer.parseInt(nSkp.trim());
				String nWrm = content.panePopulationSynthesis.tabRunPopulationSynthesis.line7.myText.getText();
				int nWarm = Integer.parseInt(nWrm.trim());
				String outputStat = content.panePopulationSynthesis.tabRunPopulationSynthesis.line8.myText.getText();
				String outputPop = content.panePopulationSynthesis.tabRunPopulationSynthesis.line10.myText.getText();
				
				Utils.SKIP_ITERATIONS = nSkips;
				Utils.WARMUP_ITERATIONS = nWarm;
				
				CensusPreparator census = new CensusPreparator(censusData);
				census.writeZonalInputFile(nBatch);
		    	census.writeCtrlFile(nBatch, configFile);
		    	System.out.println("<html>-- local controler and local description file produced");
				
				OutputFileWritter localStatAnalysis = new OutputFileWritter();
	            localStatAnalysis.OpenFile(outputStat);
	            String headers =UtilsSM.zoneId + Utils.COLUMN_DELIMETER + Utils.population;
	            for(int i = 0; i < ConfigFile.AttributeDefinitionsImportance.size(); i++){
	    			headers = headers + Utils.COLUMN_DELIMETER + ConfigFile.AttributeDefinitionsImportance.get(i).category + "SRMSE"
	    					 + Utils.COLUMN_DELIMETER + ConfigFile.AttributeDefinitionsImportance.get(i).category + "TAE_DA"
	    							 + Utils.COLUMN_DELIMETER + ConfigFile.AttributeDefinitionsImportance.get(i).category + "%SAE_DA" ;
	            }
	            for(int i = 0; i < ConfigFile.AttributeDefinitionsImportance.size(); i++){
	            	for(int j = 0 ; j < ConfigFile.AttributeDefinitionsImportance.get(i).value; j++){
	            		headers = headers + Utils.COLUMN_DELIMETER  + ConfigFile.AttributeDefinitionsImportance.get(i).category + j + "TAE";
	            	}
	            }
	            for(int i = 0; i < ConfigFile.AttributeDefinitionsImportance.size(); i++){
	            	for(int j = 0 ; j < ConfigFile.AttributeDefinitionsImportance.get(i).value; j++){
	            		headers = headers + Utils.COLUMN_DELIMETER  + ConfigFile.AttributeDefinitionsImportance.get(i).category + "SAE";
	            	}
	            }
	            for(int i = 0; i < ConfigFile.AttributeDefinitionsImportance.size(); i++){
	            	for(int j = 0 ; j < ConfigFile.AttributeDefinitionsImportance.get(i).value; j++){
	            		headers = headers + Utils.COLUMN_DELIMETER  + ConfigFile.AttributeDefinitionsImportance.get(i).category + j + "Target";
	            	}
	            }
	            for(int i = 0; i < ConfigFile.AttributeDefinitionsImportance.size(); i++){
	            	for(int j = 0 ; j < ConfigFile.AttributeDefinitionsImportance.get(i).value; j++){
	            		headers = headers + Utils.COLUMN_DELIMETER  + ConfigFile.AttributeDefinitionsImportance.get(i).category + j + "Result";
	            	}
	            }
	            
	            localStatAnalysis.myFileWritter.write(headers);
	            
	            // Initialize the population pool log
	            OutputFileWritter population =  new OutputFileWritter();
	        	population.OpenFile(outputPop);
	        	headers = "agentId" + Utils.COLUMN_DELIMETER + UtilsSM.zoneId;
	            for(int i = 0; i < ConfigFile.AttributeDefinitions.size(); i++){
	    			headers = headers + Utils.COLUMN_DELIMETER + ConfigFile.AttributeDefinitions.get(i).category ;
	            }
	            population.myFileWritter.write(headers);
	            
	            World myWorld = new World(505);
	            ConfigFile.resetConfigFile();
	            myWorld = null;
		    	
	            //Create batches
		    	for(int i = 0; i < nBatch; i++){
		    		
		    		
		    		World currWorld = new World(505);
		    		currWorld.Initialize(true, 1, i, distributionsDirectory);
			    	System.out.println("--computation with: " + nThreads + " logical processors");
			    	String[] answer = currWorld.CreatePersonPopulationPoolLocalLevelMultiThreadsBatch(seedData, nThreads);
					currWorld = null;
					//System.out.println(answer[1]);
			    	localStatAnalysis.myFileWritter.write(answer[0]);
		            population.myFileWritter.write(answer[1]);
		            ConfigFile.resetConfigFile();
		    	}

	            localStatAnalysis.CloseFile();
		    	population.CloseFile();
			}
			catch(NumberFormatException | IOException e){
				System.out.println(e.getMessage());
			} 
			
	    	System.out.println("<html>-- population synthesis done</html>");
		}
	}
	

	class RunSocioDemographicInference implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			try{
				String configFile = content.paneSocioDemographicInference.tabRunSocioDemographicInference.line0.myText.getText();
				ConfigFile.InitializeImportance(configFile);
				
				String choiceDescription = content.paneSocioDemographicInference.tabRunSocioDemographicInference.line1.myText.getText();
				String hypothesis = content.paneSocioDemographicInference.tabRunSocioDemographicInference.line2.myText.getText();
				String smartcard = content.paneSocioDemographicInference.tabRunSocioDemographicInference.line3.myText.getText();
				String syntheticPopulation = content.paneSocioDemographicInference.tabRunSocioDemographicInference.line4.myText.getText();
				
				String coordinateReferenceSystem = content.paneSocioDemographicInference.tabRunSocioDemographicInference.line7.myText.getText();
				UtilsSM.CRS = coordinateReferenceSystem.trim();
				String pathGtfsStops = content.paneSocioDemographicInference.tabRunSocioDemographicInference.line5.myText.getText();
				String pathLocalZonesShp = content.paneSocioDemographicInference.tabRunSocioDemographicInference.line6.myText.getText();
				
				String model = content.paneSocioDemographicInference.tabRunSocioDemographicInference.line8.myText.getText();
				String btch = content.paneSocioDemographicInference.tabRunSocioDemographicInference.line9.myText.getText();
				int nBatches = Integer.parseInt(btch.trim());
				String output = content.paneSocioDemographicInference.tabRunSocioDemographicInference.line10.myText.getText();
				
				
				
				
				PublicTransitSystem myPublicTransitSystem = new PublicTransitSystem();
				
				BiogemeControlFileGenerator myCtrlGenerator = new BiogemeControlFileGenerator();
		    	myCtrlGenerator.initialize(choiceDescription, hypothesis);
				myCtrlGenerator.printChoiceIndex(Utils.DATA_DIR + "outputs\\choiceIndex.csv");
				System.out.println("-- control file generator initiated");
				
				myPublicTransitSystem.initializeForSocioInference(
						myCtrlGenerator,
						smartcard,
						syntheticPopulation,
						model,
						pathGtfsStops,
						pathLocalZonesShp);
				
				/*myPublicTransitSystem.initializeForSocioInference(
						myCtrlGenerator, 
						smartcard, 
						geoDico,
						syntheticPopulation,
						model
						);*/
				
				
				
				myPublicTransitSystem.getValues();
				System.out.println("--pt system initialized");
				myPublicTransitSystem.createZonalSmartcardIndex();
				myPublicTransitSystem.createZonalPopulationIndex();
				
				//myPublicTransitSystem.printStation(Utils.DATA_DIR + "ptSystem\\station_smartcard.csv");
				System.out.println("--potential smartcard assigned");
				
				//########
				Utils.occupationCriterion = true;
				//myPublicTransitSystem.processMatchingStationByStation();
				//myPublicTransitSystem.processMatchingOnPtRiders();
				myPublicTransitSystem.processMatchingOnPtRidersByBatch(nBatches);
				myPublicTransitSystem.printSmartcards(output);
			}
			catch(IOException|NumberFormatException | MismatchedDimensionException | ParseException | TransformException | FactoryException e1){
				System.out.println(e1);
			}
			
		}
		
	}
	
	class RunModelValidation implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			try{
				String travelSurvey = content.paneModelCalibration.tabRunModelValidation.line1.myText.getText();
				String choiceDescription = content.paneModelCalibration.tabRunModelValidation.line2.myText.getText();
				String hypothesis = content.paneModelCalibration.tabRunModelValidation.line3.myText.getText();
				String model = content.paneModelCalibration.tabRunModelValidation.line4.myText.getText();
				int choiceSetType = (int) content.paneModelCalibration.tabRunModelValidation.line5.myComboBox.getSelectedItem();
				String output = content.paneModelCalibration.tabRunModelValidation.line6.myText.getText();
				
				BiogemeSimulator mySimulator;
				BiogemeControlFileGenerator myCtrlGenerator = new BiogemeControlFileGenerator();
	    		myCtrlGenerator.initialize(choiceDescription, hypothesis);
	    		
				//String pathToModel = Utils.DATA_DIR + "ptSystem\\ctrlWithZones~1.F12";
				mySimulator = new BiogemeSimulator(myCtrlGenerator);
				mySimulator.initialize(travelSurvey);
				//Utils.DATA_DIR + "biogeme\\dataZones2.csv"
				mySimulator.importBiogemeModel(model);
				mySimulator.importNest(model);
				mySimulator.applyModelOnTravelSurveyPopulation(output,choiceSetType, true);
			}
			catch(IOException e1){
				System.out.println(e1.getMessage());
			}
		}
		
	}
	
	class PrepareTravelSurvey implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			try {
				String choiceDescription = content.paneModelCalibration.tabPrepareBiogemeCalibration.line5.myText.getText();
				String hypothesis = content.paneModelCalibration.tabPrepareBiogemeCalibration.line6.myText.getText();
				String pathToTravelSurvey = content.paneModelCalibration.tabPrepareBiogemeCalibration.line7.myText.getText();
				int nThreads = (int) content.paneModelCalibration.tabPrepareBiogemeCalibration.line8.myComboBox.getSelectedItem();
				String nAlt = content.paneModelCalibration.tabPrepareBiogemeCalibration.line9.myText.getText();
				int nAlternatives = Integer.parseInt(nAlt.trim());
				String output = content.paneModelCalibration.tabPrepareBiogemeCalibration.line10.myText.getText();
				
				BiogemeControlFileGenerator myCtrlGenerator = new BiogemeControlFileGenerator();
				TravelSurveyPreparator odGatineau = new TravelSurveyPreparator();
	    		myCtrlGenerator.initialize(choiceDescription, hypothesis);
	    		odGatineau.initialize(pathToTravelSurvey,output);
		    	
		    	System.out.println("--computation with: " + nThreads + " logical processors");
		    	odGatineau.processDataMultiThreads(nThreads, nAlternatives, myCtrlGenerator);
		    	System.out.println("--data was prepared");
		    	
			} catch (IOException | NumberFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.out.println(e1.getMessage());
			}
		}
		
	}
	
	class PrepareBiogemeCtrlFile implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String choiceDescription = content.paneModelCalibration.tabPrepareBiogemeCalibration.line1.myText.getText();
			String hypothesis = content.paneModelCalibration.tabPrepareBiogemeCalibration.line2.myText.getText();
			String output = content.paneModelCalibration.tabPrepareBiogemeCalibration.line3.myText.getText();
			
			BiogemeControlFileGenerator myCtrlGenerator = new BiogemeControlFileGenerator();
	    	try {
	    		myCtrlGenerator.initialize(choiceDescription, hypothesis);
				myCtrlGenerator.generateBiogemeControlFile(output);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.out.println(e1.getMessage());
			}
		}
		
	}
	
	class PrepareGlobalPopulationSynthesisButton implements ActionListener{
		public void actionPerformed(ActionEvent arg0){
			String data = content.panePopulationSynthesis.tabPreparePopulationSynthesis.line1.myText.getText();
			String dataDescFile = content.panePopulationSynthesis.tabPreparePopulationSynthesis.line2.myText.getText();
			String destPath = content.panePopulationSynthesis.tabPreparePopulationSynthesis.line3.myText.getText();
			ConditionalGenerator condGenerator = new ConditionalGenerator();
			try {
				condGenerator.GenerateConditionalsStepByStep(data,dataDescFile,destPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			}
		}
	}
		
	class PrepareLocalPopulationSynthesisButton implements ActionListener{

		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			String data = content.panePopulationSynthesis.tabPreparePopulationSynthesis.line5.myText.getText();
			System.out.println("<html>-- start  creating local distributions from <br>	"+ data +"</html>");
			CensusPreparator census = new CensusPreparator(data);
			
	    	try {
				census.createLocalConditionalDistributions();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(e.toString());
			}
	    	System.out.println("<html>-- done creating local distributions</html>");
		}
	}
	
	
	class HelpListenerButton implements MouseListener{
		public void actionPerformed(MouseEvent arg0){}

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			String hlp = ((Button)e.getSource()).help.toString();
			informationPane.helpText.setText(hlp);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			informationPane.helpText.setText("--");
		}
	}
	
	class HelpListenerComboBox implements MouseListener{
		public void actionPerformed(MouseEvent arg0){}

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			String hlp = ((ComboBox)e.getSource()).help.toString();
			informationPane.helpText.setText(hlp);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			informationPane.helpText.setText("--");
		}
	}
	
	class HelpListenerText implements MouseListener{
		public void actionPerformed(MouseEvent arg0){}

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			String hlp = ((TextField)e.getSource()).help.toString();
			informationPane.helpText.setText(hlp);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			informationPane.helpText.setText("--");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {}
		  
}
