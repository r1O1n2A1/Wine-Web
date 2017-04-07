/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.afcepf.atod.mbeans.mbeanproduct;

import fr.afcepf.atod.business.product.api.IBuProduct;
import fr.afcepf.atod.mbeans.mbeanuser.MBeanConnexion;
import fr.afcepf.atod.util.UtilFindPath;
import fr.afcepf.atod.util.UtilParameters;
import fr.afcepf.atod.vin.data.exception.WineException;
import fr.afcepf.atod.webservice.image.IServiceImage;
import fr.afcepf.atod.webservice.image.ServiceImageService;
import fr.afcepf.atod.webservice.image.WineServerUtilException_Exception;
import fr.afcepf.atod.wine.entity.Product;
import fr.afcepf.atod.wine.entity.ProductAccessories;
import fr.afcepf.atod.wine.entity.ProductType;
import fr.afcepf.atod.wine.entity.ProductVarietal;
import fr.afcepf.atod.wine.entity.ProductVintage;
import fr.afcepf.atod.wine.entity.ProductWine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UICommand;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;

/**
 *
 * @author ronan
 */
@ManagedBean
@SessionScoped
public class MBeanProduct implements Serializable {

	private static final long serialVersionUID = -8118205383226441401L;
	private Logger log = Logger.getLogger(MBeanConnexion.class);

	private IServiceImage proxy;

	@ManagedProperty(value = "#{buProduct}")
	private IBuProduct buProduct;

	private ProductAccessories accessory;
	private ProductWine currentProd;
	private String nameProd;
	private String errorSearch;

	private List<Product> promotedList;
	private List<ProductWine> promotedWinesList;
	private List<ProductType> wineTypes;
	private List<Product> expensiveProducts;
	private List<ProductWine> expensiveWines;
	private List<ProductWine> threeSimilarProductsList;
	private Map<ProductType, List<String>> appellations;
	private Map<ProductType, List<ProductVarietal>> varietals;
	private Map<ProductType, Map<Integer, Integer>> pricesRepartition;
	private List<ProductWine> winesList;
	private ProductType currentProdType;
	private Object currentSubCategory;
	private String subSelectionTypeLabel;

	/**
	 * pagination stuff
	 */
	private int totalRows;
	private int firstRow;
	private int rowsPerPage;
	private int totalPages;
	private int pageRange;
	private Integer[] pages;
	private int currentPage;

	public MBeanProduct() {
		super();
		nameProd = "";
		errorSearch = "";
		accessory = new ProductAccessories();
		rowsPerPage = 8; // Default rows per page (max amount of rows to be displayed at once).
		pageRange = 5;
	}

	/*@PostConstruct
    public void initExpensive() {
    	try {
			expensiveProducts = buProduct.findExpensive(500.0);
		} catch (WineException e) {			
			e.printStackTrace();
		}
    }*/
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void initIndex() {
		proxy = new ServiceImageService().getServiceImagePort();
		if (promotedList == null) {
			try {
				promotedList = buProduct.getPromotedProductsSelection();   
				promotedWinesList = (List<ProductWine>)(List<?>) promotedList;                
				for (ProductWine wine : promotedWinesList) {    
					Map<String,String> imagesWS = proxy.downloadImages(wine.getApiId().toString());
					List<String> orderedImages = orderImgsFromWS(imagesWS, wine.getApiId().toString());
					wine.setImgsWS(orderedImages);
				}
			} catch (WineException e) {
				log.error(e.getErreurVin() + "|" + e.getMessage());
			}catch (WineServerUtilException_Exception e) {
				log.error(e);
			} 
		}

		//Données Nav
		if (wineTypes == null) {
			try {
				wineTypes = buProduct.getWineTypes();
				appellations = buProduct.getAppellationsByType(wineTypes);
				varietals = buProduct.getVarietalsByType(wineTypes);
				pricesRepartition = buProduct.getPricesRepartitionByType(wineTypes);
				log.info(appellations);
			} catch (WineException e) {
				log.error(e.getErreurVin() + "|" + e.getMessage());
			}
		}
		if (expensiveProducts == null) {
			try {
				expensiveProducts = buProduct.findExpensive(500.0);
				expensiveWines = (List<ProductWine>) (List<?>) expensiveProducts;
				for (ProductWine wine : expensiveWines) {    
					Map<String,String> imagesWS = proxy.downloadImages(wine.getApiId().toString());
					List<String> orderedImages = orderImgsFromWS(imagesWS, wine.getApiId().toString());
					wine.setImgsWS(orderedImages);
				}
			} catch (WineException  e) {
				log.error(e.getErreurVin() + "|" + e.getMessage());
			} catch (WineServerUtilException_Exception e) {
				log.error(e);
			}
		}
	}

	public String getProductParam(FacesContext fc) {
		Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
		return params.get("product");
	}

	public String findByNameProduct() throws WineException {
		String str = null;
		if (!nameProd.equalsIgnoreCase("")) {
			buProduct.findByName(nameProd);
		}
		return str;
	}

	public String article() throws WineException {
		String str = null;
		FacesContext fc = FacesContext.getCurrentInstance();
		Integer id = Integer.valueOf(getProductParam(fc));
		if (id > 0) {
			if (winesList != null && winesList.size() > 3) {
				threeSimilarProductsList = new ArrayList<ProductWine>();
				for (Product product : winesList) {
					if (threeSimilarProductsList.size() < 3 && product.getIdProduct() != id) {
						threeSimilarProductsList.add((ProductWine) product);
					}
				}
			} else if (winesList.size() <= 3) {
				threeSimilarProductsList = buProduct.categoryAccordingToObjectType(currentProdType, currentSubCategory, 0, 3);
				Integer count = buProduct.countCategoryAccordingToObjectType(currentProdType, currentSubCategory);
				if (count < 3) {
					threeSimilarProductsList.addAll(buProduct.categoryAccordingToObjectType(currentProdType, null, 0, 3 - count));
				}
			} else {
				//TODO a corriger
			}
			currentProd = (ProductWine) buProduct.findById(id);
			Map<String, String> imagesWS = new HashMap<>();
			try {
				imagesWS = proxy.downloadImages(currentProd.getApiId().toString());
			} catch (WineServerUtilException_Exception e) {
				log.error(e);
			}
			List<String> orderedImages = orderImgsFromWS(imagesWS, currentProd.getApiId().toString());
			currentProd.setImgsWS(orderedImages);
			str = UtilFindPath.findURLPath("article.jsf");
		}
		return str;
	}

	public String category(ProductType type) {
		String str = null;
		currentProdType = type;
		currentSubCategory = null;
		getWinesList();
		str = UtilFindPath.findURLPath("category.jsf");
		return str;
	}

	public String category(ProductType type, Object o) {
		String str = null;
		currentProdType = type;
		currentSubCategory = o;
		getWinesList();
		str = UtilFindPath.findURLPath("category.jsf");
		return str;
	}
	
	

	/**
	 * Order image from the WS to set the view
	 * @param imagesWS Map from the WS key-name / Value-content
	 * @return
	 */
	public List<String> orderImgsFromWS(Map<String, String> imagesWS, String idImg) {
		String[] orderedImgs = new String[4];
		for (String key : imagesWS.keySet()) {
			if (key.contains(UtilParameters.BACK_IMG)) {
				orderedImgs[0] = imagesWS.get(key);
			} else if (key.contains(UtilParameters.FRONT_IMG)) {
				orderedImgs[1] = imagesWS.get(key);
			} else if (key.contains( UtilParameters.M_IMG + UtilParameters.IMG_JPG)) {
				orderedImgs[2] = imagesWS.get(key);
			} else if (key.split(Pattern.quote("."))[0] != null 
					&& !key.split(Pattern.quote("."))[0].equalsIgnoreCase(idImg)) {
				orderedImgs[3] = imagesWS.get(key);
			}
		}
		return Arrays.asList(orderedImgs);
	}

	private void loadList() {
		try {
			winesList = buProduct.categoryAccordingToObjectType(currentProdType, currentSubCategory, firstRow, rowsPerPage);
			for (ProductWine wine : winesList) {              	
				Map<String,String> imagesWS = proxy.downloadImages(wine.getApiId().toString());
				List<String> orderedImages = orderImgsFromWS(imagesWS, wine.getApiId().toString());
				wine.setImgsWS(orderedImages);
			}
			
			totalRows = buProduct.countCategoryAccordingToObjectType(currentProdType, currentSubCategory);

			// Set currentPage, totalPages and pages.
			currentPage = (totalRows / rowsPerPage) - ((totalRows - firstRow) / rowsPerPage) + 1;
			totalPages = (totalRows / rowsPerPage) + ((totalRows % rowsPerPage != 0) ? 1 : 0);
			int pagesLength = Math.min(pageRange, totalPages);
			pages = new Integer[pagesLength];

			// firstPage must be greater than 0 and lesser than
			// totalPages-pageLength.
			int firstPage = Math.min(Math.max(0, currentPage - (pageRange / 2)), totalPages - pagesLength);

			// Create pages (page numbers for page links).
			for (int i = 0; i < pagesLength; i++) {
				pages[i] = ++firstPage;
			}
		} catch (WineException | WineServerUtilException_Exception e) {
			log.error(e);
		}
	}

	private void page(int firstRow) {
		this.firstRow = firstRow;
		loadList();
	}

	// Paging actions -----------------------------------------------------------------------------
	public void pageFirst() {
		page(0);
	}

	public void pageNext() {
		page(firstRow + rowsPerPage);
	}

	public void pagePrevious() {
		page(firstRow - rowsPerPage);
	}

	public void pageLast() {
		page(totalRows - ((totalRows % rowsPerPage != 0) ? totalRows % rowsPerPage : rowsPerPage));
	}

	public void page(ActionEvent event) {
		log.info(((UICommand) event.getComponent()).getValue());
		page(((Integer) ((UICommand) event.getComponent()).getValue() - 1) * rowsPerPage);
	}

	/**
	 *
	 * @param min
	 * @return
	 */
	public List<Product> findExpensiveProducts(double min) {
		String str = null;
		expensiveProducts = new ArrayList<>();
		if (min >= 0.0) {
			try {
				expensiveProducts = buProduct.findExpensive(min);
			} catch (WineException ex) {
				errorSearch = "Research not found in the Database.";
			}
			if (!expensiveProducts.isEmpty()) {

			} else {
				errorSearch = "Research not found in the Database.";
			}
		} else {
			errorSearch = "Define positive criteria...";
		}
		return expensiveProducts;
	}

	// ----------- Getters && Setters ----------------//
	public String getNameProd() {
		return nameProd;
	}

	public String getSubSelectionTypeLabel() {
		return subSelectionTypeLabel;
	}

	public void setSubSelectionTypeLabel(Object o) {
		if (o instanceof ProductVarietal) {
			ProductVarietal pv = (ProductVarietal) o;
			subSelectionTypeLabel = "Cépage : " + pv.getDescription();
		} else if (o instanceof ProductVintage) {
			ProductVintage pv = (ProductVintage) o;
			subSelectionTypeLabel = "Millésime : " + pv.getYear();
		} else if (o instanceof String) {
			subSelectionTypeLabel = "Appelation : " + o;
		} else if (o instanceof Integer) {
			subSelectionTypeLabel = "Prix : ";
			Integer i = (Integer) o;
			if (i == 0) {
				subSelectionTypeLabel = subSelectionTypeLabel + " de 0 à 50 €";
			} else if (i == 50) {
				subSelectionTypeLabel = subSelectionTypeLabel + " de 50 à 100 €";
			} else {
				subSelectionTypeLabel = subSelectionTypeLabel + " de 100 €";
			}

		} else {
			subSelectionTypeLabel = "";
		}
	}

	public void setNameProd(String nameProd) {
		this.nameProd = nameProd;
	}

	public IBuProduct getBuProduct() {
		return buProduct;
	}

	public void setBuProduct(IBuProduct buProduct) {
		this.buProduct = buProduct;
	}

	public List<Product> getPromotedList() {
		return promotedList;
	}

	public void setPromotedList(List<Product> promotedList) {
		this.promotedList = promotedList;
	}

	public List<ProductWine> getPromotedWinesList() {
		return promotedWinesList;
	}

	public void setPromotedWinesList(List<ProductWine> promotedWinesList) {
		this.promotedWinesList = promotedWinesList;
	}

	public List<ProductType> getWineTypes() {
		return wineTypes;
	}

	public void setWineTypes(List<ProductType> wineTypes) {
		this.wineTypes = wineTypes;
	}

	public Map<ProductType, List<String>> getAppellations() {
		return appellations;
	}

	public void setAppellations(Map<ProductType, List<String>> appellations) {
		this.appellations = appellations;
	}

	public Map<ProductType, List<ProductVarietal>> getVarietals() {
		return varietals;
	}

	public void setVarietals(Map<ProductType, List<ProductVarietal>> varietals) {
		this.varietals = varietals;
	}

	public ProductAccessories getAccessory() {
		return accessory;
	}

	public void setAccessory(ProductAccessories accessory) {
		this.accessory = accessory;
	}

	public ProductWine getCurrentProd() {
		return currentProd;
	}

	public List<Product> getExpensiveProducts() {
		return expensiveProducts;
	}

	public List<ProductWine> getWinesList() {
		loadList();
		setSubSelectionTypeLabel(currentSubCategory);
		return winesList;
	}

	public void setExpensiveProducts(List<Product> expensiveProducts) {
		this.expensiveProducts = expensiveProducts;
	}

	public void setWinesList(List<ProductWine> winesList) {
		this.winesList = winesList;
	}

	public int getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}

	public int getFirstRow() {
		return firstRow;
	}

	public void setFirstRow(int firstRow) {
		this.firstRow = firstRow;
	}

	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getPageRange() {
		return pageRange;
	}

	public void setPageRange(int pageRange) {
		this.pageRange = pageRange;
	}

	public Integer[] getPages() {
		return pages;
	}

	public void setPages(Integer[] pages) {
		this.pages = pages;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public ProductType getCurrentProdType() {
		return currentProdType;
	}

	public Object getCurrentSubCategory() {
		return currentSubCategory;
	}

	public List<ProductWine> getThreeSimilarProductsList() {
		return threeSimilarProductsList;
	}

	public Map<ProductType, Map<Integer, Integer>> getPricesRepartition() {
		return pricesRepartition;
	}

	public List<ProductWine> getExpensiveWines() {
		return expensiveWines;
	}

	public void setExpensiveWines(List<ProductWine> expensiveWines) {
		this.expensiveWines = expensiveWines;
	}

	public void setThreeSimilarProductsList(List<ProductWine> threeSimilarProductsList) {
		this.threeSimilarProductsList = threeSimilarProductsList;
	}
}
