/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package thesaurusotomatis;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 *
 * @author AllSeven64
 */
public class ValidationKateglo {
       
 /**
   * baca file JSON
   */   
  public String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }
  
  
  /**
   * koneksi ke api kateglo serta baca file JSON
   */ 
  public JSONObject readJsonFromUrl(String url) throws IOException, JSONException, InterruptedException {
    HtmlUnitDriver driver = new HtmlUnitDriver(BrowserVersion.CHROME);
    driver.get(url);
    driver.setJavascriptEnabled(true);
    //driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    //WebDriverWait wait = new WebDriverWait(driver, 10);
    //wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*")));  
    
    Thread.sleep(1000);
    String page = driver.getPageSource();       
    driver.quit();

    StringReader reader = new StringReader(page);
    String jsonText = readAll(reader);
    JSONObject json = new JSONObject(jsonText);
    return json;
    
  }
  
/**
 * cek apakah url mengandung file JSON
*/   
public boolean isJSONValid(String kata) throws IOException, InterruptedException {
    try {
        HtmlUnitDriver driver = new HtmlUnitDriver(BrowserVersion.CHROME);
        driver.get("http://kateglo.com/api.php?format=json&phrase="+kata);
        driver.setJavascriptEnabled(true);
        //driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        //WebDriverWait wait = new WebDriverWait(driver, 10);
        //wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*")));
        
        String page = driver.getPageSource(); 
        Thread.sleep(1000);
        driver.quit();

        StringReader reader = new StringReader(page);
        String jsonText = readAll(reader);
        JSONObject json = new JSONObject(jsonText);            
    
    }catch (JSONException ex) {
        return false;      
    }
    return true;  
}
  
/**
 * ambil sinonim kata dari kateglo 
*/  
  public List<String> getData (String kata) throws IOException, JSONException, InterruptedException{
        List<String> sinonimKateglo = new ArrayList<>();        
        JSONObject json = readJsonFromUrl("http://kateglo.com/api.php?format=json&phrase="+kata);  
        JSONObject kateglo = json.getJSONObject("kateglo");
        String lexClassQ = kateglo.getString("lex_class");
        JSONObject relasi = kateglo.getJSONObject("relation");
        JSONObject sinonim = relasi.getJSONObject("s");

       // System.out.println(kata +"  "+lexClassQ);
        Iterator<String> keys = (Iterator<String>) sinonim.keys();
        while (keys.hasNext()) {
            String key = keys.next();
          
            if (!key.contains("name")) {
                JSONObject value = sinonim.getJSONObject(key);             
               
                if (!value.get("lex_class").equals(null) && value.getString("lex_class").contains(lexClassQ)){
                    String relatedPhrase = value.getString("related_phrase");
                    //System.out.println("Relasi : " + relatedPhrase);
                    sinonimKateglo.add(relatedPhrase);
                }
            }              
        }
        return sinonimKateglo;   
  }
}
