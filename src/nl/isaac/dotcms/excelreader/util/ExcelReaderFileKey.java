package nl.isaac.dotcms.excelreader.util;

public class ExcelReaderFileKey {
	private final String path;
	private final boolean skipEmptyLines;
	
	public ExcelReaderFileKey(String path, boolean skipEmptyLines) {
		this.path = path;
		this.skipEmptyLines = skipEmptyLines;
	}
	
	public String getPath() {
		return path;
	}
	public boolean isSkipEmptyLines() {
		return skipEmptyLines;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ExcelReaderFileKey) {
			return ((ExcelReaderFileKey)obj).getPath().equals(getPath()) && skipEmptyLines == ((ExcelReaderFileKey) obj).isSkipEmptyLines();
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
