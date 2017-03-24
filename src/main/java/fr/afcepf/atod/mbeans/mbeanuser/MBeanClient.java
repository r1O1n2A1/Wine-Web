package fr.afcepf.atod.mbeans.mbeanuser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.SelectItem;

import fr.afcepf.atod.business.customer.api.IBuCustomer;
import fr.afcepf.atod.business.product.api.IBuAdress;
import fr.afcepf.atod.business.product.api.IBuCity;
import fr.afcepf.atod.vin.data.exception.WineException;
import fr.afcepf.atod.wine.entity.Adress;
import fr.afcepf.atod.wine.entity.City;
import fr.afcepf.atod.wine.entity.Civility;
import fr.afcepf.atod.wine.entity.Customer;
import fr.afcepf.atod.wine.entity.User;

@ManagedBean(name = "mBeanClient")
@SessionScoped
public class MBeanClient implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{buCity}")
	private IBuCity buCity;
	@ManagedProperty(value = "#{buCustomer}")
	private IBuCustomer buCustomer;
	@ManagedProperty(value = "#{buAdress}")
	private IBuAdress buAdress;

	private Adress adress;
//	private Customer customer;
	private User user;
	private Civility[] civilities;
	private List<City> maListe;
	private List<SelectItem> cities;
	private List<City> villeListe;

	@PostConstruct
	public void initInscription() {
//		customer = new Customer();
		user = new User();
		adress = new Adress();
//		adress.setCity(cities.get(index));
		user.setAdress(adress);
	}

	public void addCustomer() {
		try {
			buAdress.addNewAdress(adress);
			buCustomer.addNewCustomer((Customer) user);
		} catch (WineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void validatePassword(ComponentSystemEvent event) {

		FacesContext fc = FacesContext.getCurrentInstance();

		UIComponent components = event.getComponent();

		// get password
		UIInput uiInputPassword = (UIInput) components.findComponent("pwd");
		String pwd = uiInputPassword.getLocalValue() == null ? "" : uiInputPassword.getLocalValue().toString();
		String passwordId = uiInputPassword.getClientId();

		// get confirm password
		UIInput uiInputConfirmPassword = (UIInput) components.findComponent("confirmedPwd");
		String confirmedPwd = uiInputConfirmPassword.getLocalValue() == null ? ""
				: uiInputConfirmPassword.getLocalValue().toString();

		// Let required="true" do its job.
		if (pwd.isEmpty() || confirmedPwd.isEmpty()) {
			return;
		}

		if (!pwd.equals(confirmedPwd)) {

			FacesMessage msg = new FacesMessage("Les mots de passe doivent etre correspondre");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			fc.addMessage(passwordId, msg);
			fc.renderResponse();

		}
	}

	public void validateMail(ComponentSystemEvent event) {

		FacesContext fc = FacesContext.getCurrentInstance();

		UIComponent components = event.getComponent();

		// get password
		UIInput uiInputPassword = (UIInput) components.findComponent("mail1");
		String mail1 = uiInputPassword.getLocalValue() == null ? "" : uiInputPassword.getLocalValue().toString();
		String passwordId = uiInputPassword.getClientId();

		// get confirm password
		UIInput uiInputConfirmPassword = (UIInput) components.findComponent("confirmedMail");
		String confirmedMail = uiInputConfirmPassword.getLocalValue() == null ? ""
				: uiInputConfirmPassword.getLocalValue().toString();

		// Let required="true" do its job.
		if (mail1.isEmpty() || confirmedMail.isEmpty()) {
			return;
		}

		if (!mail1.equals(confirmedMail)) {

			FacesMessage msg = new FacesMessage("Les mails doivent etre correspondre");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			fc.addMessage(passwordId, msg);
			fc.renderResponse();

		}
	}

	// ----------- Getters && Setters ----------------//

	public IBuCity getBuCity() {
		return buCity;
	}

	public Adress getAdress() {
		return adress;
	}

	public void setAdress(Adress adress) {
		this.adress = adress;
	}

	public IBuAdress getBuAdress() {
		return buAdress;
	}

	public void setBuAdress(IBuAdress buAdress) {
		this.buAdress = buAdress;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setBuCity(IBuCity buCity) {
		this.buCity = buCity;
	}

	public IBuCustomer getBuCustomer() {
		return buCustomer;
	}

	public void setBuCustomer(IBuCustomer buCustomer) {
		this.buCustomer = buCustomer;
	}

//	public User getCustomer() {
//		return customer;
//	}
//
//	public void setCustomer(Customer user) {
//		this.customer = user;
//	}

	public Civility[] getCivilities() {
		return Civility.values();
	}

	public void setCivilities(Civility[] civilities) {
		this.civilities = civilities;
	}

	public List<City> getMaListe() {
		return maListe;
	}

	public void setMaListe(List<City> maListe) {
		this.maListe = maListe;
	}

	public List<City> getVilleListe() {
		return villeListe;
	}

	public void setVilleListe(List<City> villeListe) {
		this.villeListe = villeListe;
	}

	public List<SelectItem> getCities() {
		if (cities == null) {
			cities = new ArrayList<SelectItem>();
			try {
				List<City> listVille = buCity.findAllCities();
				for (City obj : buCity.findAllCities()) {
					cities.add(new SelectItem(obj.getId(), obj.getName()));
					System.out.println("ff");
				}
			} catch (WineException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return cities;
	}

	public void setCities(List<SelectItem> cities) {
		this.cities = cities;
	}
}
