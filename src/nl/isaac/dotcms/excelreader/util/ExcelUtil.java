package nl.isaac.dotcms.excelreader.util;
/**
* ExcelReader by ISAAC - The Full Service Internet Agency is licensed 
* under a Creative Commons Attribution 3.0 Unported License
* - http://creativecommons.org/licenses/by/3.0/
* - http://www.geekyplugins.com/
* 
* @copyright Copyright (c) 2011 ISAAC Software Solutions B.V. (http://www.isaac.nl)
*/

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

import com.dotmarketing.util.Logger;

public class ExcelUtil {

	/**
	 * Parses an excel sheet (in the FileInputStream). The first row is read and stored in a Map,
	 * all the other rows are parsed using the given RowStrategy. All thrown exceptions in the row
	 * are stored in the returned ExcelUtilStatus and returned after executing the stategy.
	 *  
	 * @param fis The FileInputStream containing the excel sheet (.xls, not .xlsx)
	 * @param rowStrategy The method to perform on all the rows except for the header row
	 * @return an ExcelUtilStatus containing info about the executed rows
	 * @throws IOException when there's a problem with the excel sheet file
	 */
	public static void executeStrategyOnExcelSheet(InputStream is, RowStrategy rowStrategy, ExcelUtilStatus status) throws IOException {
		//The bufferedInputStream is used, because the WorkbookFactory reads first 8 bytes of data before it go's to the exception.
		//Without this inputStream the first 8 characters of the first columnname will be removed.
		BufferedInputStream bis = new BufferedInputStream(is);
		bis.mark(32);
		Map<String, Integer> headerMapping = new HashMap<String, Integer>();
		try {
			Workbook workbook = WorkbookFactory.create(bis);
			Sheet sheet = workbook.getSheetAt(0);
			status.setTotalNumberOfRows(sheet.getLastRowNum());
			Iterator<Row> rowIterator = sheet.rowIterator();
			while (rowIterator.hasNext()) { 
				status.newRow();
				Row row = rowIterator.next();
				if(status.getCurrentRowNumber() == 1) {
					headerMapping = readHeaderMapping(row);
				} else {
					try {
						Map<String, Object> content = getRowAsMap(row, headerMapping);
						rowStrategy.executeRow(content);
						status.addSuccesfulRow();
					} catch (Exception e) {
						status.addFailedRowWithException(e);
					}
				}
			}
		} catch(Exception e) {
			bis.reset();
			//TODO: catch the exceptions from the workbookfactory: IllegalArgumentException + IllegalFormatException
			Logger.warn(ExcelUtil.class, "Can't read Excel file, trying csv format: " + e.getMessage());
			ICsvMapReader inFile = new CsvMapReader(new InputStreamReader(bis), CsvPreference.EXCEL_PREFERENCE);
			try {
				final String[] header = inFile.getCSVHeader(true);
				Map<String, String> stringMap;
				while((stringMap = inFile.read(header)) != null) {
					Map<String, Object> row = getStringMapAsMap(stringMap); 
					try {
						rowStrategy.executeRow(row);
						status.addSuccesfulRow();
					} catch (Exception e2) {
						status.addFailedRowWithException(e2);
					}
				}
			} catch (IOException ioe) {
				Logger.error(ExcelUtil.class, "Can't read excel file as CSV", ioe);
				Logger.error(ExcelUtil.class, "Original WorkbookFactory excel reader error:", e);
			} finally {      
				inFile.close();
			}			
		} finally {
			bis.close();
		}
		
		status.setFinished();
		
	}
	
	/**
	 * Read a row and use that row as the headers
	 * @return
	 */
	public static Map<String, Integer> readHeaderMapping(Row row) {
		List<Object> rowAsList = getRowAsList(row);
		Map<String, Integer> headerMapping = new HashMap<String, Integer>();
		for(int i=0; i<rowAsList.size(); i++) {
			headerMapping.put(rowAsList.get(i).toString(), Integer.valueOf(i));
		}
		
		Logger.info(ExcelUtil.class, "The read excel sheet has " + rowAsList.size() + " rows: " + rowAsList);

		return headerMapping;
	}
	
	/**
	 * @return a row as a list of objects 
	 */
	public static List<Object> getRowAsList(Row row) {
		List<Object> content = new ArrayList<Object>();
		Iterator<Cell> cellIterator = row.cellIterator();
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			switch(cell.getCellType()) {
				case Cell.CELL_TYPE_BLANK: 		content.add(null); break;
				case Cell.CELL_TYPE_BOOLEAN:	content.add(Boolean.valueOf(cell.getBooleanCellValue())); break;
				case Cell.CELL_TYPE_NUMERIC:	if(DateUtil.isCellDateFormatted(cell)) {content.add(cell.getDateCellValue());} else {content.add(Double.valueOf(cell.getNumericCellValue()));}; break;
				default:							content.add(cell.getStringCellValue()); 
			}
		}

		return content;
	}
	
	/**
	 * @return a row as a Map, based on a headerMapping (columnName, columnIndex) 
	 */
	public static Map<String, Object> getRowAsMap(Row row, Map<String, Integer> headerMapping) throws IllegalArgumentException {
		List<Object> content = getRowAsList(row);
		if(content.size() != headerMapping.size()) {
			throw new IllegalArgumentException("The size of the row is not the correct size. Header size = " + headerMapping.size() + " and row size = " + content.size());
		}
			
		Map<String, Object> rowMap = new HashMap<String, Object>();
		for(Entry<String, Integer> mapping: headerMapping.entrySet()) {
			rowMap.put(mapping.getKey(), content.get(mapping.getValue()));
		}
		
		return rowMap;
	}
	
	/**
	 * @return convert a Map<String, String> to a Map<String, Object> (for the CSV reader)  
	 */
	private static Map<String, Object> getStringMapAsMap(Map<String, String> stringMap) {
		Map<String, Object> objectMap = new HashMap<String, Object>();
		for(Entry<String, String> entry: stringMap.entrySet()) {
			objectMap.put(entry.getKey(), entry.getValue());
		}
		return objectMap;
	}
	
	/**
	 * A strategy that can be performed on a given row.
	 * An exception can be thrown when there's something wrong with the data.
	 */
	public interface RowStrategy {
		public void executeRow(Map<String, Object> row) throws Exception;
	}
}
