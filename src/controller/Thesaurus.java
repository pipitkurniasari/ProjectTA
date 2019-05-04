/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import org.json.JSONException;

/**
 *
 * @author AllSeven64
 */
public class Thesaurus {
    //variabel ini untuk menampung seruluh term pada setiap dokumen ke dalam array
    private List <String[]> termsDocArrays = new ArrayList<>();
    private List <String[]> termsDocArraysNotDuplicate = new ArrayList<>();
    private List <String> allTerms = new ArrayList<>();
    private List <String> pairTerms = new ArrayList<>();
    private List<String[]> allPairsTerms = new ArrayList<>();
    private List <double[]> tfidfDocsVectorTerm = new ArrayList<>();
    private List <double[]> tfidfDocsVectorPairTerm = new ArrayList<>();
    private List <Double> totalTermWij = new ArrayList<>();
    private List <Double> dfVector = new ArrayList<>();
    private List <Double> totalPairsWijk = new ArrayList<>();
    private List <Double> clusterWeight = new ArrayList<>();
    private List <String[]> validPairTerm = new ArrayList<>();
    private List <String> expandTerm = new ArrayList<>();
    private String[] dok = new String[120];
    private String[] stopWord;
    
    /**
     * fungsi untuk membaca file .txt
     * @param bacateks
     * @return
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public String readDokumenTeks(String bacateks) throws FileNotFoundException, IOException {
        File bacafile = new File(bacateks);
        FileReader inputDokumen = new FileReader(bacafile);
        BufferedReader bufferBaca = new BufferedReader(inputDokumen);
        StringBuilder content = new StringBuilder();
        String barisData;
        while ((barisData = bufferBaca.readLine()) != null) {
            content.append(barisData);
            content.append(System.getProperty("line.separator"));
        }
        return content.toString();
    }
    
    /**
     * methode untuk membaca kamus stopword.txt dan data disimpan pada variabel String[] stopWord
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public void bacaKamusStopword() throws FileNotFoundException, IOException {
        String readKamusStopword;
        List <String> listKamusStopword = new ArrayList<>();
        readKamusStopword = readDokumenTeks("D:\\kamusStopword.txt");
        StringTokenizer strKamus = new StringTokenizer(readKamusStopword, ",");
        while (strKamus.hasMoreTokens()) {
            listKamusStopword.add(strKamus.nextToken());
        }
        stopWord = listKamusStopword.toArray(new String[0]);
    }
    
    /**
     * Method untuk membaca file dokument.txt dan menyimpan data pada list.
     * output: Mendapatkan seluruh term yang ada pada dokumen dan disimpan pada variabel List<String> allTerms
     * output: Mendapatkan pasangan term dari setiap dokumen dan disimpan pada variabel List<String[]> allPairTerms
     * @param filePath : source file path
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void parseFile (String filepath) throws FileNotFoundException, IOException {
        File[] allFiles = new File(filepath).listFiles();
        int z = 1;
        List <String> hasilStemm = new ArrayList<>();
        bacaKamusStopword();
        
        //membaca file dokumen txt
        for (File f : allFiles ){
            if (f.getName().endsWith(".txt")){
                BufferedReader in = new BufferedReader(new FileReader (f));
                StringBuilder sb = new StringBuilder(); 
                String s; 
                
                while ((s = in.readLine()) != null){  
                    sb.append(s); 
                    dok[z] = s; 
                }
                //System.out.println(sb.toString()); //untuk melihat hasilnya bahwa seluruh file dan isinya telah dimasukkan pada varibel sb
                  
                String[] tokenizedTermsDoc = sb.toString().toLowerCase().replaceAll("[+.^:;,\"'()?!]", "").replaceAll("-", " ").split(" ");
                List <String> listTermDoc = new ArrayList<String>(Arrays.asList(tokenizedTermsDoc));
                listTermDoc.removeAll(Arrays.asList(stopWord));  //stopword pada TermDoc
                    
                for (String stemm : listTermDoc){
                    Stemming st = new Stemming(); //Stemm
//                    stemm = st.KataDasar(stemm);
                    hasilStemm.add(stemm);
                       
                    if(!allTerms.contains(stemm)){ //menghindari duplikat entri. untuk mendapatkan seluruh term pada dokumen
                        allTerms.add(stemm);
                    }
                }                
                String[] termDocAfterStopWord = hasilStemm.toArray(new String[0]);
                
                termsDocArrays.add(termDocAfterStopWord); //token yang sudah dipraproses masuk ke termsDocArrays
                hasilStemm.clear();
               // System.out.println("tokenizedTermDoc, token asli dari Dokumen" +Arrays.toString(tokenizedTermsDoc));     
            }
              z = z+1;       
        } 
        
        //termDocArrays remove same value on string array
        for(String[] listTermDoc : termsDocArrays){
            listTermDoc = new LinkedHashSet<String>(Arrays.asList(listTermDoc)).toArray(new String[0]);
            termsDocArraysNotDuplicate.add(listTermDoc);
        }
        
        //get pair windowsSize 2. menggunakan dokumen yang disetiap dokumen tidak ada kata yang duplikat. 
        //untuk mendapatkan pasangan term disetiap dokumen
        for(String[] listTermDoc : termsDocArraysNotDuplicate){
            int windowSize = 2;
            for (int i = 0; i < listTermDoc.length; i++){
                for(int j = i-windowSize; j <= i+windowSize; j++){
                    if(j>=0 && j<listTermDoc.length && i!=j){
                        pairTerms.add(listTermDoc[i]+ " "+listTermDoc[j]);
                    }
                }
            }
        }
        
        //pair yg isinya string, diubah jadi list<string>
        List <List<String>> bc = new ArrayList<>();         
        for(String pair : pairTerms){
            List<String> cc = new ArrayList<>(Arrays.asList(pair.split(" "))); 
            if(!bc.contains(cc)){ // menghindari duplikat pairTerms. untuk foreach type data primitif String/String[] tidak boleh ada operasi add/remove, jadi ubah dalam bentuk list(bc) 
                bc.add(cc);
            }
        }  
        
        //rubah List<String> jadi String[]
        for(List<String> l : bc){
            String [] startArr = l.toArray(new String[0]);
            allPairsTerms.add(startArr);
        }
                
        /* hasil output dari metode parseFile
        //print pair All term
        for(String [] term : allPairsTerms){
            System.out.println(Arrays.toString(hh));
        }  
        System.out.println(allPairsTerms.size());
        */
    }
    
    /**
     * Method untuk membuat vector perhitungan tfidf seluruh allTerm .
     * output : tfidf vector allTerm simpan didalam variabel tfidfDocsVectorTerm
     * output : list nilai df allTerm simpan didalam variabel dfVector
     * output : total dari penjumlahan bobot/tfidf setiap dokumen simpan didalam variabel totalTermWij
     */
    
    public void tfIdfTermCalculator() {
        double tf; //term frequency
        double idf; //inverse document frequency
        double tfidf; //term requency inverse document frequency 
        double df = 0;
       
        //get tfidf vector
        for (String[] docTermsArray : termsDocArrays) {
            double[] tfidfvectors = new double[allTerms.size()];
                      
            int count = 0;
            for (String terms : allTerms) {
                tf = new TfIdfTerm().tfCalculator(docTermsArray, terms);
                idf = new TfIdfTerm().idfCalculator(termsDocArrays, terms);  
                tfidf = tf * idf;
                tfidfvectors[count] = tfidf;               
                count++; 
            }
            tfidfDocsVectorTerm.add(tfidfvectors);  //storing document vectors;              
        }
        
        //get df
        for (String terms : allTerms) {
            df = new TfIdfTerm().dfCalculator(termsDocArrays, terms);
            dfVector.add(df);
        }
                
        //get total Wij        
        double[] total = new double [allTerms.size()];
        for(double[] s : tfidfDocsVectorTerm){
            for (int i=0; i<total.length; i++){
                total[i] += s[i]; 
            }
        }
        
        //simpan double[] total ke dalam List <Double> totalTermWij
        for (double d : total){
            totalTermWij.add(d);
        } 
            
    }
    
    /**
     * Fungsi untuk mengembalikan index allTerm untuk pencarian saat mendapatkan nilai pada pairTerm kata ke2
     */
    public int locateWord(String cari, List <String> y){     
        int index = 0;
        for (int i=0; i<=y.size()-1; i++){
            if (y.get(i).equals(cari)){
                index = i;
                break;
            }
        }
        return index;
    }    
      
    /**
     * Method to create termVector according to its tfidf pairAllTerm score.
     * output : tfidf vector allPairTerm
     * output : total dari penjumlahan bobot/tfidf allPairTerm setiap dokumen
     */
    public void tfIdfPairsTermCalculator() {
        double tf; //term frequency
        double idf; //inverse document frequency
        double tfidf; //term requency inverse document frequency
                
        //get tfidf vector pairTerm
        for (String[] docTermsArray : termsDocArrays) { 
            double[] tfidfvectors = new double[allPairsTerms.size()];
            int count = 0;
    
            for (String[] terms : allPairsTerms) {      
                tf = new TfIdfPairTerm().tfPairCalculator(docTermsArray, terms);
                idf = new TfIdfPairTerm().idfPairCalculator(termsDocArrays, terms);
                tfidf = tf * idf;
                tfidfvectors[count] = tfidf;               
                count++; 
            }         
            tfidfDocsVectorPairTerm.add(tfidfvectors);  //storing document vectors;              
        }
                     
        //to get total Wijk
        double[] total = new double [allPairsTerms.size()];
        for(double []s : tfidfDocsVectorPairTerm){           
            for (int i=0; i<total.length; i++){
                total[i] += s[i];
            }
        }
       
        //simpan double[] total ke dalam LIST
        for (double d : total){
            totalPairsWijk.add(d);
        } 
    }
    
    /**
     * Methode untuk menghitung nilai ClusterWeight
     * input: totalWij , totalPairWijk, df
     * output : kemiripan hasilclusterweight diatas 0,5 menjadi kandidat thesaurus. hasil disimpan dalam file text
     * @throws IOException 
     */
    public void clusterWeight() throws IOException{
        int j = 0;
        double hasilClusterWeight;
        
        for (String [] s : allPairsTerms){   
            String data1 = s[0];
            String data2 = s[1];
            Double WijBobot = totalTermWij.get(locateWord(data1, allTerms));
            Double dfk = dfVector.get(locateWord(data2, allTerms));
            Double bobotWijk = totalPairsWijk.get(j);
            double N = termsDocArrays.size();
      
            hasilClusterWeight = (bobotWijk/WijBobot)*(Math.log10(N/dfk)/Math.log10(N));            
            //System.out.println(Arrays.toString(s)+" , Wijk = "+bobotWijk+", Wij = "+WijBobot+", dfk = "+dfk+", N = "+N+ " CW = "+hasilClusterWeight);          
            j++;
            clusterWeight.add(hasilClusterWeight);
        }        
               
        //untuk validasii hasil clusterWeight yang nilainya diatas 0.5
        for (int i=0; i<allPairsTerms.size(); i++){ 
            if (clusterWeight.get(i) > 0.5 && !clusterWeight.get(i).isInfinite()){
               // System.out.println(clusterWeight.get(i));
                validPairTerm.add(allPairsTerms.get(i));
            } 
        }  
                
        //print validPairTerm
        for (String[] ss: validPairTerm){
            System.out.println(Arrays.toString(ss));
        }
        System.out.println(validPairTerm.size());   
            
        //Tulis hasil thesaurus ke file .txt
        FileWriter fw = new FileWriter("D:\\hasilThesaurusSize2.txt"); 
        for(String[] str: validPairTerm) {
            for (int i = 0; i < str.length; i++) {
                fw.write(str[i] + " ");
            }     
            fw.write("\n");
        }
        fw.close(); 
                
    }
    
    /**
     * Methode untuk mengecek hasil Thesaurus dengan data dikateglo
     * input : file txt hasil Thesaurus
     * output : hasil file txt hasil validasi Thesaurus.
     */
    public void cekKateglo() throws FileNotFoundException, IOException, InterruptedException, JSONException{
        String kata1;
        String kata2;
        Boolean adaKata;
        List<String> sinom = new ArrayList<>();
        
        //Set dipake untuk membandingkan 2 array untuk mendapatkan array not duplikat.
        Set<String[]> dataSinonim = new TreeSet<>(new Comparator<String[]>() {
            @Override
            public int compare(String[] o1, String[] o2) {
              return Arrays.equals(o1, o2)? 0 : Arrays.hashCode(o1) - Arrays.hashCode(o2);
            }
        });       
        
        //baca file .txt simpan di variabel bacaThesaurus
        Scanner sc = new Scanner(new File("D:\\hasilThesaurusSize2.txt"));
        List<String> lines = new ArrayList<String>();
        List<String[]> bacaThesaurus = new ArrayList<>(); 
        
        while (sc.hasNextLine()) {
            lines.add(sc.nextLine());
        }
        
        String[] arr = lines.toArray(new String[0]);
        
        for(String ss : arr){
            bacaThesaurus.add(ss.split(" "));
        }
        
        //iterasi setiap string[] untuk mengecek sinonim
        Iterator<String[]> iter = bacaThesaurus.iterator();
        while (iter.hasNext()) {
            String[] array = iter.next();
            kata1 = array[0];
            kata2 = array[1];
            System.out.println("data : "+Arrays.toString(array));
            
            ValidationKateglo data = new ValidationKateglo();
            adaKata = data.isJSONValid(kata1);
            
            //jika data memiliki file JSON
            if(adaKata){
                sinom = data.getData(kata1);
                System.out.println("sinonim kata1 = "+sinom);
                
                if (sinom.contains(kata2)){
                    //System.out.println(Arrays.toString(array)+" -> mirip");
                    dataSinonim.add(array);
                }
                else{
                     for (String s : sinom){                       
                        String[] dataTambah = (kata1+"-"+s).split("-");
                        String[] dataTambah2 = (s+"-"+kata1).split("-");
                        
                        dataSinonim.add(dataTambah);          
                        dataSinonim.add(dataTambah2); 
                     }
                    iter.remove();
                }
            }// jika tidak ada file JSON dikateglo, hapus string[]
            else{
                iter.remove();             
            }
        }
        
//        System.out.println("\n----\n");
//        for (String[] ss : baca){
//            System.out.println(Arrays.toString(ss));
//        }
        System.out.println("baca size : "+bacaThesaurus.size());
        System.out.println("hasilDataValidasiSinonim size : "+dataSinonim.size());
        
        //Tulis hasil validasi thesaurus akhir  ke file .txt
        FileWriter fw = new FileWriter("D:\\newHasilValidationTheSize2.txt"); 
        for(String[] str: dataSinonim) {
            for (int i = 0; i < str.length; i++) {
                fw.write(str[i] + " ");
            }     
            fw.write("\n");
        }
        fw.close();        
    }
    
    public void bacaHasilValidasi() throws FileNotFoundException{
        //baca kamus Thesaurus Akhir (validasi Kateglo)
        Scanner sc = new Scanner(new File("D:\\Data\\hasilThesaurus\\newHasilValidationTheSize2.txt"));
        List<String> lines = new ArrayList<String>();
        List<String[]> bacaThesaurus = new ArrayList<>();
        while (sc.hasNextLine()) {
            lines.add(sc.nextLine());
        }
  
        String[] arr = lines.toArray(new String[0]);
        System.out.println("get Thesaurus : "+Arrays.toString(arr));
        
        for(String ss : arr){
            bacaThesaurus.add(ss.split(" "));
            System.out.println(ss);
        }
        System.out.println(arr.length);
        System.out.println(bacaThesaurus.size());
        
    }
    
}
