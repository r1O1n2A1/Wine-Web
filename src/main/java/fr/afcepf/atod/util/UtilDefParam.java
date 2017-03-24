package fr.afcepf.atod.util;

import java.util.Map;

import javax.faces.context.FacesContext;

public final class UtilDefParam {
	private UtilDefParam() {

	}
	public static String getProductParam(FacesContext fc){
		Map<String,String> params = fc.getExternalContext().getRequestParameterMap();
		return params.get("product");
	}
}
