/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.List;
import entity.TermList;
import entity.Dokumen;
import entity.Term;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Asus
 */
public class Prapengolahan {
    private TermList daftarTerm;
    private TermList daftarTermUnik;
    private List<Dokumen> docList;
    
    public Prapengolahan() {
        daftarTermUnik = new TermList();
    }
    
    public void doPrapengolahan(Dokumen dok) throws IOException {
        String[] simbol = hapusSimbol(dok.getIsiDokumen().toLowerCase()).split("\\s+");    
        String[] stemming = stemming(simbol);
        
	daftarTerm = new TermList();

        for(int i=0; i<stemming.length; i++){
            if(stemming[i].length()>1) {
		if(!findStopWord(stemming[i])) {
                    stemming[i] = stemming[i].toLowerCase();
                    daftarTerm.addTerm(new Term(stemming[i]));
                    
                    Term term = daftarTermUnik.checkTerm(stemming[i]);
                    if(term==null) {
			daftarTermUnik.addTerm(new Term(stemming[i]));
                    }
		}
            }
        }
    }
    
    private String hapusSimbol(String dokumen){
        String simbol = "[!\"$%&'()*\\+,.;:/<=>?\\[\\]^~_\\`{|}…0987654321]";
        return dokumen.replaceAll(simbol, " ");
    }
    
    private String[] stemming (String[] token) throws IOException{
        Stemming stem = new Stemming();
        
        String st = "";
        String[] result;
        for(int i=0; i<token.length; i++){
            st += stem.kataDasar(token[i])+ "\n";
        }
        result = st.split("\n");
        return result;

    }
    
    public boolean findStopWord(String str) {
        List<String>listStopword = new ArrayList<String>();
        DocumentReader dr = new DocumentReader();
        
        listStopword = dr.readStopWord();
        
        boolean ada=false;
        for(int i=0; i<listStopword.size(); i++) {
            if(listStopword.get(i).contains(str)) {
                ada=true;
                break;
            }
            
        }
        //System.out.println(ada);
	return ada;
    }
    
    public TermList getDaftarTerm() {
	return daftarTerm;
    }

    public TermList getDaftarTermUnik() {
	return daftarTermUnik;
    }
}
