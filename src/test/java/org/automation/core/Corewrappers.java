package org.automation.core;

import org.automation.main.StartExecution;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.apache.log4j.Logger;


public class Corewrappers extends StartExecution {

	public final Logger logger = Logger.getLogger(StartExecution.class);

	public String getLocatorType(String locator) {
		return (locator.split("~")[0].toLowerCase());
	}

	public String getLocator(String locator) {
		return (locator.split("~")[1]);
	}

	public WebElement getWebElement(String locator) {
		WebElement webElement = null;

		try {

			if (getLocatorType(locator).equalsIgnoreCase("XPATH")) {

				webElement = driver.findElement(By.xpath(getLocator(locator)));

			} else if (getLocatorType(locator).equalsIgnoreCase("ID")) {

				webElement = driver.findElement(By.id(getLocator(locator)));

			} else if (getLocatorType(locator).equalsIgnoreCase("NAME")) {

				webElement = driver.findElement(By.name(getLocator(locator)));

			} else if (getLocatorType(locator).equalsIgnoreCase("CSS")) {

				webElement = driver.findElement(By.cssSelector(getLocator(locator)));

			} else if (getLocatorType(locator).equalsIgnoreCase("LINKTEXT")) {

				webElement = driver.findElement(By.linkText(getLocator(locator)));
			}

		} catch (Exception e) {
			logger.error(e);
		}

		return webElement;
	}

	public void click(String locator) {
		WebElement w = getWebElement(locator);
		if (w.isDisplayed()) {
			w.click();			
			//WebElement myDynamicElement = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.className("icn icn-tools")));
			
		}
	}

	public void setText(String locator, String args) {
		WebElement w = getWebElement(locator);
		if (w.isDisplayed()) {
			w.sendKeys(args);
		}

	}

}
