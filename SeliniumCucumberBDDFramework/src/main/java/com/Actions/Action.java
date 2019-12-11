package com.Actions;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class Action {

	public WebDriver driver;
	
	public Action(WebDriver driver) {
		this.driver = driver;
	}
	
	public void captureImage(String ReportScreenShotName) throws IOException {
		File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(scrFile, new File(ReportScreenShotName));
	}
}
