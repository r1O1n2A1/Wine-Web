
package fr.afcepf.atod.webservice.image;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for wineServerCodeError.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="wineServerCodeError">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="FICHIER_INTROUVABLE"/>
 *     &lt;enumeration value="URL_JOINTE_FAUSSE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "wineServerCodeError")
@XmlEnum
public enum WineServerCodeError {

    FICHIER_INTROUVABLE,
    URL_JOINTE_FAUSSE;

    public String value() {
        return name();
    }

    public static WineServerCodeError fromValue(String v) {
        return valueOf(v);
    }

}
