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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Asus
 */
public class CosineSimilarity {

    
    
    
    private DocumentReader dr;
    private TermList daftarTermUnik;
    private double[][] hasilPembobotan;
    private Pembobotan bobot;
    private double[][] bobotPembanding, bobotAsli;
    private double[] docVector1;
    private double[] docVector2;

    public CosineSimilarity(double[][] bobotPembanding, double[][] bobotAsli) {
        this.bobotPembanding = bobotPembanding;
        this.bobotAsli = bobotAsli;
    }

    public void count() {
    
        for (int i = 0; i < bobotPembanding.length; i++) {
            for (int j = 0; j < bobotPembanding[0].length; j++) {
                docVector1[j] = bobotPembanding[i][j];
            }            
                        
            for (int k = 0; k < bobotAsli.length; k++) {
                for (int l = 0; l < bobotAsli[0].length; l++) {
                    docVector2[l] = bobotAsli[l][k];
                }
                getCosineSimilarity(docVector1, docVector2);
            }
            
        }
    }
    
    public double getCosineSimilarity(double[] docVector1, double[] docVector2) {
        double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;
        double cosineSimilarity = 0.0;

        for (int i = 0; i < docVector1.length; i++) //docVector1 and docVector2 must be of same length
        {
            dotProduct += docVector1[i] * docVector2[i];  //a.b
            magnitude1 += Math.pow(docVector1[i], 2);  //(a^2)
            magnitude2 += Math.pow(docVector2[i], 2); //(b^2)
        }

        magnitude1 = Math.sqrt(magnitude1);//sqrt(a^2)
        magnitude2 = Math.sqrt(magnitude2);//sqrt(b^2)

        if (magnitude1 != 0.0 | magnitude2 != 0.0) {
            cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
        } else {
            return 0.0;
        }
        return cosineSimilarity;
    }
}
