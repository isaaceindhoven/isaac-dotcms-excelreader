package nl.isaac.dotcms.excelreader.osgi;

import nl.isaac.dotcms.excelreader.viewtool.ExcelReaderTool;
import nl.isaac.dotcms.util.osgi.ExtendedGenericBundleActivator;
import nl.isaac.dotcms.util.osgi.ViewToolScope;

import com.dotcms.repackage.org.osgi.framework.BundleContext;

public class Activator extends ExtendedGenericBundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		// Default DotCMS call
		initializeServices(context);
		
		addViewTool(context, ExcelReaderTool.class, "excel", ViewToolScope.REQUEST);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		unregisterServices(context);
	}

}
