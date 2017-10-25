package org.automation.excel;

import java.io.File;
import java.io.FileInputStream;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelRead {

	private static final Logger logger = Logger.getLogger(ExcelRead.class);
	public static String[][] a = null;
	public  static Object[][] readexcel(String filename) {
		
		try {
			logger.info("Reading excel Sheet from TC.xml.");
			FileInputStream fs = new FileInputStream(
					new File(filename));
			@SuppressWarnings("resource")
			XSSFWorkbook wb = new XSSFWorkbook(fs);
			XSSFSheet sheet = wb.getSheetAt(0);
			XSSFRow row = null;
			XSSFCell cell;

			int rows; // No of rows
			rows = sheet.getPhysicalNumberOfRows();
			int cols = 0; // No of columns
			int tmp = 0;

			// This trick ensures that we get the data properly even if it
			// doesn't start from first few rows
			for (int i = 0; i < 10 || i < rows; i++) {
				row = sheet.getRow(i);
				if (row != null) {
					tmp = sheet.getRow(i).getPhysicalNumberOfCells();
					if (tmp > cols) {
						cols = tmp;
					}
				}
			}

			a = new String[rows - 1][cols];

			for (int r = 1; r < rows; r++) {
				row = sheet.getRow(r);
				if (row != null) {
					for (int c = 0; c < cols; c++) {
						cell = row.getCell((short) c);
						if (cell != null) {
							a[r - 1][c] = cell.toString();
						}
					}
				}
			}
			logger.info("Reading excel data  completed and created multidimensional array.");
		} catch (Exception ioe) {
			ioe.toString();
			logger.error(ioe);
		}
		return a;
	}

	
	
	
}
