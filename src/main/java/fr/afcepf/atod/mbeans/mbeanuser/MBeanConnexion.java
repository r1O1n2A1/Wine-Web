/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.afcepf.atod.mbeans.mbeanuser;

import fr.afcepf.atod.business.customer.api.IBuCustomer;
import fr.afcepf.atod.wine.entity.User;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.http.HttpRequest;


/**
 *
 * @author ronan
 */
@ManagedBean(name ="mBeanConnexion")
@SessionScoped
public class MBeanConnexion implements Serializable {
	private static Logger log = Logger.getLogger(MBeanConnexion.class);
    /**
     *
     */
    private static final long serialVersionUID = -3560234336879075955L;

    @ManagedProperty(value = "#{buCustomer}")
    private IBuCustomer buCustomer;
    
    private String absUrl;
    /**
     * user / customer
     */
    private User userConnected;
    /**
     * map of errors
     */
    private Map<String, String> errors;
    /**
     * Message invalid connexion
     */
    private String invalidConnexion
            = "False username and password combination.";
    /**
     * result connection
     */
    private String resultConnection;

    public MBeanConnexion() {
        super();
        userConnected = new User();
    }

    /**
     * test mail/password
     *
     * @return page
     */
    public String connect() {
        String page = null;

        errors = new HashMap<String, String>();
        if (!userConnected.getMail().equalsIgnoreCase("")
                && !userConnected.getPassword().equalsIgnoreCase("")) {
            try {
                userConnected = buCustomer.connect(userConnected.getMail(),
                        userConnected.getPassword());
                page = "index.jsf";
                if (!userConnected.getLastname().equalsIgnoreCase("")) {

                } else {
                    errors.put(invalidConnexion, "User not registered into the database");
                }
            } catch (Exception e) {
                errors.put(invalidConnexion, e.getMessage());
            }
        } else {
            errors.put("Empty Field", "Please complete all mandatory fields before "
                    + "you send off the form.");
        }
        // validate the connection
        validateConnection();
        return page;
    }

    public void validateConnection() {
        if (errors.isEmpty()) {
            resultConnection = null;
        } else {
            resultConnection = errors.get(invalidConnexion);
        }
    }   

    /*
    public static String getINVALID_CO() {
        return INVALID_CO;
    }*/

    /**
     * disconnect current user/customer
     *
     * @return
     */
    public String disconnect() {
        String page = null;
        ((HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true)).invalidate();
        return page;
    }
    
    

    // ----------- Getters && Setters ----------------//
    public String getInvalidConnexion() {
        return invalidConnexion;
    }

    public void setInvalidConnexion(String invalidConnexion) {
        this.invalidConnexion = invalidConnexion;
    }

    public User getUserConnected() {
        return userConnected;
    }

    public void setUserConnected(User userConnected) {
        this.userConnected = userConnected;
    }

    public String getResultConnection() {
        return resultConnection;
    }

    public void setResultConnection(String resultCo) {
        this.resultConnection = resultCo;
    }

    public IBuCustomer getBuCustomer() {
        return buCustomer;
    }

    public void setBuCustomer(IBuCustomer buCustomer) {
        this.buCustomer = buCustomer;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }

    //  ############################################################# //
    /**
     * ********************************************************
     * Methode pour initialiser la session a cause de bootstrap.
     *********************************************************
     */
    /**
     * When should one use the f:viewAction or preRenderView event to initialize
     * data for a page verses using the @PostConstruct annotation?
     *
     * Use the <f:viewAction> when you want to execute a method before the HTML
     * is been rendered. This is particularly useful if you want to perform
     * actions based on model values set by <f:viewParam>
     * during update model values phase. Namely, they are not available at the
     * moment the @PostConstruct runs. In JSF 2.0/2.1, this tag didn't exist and
     * you have to use the preRenderView workaround.
     *
     * If the backing bean is @RequestScoped, do they effectively do the exact
     * same thing? (and so then it is up to developer choice? (@PostConstruct
     * seems "cleaner").
     *
     * No, they do definitely not effectively do the same thing. The
     * @PostConstruct is intented to perform actions directly after bean's
     * construction and setting of all injected dependencies and managed
     * properties such as @EJB, @Inject, @ManagedProperty, etc. Namely, the
     * injected dependencies are not available inside the bean's constructor.
     * This will thus run only once per view, session or application when the
     * bean is view, session or application scoped. The <f:viewAction>
     * is by default only invoked on initial GET request, but can via
     * onPostback="true" attribute be configured to be invoked on postback
     * requests as well. The preRenderView event is invoked on every HTTP
     * request (yes, this also includes ajax requests!). Summarized, use
     * @PostConstruct if you want to perform actions on injected dependencies
     * and managed properties which are set by @EJB, @Inject, @ManagedProperty,
     * etc during bean's construction. Use <f:viewAction> if you also want to
     * perform actions on properties set by <f:viewParam>. If you're still on
     * JSF 2.0/2.1, use preRenderView instead of <f:viewAction>. You can if
     * necessary add a check on FacesContext#isPostback() to perform the
     * preRenderView action on initial request only.
     */
    public void initSession() {
        if (FacesContext.getCurrentInstance()
                .getExternalContext().getSessionId(true) != null) {

        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSession(true);            
        }
    }
    
    public void initPath() {
    	log.info("\t ### Init path  ###");
    	HttpServletRequest request = (HttpServletRequest) FacesContext
    			.getCurrentInstance()
    			.getExternalContext()
    			.getRequest();
    	String url  = request.getRequestURL().toString();
    	String uri  = request.getRequestURI().toString();
    	String cpath = request.getContextPath();
    	String str  = request.getRequestURL()
    			.substring(0, request.getRequestURL().length() 
    					- request.getRequestURI().length()) + 
    			request.getContextPath();
    	if(url.contains("pages")==false && url.contains("index.jsf")==false){
    		str=str+"/pages";
    	}
    	this.absUrl=str;
    	//#{request.requestURL.substring(0, request.requestURL.length() - request.requestURI.length())}#{request.contextPath}/"
    	log.info(url);
    	log.info(str);
    }

	public String getAbsUrl() {
		return absUrl;
	}

	public void setAbsUrl(String absUrl) {
		this.absUrl = absUrl;
	}
    
    

}
