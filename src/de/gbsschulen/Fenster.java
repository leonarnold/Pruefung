package de.gbsschulen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class Fenster extends JFrame{

    private JPanel jpNorth, jpSouth;
    private JComboBox<Gegenstand> jComboBox;
    private JTextField jtxtAnzahl;
    private JButton jbtnEintragen, jbtnLoeschen;
    private JLabel jlGesamtpreis;
    private JMenuBar jMenuBar;
    private JMenu jmenuDatei;
    private JMenuItem jmiNew, jmiSpeichern, jmiBeenden;

    private JTable jTable;
    private MeinTableModel meinTableModel;
    private JScrollPane jScrollPane;

    private Dao dao;

    private JFileChooser jFileChooser;


    public Fenster() throws HeadlessException, SQLException {
       super();
       this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
       this.dao = new Dao();
       this.initMenu();
       this.iniComponents();
       this.iniEvents();
       this.setSize(600, 400);
       this.setVisible(true);
   }

    private void iniEvents() {
       this.addWindowListener(new WindowAdapter() {
           @Override
           public void windowClosing(WindowEvent e) {
               beenden();
           }
       });

       jmiBeenden.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               beenden();
           }
       });

       jbtnEintragen.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               eintragen();
           }
       });

       jmiNew.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               neu();
           }
       });

       jmiSpeichern.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               speichern();
           }
       });
       
       jbtnLoeschen.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               loeschen();
           }
       });
    }

    private void loeschen() {
        int selectedRow = jTable.getSelectedRow();
        if (selectedRow >= 0){
            String bezeichnung = (String) meinTableModel.getValueAt(selectedRow, 1);
            meinTableModel.loeschen(bezeichnung);
            anzeigeAktuallisieren();
        }
    }

    private void speichern() {
        int result = jFileChooser.showSaveDialog(this);
        if (result == JFileChooser.CANCEL_OPTION){
            return;
        }
        File file = jFileChooser.getSelectedFile();
        try {
            meinTableModel.speichern(file);
        }catch (IOException e){
            JOptionPane.showMessageDialog(this, "Datei kann nicht geschrieben werden", "Fehler", JOptionPane.WARNING_MESSAGE);
        }

        JOptionPane.showMessageDialog(this, "Datei erfolgreich gespeichert", "Erfolg", JOptionPane.INFORMATION_MESSAGE);
        this.setTitle("Einkaufsliste: " + file.getName());
    }

    private void anzeigeAktuallisieren(){
        jlGesamtpreis.setText(String.valueOf(meinTableModel.getGesamtpreis()));
    }

    private void neu() {
        meinTableModel = new MeinTableModel();
        jTable.setModel(meinTableModel);
        meinTableModel.fireTableDataChanged();
        anzeigeAktuallisieren();
    }

    private void eintragen() {
        if (jComboBox.getSelectedIndex() == 0){
            return;
        }
        String eingabe = jtxtAnzahl.getText();
        int anzahl = 0;
        try {
            anzahl = Integer.parseInt(eingabe);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Falsche Eingabe", "Fehler", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Gegenstand gegenstand = (Gegenstand) jComboBox.getSelectedItem();
        Gegenstand neuerGegenstand = new Gegenstand(gegenstand.getBezeichnung(), gegenstand.getEinzelPreis(), gegenstand.getAnzahl());
        if (anzahl > 0){
            neuerGegenstand.setAnzahl(anzahl);
            meinTableModel.hinzufuegen(neuerGegenstand);
            meinTableModel.fireTableDataChanged();
            jComboBox.setSelectedIndex(0);
            jtxtAnzahl.setText("");
            anzeigeAktuallisieren();
        }
    }

    private void beenden() {
       int result = JOptionPane.showConfirmDialog(this, "wollen Sie wirklich beenden?", "Beenden?", JOptionPane.YES_NO_OPTION);
       if (result == JOptionPane.YES_OPTION){
           System.exit(NORMAL);
       }
    }

    private void initMenu(){
       jMenuBar = new JMenuBar();
       jmenuDatei = new JMenu("Datei");
       jmiNew = new JMenuItem("Neu");
       jmiSpeichern = new JMenuItem("Speichern");
       jmiBeenden = new JMenuItem("Beenden");

       jmenuDatei.add(jmiNew);
       jmenuDatei.add(jmiSpeichern);
       jmenuDatei.add(jmiBeenden);

       jMenuBar.add(jmenuDatei);

       this.setJMenuBar(jMenuBar);
   }

    private void iniComponents() throws SQLException {
        jFileChooser = new JFileChooser();
       jpNorth = new JPanel();
       jComboBox = new JComboBox<Gegenstand>();

       befuelleCombobox();
       jtxtAnzahl = new JTextField(2);
       jbtnEintragen = new JButton("Eintragen");
       jbtnLoeschen = new JButton("Löschen");
       jpNorth.add(jComboBox);
       jpNorth.add(new JLabel("Anzahl: "));
       jpNorth.add(jtxtAnzahl);
       jpNorth.add(jbtnEintragen);
       jpNorth.add(jbtnLoeschen);


       meinTableModel = new MeinTableModel();
       jTable = new JTable(meinTableModel);
       jScrollPane = new JScrollPane(jTable);



       jpSouth = new JPanel();
       jpSouth.setLayout(new FlowLayout(FlowLayout.RIGHT));
       jpSouth.add(new JLabel("Gesamtpreis: "));
       jlGesamtpreis = new JLabel("0.00");
       jpSouth.add(jlGesamtpreis);

       this.add(jpNorth, BorderLayout.NORTH);
       this.add(jScrollPane, BorderLayout.CENTER);
       this.add(jpSouth, BorderLayout.SOUTH);
    }

    private void befuelleCombobox() throws SQLException {
        jComboBox.addItem(new Gegenstand("Bitte auswählen...", 0, 0));
        try{
            dao.findeArtikel("%%");
            for (Gegenstand gegenstand : dao.getGegenstaende()){
                jComboBox.addItem(gegenstand);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try{
                dao.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            new Fenster();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
