package nl.isaac.dotcms.excelreader.util;
/**
* ExcelReader by ISAAC - The Full Service Internet Agency is licensed 
* under a Creative Commons Attribution 3.0 Unported License
* - http://creativecommons.org/licenses/by/3.0/
* - http://www.geekyplugins.com/
* 
* @copyright Copyright (c) 2011 ISAAC Software Solutions B.V. (http://www.isaac.nl)
*/

import com.google.gson.JsonObject;

import java.util.Map;
import java.util.TreeMap;

/**
 * The ExcelUtilStatus can be used to store import status messages
 *
 */
public class ExcelUtilStatus {
	private int currentRowNumber = 0;
	private int numberOfImportedRows = 0;
	private int totalNumberOfRows = 0;
	private Map<Integer, Exception> exceptions = new TreeMap<Integer, Exception>();
	private boolean finished = false;
	
	
	void addFailedRowWithException(Exception exception) {
		exceptions.put(currentRowNumber, exception);
	}
	
	void addSuccesfulRow() {
		numberOfImportedRows++;
	}
	
	void newRow() {
		currentRowNumber++;
	}
	
	void setFinished() {
		this.finished = true;
	}
	
	void setTotalNumberOfRows(int numberOfRows) {
		this.totalNumberOfRows = numberOfRows;
	}
	
	public int getCurrentRowNumber() {
		return currentRowNumber;
	}
	
	public int getNumberOfImportedRows() {
		return numberOfImportedRows;
	}
	
	public int getNumberOfFailedRows() {
		return exceptions.size();
	}
	
	public Map<Integer, Exception> getMapWithRowNumbersAndExceptions() {
		return exceptions;
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	public JsonObject toJSON() {
		JsonObject status = new JsonObject();
		status.addProperty("currentRowNumber", Integer.valueOf(currentRowNumber));
		status.addProperty("totalNumberOfRows", Integer.valueOf(totalNumberOfRows));
		status.addProperty("numberOfImportedRows", Integer.valueOf(numberOfImportedRows));
		status.addProperty("finished", Boolean.valueOf(finished));
		return status;
	}
	
	public int getTotalNumberOfRows() {
		return this.totalNumberOfRows;
	}
	
}
