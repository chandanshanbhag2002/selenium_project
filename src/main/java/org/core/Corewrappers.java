package org.core;

import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class Corewrappers {

	public final Logger logger = Logger.getLogger(Corewrappers.class);
	public static WebDriver driver = null;
	public String getLocType(String locator) {
		return (locator.split("~")[0].toLowerCase());
	}

	public String getLocator(String locator) {
		return (locator.split("~")[1]);
	}

	public WebElement getWebElement(String locator) {
		WebElement webElement = null;

		try {

			if (getLocType(locator).equalsIgnoreCase("XPATH")) {

				webElement = driver.findElement(By.xpath(getLocator(locator)));

			} else if (getLocType(locator).equalsIgnoreCase("ID")) {

				webElement = driver.findElement(By.id(getLocator(locator)));

			} else if (getLocType(locator).equalsIgnoreCase("NAME")) {

				webElement = driver.findElement(By.name(getLocator(locator)));

			} else if (getLocType(locator).equalsIgnoreCase("CSS")) {

				webElement = driver.findElement(By.cssSelector(getLocator(locator)));

			} else if (getLocType(locator).equalsIgnoreCase("LINKTEXT")) {

				webElement = driver.findElement(By.linkText(getLocator(locator)));
			}

		} catch (Exception e) {
			logger.error(e);
		}

		return webElement;
	}

	public void click(String locator) throws InterruptedException {
		WebElement w = getWebElement(locator);
		if (w.isDisplayed() || w.isEnabled()) {
			w.click();

		}
	}

	public void login(String username, String password, WebDriver driver) throws InterruptedException {
		//logoWait(driver);
		this.setText("id~username", username);
		this.setText("name~pwd", password);
		this.click("xpath~//*[@id='loginButton']/div");
	}

	public void logoWait(WebDriver driver) throws InvalidSelectorException {
		WebElement ele = driver.findElement(By.className("content-loader"));

		try {
			if (ele.isDisplayed() || ele.isEnabled() || ele.isSelected()) {

				WebDriverWait wait = new WebDriverWait(driver, 30);
				By addItem = By.className("content-loader");
				WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(addItem));
				wait.until(ExpectedConditions.stalenessOf(element));
			}
		} catch (InvalidSelectorException e) {
			logger.info("no class found");
		}
	}

	public void progressWait(WebDriver driver) throws InvalidSelectorException {
		WebElement ele = driver.findElement(By.className("pace  pace-active"));
		// WebElement ele2=driver.findElement(By.className("pace-progress"));
		try {

			if (ele.isDisplayed() || ele.isEnabled() || ele.isSelected()) {
				// if (ele2.isDisplayed() || ele2.isEnabled() ||
				// ele2.isSelected()) {
				WebDriverWait wait = new WebDriverWait(driver, 30);
				By addItem = By.className("pace  pace-active");
				WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(addItem));
				wait.until(ExpectedConditions.stalenessOf(element));
				// }
			}
		} catch (InvalidSelectorException e) {
			logger.info("no class found");
		}
	}

	public void alertWait(WebDriver driver) throws InvalidSelectorException {
		WebElement ele = driver.findElement(By.className("alert alert-info"));
		try {
			if (ele.isDisplayed() || ele.isEnabled() || ele.isSelected()) {
				WebDriverWait wait = new WebDriverWait(driver, 30);
				By addItem = By.className("alert alert-info");
				WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(addItem));
				wait.until(ExpectedConditions.stalenessOf(element));
			}
		} catch (InvalidSelectorException e) {
			logger.info("no class found");
		}
	}

	public void setText(String locator, String args) {
		WebElement w = getWebElement(locator);
		if (w.isDisplayed()) {
			w.sendKeys(args);
		}

	}
	public void impicitWait(int i) throws InterruptedException{
		driver.manage().timeouts().implicitlyWait(i, TimeUnit.SECONDS);
	}
	
	public void toWait() throws InterruptedException{	
		/*System.out.println(i);
	int j=Integer.parseInt(i.split(" .")[0])*1000;
	System.out.println(j);*/
	
	Thread.sleep(6000);
	}

}
