package it.polito.tdp.meteo.DAO;

import java.sql.Connection;
import java.sql.Date;
import java.time.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.model.Citta;
import it.polito.tdp.meteo.model.Rilevamento;

public class MeteoDAO {
	
	/* Tutti i rilevamenti (tutte le citta', tutti i giorni) */
	public List<Rilevamento> getAllRilevamenti() {

		final String sql = "SELECT Localita, Data, Umidita FROM situazione ORDER BY data ASC";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/* Tutti i rilevamenti di una data citta' in un certo mese */
	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) {
		
		String sql = "SELECT Localita, Data, Umidita "
				+ "FROM situazione "
				+ "WHERE localita = ? AND MONTH(DATA) = ? "
				+ "ORDER BY DATA ASC";
		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();
		
		try {
			Connection conn = ConnectDB.getConnection();	// ricavo, apro una nuova connessione
			PreparedStatement st = conn.prepareStatement(sql);	// preparo uno statement
			// Preparazione statement con settaggio dei parametri posti a "?"
			st.setString(1, localita);
			st.setInt(2, mese);
			
			ResultSet rs = st.executeQuery();
			while(rs.next())
				rilevamenti.add(new Rilevamento(rs.getString("localita"),
									rs.getDate("data"),
									rs.getInt("umidita")));
			conn.close();	// chiudo la connessione
			return rilevamenti;
		} catch(SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/* Metodo che restituisce l'umidita' media di una specifica citta' in un dato mese */
	public double avgUmidCitta(String localita, int mese) {
		
		String sql = "SELECT AVG(umidita) AS umid "
				+ "FROM situazione "
				+ "WHERE localita = ? AND MONTH(DATA) = ?";
		double umidita;
		try {
			Connection conn = ConnectDB.getConnection();	// ricavo, apro una nuova connessione
			PreparedStatement st = conn.prepareStatement(sql);	// preparo uno statement
			// Preparazione statement con settaggio dei parametri posti a "?"
			st.setString(1, localita);
			st.setInt(2, mese);
			
			ResultSet rs = st.executeQuery();
			
			rs.next();	// rs.first(); si posiziona su prima e unica riga
			umidita = rs.getDouble("umid");
			
			conn.close();	// chiudo la connessione
			return umidita;
		} catch(SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}
	
	/* Metodo che restituisce l'umidita' media di ogni citta' in un mese specificato */
	public String avgUmid(int mese) {
		
		String sql = "SELECT localita, AVG(umidita) AS umid "
				+ "FROM situazione "
				+ "WHERE MONTH(DATA) = ? "
				+ "GROUP BY localita";
		String risultato = "";
		try {
			Connection conn = ConnectDB.getConnection();	// ricavo, apro una nuova connessione
			PreparedStatement st = conn.prepareStatement(sql);	// preparo uno statement
			// Preparazione statement con settaggio dei parametri posti a "?"
			st.setInt(1, mese);
			
			ResultSet rs = st.executeQuery();
			while(rs.next())
				risultato += (rs.getString("localita") + "\t\t" + (rs.getDouble("umid")) + " %\n");
			conn.close();	// chiudo la connessione
			return risultato;
		} catch(SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}	
	}
	
	/* Metodo che restituisce tutte le citta' presenti nel db */
	public List<Citta> allCity() {
		
		String sql = "SELECT DISTINCT localita "
				+ "FROM situazione "
				+ "ORDER BY localita ASC";
		List<Citta> allCity = new ArrayList<Citta>();
		try {
			Connection conn = ConnectDB.getConnection();	// ricavo, apro una nuova connessione
			PreparedStatement st = conn.prepareStatement(sql);	// preparo uno statement
			
			ResultSet rs = st.executeQuery();
			while(rs.next())
				allCity.add(new Citta(rs.getString("localita")));
			conn.close();	// chiudo la connessione
			return allCity;
		} catch(SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}	
	}
	
	/* Metodo che restituisce tutti i mesi in cui sono stati fatti Rilevamenti, tutti i mesi dell'anno */
	public List<Month> allMonths() {
		
		String sql = "SELECT DISTINCT MONTH(data) AS mese " +	/* MONTH(data) estrae il mese come numero */
						"FROM SITUAZIONE " +
						"ORDER BY MONTH(data)";
		List<Month> allMonths = new ArrayList<Month>();
		try {
			Connection conn = ConnectDB.getConnection();	// ottengo una connessione
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			
			while(rs.next())
				allMonths.add(Month.of(rs.getInt("mese")));	// Month.of ritorna il mese come oggetto di classe Month
			conn.close(); 	// chiudo la connessione
			return allMonths;
		} catch(SQLException sqle) {
			sqle.printStackTrace();
			throw new RuntimeException("Errore nella query", sqle);
		}
	}
}
