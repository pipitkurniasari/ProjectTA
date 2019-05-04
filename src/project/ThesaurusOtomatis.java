/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package thesaurusotomatis;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author AllSeven64
 */
public class ThesaurusOtomatis {

   /**
    * 
    * @param args    the command line arguments
    * @exception IOException
    */
    public static void main(String[] args)
	  throws IOException, FileNotFoundException, InterruptedException {
    
        Thesaurus ts = new Thesaurus();
        ts.parseFile("D:\\Data\\ayatValid");
        ts.tfIdfTermCalculator();
        ts.tfIdfPairsTermCalculator();
        ts.clusterWeight();
        ts.cekKateglo();
        //ts.bacaHasilValidasi();
    }   
}
