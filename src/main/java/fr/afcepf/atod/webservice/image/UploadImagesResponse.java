
package fr.afcepf.atod.webservice.image;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for uploadImagesResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="uploadImagesResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="uploadImages" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "uploadImagesResponse", propOrder = {
    "uploadImages"
})
public class UploadImagesResponse {

    protected String uploadImages;

    /**
     * Gets the value of the uploadImages property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUploadImages() {
        return uploadImages;
    }

    /**
     * Sets the value of the uploadImages property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUploadImages(String value) {
        this.uploadImages = value;
    }

}
