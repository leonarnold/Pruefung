package de.gbsschulen;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Dao {

    private Connection connection;
    private PreparedStatement preparedStatement;
    private List<Gegenstand> gegenstaende;

    public Dao() throws SQLException {
       this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/gegenstaende", "root", "mysql");
       gegenstaende = new ArrayList<>();
       preparedStatement = connection.prepareStatement("SELECT bezeichnung, preis FROM gegenstand WHERE bezeichnung LIKE ? ORDER BY bezeichnung");
    }

    public void close() throws SQLException {
        if (connection != null){
            connection.close();
        }
    }

    public void findeArtikel(String bezeichnung) throws SQLException {
        preparedStatement.setString(1, bezeichnung);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            Gegenstand gegenstand = new Gegenstand();
            gegenstand.setBezeichnung(resultSet.getString(1));
            gegenstand.setEinzelPreis(resultSet.getDouble(2));
            gegenstaende.add(gegenstand);
        }
    }

    public List<Gegenstand> getGegenstaende() {
        return gegenstaende;
    }


}
