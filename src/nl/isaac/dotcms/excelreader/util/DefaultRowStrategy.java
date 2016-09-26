package nl.isaac.dotcms.excelreader.util;
/**
* ExcelReader by ISAAC - The Full Service Internet Agency is licensed 
* under a Creative Commons Attribution 3.0 Unported License
* - http://creativecommons.org/licenses/by/3.0/
* - http://www.geekyplugins.com/
* 
* @copyright Copyright (c) 2011 ISAAC Software Solutions B.V. (http://www.isaac.nl)
*/

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.isaac.dotcms.excelreader.util.ExcelUtil.RowStrategy;
/**
 * This row strategy just returns given row
 * 
 * @author xander
 *
 */
public class DefaultRowStrategy implements RowStrategy {
	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
	private boolean skipEmptyLines = false;
	public DefaultRowStrategy() {}
	public DefaultRowStrategy(boolean skipEmptyLines) {
		this.skipEmptyLines = skipEmptyLines;
	}
	
	public void executeRow(Map<String, Object> row) throws Exception {
		if (skipEmptyLines) {
			for (Map.Entry<String, Object> value : row.entrySet()) {
				if (value.getValue() != null && (!(value.getValue() instanceof String) || ((String) value.getValue()).length() > 0)) {
					data.add(row);
					return;
				}
			}
		} else {
			data.add(row);
		}
	}
	
	public List<Map<String, Object>> getData() {
		return data;
	}

}
