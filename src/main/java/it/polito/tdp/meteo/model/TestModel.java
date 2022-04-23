package it.polito.tdp.meteo.model;

import java.time.Month;

public class TestModel {

	public static void main(String[] args) {
		
		Model m = new Model();
		
		for(Month month: m.allMonths())
			System.out.println(month);
		
		// System.out.println(m.getUmiditaMedia(12));
		
		// System.out.println(m.trovaSequenza(5));
		

	}

}
