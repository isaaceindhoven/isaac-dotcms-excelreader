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
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nl.isaac.dotcms.excelreader.shared.FileTools;
import nl.isaac.dotcms.excelreader.shared.ItemHandler;

import com.dotmarketing.portlets.fileassets.business.FileAsset;
import com.dotmarketing.util.Logger;

/**
 * A class that handles the importing of a single Excel file. Using the CacheGroupHandler/ItemHandler classes, the result is stored in the dotCMS cache.
 * 
 * @author xander
 */

public class ExcelReaderItemHandler implements ItemHandler<ExcelReaderFileKey, List<Map<String, Object>>> {
	private Map<ExcelReaderFileKey, Long> lastModDates = new HashMap<ExcelReaderFileKey, Long>();
	
	/**
	 * @return whether the key has changed since it was last requested 
	 */
	public boolean isChanged(ExcelReaderFileKey key) {
		try {
			if(lastModDates.containsKey(key)) {
				if(key instanceof ExcelReaderDotCMSFileKey) {
					//get from dotCMS
					ExcelReaderDotCMSFileKey dotcmsKey = (ExcelReaderDotCMSFileKey)key;
					FileAsset file = FileTools.getFileAssetByURI(dotcmsKey.getPath(), dotcmsKey.getHost(), dotcmsKey.isLive());
					return file == null || !lastModDates.get(key).equals(file.getModDate().getTime());
				} else {
					//get from the file system
					File file = new File(key.getPath());
					return file == null || !lastModDates.get(key).equals(file.lastModified()); 
				}
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
	public List<Map<String, Object>> get(ExcelReaderFileKey key) {
		Calendar start = Calendar.getInstance();
		InputStream is = null;
		File file = null;
		FileAsset dotcmsFile = null;
		try {
			if(key instanceof ExcelReaderDotCMSFileKey) {
				//get from dotCMS
				ExcelReaderDotCMSFileKey dotcmsKey = (ExcelReaderDotCMSFileKey)key;
				dotcmsFile = FileTools.getFileAssetByURI(dotcmsKey.getPath(), dotcmsKey.getHost(), dotcmsKey.isLive());
				is = dotcmsFile.getInputStream();
			} else {
				//get from the file system
				file = new File(key.getPath());
				is = new FileInputStream(file);
			}
			
			DefaultRowStrategy strategy = new DefaultRowStrategy(key.isSkipEmptyLines());
			ExcelUtilStatus status = new ExcelUtilStatus();
			ExcelUtil.executeStrategyOnExcelSheet(is, strategy, status);
			Logger.info(this.getClass(), "Reading of excel '" + key + "' took " + (Calendar.getInstance().getTimeInMillis() - start.getTimeInMillis()) + "ms");
			
			if(key instanceof ExcelReaderDotCMSFileKey) {
				lastModDates.put(key, dotcmsFile.getModDate().getTime());
			} else {
				lastModDates.put(key, file.lastModified());
			}
			for(Entry<Integer, Exception> entry: status.getMapWithRowNumbersAndExceptions().entrySet()) {
				Logger.info(this.getClass(), "Row " + entry.getKey() + " has error: " + entry.getValue().getMessage());
			}
			Logger.info(this.getClass(), "Successfully read " + status.getNumberOfImportedRows() + " row and skipped " + status.getNumberOfFailedRows());
			return strategy.getData(); 
		} catch (Throwable t) {
			Logger.error(this.getClass(), "Error while reading excel", t);
			return null;
		} finally {
			if(is != null) {
				try {
					is.close();
				} catch (IOException e) {
					Logger.warn(this.getClass(), "Unable to close file connection to " + key.getPath());
				}
			}
		}
	}
	
	@Override
	public Map<ExcelReaderFileKey, List<Map<String, Object>>> getInitialCache() {
		return new HashMap<ExcelReaderFileKey, List<Map<String, Object>>>();
	}

}
