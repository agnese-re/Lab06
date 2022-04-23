package it.polito.tdp.meteo.model;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;

	private MeteoDAO meteoDao;
	private List<Citta> tutteLeCitta;
	private List<Citta> soluzioneMigliore;
	
	public Model() {
		meteoDao = new MeteoDAO();	// istanza di MeteoDAO per richiamare i suoi metodi
		tutteLeCitta = meteoDao.allCity();
		soluzioneMigliore = new ArrayList<Citta>();
	}

	// Dal DAO al Modello per ottenere tutti i mesi dell'anno
	public List<Month> allMonths() {
		return meteoDao.allMonths();
	}
	
	// of course you can change the String output with what you think works best
	public String getUmiditaMedia(int mese) {
		return meteoDao.avgUmid(mese);
	}
	
	// of course you can change the String output with what you think works best
	public List<Citta> trovaSequenza(int mese) {
		
		List<Citta> soluzioneParziale = new ArrayList<Citta>();
		
		for(Citta citta: tutteLeCitta)
			citta.setRilevamenti(meteoDao.getAllRilevamentiLocalitaMese(mese, citta.getNome()));
		
		cercaRicorsiva(soluzioneParziale,0);	// avvio della ricorsione
		return soluzioneMigliore;
		
	}
	
	public void cercaRicorsiva(List<Citta> soluzioneParziale, int livello) {
		
		// caso terminale: livello pari al numero di giorni totali
		if(livello == NUMERO_GIORNI_TOTALI) {
			double costo = calcolaCosto(soluzioneParziale);
			if(soluzioneMigliore == null || costo < calcolaCosto(soluzioneMigliore))
				soluzioneMigliore = new ArrayList<Citta>(soluzioneParziale);
		} else {
			for(Citta c: tutteLeCitta) 
				if(aggiuntaValida(c,soluzioneParziale)) {
					soluzioneParziale.add(c);
					cercaRicorsiva(soluzioneParziale,livello+1);
					soluzioneParziale.remove(soluzioneParziale.size()-1);
				}
		}		
	}
	
	public double calcolaCosto(List<Citta> soluzioneParziale) {
		
		double costoTotale = 0.0;
		
		for(int giorno = 1; giorno <= NUMERO_GIORNI_TOTALI; giorno++)
			costoTotale += soluzioneParziale.get(giorno-1).getRilevamenti().get(giorno-1).getUmidita();
		
		for(int indice = 0; indice < soluzioneParziale.size()-1; indice++)
			if(!soluzioneParziale.get(indice).equals(soluzioneParziale.get(indice+1)))
				costoTotale += COST; // ad ogni cambiamento
		
		return costoTotale;	
	}
	
	public boolean aggiuntaValida(Citta c, List<Citta> parziale) {
		
		/* 1) citta non compare piu' di NUMERO_GIORNI_CITTA_MAX in soluzione parziale */
		int comparsa = 0;
		for(int indice = 0; indice < parziale.size(); indice++)
			if(parziale.get(indice).equals(c))
				comparsa++;
		if(comparsa == NUMERO_GIORNI_CITTA_MAX)
			return false;	// non posso aggiungere
		
		/* 2) verifica dei giorni minimi di permanenza in citta' */
		if(parziale.size() == 0)	// se e' la prima citta' che sto aggiungendo
			return true;	// posso aggiungere
		else {
			if(parziale.size() == 1 || parziale.size() == 2)	// se ho gia' aggiunto 1 citta', quella che aggiungo deve essere uguale
				if(c.equals(parziale.get(parziale.size()-1)))	// se e' diversa non posso aggiungere 
					return true;
		}
		
		if (parziale.get(parziale.size()-1).equals(c))
			return true; 
		
		// se cambio citta' deve essere rimasto fermo almeno per 3 volte
		if (parziale.get(parziale.size()-1).equals(parziale.get(parziale.size()-2)) 
				&& parziale.get(parziale.size()-2).equals(parziale.get(parziale.size()-3)))
					return true;
		
		return false;
		
	}

}
