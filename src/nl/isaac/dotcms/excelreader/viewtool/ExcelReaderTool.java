package nl.isaac.dotcms.excelreader.viewtool;
/**
* ExcelReader by ISAAC - The Full Service Internet Agency is licensed 
* under a Creative Commons Attribution 3.0 Unported License
* - http://creativecommons.org/licenses/by/3.0/
* - http://www.geekyplugins.com/
* 
* @copyright Copyright (c) 2011 ISAAC Software Solutions B.V. (http://www.isaac.nl)
*/

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import nl.isaac.dotcms.excelreader.shared.RequestUtil;
import nl.isaac.dotcms.excelreader.util.ExcelReaderCacheGroupHandler;
import nl.isaac.dotcms.excelreader.util.ExcelReaderDotCMSFileKey;
import nl.isaac.dotcms.excelreader.util.ExcelReaderFileKey;

import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.tools.ViewTool;

import com.dotmarketing.beans.Host;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
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
	public List<Map<String, Object>> readExcel(String file) {
		return ExcelReaderCacheGroupHandler.getInstance().get(new ExcelReaderFileKey(file));
	}
	
	public List<Map<String, Object>> readExcelFromDotCMS(String url) {
		return ExcelReaderCacheGroupHandler.getInstance().get(new ExcelReaderDotCMSFileKey(url, getCurrentHost(), isLive()));
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
		return RequestUtil.isLiveMode(request);
	}
	
}
