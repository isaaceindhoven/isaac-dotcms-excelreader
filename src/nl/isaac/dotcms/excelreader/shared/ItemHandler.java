package nl.isaac.dotcms.excelreader.shared;
/**
* ExcelReader by ISAAC - The Full Service Internet Agency is licensed 
* under a Creative Commons Attribution 3.0 Unported License
* - http://creativecommons.org/licenses/by/3.0/
* - http://www.geekyplugins.com/
* 
* @copyright Copyright (c) 2011 ISAAC Software Solutions B.V. (http://www.isaac.nl)
*/

import java.util.Map;

/**
 * Interface for handling an item that is not available in the cache
 * 
 * @author xander
 *
 * @param <T>
 */
public interface ItemHandler<T> {
	public T get(String key);
	public boolean isChanged(String key);
	public Map<String,T> getInitialCache();
}