package com.Drivers;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import com.reports.Report;

public class TestSetup {

public static WebDriver driver = null;
	
	

	public static void openBrowser(String browserName) throws Exception {
		if (Report.getOSName().toLowerCase().indexOf("win") >= 0) {

			// Singleton pattern.

			if (driver == null) {
				if (browserName.equalsIgnoreCase("chrome")) {

					System.setProperty("webdriver.chrome.driver",
							System.getProperty("user.dir") + "/drivers/chromedriver.exe");
					driver = new ChromeDriver();

				} else if (browserName.equalsIgnoreCase("firefox")) {
					System.setProperty("webdriver.gecko.driver",
							System.getProperty("user.dir") + "/drivers/geckodriver.exe");
					driver = new FirefoxDriver();

				}
				driver.manage().deleteAllCookies();
				driver.manage().window().maximize();
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
				Report.LoadConfigProperty("reportConfig");
				String baseUrl = Report.config.getProperty("Url");
				driver.get(baseUrl);

			}

		}
	}

	public static void closeAllBrowser() {

		System.out.println("Quitting all browser");
		driver.quit();
		driver = null;

	}

	public static void closeBrowser() {

		System.out.println("Closing browser");
		driver.close();
		driver = null;

	}
	
	public WebDriver getDriver() {
		return driver;
	}

}
