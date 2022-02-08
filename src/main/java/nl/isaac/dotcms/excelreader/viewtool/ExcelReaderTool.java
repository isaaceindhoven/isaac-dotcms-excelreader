package nl.isaac.dotcms.excelreader.viewtool;
/**
* ExcelReader by ISAAC - The Full Service Internet Agency is licensed 
* under a Creative Commons Attribution 3.0 Unported License
* - http://creativecommons.org/licenses/by/3.0/
* - http://www.geekyplugins.com/
* 
* @copyright Copyright (c) 2011 ISAAC Software Solutions B.V. (http://www.isaac.nl)
*/

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import nl.isaac.dotcms.excelreader.osgi.Activator;
import nl.isaac.dotcms.excelreader.util.DefaultRowStrategy;
import nl.isaac.dotcms.excelreader.util.ExcelReaderCacheGroupHandler;
import nl.isaac.dotcms.excelreader.util.ExcelReaderDotCMSFileKey;
import nl.isaac.dotcms.excelreader.util.ExcelReaderFileKey;
import nl.isaac.dotcms.excelreader.util.ExcelUtil;
import nl.isaac.dotcms.excelreader.util.ExcelUtilStatus;

import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.tools.ViewTool;

import org.osgi.framework.FrameworkUtil;
import com.dotmarketing.beans.Host;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.util.Logger;
import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import nl.isaac.dotcms.shared.request.DotCMSRequestUtil;
/**
 * A ViewTool to get the information out of Excel files
 * 
 * @author xander
 *
 */
public class ExcelReaderTool implements ViewTool {
	private HttpServletRequest request;
	
	public void init(Object arg0) {
		ViewContext context = (ViewContext) arg0;
		this.request = context.getRequest();
	}
	
	/**
	 * Get the information of an Excel file as a list of maps.
	 */
	public List<Map<String, Object>> readExcel(String file, boolean skipEmptyLines) {
		return ExcelReaderCacheGroupHandler.getInstance().get(new ExcelReaderFileKey(file, skipEmptyLines));
	}
	public List<Map<String, Object>> readExcel(String file) {
		return readExcel(file, false);
	}
	
	public List<Map<String, Object>> readExcelFromDotCMS(String url) {
		return readExcelFromDotCMS(url, false);
	}
	public List<Map<String, Object>> readExcelFromDotCMS(String url, boolean skipEmptyLines) {
		return ExcelReaderCacheGroupHandler.getInstance().get(new ExcelReaderDotCMSFileKey(url, getCurrentHost(), isLive(), skipEmptyLines));
	}
	
	/**
	 * Filter on a particular field with a particular value. It filters on row.get(key).toString().toLowerCase()).contains(value.toString().toLowerCase()) 
	 */
	public List<Map<String, Object>> filter(List<Map<String, Object>> sheet, String key, Object value) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Iterator<Map<String, Object>> iterator = sheet.iterator();
		while(iterator.hasNext()) {
			Map<String, Object> row = iterator.next();
			if(row.get(key) != null && (row.get(key).toString().toLowerCase()).contains(value.toString().toLowerCase())) {
				result.add(row);
			}
		}
		return result;
	}
	
	/**
	 * Sort a result on the field with the given key
	 */
	public void sort(List<Map<String, Object>> sheet, String key) {
		Collections.sort(sheet, new FieldComparator(key));
	}
	
	public List<Map<String, Object>> readTestExcel(String testFileName) {
		DefaultRowStrategy strategy = new DefaultRowStrategy(true);
		ExcelUtilStatus status = new ExcelUtilStatus();
		try (InputStream is = FrameworkUtil.getBundle(Activator.class).getResource("ext/test/" + testFileName).openStream()) {
			ExcelUtil.executeStrategyOnExcelSheet(is, strategy, status);
			return strategy.getData();
		} catch (IOException e) {
			Logger.warn(this, "Exception while reading test excel file " + testFileName, e);
		}
		
		return null;
	}
	
	private class FieldComparator implements Comparator<Map<String, Object>> {
		private String field;
		
		public FieldComparator(String field) {
			this.field = field;
		}
		
		public int compare(Map<String, Object> m1, Map<String, Object> m2) {
			Object o1 = m1.get(field);
			Object o2 = m2.get(field);
			if(o1 == o2) {
				return 0;
			} else if(o1 == null) {
				return -1;
			} else if(o2 == null) {
				return 1;
			} else {
				return o1.toString().compareTo(o2.toString());
			}
			
		}
	}
	
	public Host getCurrentHost() {
		try {
			return WebAPILocator.getHostWebAPI().getCurrentHost(request);
		} catch (SystemException e) {
			throw new RuntimeException("Error while getting host", e);
		} catch (PortalException e) {
			throw new RuntimeException("Error while getting host", e);
		} catch (DotDataException e) {
			throw new RuntimeException("Error while getting host", e);
		} catch (DotSecurityException e) {
			throw new RuntimeException("Error while getting host", e);
		}
	}
	
	public boolean isLive() {
		return new DotCMSRequestUtil(request).isLiveMode();
	}
	
}
