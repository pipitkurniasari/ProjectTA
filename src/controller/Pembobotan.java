/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import entity.Dokumen;
import entity.TermList;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Asus
 */
public class Pembobotan {
    private final List<Dokumen> dokumenSet;
    private TermList daftarTermUnik;
    private double[][] hasilPembobotan;
    private Prapengolahan prapengolahan = new Prapengolahan();
    
    public Pembobotan(List<Dokumen> dokumenList) throws IOException{
        dokumenSet = dokumenList;
    }
    
    public void siapData() throws IOException {

        for(int i=0; i< dokumenSet.size();i++) {
            Dokumen dok = dokumenSet.get(i);
                
            prapengolahan.doPrapengolahan(dok);

            dok.setTermList(prapengolahan.getDaftarTerm());

        }
        daftarTermUnik = prapengolahan.getDaftarTermUnik();
        

    }
    
    public void doPembobotan(){

        hasilPembobotan = new double[daftarTermUnik.getTotalTerm()][dokumenSet.size()];
        for(int i=0; i<dokumenSet.size(); i++){
            String docs[] = dokumenSet.get(i).getTermList().toStringArray(); 
            System.out.println(dokumenSet.get(i).getJudulDokumen() + " " + dokumenSet.get(i).getIsiDokumen() + " : " + Arrays.toString(dokumenSet.get(i).getTermList().toStringArray()));
            for(int j=0; j<hasilPembobotan.length; j++){
                
                hasilPembobotan[j][i] = tf(docs, daftarTermUnik.getTermAt(j).getTerm()) * 
                        idf(dokumenSet, daftarTermUnik.getTermAt(j).getTerm());
                
                System.out.print(daftarTermUnik.getTermAt(j).getTerm() + ": ");
                System.out.print(tf(docs, daftarTermUnik.getTermAt(j).getTerm()) + " * " 
                        + idf(dokumenSet, daftarTermUnik.getTermAt(j).getTerm()) + " = ");
                System.out.println(hasilPembobotan[j][i]);
            }

        }

    }
    
    private static double tf(String[] tweet, String term){
        double n = 0;
        double tf;
        for(String s: tweet){
            if(s.equalsIgnoreCase(term)){
                n++;
            }
        }

        return n;
    }
    
    private static double idf(List<Dokumen> dokList, String term){
        double n = 0; double idf;
        for(int i=0; i<dokList.size(); i++){
            for(int j=0; j<dokList.get(i).getTermList().getTotalTerm(); j++){
                String s = dokList.get(i).getTermList().getTermAt(j).getTerm();
                if(s.equalsIgnoreCase(term)){
                    n++;
                    break;
                }
            }
        }
//        System.out.println("df = "+n);
//        System.out.println("idf = "+Math.log10(2));
        idf = Math.log10(dokList.size()/n);
        return idf;
    }
    
}
