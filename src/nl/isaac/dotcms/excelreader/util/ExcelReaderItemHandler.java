package nl.isaac.dotcms.excelreader.util;
/**
* ExcelReader by ISAAC - The Full Service Internet Agency is licensed 
* under a Creative Commons Attribution 3.0 Unported License
* - http://creativecommons.org/licenses/by/3.0/
* - http://www.geekyplugins.com/
* 
* @copyright Copyright (c) 2011 ISAAC Software Solutions B.V. (http://www.isaac.nl)
*/

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dotmarketing.util.Logger;

import nl.isaac.dotcms.excelreader.shared.ItemHandler;

/**
 * A class that handles the importing of a single Excel file. Using the CacheGroupHandler/ItemHandler classes, the result is stored in the dotCMS cache.
 * 
 * @author xander
 */

public class ExcelReaderItemHandler implements ItemHandler<List<Map<String, Object>>> {
	private Map<String, Long> lastModDates = new HashMap<String, Long>();
	
	/**
	 * @return whether the key has changed since it was last requested 
	 */
	public boolean isChanged(String key) {
		try {
			if(lastModDates.containsKey(key)) {
				File file = new File(key);
				return file == null || !lastModDates.get(key).equals(file.lastModified()); 
			} else {
				return true;
			}
		} catch (Throwable t) {
			Logger.warn(this.getClass(), "Exception while checking date in file '" + key + "'", t);
			return false;
		}
	}
	
	/**
	 * @return an excel sheet as a List of Maps. The key is the location of the file (on the harddisk)
	 */
	public List<Map<String, Object>> get(String key) {
		Calendar start = Calendar.getInstance();
		try {
			File file = new File(key);
			FileInputStream fis = new FileInputStream(file);
			DefaultRowStrategy strategy = new DefaultRowStrategy();
			ExcelUtilStatus status = new ExcelUtilStatus();
			ExcelUtil.executeStrategyOnExcelSheet(fis, strategy, status);
			Logger.info(this.getClass(), "Reading of excel '" + key + "' took " + (Calendar.getInstance().getTimeInMillis() - start.getTimeInMillis()) + "ms");
			lastModDates.put(key, file.lastModified());
			return strategy.getData(); 
		} catch (Throwable t) {
			Logger.error(this.getClass(), "Error while reading excel", t);
			return null;
		}
	}
	
	
	public Map<String, List<Map<String, Object>>> getInitialCache() {
		return new HashMap<String, List<Map<String, Object>>>();
	}

}
