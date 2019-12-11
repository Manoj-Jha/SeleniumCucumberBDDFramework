
package com.TestRunner;

import cucumber.api.CucumberOptions;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;

import org.testng.annotations.Parameters;
import cucumber.api.testng.TestNGCucumberRunner;
import com.reports.Report;
import com.reports.ReportContext;
import cucumber.api.testng.CucumberFeatureWrapper;

import java.io.IOException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Drivers.TestSetup;
import com.reports.*;


@CucumberOptions(
		features = "src/test/java/features",
		glue = {"stepDefinitions","runner"},
		tags = {"@wip"})

public class TestRunner {
	private TestNGCucumberRunner testNGCucumberRunner;
	public static WebDriver driver = null;

	@BeforeSuite(alwaysRun = true)
	@Parameters({"browser"})
	public void setUpClass(String browser) throws Exception {
		testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
		Report.LoadConfigProperty("reportConfig");
		String reportTemplatePath = Report.config.getProperty("reportTemplatePath");
		ReportContext.setContext("ReportTemplatePath", reportTemplatePath);
		setUpReportDirectories();
		System.out.println("Temporary Directories ready for report generation");
		TestSetup.openBrowser(browser);
	}

	@Test(groups = "cucumber", description = "Runs Cucumber Feature", dataProvider = "features")
	public void feature(CucumberFeatureWrapper cucumberFeature) {
		testNGCucumberRunner.runCucumber(cucumberFeature.getCucumberFeature());
	}

	@DataProvider
	public Object[][] features() {
		return testNGCucumberRunner.provideFeatures();
	}

	@Before
	public void beforeScenario(Scenario scenario){
		String scenarioName = scenario.getName();
		String[] idAndDescription = scenarioName.split("-");
		String testCaseId = idAndDescription[0];
		System.out.println(testCaseId);
		String testCaseDescription = idAndDescription[1];
		System.out.println(testCaseDescription);
		System.out.println("Running : "+scenarioName);
		Report.TestCaseStarts(testCaseId, testCaseDescription);
	}

	/*@After
	public void afterScenario() throws IOException{
		HelperFunctions helpers = new HelperFunctions(driver);
		helpers.loadPage("Logout");
	}*/

	@AfterSuite(alwaysRun = true)
	@Parameters({"browser"})
	public void tearDownClass(String browser) throws Exception {
		System.out.println("REPORT CREATION BEGINS:::::");
		Report.CreateReportFromXML(browser);
		TestSetup.closeAllBrowser();
		testNGCucumberRunner.finish();
	}
/*
	public void openBrowser(String browserType) throws Exception {
		if (HelperFunctions.getOSName().toLowerCase().indexOf("win") >= 0){
			switch(browserType) {
			case "Chrome":
				System.setProperty("webdriver.chrome.driver", ".\\chromedriver.exe");
				driver = new ChromeDriver();
				break;
			case "Firefox":
				System.setProperty("webdriver.gecko.driver", ".\\geckodriver.exe");
				driver = new FirefoxDriver();
				break;
			}
		}
		else {
			switch(browserType) {
			case "Chrome":
				System.setProperty("webdriver.chrome.driver", "./chromedriver");
				driver = new ChromeDriver();
				break;
			case "Firefox":
				System.setProperty("webdriver.gecko.driver", "./geckodriver");
				driver = new FirefoxDriver();
				break;
			}
		}
	}

	public void maximizeWindow() {
		driver.manage().window().maximize();
	}

	public void setEnv() throws Exception {
		HelperFunctions.LoadConfigProperty("devConfig");
		String baseUrl = HelperFunctions.config.getProperty("baseUrl");
		driver.get(baseUrl);
	}

	public void setupBrowserAndEnv(String browser) throws Exception{
		openBrowser(browser);
		maximizeWindow();
		setEnv();
	}

	public void quit() throws IOException, InterruptedException {
		driver.quit();
	}

	public WebDriver getDriver() {
		return driver;
	} 
	*/

	public void setUpReportDirectories() {
		Report.getMachineName();
		String machineName = ReportContext.getContext("ReportHostName").toLowerCase();
		
		String TempFolderPath = System.getProperty("java.io.tmpdir");
		String varTimeStemp = Report.getSplitedCurrentTime();
		
		ReportContext.setContext("ReportStartExecutionTime", Report.getCurrentTime());

		String ReportTempFolderPath = TempFolderPath + "\\Report -"+machineName +"-" + varTimeStemp ;
		String ReportTempFilesFolderPath = ReportTempFolderPath + "\\Report-"+machineName +"-"+  varTimeStemp ;
		String ReportTempXMLFilePath= ReportTempFilesFolderPath + "\\Report-" +machineName +"-"+ varTimeStemp + ".xml";

		if (Report.getOSName().toLowerCase().indexOf("win") >= 0) 
		{

		} 
		else 
		{
			ReportTempFolderPath = ReportTempFolderPath.replace("\\", "/");
			ReportTempXMLFilePath = ReportTempXMLFilePath.replace("\\", "/");
			ReportTempFilesFolderPath = ReportTempFilesFolderPath.replace("\\", "/");
		}

		ReportContext.setContext("ReportTempFolderPath", ReportTempFolderPath);
		ReportContext.setContext("ReportTempXMLFilePath", ReportTempXMLFilePath);
		ReportContext.setContext("ReportTempFilesFolderPath", ReportTempFilesFolderPath);
		Report.CreateFolder(ReportTempFolderPath);
		Report.CreateFolder(ReportTempFilesFolderPath);
		Report.CreateFile("",ReportTempXMLFilePath);
	}

}
