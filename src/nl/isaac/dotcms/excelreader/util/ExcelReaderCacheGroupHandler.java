package nl.isaac.dotcms.excelreader.util;
/**
* ExcelReader by ISAAC - The Full Service Internet Agency is licensed 
* under a Creative Commons Attribution 3.0 Unported License
* - http://creativecommons.org/licenses/by/3.0/
* - http://www.geekyplugins.com/
* 
* @copyright Copyright (c) 2011 ISAAC Software Solutions B.V. (http://www.isaac.nl)
*/

import java.util.List;
import java.util.Map;

import nl.isaac.dotcms.excelreader.shared.CacheGroupHandler;
/**
 * This class handles the cache for the Excelreader plugin
 *  
 * @author xander
 *
 */
public class ExcelReaderCacheGroupHandler extends CacheGroupHandler<List<Map<String, Object>>> {
	private static ExcelReaderCacheGroupHandler cache;
	
	private ExcelReaderCacheGroupHandler() {
		super("ExcelReader_plugin", new ExcelReaderItemHandler());
	}
	
	public static ExcelReaderCacheGroupHandler getInstance() {
		if(cache == null) {
			cache = new ExcelReaderCacheGroupHandler();
		}
		return cache;
	}
	

}
