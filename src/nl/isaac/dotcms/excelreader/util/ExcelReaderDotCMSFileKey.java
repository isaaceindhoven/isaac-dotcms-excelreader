package nl.isaac.dotcms.excelreader.util;

import com.dotmarketing.beans.Host;

public class ExcelReaderDotCMSFileKey extends ExcelReaderFileKey {
	private final Host host;
	private final boolean live;
	
	public ExcelReaderDotCMSFileKey(String path, Host host, boolean live) {
		super(path);
		this.host = host;
		this.live = live;
	}
	
	public Host getHost() {
		return host;
	}
	
	public boolean isLive() {
		return live;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ExcelReaderDotCMSFileKey) {
			ExcelReaderDotCMSFileKey key = (ExcelReaderDotCMSFileKey)obj;
			return super.equals(key) && 
				key.getHost().getHostname().equals(getHost().getHostname()) &&
				(key.isLive() == isLive());
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + "_" + getHost().getHostname() + "_" + isLive();
	}
	
}
