/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.afcepf.atod.mbeans.mbeanorder;

import fr.afcepf.atod.mbeans.mbeanproduct.MBeanProduct;
import fr.afcepf.atod.mbeans.mbeanuser.MBeanConnexion;
import fr.afcepf.atod.util.SingletonSessionOrderTemp;
import fr.afcepf.atod.util.UtilConverter;
import fr.afcepf.atod.util.UtilDefParam;
import fr.afcepf.atod.util.UtilFindPath;
import fr.afcepf.atod.vin.data.exception.WineException;
import fr.afcepf.atod.wine.business.order.api.IBuOrder;
import fr.afcepf.atod.wine.entity.Customer;
import fr.afcepf.atod.wine.entity.Order;
import fr.afcepf.atod.wine.entity.OrderDetail;
import fr.afcepf.atod.wine.entity.PaymentInfo;
import fr.afcepf.atod.wine.entity.Product;
import fr.afcepf.atod.wine.entity.ShippingMethod;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;

@SessionScoped
@ManagedBean(name = "mBeanCartManagement")
public class MBeanCartManagement implements Serializable {

	private static final long serialVersionUID = -2317461571703883416L;
	// temporary
	private static Logger log
	= Logger.getLogger(MBeanCartManagement.class);
	// create a new command if necessary or 
	private Order order = SingletonSessionOrderTemp.getInstance().getOrder();
	// set transforme en list
	private List<OrderDetail> listOrderDetails;

	// global error adding product
	private String errorAddProduct;
	@ManagedProperty(value = "#{mBeanProduct}")
	private MBeanProduct mBeanProduct;
	private Order lastOrder = new Order();
	@ManagedProperty(value = "#{buOrder}")
	private IBuOrder buOrder;
	@ManagedProperty(value = "#{mBeanConnexion}")
	private MBeanConnexion mBeanConnexion;
	private boolean validOrder;
	private Customer customer = new Customer();
	
	DecimalFormat df = new DecimalFormat ( ) ;
	

	public MBeanCartManagement() {
		super();
		errorAddProduct = "";
		validOrder = false;
	}

	/**
	 *
	 * @param product
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String addProductCart() {
		String page = null;
		validOrder = false;
		String id = UtilDefParam.getProductParam(FacesContext.getCurrentInstance());
		Product product = null;
		try {
			product = mBeanProduct.getBuProduct()
					.findById(Integer.parseInt(id));
		} catch (NumberFormatException e) {			
			e.printStackTrace();
		} catch (WineException e) {			
			e.printStackTrace();
		}
		if (!product.getName().equalsIgnoreCase("")
				&& product.getPrice() >= 0
				&& product.getProductSuppliers() != null) {
			try {
				if (order == null || order.getPaidAt()!=null) {
					order = new Order();
					order.setCreatedAt(new Date());
					order.setPaidAt(null);

				}
				order = buOrder.addItemCart(order, product);
				listOrderDetails = UtilConverter.retrieveListAsSet(order.getOrdersDetail());

				/*The symptoms indicate that the page was requested by a POST request and that
                you're ignoring the webbrowser's warning that the data will be resent when refreshing
                the request. Refreshing a POST request will of course result in it being re-executed.
                This is not a JSF specific problem.The common solution to that is to send a redirect
                to a GET request after executing the POST request. This way the client will end up
                having the GET request in the browser view. Refreshing this will then only re-execute
                the GET request which doesn't (shouldn't) modify anything (unless you're doing this in
                the constructor of a request scoped bean associated with the view). This is also known
                as the POST-Redirect-GET pattern.With JSF 2.0, you can achieve this by simply adding
                faces-redirect=true parameter to the bean action's outcome.

                N.B:1)If you're still using old fashioned <navigation-case>s in faces-config.xml,
                then the same effect can be achieved by adding <redirect/> to the case
                    2) In JSF 2.0+ you could instead use the flash scope for this or to just let
                the POST take place by <f:ajax> submit instead of a normal submit.
                    3) Another method
				 */								
				page = UtilFindPath.findURLPath("basket.jsf");
				return page;
			} catch (WineException ex) {
				errorAddProduct = "Product not available, stock empty";
			}
			if (order.getOrdersDetail().isEmpty()) {
				errorAddProduct = "Product not available, stock empty";
			}
		} else {
			errorAddProduct = "Product not available, stock empty";
		}
		return page;
	}


	/**
	 * supprimer une ligne de commande
	 *
	 * @param orderDetail
	 */
	public String removeProductCart(OrderDetail orderDetail) {
		String page = null;
		if (!order.getOrdersDetail().isEmpty()) {
			listOrderDetails.remove(orderDetail);
			Set<OrderDetail> set = UtilConverter.retrieveSetAsList(listOrderDetails);
			order.setOrdersDetail(set);
		}
		return "#?faces-redirect=true";
	}

	/**
	 *
	 * @param orderDetail
	 * @return
	 */
	public double calculDiscount(OrderDetail orderDetail) {
//		df.setMaximumFractionDigits ( 2 ) ; //arrondi à 2 chiffres apres la virgules 
		double discount = 0.0;
		double prix = 0.0;
		double pourcentage = 0.0;
		if (orderDetail != null
				&& orderDetail.getProductOrdered()
				.getSpeEvent()!= null) 
		{
			prix = orderDetail.getProductOrdered().getPrice();
			pourcentage = orderDetail.getProductOrdered()
					.getSpeEvent().getPourcentage();
			discount = prix/100 * pourcentage;
//			Double.parseDouble(df.format(discount));
			
		}
		return discount ;
	}

	/**
	 *
	 * @param orderDetail
	 * @return
	 */
	public double calculTotalLine(OrderDetail orderDetail) {
		double totalLine = 0.0;
		if (orderDetail != null) {
			totalLine = orderDetail.getQuantite()
					* (orderDetail.getProductOrdered().getPrice() - calculDiscount(orderDetail));
		}
		return totalLine;
	}

	/**
	 * @return
	 */
	public double calculSubTotal() {
		double subTotal = 0.0;
		if (!order.getOrdersDetail().isEmpty()) {
			for (OrderDetail o : order.getOrdersDetail()) {
				subTotal = subTotal + calculTotalLine(o);
			}
		}

		return subTotal;
	}

	/**
	 * quantite total des articles dans le panier
	 *
	 * @return
	 */
	public int calculerNumTotalQantity() {
		int numTotalQuantity = 0;
		if (order != null && order.getCreatedAt() != null 
				&& order.getPaidAt() == null) {
			for (OrderDetail o : this.order.getOrdersDetail()) {
				numTotalQuantity = numTotalQuantity + o.getQuantite();
			}
		}
		return numTotalQuantity;
	}

	/**
	 * Calculer frais transport mode livaison colissomo
	 *
	 * @param orderDetail
	 * @return
	 */
	public double caclulShippingFree() {
		double shipping = 0.0;
		//        if (calculerNumTotalQantity() != 0.0 & order.getShippingMethod().getId()==1) 
		if (calculerNumTotalQantity() != 0.0) {
			shipping = calculerNumTotalQantity() * 1.5;
		}
		return shipping;
	}

	/**
	 * Calculer le total de la commande: total articles + frais transport
	 *
	 * @param orderDetail
	 * @return
	 */
	public double calculTotal() {
		double subtotal = 0.0;
		for (OrderDetail o : order.getOrdersDetail()) {
			subtotal = subtotal + calculTotalLine(o);
		}
		return subtotal + caclulShippingFree();
	}


	/**
	 *verifier si le customer est connecté
	 *si oui creer date order et diriger vers page valide adresse
	 *sinon direger vers page register
	 **/
	public String validePanier(){
		String page = null;
		if (mBeanConnexion.getUserConnected().getId() != null && order.getOrdersDetail().size()!=0) {
			order.setCustomer((Customer)mBeanConnexion.getUserConnected());
			order.setCreatedAt(new Date());
			page = "/pages/checkout1adress.jsf?faces-redirect=true";
		} else {
			page ="/pages/register.jsf?faces-redirect=true";
		}
		return page;
	}
	/**
	 *customer non connecté, soit s'inscrire soit connecter a partir du register 
	 *pour valider le panier et direger vers valide adresse
	 **/

	public String connectedGoToCheckout1(){
		String page = null;

		if(mBeanConnexion.connect()!=null){
			order.setCustomer((Customer)mBeanConnexion.getUserConnected());
			page ="/pages/checkout1adress.jsf?faces-redirect=true";
		}
		return page;
	}

	/**
	 * valider adresse livraison et direger vers la page paiement
	 * */
	public String validerAdresse(){
		String page = null;
		if(order.getCustomer().getAdress()!= null 
				&& order.getOrdersDetail().size()!=0){
			//order.getCustomer().setAdress(adress);
			page ="/pages/checkout2livraison.jsf?faces-redirect=true";
		}
		return page;
	}
	/**
	 * valider mode de livraison en colissomo et direger vers la page paiement
	 * 
	 * */
	public String validerLivraison(){
		String page = null;

		if(order.getCustomer().getId()!=null & order.getOrdersDetail().size()!=0){
			order.setShippingMethod(new ShippingMethod(1, "Colissomo"));
			page = "/pages/checkout3payment.jsf?faces-redirect=true";
		}
		return page;
	}

	/**
	 * valider mode de paiement et date paiement, Ajouter une nouvelle commande a la base
	 *apres l'etape validePanier, valide adress, valide transport, valide paiement
	 * @param orderDetail
	 * @return
	 */
	public String addNewOrder() {
		String page = null;
		if (mBeanConnexion.getUserConnected().getId() != null && order.getCreatedAt()!=null 
				&& order.getShippingMethod()!=null && order.getOrdersDetail().size()!=0) {        	
			try {
				order.setPaidAt(new Date());
				order.setPaymentInfo(new PaymentInfo(1, "visa"));
				buOrder.addNewOrder(order);
				validOrder = true;
				getLastOrder(customer);				
				page = "/pages/checkout4confirmation.jsf?faces-redirect=true";
			} catch (WineException e) {
				e.printStackTrace();
			}
		} 
		return page;
	}

	/**
	 * recuperer le dernier commande de client qui viens de passer pour le recap confirmation
	 * */
	public Order getLastOrder(Customer customer){
		customer = (Customer) mBeanConnexion.getUserConnected();
		lastOrder = buOrder.getLastOrderByCustomer(customer);	
		return lastOrder;
	}    

	public void initializeOrder(){
		validOrder = true;		
	}


	//  ######################################################## //
	/**
	 * ********************************************************
	 * Methode pour initialiser le panier pour faire le parcours
	 * panier/validation paiement/.
	 * *
	 * @return *******************************************************
	 */


	public Order getOrder() {
		return order;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Order getLastOrder() {
		return lastOrder;
	}

	public void setLastOrder(Order lastOrder) {
		this.lastOrder = lastOrder;
	}

	public List<OrderDetail> getListOrderDetails() {
		return listOrderDetails;
	}

	public void setListOrderDetails(List<OrderDetail> listOrderDetails) {
		this.listOrderDetails = listOrderDetails;
	}

	public MBeanConnexion getmBeanConnexion() {
		return mBeanConnexion;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public String getErrorAddProduct() {
		return errorAddProduct;
	}

	public void setErrorAddProduct(String errorAddProduct) {
		this.errorAddProduct = errorAddProduct;
	}

	public IBuOrder getBuOrder() {
		return buOrder;
	}

	public void setBuOrder(IBuOrder buOrder) {
		this.buOrder = buOrder;
	}

	public boolean isValidOrder() {
		return validOrder;
	}

	public void setValidOrder(boolean validOrder) {
		this.validOrder = validOrder;
	}

	public MBeanProduct getmBeanProduct() {
		return mBeanProduct;
	}

	public void setmBeanProduct(MBeanProduct mBeanProduct) {
		this.mBeanProduct = mBeanProduct;
	}
 
	public void setmBeanConnexion(MBeanConnexion mBeanConnexion) {
		this.mBeanConnexion = mBeanConnexion;
	}

}
