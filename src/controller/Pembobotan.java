/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import entity.Dokumen;
import entity.TermList;
import java.io.FileNotFoundException;
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
    private double[][] idf;
    private Prapengolahan prapengolahan = new Prapengolahan();
    
    
    public Pembobotan(List<Dokumen> dokumenList) throws IOException{
        dokumenSet = dokumenList;
    }
    
    public void prepareDokumenAsli() throws IOException {

        for(int i=0; i< dokumenSet.size();i++) {
            Dokumen dok = dokumenSet.get(i);
                
            prapengolahan.doPrapengolahan(dok);

            dok.setTermList(prapengolahan.getDaftarTerm());

        }
        daftarTermUnik = prapengolahan.getDaftarTermUnik();
        
        
    }
    
    public void prepareDokumenPembanding() {
        try{
            
            for(int i=0; i< dokumenSet.size();i++) {
                Dokumen doc = dokumenSet.get(i);
                
                prapengolahan.doPrapengolahan(doc);

                doc.setTermList(prapengolahan.getDaftarTerm());
                

            }

        }catch(FileNotFoundException ex){
            
        }catch(IOException ex){
            
        }
        System.out.println("sukses");
    }
    
    public void doPembobotan(){

        hasilPembobotan = new double[daftarTermUnik.getTotalTerm()][dokumenSet.size()];
        idf = new double[daftarTermUnik.getTotalTerm()][dokumenSet.size()];
        for(int i=0; i<dokumenSet.size(); i++){
            String docs[] = dokumenSet.get(i).getTermList().toStringArray(); 
            System.out.println(dokumenSet.get(i).getJudulDokumen() + " " + dokumenSet.get(i).getIsiDokumen() + " : " + Arrays.toString(dokumenSet.get(i).getTermList().toStringArray()));
            for(int j=0; j<hasilPembobotan.length; j++){
                
                hasilPembobotan[j][i] = tf(docs, daftarTermUnik.getTermAt(j).getTerm()) * 
                        idf(dokumenSet, daftarTermUnik.getTermAt(j).getTerm());
                
                idf[j][i] = idf(dokumenSet, daftarTermUnik.getTermAt(j).getTerm());
                
                System.out.print(daftarTermUnik.getTermAt(j).getTerm() + ": ");
                System.out.print(tf(docs, daftarTermUnik.getTermAt(j).getTerm()) + " * " 
                        + idf(dokumenSet, daftarTermUnik.getTermAt(j).getTerm()) + " = ");
                System.out.println(hasilPembobotan[j][i]);
            }

        }

    }
    
    //    Transpose matriks double biasa
    public double[][] transpose(double[][] data){
        double[][] transposedMatrix = new double[data[0].length][data.length];
        for(int rows = 0; rows < data.length; rows++){
            for(int cols = 0; cols < data[0].length; cols++){
                transposedMatrix[cols][rows] = data[rows][cols];
            }
        }

        return transposedMatrix;
    }
    
    public double[][] getHasilPembobotan() {
        return transpose(hasilPembobotan);
    }
    
    public double[][] getIdf() {
        return idf;
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

        idf = Math.log10(dokList.size()/n);
        return idf;
    }
    
}
