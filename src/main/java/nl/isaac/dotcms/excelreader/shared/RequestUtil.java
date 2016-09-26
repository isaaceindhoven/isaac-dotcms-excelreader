package nl.isaac.dotcms.excelreader.shared;

import javax.servlet.http.HttpServletRequest;

import com.dotmarketing.util.WebKeys;

public class RequestUtil {
	public static boolean isLiveMode(HttpServletRequest request) {
		return !(isEditMode(request) || isPreviewMode(request));
	}
	
	public static boolean isEditMode(HttpServletRequest request) {
		Object EDIT_MODE_SESSION = request.getSession().getAttribute(com.dotmarketing.util.WebKeys.EDIT_MODE_SESSION);
		if(EDIT_MODE_SESSION != null) {
			return Boolean.valueOf(EDIT_MODE_SESSION.toString());
		}
		return false; 
	}
	
	public static boolean isPreviewMode(HttpServletRequest request) {
		Object PREVIEW_MODE_SESSION = request.getSession().getAttribute(com.dotmarketing.util.WebKeys.PREVIEW_MODE_SESSION);
		if(PREVIEW_MODE_SESSION != null) {
			return Boolean.valueOf(PREVIEW_MODE_SESSION.toString());
		}
		return false; 
	}

	public static Integer getLanguage(HttpServletRequest request) {
		return (Integer)request.getSession().getAttribute(WebKeys.HTMLPAGE_LANGUAGE);
	}
	
	public static Integer getSelectedLanguage(HttpServletRequest request) {
		return (Integer)request.getSession().getAttribute(WebKeys.LANGUAGE);
	}

}
