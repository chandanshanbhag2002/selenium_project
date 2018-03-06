package org.automation.main;

import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertFalse;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.core.Corewrappers;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.axe.*;
import org.excel.*;

public class StartExecutionAccessability extends Corewrappers {

	public final Logger logger = Logger.getLogger(StartExecutionAccessability.class);
	public String[][] b;
	public static String seleniumHome = System.getProperty("user.dir");
	public static String webdriver, username, password, classname, methodname, murl, browser, dbMachineName, dbSid,
			schemaName, schemaPassword, fileName;
	public static URL serverurl;
	public static Connection con;
	private static String comment = null;
	private static final URL scriptUrl = StartExecutionAccessability.class.getResource("/axe.js");

	@BeforeSuite
	public void beforesuite() {
		try {
			System.out.println("befor suite.....");
			PropertyConfigurator.configure("conf" + File.separator + "log4j.properties");
			/* Read build.property file contents and save */
			Properties prop = new Properties();
			prop.load(new FileReader("conf" + File.separator + "build.properties"));
			murl = prop.getProperty("url");
			username = prop.getProperty("username");
			password = prop.getProperty("password");
			browser = prop.getProperty("browser");
			webdriver = prop.getProperty("webdriver");
			// "driver\\ChomeDriver\\chromedriver.exe"
			// "driver\\FFDriver\\geckodriver.exe"
			// "driver\\IEDriver\\IEDriverServer.exe"
			if (System.getProperty("os.name").equalsIgnoreCase("windows")) {
				if (browser.toLowerCase().equals("chrome")) {
					System.setProperty("webdriver.chrome.driver", webdriver);
					driver = new ChromeDriver();
					driver.manage().window().maximize();
				} else if (browser.toLowerCase().equals("firefox")) {
					System.setProperty("webdriver.gecko.driver", webdriver);
					driver = new FirefoxDriver();
					driver.manage().window().maximize();
				} else if (browser.toLowerCase().equals("ie")) {
					System.setProperty("webdriver.ie.driver", webdriver);
					driver = new InternetExplorerDriver();
					driver.manage().window().maximize();
				}
			} else if (System.getProperty("os.name").equalsIgnoreCase("linux")) {
				if (browser.toLowerCase().equals("chrome")) {
					DesiredCapabilities capabilities = DesiredCapabilities.chrome();
					serverurl = new URL("http://localhost:9515");
					driver = new RemoteWebDriver(serverurl, capabilities);
					driver.manage().window().maximize();
				} else if (browser.toLowerCase().equals("firefox")) {
					DesiredCapabilities capabilities = DesiredCapabilities.firefox();
					serverurl = new URL("http://localhost:9515");
					driver = new RemoteWebDriver(serverurl, capabilities);
					driver.manage().window().maximize();
				} else if (browser.toLowerCase().equals("ie")) {
					DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
					serverurl = new URL("http://localhost:9515");
					driver = new RemoteWebDriver(serverurl, capabilities);
					driver.manage().window().maximize();
				}
			}

		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

	@AfterSuite
	public void afterSuite() throws InterruptedException {
		System.out.println("after suite.....");
		// driver.quit();
		driver.close();
	}

	@BeforeTest
	@Parameters({ "testname" })
	public void beforeTest(String testname) {
		fileName = seleniumHome + File.separator + testname;
	}

	@DataProvider(name = "excelData")
	public Object[][] createData() {
		logger.info("Reading excel data using @Dataprovider");
		return ExcelRead.readexcel(fileName);
	}

	@SuppressWarnings({ "rawtypes" })
	@Test(dataProvider = "excelData")
	public void execute(String tcId, String comment, String function, String url, String result)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException,
			SecurityException, IllegalArgumentException, InvocationTargetException, InterruptedException {

		logger.info("Entering Main method...");
		logger.info("Absolute path of the Project : " + seleniumHome);

		this.setComment(comment);
		String[] args = { url, result };
		Class<?> classObj = null;
		Object obj = null;

		if (function.equalsIgnoreCase("login")) {

			login(username, password,driver);
			logoWait(driver);
			// progressWait(driver);
		} else if (function.equalsIgnoreCase("goto")) {
			driver.get(murl + url);
			logoWait(driver);
			// progressWait(driver);
			// alertWait(driver);
			logger.info("navigated to " + url);

		} else if (function.equalsIgnoreCase("acc")) {
			StartExecutionAccessability sa = new StartExecutionAccessability();
			sa.testAccessibility(result);

		} else {
			classObj = Class.forName("org.core.Corewrappers");
			obj = classObj.newInstance();
			methodname = function;
			Class[] classArr = null;
			ArrayList<Class> classList = new ArrayList<Class>();
			ArrayList<String> largs = new ArrayList<String>();
			for (int i = 0; i < args.length; i++) {
				if (args[i] != null && !args[i].equals("")) {
					classList.add(String.class);
					largs.add(args[i]);
				}
			}
			classArr = new Class[classList.size()];
			classArr = classList.toArray(classArr);
			Method m = classObj.getMethod(methodname, classArr);
			m.invoke(obj, largs.toArray());
		}
	}

	public void testAccessibility(String filename) {
		JSONObject responseJSON = new AXE.Builder(driver, scriptUrl).analyze();
		JSONArray violations = responseJSON.getJSONArray("violations");
		AXE.writeResults(filename, responseJSON);
		String v = AXE.report(violations);
		logger.info("violations found");
		if (v.isEmpty()) {
			assertTrue("No violations found", true);
			logger.info("No violations found");
		} else {
			assertFalse(true, v);
		}
	}

	@AfterMethod
	public void takeSnapshot(ITestResult result) {
		if (ITestResult.FAILURE == result.getStatus()) {
			try {
				TakesScreenshot ts = (TakesScreenshot) driver;
				File source = ts.getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(source,
						new File("log" + File.separator + "ScreenShot" + File.separator + getComment() + ".png"));
				System.out.println("Screenshot taken");
			} catch (Exception e) {

				System.out.println("Exception while taking screenshot " + e.getMessage());
			}
		}

	}

	public static String getComment() {
		return comment;
	}

	public static void setComment(String comment) {
		StartExecutionAccessability.comment = comment;
	}

}
