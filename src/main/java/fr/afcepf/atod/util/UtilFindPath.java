package fr.afcepf.atod.util;

import javax.faces.context.FacesContext;

public final class UtilFindPath {
	private UtilFindPath() {
		
	}
	
	public static String findURLPath(String pageURL) {
		String str 		= "";
		String referrer = FacesContext.getCurrentInstance()
				.getExternalContext()
				.getRequestHeaderMap().get("referer");
		str = pageURL + "?faces-redirect=true";
		if(!referrer.contains("/pages/")){
    		str = "pages/" + str;
    	}
		return str;
	}
}
