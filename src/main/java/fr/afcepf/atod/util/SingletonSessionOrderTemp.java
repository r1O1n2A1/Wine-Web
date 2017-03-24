package fr.afcepf.atod.util;

import fr.afcepf.atod.wine.entity.Order;

public final class SingletonSessionOrderTemp {
	
	private static Order order = null;
	//constructeur private pour l'objet
	private SingletonSessionOrderTemp(){
		super();
	}
	
	// instance de la class singleton
	public static SingletonSessionOrderTemp instance = null;

	// methode permetttant de renvoyer une instance de la classe singleton(getter)
	//return l'instance singleton 
	public final static SingletonSessionOrderTemp getInstance() {
		//le "double check singleton" permet d'éviter un appel couteux a synchronized
		//une fois que l'instanciation est faite.
		if(SingletonSessionOrderTemp.instance == null){
			 // Le mot-clé synchronized sur ce bloc empêche toute instanciation
            // multiple même par différents "threads".
            // Il est TRES important.
			synchronized (SingletonSessionOrderTemp.class) {
				if(SingletonSessionOrderTemp.instance==null){
					SingletonSessionOrderTemp.instance = new SingletonSessionOrderTemp();
				}
			}
		}
		return SingletonSessionOrderTemp.instance;
	}

	public static Order getOrder() {
		return order;
	}

	public static void setOrder(Order order) {
		SingletonSessionOrderTemp.order = order;
	}
}
