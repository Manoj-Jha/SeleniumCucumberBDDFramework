package stepDefinitions;

import com.TestRunner.TestRunner;
import com.reports.Report;

import cucumber.api.java.en.Given;

public class Login {
	
	TestRunner run= new TestRunner();
	@Given("^Open the browser and launch the appication$")
	public void goToHomepage() { 
		try {
			run.setUpClass("firefox");
			Report.Remarks("Browser launched", "pass", "");
		} catch (Exception e) {
			Report.Remarks("Unable to launch the browser", "fail", "");
			e.printStackTrace();
		}
	   } 
}
