/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.afcepf.atod.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Classe util
 *
 * @author ronan
 */

public final class UtilConverter {

    private UtilConverter() {
        super();
    }
    /**
     * from a set to a list
     * @param set
     * @return 
     */
    public static List retrieveListAsSet(Set set) {
        return new ArrayList(set);
    }
    public static Set retrieveSetAsList(List list) {
        return new HashSet(list);
    }
    
    
}
