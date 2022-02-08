package nl.isaac.dotcms.excelreader.osgi;

import nl.isaac.dotcms.excelreader.viewtool.ExcelReaderTool;

import nl.isaac.dotcms.shared.osgi.ViewToolScope;
import org.osgi.framework.BundleContext;
import nl.isaac.dotcms.shared.osgi.ExtendedGenericBundleActivator;

public class Activator extends ExtendedGenericBundleActivator {

	@Override
	public void init(BundleContext context) {
		addViewTool(context, ExcelReaderTool.class, "excel", ViewToolScope.REQUEST);
	}

}
