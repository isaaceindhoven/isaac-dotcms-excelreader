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
import java.util.Map.Entry;

import com.dotmarketing.business.CacheLocator;
import com.dotmarketing.business.DotCacheAdministrator;
import com.dotmarketing.business.DotCacheException;
import com.dotmarketing.util.Logger;
/**
 * Class that handles the dotCMS cache. It uses an ItemHandler<T> to retrieve items that aren't stored in the cache yet
 * 
 * @author xander
 *
 * @param <T>
 */
public class CacheGroupHandler<K, V> {
	private String groupName;
	protected ItemHandler<K, V> itemHandler;
	
	public CacheGroupHandler(String groupName, ItemHandler<K, V> itemHandler) {
		this.groupName = groupName;
		this.itemHandler = itemHandler;
	}
	
	public V get(K key) {
		DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
		Object o = null;
		
		if(!itemHandler.isChanged(key)) {
			try {
				o = cache.get(key.toString(), groupName);
			} catch (DotCacheException e) {
				Logger.info(this.getClass(), String.format("DotCacheException for Group '%s', key '%s', message: %s", groupName, key, e.getMessage()));
			}
		}
		
		if(o == null) {
			V t = itemHandler.get(key);
			put(key, t);
			return t;
		} else {
			return (V)o;
		}
	}
	
	public void put(K key, V t) {
		DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
		cache.put(key.toString(), t, groupName);
	}
	
	/**
	 * Updates the given key by calling the itemhandler's get method
	 */
	public void updateWithItemHandler(K key) {
		remove(key);
		put(key, itemHandler.get(key));
	}
	
	public void remove(K key) {
		DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
		cache.remove(key.toString(), groupName);
	}
	
	public void fillInitialCache() {
		removeAll();
		Map<K, V> initialCache = itemHandler.getInitialCache();
		for(Entry<K, V> entry: initialCache.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}
	
	public void removeAll() {
		DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
		cache.flushGroup(groupName);
	}
	
}
