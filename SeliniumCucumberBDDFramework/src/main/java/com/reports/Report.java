package com.reports;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//import com.reports.*;

public class Report {
	
	public static String testCaseID;
	
	public static Properties config = null;

	public static void TestCaseStarts(String TestCaseID, String TestCaseDescription)
	{
		try{
			testCaseID=TestCaseID;
			String ReportTestCaseIDTemplate = ReportContext.getContext("ReportTemplatePath") + "TestCaseIDTemplate.txt";
			String ReportTestCaseIDtext;
			String ReportText;
			TestCaseID = replaceSpacialCharacterForReport(TestCaseID);
			TestCaseDescription = replaceSpacialCharacterForReport(TestCaseDescription);	
			ReportTestCaseIDtext = ReadAllFile(ReportTestCaseIDTemplate);
			ReportTestCaseIDtext = ReportTestCaseIDtext.replace("$testCaseID$", TestCaseID);
			ReportTestCaseIDtext = ReportTestCaseIDtext.replace("$TestCaseDescription$", " - " + TestCaseDescription);	
			String ReportTempXMLPath = ReportContext.getContext("ReportTempXMLFilePath");
			ReportText = ReadAllFile(ReportTempXMLPath);
			ReportText = ReportText.replace("$testCaseStep$", "");
			com.reports.Report.CreateFile(ReportText + ReportTestCaseIDtext , ReportTempXMLPath);
			ReportContext.setContext("ReportStepExecutionTime", String.valueOf(System.currentTimeMillis()));
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			Report.Remarks("Exception Inside Test case start", "Fail", "");
		}
	}
		
		public static void Remarks(String Description, String Status, String Remarks)
		{
			try
			{
				/*Date d1 = new Date();*/

				String ReportChildRowDescription = "";
				String ReportXMLText;
				String ReportChildRowPath = ReportContext.getContext("ReportTemplatePath") + "TCStepTemplate.txt";
				String ReportFolderPath = ReportContext.getContext("ReportTempFilesFolderPath");
				String ReportScreenShotName = ReportFolderPath + "\\" + String.valueOf(System.currentTimeMillis() + ".jpg"); 

				if (Report.getOSName().toLowerCase().indexOf("win") >= 0) 
				{
				} else {
					ReportScreenShotName = ReportScreenShotName.replace("\\", "/");
				}

				String ReportTempXMLPath;
				long ReportStepExecutionTime;

				if (Status.toLowerCase().equals("fail"))
				{
					//Actions.captureImage(ReportScreenShotName);
				} else 
				{
					ReportScreenShotName = "";
				}

				Remarks = replaceSpacialCharacterForReport(Remarks);
				Description = replaceSpacialCharacterForReport(Description);
				ReportChildRowDescription = ReportChildRowDescription + com.reports.Report.ReadAllFile(ReportChildRowPath);
				ReportChildRowDescription = ReportChildRowDescription.replace("$Child-Steps$", Description);
				ReportChildRowDescription = ReportChildRowDescription.replace("$Child-Status$", Status);
				ReportChildRowDescription = ReportChildRowDescription.replace("$Child-ImagePath$", ReportScreenShotName);
				ReportStepExecutionTime = (System.currentTimeMillis() - Long.parseLong(ReportContext.getContext("ReportStepExecutionTime")));
				ReportChildRowDescription = ReportChildRowDescription.replace("$Child-ExecutionTime$", String.valueOf(ReportStepExecutionTime));
				ReportContext.setContext("ReportStepExecutionTime", String.valueOf(System.currentTimeMillis()));
				ReportChildRowDescription = ReportChildRowDescription.replace("$Child-Remarks$", Remarks);
				ReportChildRowDescription = ReportChildRowDescription.replace("$StepStatus$", Status);	
				ReportTempXMLPath = ReportContext.getContext("ReportTempXMLFilePath");
				ReportXMLText = ReadAllFile(ReportTempXMLPath);
				ReportXMLText = ReportXMLText.replace("$testCaseStep$", ReportChildRowDescription);
				Report.CreateFile(ReportXMLText, ReportTempXMLPath);
			}
			catch(Exception ex)
			{
				System.out.println("SOMETHING WENT WRONG");
				ex.printStackTrace();
			}
		}
		
		public static String replaceSpacialCharacterForReport(String text)
		{
			String Description = "";
			try{
				Description = text.replace("&", "&amp;");
				Description = Description.replace("\"", "&quot;");
				Description = Description.replace("'", "&apos;");
				Description = Description.replace("<", "&lt;");
				Description = Description.replace(">", "&gt;");
			}
			catch(Exception ex)
			{

			}
			return Description;
		}
		
		@SuppressWarnings("resource")
		public static String ReadAllFile(String FilePath)
		{
			InputStream inputStream = null;
			BufferedReader br = null;
			try {
				inputStream = new FileInputStream(FilePath);
				br = new BufferedReader(new InputStreamReader(inputStream));
				StringBuilder sb = new StringBuilder();
				String line;

				while ((line = br.readLine()) != null) {

					sb.append(line); 

					sb.append('\n');

				}

				return sb.toString();

			}
			catch (Exception e){
				//System.out.println(e.getMessage());
			}
			return null;
		}
		
		public static void copyFoldersStructure(String srcFolderPath, String destFolderPath)
		{	
			File srcFolder = new File(srcFolderPath);
			File destFolder = new File(destFolderPath);
			if(!srcFolder.exists()){
				System.out.println("Directory does not exist.");
				System.exit(0);
			}
			else{
				try{
					copyFolder(srcFolder,destFolder);
				}catch(Exception e){
					System.exit(0);
				}
			}
			System.out.println("Done");
		}
		
		public static void copyFolder(File src, File dest)  {
			try { 
				if(src.isDirectory()){
					//if directory not exists, create it
					if(!dest.exists()){
						dest.mkdir();
						System.out.println("Directory copied from " 
								+ src + "  to " + dest);
					}

					//list all the directory contents
					String files[] = src.list();
					for (String file : files) {
						//construct the src and dest file structure
						File srcFile = new File(src, file);
						File destFile = new File(dest, file);
						//recursive copy
						copyFolder(srcFile,destFile);
					}

				}
				else{
					//if file, then copy it
					//Use bytes stream to support all file types
					InputStream in = new FileInputStream(src);
					OutputStream out = new FileOutputStream(dest); 

					byte[] buffer = new byte[1024];

					int length;
					//copy the file content in bytes 
					while ((length = in.read(buffer)) > 0){
						out.write(buffer, 0, length);
					}
					in.close();
					out.close();
					System.out.println("File copied from " + src + " to " + dest);
				}
			} 
			catch (Exception e){
				System.out.println("Files couldn't copy from  " + src + " to " + dest + ". Error message :" + e.getMessage());
			}
		}
		
		public static void CreateFile(String text, String FilePath)
		{
			BufferedWriter out =null;
			try
			{
				File file = new File (FilePath);			
				out = new BufferedWriter(new FileWriter(file)); 
				out.write(text);
				//out.close();
			} 
			catch (Exception e)
			{
				System.out.println(e.getMessage());
			}
			finally{
				if(out!=null)
				{
					try {
						out.flush();
						//out.close();

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		public static void CreateFolder(String FilePath)
		{
			try
			{
				File file = new File (FilePath);
				file.mkdir();
				System.out.println("Folder Created : "+ FilePath);
			} 
			catch (Exception e)			
			{
				System.out.println(e.getMessage());
			}
		}

		public static void getMachineName()
		{
			String hostname = "Unknown";

			try
			{
				InetAddress addr;
				addr = InetAddress.getLocalHost();
				hostname = addr.getHostName();
				ReportContext.setContext("ReportHostName", hostname);

			}
			catch (UnknownHostException ex)
			{
				ReportContext.setContext("ReportHostName", "Unknown");

			}
		}
//-----------------------------------------------
		public static String ConvertMicroSecondIntoTime(String stepTiming) 
		{
			int seconds = 0;
			int minutes = 0;
			int hours = 0;

			int MicroSeconds = Integer.parseInt(stepTiming);
			final int MINUTES_IN_AN_HOUR = 60;
			final int SECONDS_IN_A_MINUTE = 60;
			final int MICROSECOND_IN_A_SECOND = 1000;

			seconds = MicroSeconds / MICROSECOND_IN_A_SECOND;
			MicroSeconds -= seconds * MICROSECOND_IN_A_SECOND;

			minutes = seconds / SECONDS_IN_A_MINUTE;
			seconds -= minutes * SECONDS_IN_A_MINUTE;

			hours = minutes / MINUTES_IN_AN_HOUR;
			minutes -= hours * MINUTES_IN_AN_HOUR;

			return hours+ ":" + minutes + ":" + seconds ;	
		}
		
		public static String getSplitedCurrentTime()
		{
			try{
				String CurrentTime = getCurrentTime();
				CurrentTime = CurrentTime.replace("/", "-");
				CurrentTime = CurrentTime.replace(":", "-");
				CurrentTime = CurrentTime.replace(" ", "-");
				return CurrentTime;
			}
			catch(Exception ex)
			{

			}
			return "";
		}

		public static String getCurrentTime()
		{
			try{
				Calendar now = Calendar.getInstance();
				int year = now.get(Calendar.YEAR);
				int month = now.get(Calendar.MONTH); // Note: zero based!
				int day = now.get(Calendar.DAY_OF_MONTH);
				int hour = now.get(Calendar.HOUR_OF_DAY);
				int minute = now.get(Calendar.MINUTE);
				int second = now.get(Calendar.SECOND);
				return String.valueOf(month+1) + "/" + String.valueOf(day) + "/" + String.valueOf(year)+" " 
				+ String.valueOf(hour) + ":" + String.valueOf(minute) + ":" + String.valueOf(second);
			}
			catch(Exception ex)
			{

			}
			return "";
		}

		public static String getOSName()
		{
			String OSName= "";
			try
			{
				OSName = System.getProperty("os.name");
			} 
			catch (Exception e )
			{
				OSName = "unknown";
			}
			return OSName;
		}
		
		/*
		 Makes the .properties data file readable
		 */
		public static void LoadConfigProperty(String fileName) throws IOException {
			config = new Properties();
			FileInputStream ip = new FileInputStream(
					System.getProperty("user.dir") + "//src//main//java//com//reports//"+fileName+".properties");
			config.load(ip);
		}
		//-------------------------------------------------
		public static void CreateReportFromXML(String browser)
		{
			try {
				String ReportHeaderTemplatePath = ReportContext.getContext("ReportTemplatePath") + "HtmlReportTemplate.htm";
				String ReportParentTemplatePath = ReportContext.getContext("ReportTemplatePath") + "ParentRow.htm";;
				String ReportChildTemplatePath =  ReportContext.getContext("ReportTemplatePath") + "ChildRow.htm";
				String ReportHTMLBodyTemplatePath = ReportContext.getContext("ReportTemplatePath") + "ReportTemplate.txt";
				String ReportHeaderText;
				String ReportParentText = "";
				String ReportChildText = "";
				String ReportCompleteText = "";
				String FinalStatus;
				String FinalChildRemarks;
				String ReporTestCaseName = "";
				String ReportTestCaseDesription = "";
				String ReportTempXMLPath;
				String ReportText;
				String AltStyle;
				String ReportHTMLBodyText;
				int TotalPassed = 0;
				int TotalFailed =0;
				//int TotalWarning=0;
				int TestCaseTiming = 0;
				int SuiteTiming= 0 ;

				ReportTempXMLPath = ReportContext.getContext("ReportTempXMLFilePath");
				ReportText = Report.ReadAllFile(ReportTempXMLPath);
				ReportHTMLBodyText = Report.ReadAllFile(ReportHTMLBodyTemplatePath);
				ReportHTMLBodyText = ReportHTMLBodyText.replace("$XMLBody$", ReportText);
				Report.CreateFile(ReportHTMLBodyText, ReportTempXMLPath);
				ReportText = Report.ReadAllFile(ReportTempXMLPath);
				ReportHeaderText = ReadAllFile(ReportHeaderTemplatePath);	
				File fXmlFile = new File(ReportTempXMLPath);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);      
				doc.getDocumentElement().normalize(); 
				NodeList nList = doc.getElementsByTagName("TestCaseName");

				for (int temp = 0; temp < nList.getLength(); temp++) 
				{     		
					FinalStatus  = "Pass";
					FinalChildRemarks = "";

					Node nNode = nList.item(temp);
					Element ChildElement = (Element) nNode.getChildNodes(); 
					int TestCaseStepCount = ChildElement.getChildNodes().getLength();
					ReportParentText = ReadAllFile(ReportParentTemplatePath);

					for (int TestStepCounter = 0; TestStepCounter < TestCaseStepCount/2; TestStepCounter++ )	
					{       		
						ReportChildText = ReportChildText.trim() + ReadAllFile(ReportChildTemplatePath);
						if (nNode.getNodeType() == Node.ELEMENT_NODE) 
						{
							Element eElement = (Element) nNode; 
							ReporTestCaseName   = eElement.getAttribute("id");
							ReportTestCaseDesription   = eElement.getAttribute("description");
							String ReporTestStepRemarks = eElement.getElementsByTagName("Child-Steps").item(TestStepCounter).getTextContent();
							String ReporTestStepStatus = eElement.getElementsByTagName("Child-Status").item(TestStepCounter).getTextContent();
							String ReportTestStepExecutionTime = eElement.getElementsByTagName("Child-ExecutionTime").item(TestStepCounter).getTextContent();
							String ReportTestStepDescription = eElement.getElementsByTagName("Child-Remarks").item(TestStepCounter).getTextContent();
							String ReportTestStepChildImagePath = eElement.getElementsByTagName("Child-ImagePath").item(TestStepCounter).getTextContent();
							ReportChildText = ReportChildText.replace("$StepNo$", String.valueOf(TestStepCounter+1));
							ReportChildText = ReportChildText.replace("$Row-Childs-Id$", "RowChildsId"+String.valueOf(temp+1));
							ReportChildText = ReportChildText.replace("$RowId$", "ChildRowId"+ String.valueOf(TestStepCounter+1));
							ReportChildText = ReportChildText.replace("$Child-Steps$", ReporTestStepRemarks);

							if (ReporTestStepStatus.toLowerCase().equals("fail"))
							{
								String ImageURLTAG = "<a style=\"color: red;\" target=\"_blank\" href=\"ImagePath\">";
								ImageURLTAG = ImageURLTAG.replace("ImagePath", ReportTestStepChildImagePath);
								ReportChildText = ReportChildText.replace("$Child-Status$", ImageURLTAG+ReporTestStepStatus);
							}  else {
								ReportChildText = ReportChildText.replace("$Child-Status$", ReporTestStepStatus);
							}					        
							TestCaseTiming = TestCaseTiming +  Integer.parseInt(ReportTestStepExecutionTime);				       
							ReportChildText = ReportChildText.replace("$Child-ExecutionTime$", ConvertMicroSecondIntoTime(ReportTestStepExecutionTime));	   				        
							ReportChildText = ReportChildText.replace("$Child-Remarks$", ReportTestStepDescription);		       
							ReportChildText = ReportChildText.replace("$StepStatus$", ReporTestStepStatus);
							if ( TestStepCounter % 2==0 )
							{
								AltStyle = "";
							} 
							else 
							{
								AltStyle = "class=\"alt\"";
							}		        
							ReportChildText = ReportChildText.replace("$AltStyle$", AltStyle);

							if (ReporTestStepStatus.equals("Fail"))
							{
								FinalStatus = "Fail";
								if ((FinalChildRemarks).length() == 0 ) 
								{
									FinalChildRemarks = ReportTestStepDescription;
								}
							}

						} 

					} 

					ReportCompleteText = ReportCompleteText + ReportParentText.replace("$HTMLChildRows$", ReportChildText);	
					ReportCompleteText = ReportCompleteText.replace("$Row-Childs-Id$", "RowChildsId"+String.valueOf(temp+1));
					ReportCompleteText = ReportCompleteText.replace("$TestCaseId$", ReporTestCaseName);
					ReportCompleteText = ReportCompleteText.replace("$TestCaseName$", ReportTestCaseDesription);
					ReportCompleteText = ReportCompleteText.replace("$FinalStatus$", FinalStatus);
					ReportCompleteText = ReportCompleteText.replace("$FinalStatusClass$", FinalStatus);
					ReportCompleteText = ReportCompleteText.replace("$Remarks$", FinalChildRemarks);
					ReportCompleteText = ReportCompleteText.replace("$Browser$", browser); // Actions.GetVariable("Device"));
					ReportCompleteText = ReportCompleteText.replace("$RowId$", "RowId"+String.valueOf(temp+1));
					ReportCompleteText = ReportCompleteText.replace("$TotalTestcaseDuration$", ConvertMicroSecondIntoTime(String.valueOf(TestCaseTiming)));
					getMachineName();
					ReportCompleteText = ReportCompleteText.replace("$Machine$", ReportContext.getContext("ReportHostName"));              	

					if (FinalStatus.equals("Pass"))
					{
						TotalPassed = TotalPassed + 1;
					} 
					else if (FinalStatus.equals("Fail"))
					{
						TotalFailed = TotalFailed + 1;
					}

					ReportChildText= "";
					FinalChildRemarks ="";
					SuiteTiming = SuiteTiming + TestCaseTiming ;
					TestCaseTiming = 0;

				}

				ReportHeaderText = ReportHeaderText.replace("$TotalPassed$", String.valueOf(TotalPassed)); 
				ReportHeaderText = ReportHeaderText.replace("$TotalFailed$", String.valueOf(TotalFailed));
				ReportHeaderText = ReportHeaderText.replace("$TotalWarning$", "0");
				ReportHeaderText = ReportHeaderText.replace("$TestCase(s)Executed$", String.valueOf(TotalFailed + TotalPassed ));
				ReportHeaderText = ReportHeaderText.replace("$ExecutionStartedAt$", ReportContext.getContext("ReportStartExecutionTime"));
				ReportHeaderText = ReportHeaderText.replace("$ExecutionFinishedAt$", Report.getCurrentTime());
				Report.LoadConfigProperty("reportConfig");
				ReportHeaderText = ReportHeaderText.replace("$Environment$", Report.config.getProperty("Environment"));
				ReportHeaderText = ReportHeaderText.replace("$TestSuitName$", "Demo Cucumber BDD Test Suite");
				ReportHeaderText = ReportHeaderText.replace("$TotalDuration$", ConvertMicroSecondIntoTime(String.valueOf(SuiteTiming)));       
//				ReportHeaderText = ReportHeaderText.replace("$iOS Version$", Actions.GetEnviornmetVariable("AppiumPlatformVersion"));
				ReportCompleteText = ReportHeaderText.replace("$TableBody$", ReportCompleteText);
				String ReportFinalBasePath;

				if(TotalFailed + TotalPassed > Integer.parseInt((Report.config.getProperty("TestCaseCountToChangeResultPath"))))
				{
					ReportFinalBasePath = Report.config.getProperty("TestSuiteResultsPath");
				} else 
				{
					ReportFinalBasePath = Report.config.getProperty("TestResultsLocalPath");
				}

				String CurrentSystemTiming = Report.getSplitedCurrentTime();
				Report.getMachineName();
				String MachineName = ReportContext.getContext("ReportHostName");
				String ReportFinalPath = ReportFinalBasePath+"\\Report-" + MachineName +"-" + CurrentSystemTiming + ".html";

				if (Report.getOSName().toLowerCase().indexOf("win") >= 0) 
				{
				} else {
					ReportFinalPath = ReportFinalPath.replace("\\", "/");
				}

				String ReportTempFolderPath = ReportContext.getContext("ReportTempFolderPath");
				System.out.println("ReportTempFolderPath: " + ReportTempFolderPath );
				System.out.println("ReportFinalBasePath: " + ReportFinalBasePath );  		
				copyFoldersStructure(ReportTempFolderPath, ReportFinalBasePath);		 		
				ReportCompleteText = ReportCompleteText.replace(ReportTempFolderPath, ReportFinalBasePath);  		   		
				CreateFile(ReportCompleteText, ReportFinalPath);
				System.out.println("ReportFinalPath: " + ReportFinalPath ); 

			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}

}
