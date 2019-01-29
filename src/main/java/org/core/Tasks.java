package org.core;


import org.openqa.selenium.WebDriver;
public class Tasks extends Corewrappers {
	public  static String taskUrl=StartExecutionAccessability.murl+"/tasks/tasklist.do";
	public void navigateTasks(WebDriver driver){
		driver.get(taskUrl);
	}
	
}