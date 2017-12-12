package org.automation.main;

import org.axe.*;
import org.core.Corewrappers;
import org.excel.ExcelRead;
import org.json.JSONArray;
import org.json.JSONObject;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.Wait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class StartExecutionAccessability extends Corewrappers {

	public final Logger logger = Logger.getLogger(StartExecutionAccessability.class);
	public String fileName;
	public String[][] b;
	public static String seleniumHome = System.getProperty("user.dir");
	public static String murl;
	public static String browser;
	public static String dbMachineName;
	public static String dbSid;
	public static String schemaName;
	public static String schemaPassword;
	public static int dbPort;
	public String classname, methodname;
	public static Connection con;
	private static String comment = null;
	private static final URL scriptUrl = StartExecutionAccessability.class.getResource("/axe.min.js");

	@BeforeSuite
	public void beforesuite() {
		try {
			System.out.println("befor suite.....");
			PropertyConfigurator.configure("conf" + File.separator + "log4j.properties");
			/* Read build.property file contents and save */
			Properties prop = new Properties();
			prop.load(new FileReader("conf" + File.separator + "build.properties"));
			murl = prop.getProperty("url");
			browser = prop.getProperty("browser");
			dbPort = Integer.parseInt(prop.getProperty("dbPort"));
			dbMachineName = prop.getProperty("dbMachineName");
			dbSid = prop.getProperty("dbSid");
			schemaName = prop.getProperty("schemaName");
			schemaPassword = prop.getProperty("schemaPassword");
			/* Establish database connection */
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@" + dbMachineName + ":" + dbPort + ":" + dbSid,
					schemaName, schemaPassword);
			if (browser.toLowerCase().equals("chrome")) {
				System.setProperty("webdriver.chrome.driver", "driver\\ChomeDriver\\chromedriver.exe");

				driver = new ChromeDriver();
				driver.manage().window().maximize();

			} else if (browser.toLowerCase().equals("firefox")) {
				System.setProperty("webdriver.gecko.driver", "driver\\FFDriver\\geckodriver.exe");
				driver = new FirefoxDriver();
			} else if (browser.toLowerCase().equals("ie")) {
				System.setProperty("webdriver.ie.driver", "driver\\IEDriver\\IEDriverServer.exe");
				driver = new InternetExplorerDriver();
			}
			// driver.get(url);
			// driver.manage().window().maximize();
			Thread.sleep(5000);
		} catch (Exception e) {
			logger.error(e.toString());

		}
	}

	@AfterSuite
	public void afterSuite() throws InterruptedException {
		// Thread.sleep(5000);
		// to kill the chomredriver.exe process.
		// driver.quit();
		System.out.println("after suite.....");

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
		String[] args = { url, result };
		Class<?> classObj = null;
		Object obj = null;
		if (function.equalsIgnoreCase("acc")) {
			driver.get(murl+url);
			Thread.sleep(5000);
			
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

		if (violations.length() == 0) {
			assertTrue("No violations found", true);
			logger.info("No violations found");
		} else {
			AXE.writeResults(filename, responseJSON);
			assertTrue(AXE.report(violations), false);
			logger.info("violations found");
		}
	}

	@AfterMethod
	public void takeSnapshot(ITestResult result) {
		if (ITestResult.FAILURE == result.getStatus()) {
			try {
				TakesScreenshot ts = (TakesScreenshot) driver;
				File source = ts.getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(source, new File("log/ScreenShot/" + getComment() + ".png"));
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
