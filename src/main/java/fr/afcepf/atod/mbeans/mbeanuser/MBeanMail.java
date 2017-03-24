package fr.afcepf.atod.mbeans.mbeanuser;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import fr.afcepf.atod.business.customer.test.JavaMail;


@ManagedBean(name ="mBeanMail")
@SessionScoped
public class MBeanMail {
	
	

	
	private String mailTo;
	

	public String getMailTo() {
		return mailTo;
	}


	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}




	public void sendMail(){
		JavaMail.sendMail(mailTo);
	}
	
	
	
}
