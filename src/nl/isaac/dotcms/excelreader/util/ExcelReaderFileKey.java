package nl.isaac.dotcms.excelreader.util;

public class ExcelReaderFileKey {
	private final String path;
	
	public ExcelReaderFileKey(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ExcelReaderFileKey) {
			return ((ExcelReaderFileKey)obj).getPath().equals(getPath());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return getPath().hashCode();
	}
	
	@Override
	public String toString() {
		return getPath();
	}
	
}
