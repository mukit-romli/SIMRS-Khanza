package fungsi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.JOptionPane;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class ApiOrthanc {
  private HttpHeaders headers;
  
  private JsonNode root;
  
  private HttpEntity requestEntity;
  
  private ObjectMapper mapper = new ObjectMapper();
  
  private SSLContext sslContext;
  
  private SSLSocketFactory sslFactory;
  
  private Scheme scheme;
  
  private HttpComponentsClientHttpRequestFactory factory;
  
  private String auth;
  
  private String authEncrypt;
  
  private String requestJson;
  
  private byte[] encodedBytes;
  
  private int i = 1;
  
  public ApiOrthanc() {
    try {
      auth=koneksiDB.USERORTHANC()+":"+koneksiDB.PASSORTHANC();
      encodedBytes = Base64.encodeBase64(auth.getBytes());
      authEncrypt = new String(encodedBytes);
    } catch (Exception ex) {
      System.out.println("Notifikasi : " + ex);
    } 
  }
  
  public String Auth() {
    return authEncrypt;
  }
  
  public JsonNode AmbilSeries(String Norm, String Tanggal1, String Tanggal2) {
    System.out.println("Percobaan Mengambil Photo Pasien : " + Norm);
    try {
      headers = new HttpHeaders();
      System.out.println("Auth : " + authEncrypt);
      headers.add("Authorization", "Basic " + authEncrypt);
      requestJson = "{\"Level\": \"Study\",\"Expand\": true,\"Query\": {\"StudyDate\": \"" + Tanggal1 + "-" + Tanggal2 + "\",\"PatientID\": \"" + Norm + "\"}}";
      System.out.println("Request JSON : " + requestJson);
      requestEntity = new HttpEntity(requestJson, (MultiValueMap)headers);
      System.out.println("URL : " + koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/tools/find");
      requestJson = (String)getRest().exchange(koneksiDB.URLORTHANC() + ":" + koneksiDB.URLORTHANC() + "/tools/find", HttpMethod.POST, requestEntity, String.class, new Object[0]).getBody();
      System.out.println("Result JSON : " + requestJson);
      root = mapper.readTree(requestJson);
    } catch (Exception e) {
      System.out.println("Notifikasi : " + e);
    } 
    return root;
  }
  
  public JsonNode AmbilPng(String NoRawat, String Series) {
    System.out.println("Percobaan Mengambil Gambar PNG : " + NoRawat + ", Series : " + Series);
    try {
      headers = new HttpHeaders();
      System.out.println("Auth : " + authEncrypt);
      headers.add("Authorization", "Basic " + authEncrypt);
      requestEntity = new HttpEntity((MultiValueMap)headers);
      System.out.println("URL : " + koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/series/" + Series);
      requestJson = (String)getRest().exchange(koneksiDB.URLORTHANC() + ":" + koneksiDB.URLORTHANC() + "/series/" + koneksiDB.PORTORTHANC(), HttpMethod.GET, requestEntity, String.class, new Object[0]).getBody();
      System.out.println("Result JSON : " + requestJson);
      root = mapper.readTree(requestJson);
      i = 1;
      for (JsonNode list : root.path("Instances")) {
        System.out.println("Mengambil Gambar PNG " + koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/instances/" + list.asText() + "/preview");
        headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + authEncrypt);
        headers.add("Accept", "image/png");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
        headers.setAccept(Collections.singletonList(MediaType.IMAGE_JPEG));
        HttpEntity<String> entity = new HttpEntity((MultiValueMap)headers);
        ResponseEntity<byte[]> response = getRest().exchange(koneksiDB.URLORTHANC() + ":" + koneksiDB.URLORTHANC() + "/instances/" + koneksiDB.PORTORTHANC() + "/preview", HttpMethod.GET, entity, byte[].class, new Object[0]);
        Files.write(Paths.get("./gambarradiologi/" + NoRawat + i + ".png", new String[0]), (byte[])response.getBody(), new java.nio.file.OpenOption[0]);
        i++;
      } 
      JOptionPane.showMessageDialog(null, "Pengambilan Gambar PNG dari Orthanc berhasil, silahkan lihat di dalam folder Aplikasi..!!");
    } catch (Exception e) {
      System.out.println("Notifikasi : " + e);
      JOptionPane.showMessageDialog(null, "Gagal mengambil Gambar PNG dari Orthanc, silahkan hubungi administrator ..!!");
    } 
    return root;
  }
  
  public JsonNode AmbilJpg(String NoRawat, String Series) {
    System.out.println("Percobaan Mengambil Gambar JPG : " + NoRawat + ", Series : " + Series);
    try {
      headers = new HttpHeaders();
      System.out.println("Auth : " + authEncrypt);
      headers.add("Authorization", "Basic " + authEncrypt);
      requestEntity = new HttpEntity((MultiValueMap)headers);
      System.out.println("URL : " + koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/series/" + Series);
      requestJson = (String)getRest().exchange(koneksiDB.URLORTHANC() + ":" + koneksiDB.URLORTHANC() + "/series/" + koneksiDB.PORTORTHANC(), HttpMethod.GET, requestEntity, String.class, new Object[0]).getBody();
      System.out.println("Result JSON : " + requestJson);
      root = mapper.readTree(requestJson);
      i = 1;
      for (JsonNode list : root.path("Instances")) {
        System.out.println("Mengambil Gambar JPG " + koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/instances/" + list.asText() + "/preview");
        headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + authEncrypt);
        headers.add("Accept", "image/jpeg");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
        headers.setAccept(Collections.singletonList(MediaType.IMAGE_JPEG));
        HttpEntity<String> entity = new HttpEntity((MultiValueMap)headers);
        ResponseEntity<byte[]> response = getRest().exchange(koneksiDB.URLORTHANC() + ":" + koneksiDB.URLORTHANC() + "/instances/" + koneksiDB.PORTORTHANC() + "/preview", HttpMethod.GET, entity, byte[].class, new Object[0]);
        Files.write(Paths.get("./gambarradiologi/" + NoRawat + i + ".jpg", new String[0]), (byte[])response.getBody(), new java.nio.file.OpenOption[0]);
        i++;
      } 
      JOptionPane.showMessageDialog(null, "Pengambilan Gambar JPG dari Orthanc berhasil, silahkan lihat di dalam folder Aplikasi..!!");
    } catch (Exception e) {
      System.out.println("Notifikasi : " + e);
      JOptionPane.showMessageDialog(null, "Gagal mengambil Gambar JPG dari Orthanc, silahkan hubungi administrator ..!!");
    } 
    return root;
  }
  
  public JsonNode AmbilBmp(String NoRawat, String Series) {
    System.out.println("Percobaan Mengambil Gambar BMP : " + NoRawat + ", Series : " + Series);
    try {
      headers = new HttpHeaders();
      System.out.println("Auth : " + authEncrypt);
      headers.add("Authorization", "Basic " + authEncrypt);
      requestEntity = new HttpEntity((MultiValueMap)headers);
      System.out.println("URL : " + koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/series/" + Series);
      requestJson = (String)getRest().exchange(koneksiDB.URLORTHANC() + ":" + koneksiDB.URLORTHANC() + "/series/" + koneksiDB.PORTORTHANC(), HttpMethod.GET, requestEntity, String.class, new Object[0]).getBody();
      System.out.println("Result JSON : " + requestJson);
      root = mapper.readTree(requestJson);
      i = 1;
      for (JsonNode list : root.path("Instances")) {
        System.out.println("Mengambil Gambar BMP " + koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/instances/" + list.asText() + "/preview");
        headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + authEncrypt);
        headers.add("Accept", "image/bmp");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
        headers.setAccept(Collections.singletonList(MediaType.IMAGE_JPEG));
        HttpEntity<String> entity = new HttpEntity((MultiValueMap)headers);
        ResponseEntity<byte[]> response = getRest().exchange(koneksiDB.URLORTHANC() + ":" + koneksiDB.URLORTHANC() + "/instances/" + koneksiDB.PORTORTHANC() + "/preview", HttpMethod.GET, entity, byte[].class, new Object[0]);
        Files.write(Paths.get("./gambarradiologi/" + NoRawat + i + ".bmp", new String[0]), (byte[])response.getBody(), new java.nio.file.OpenOption[0]);
        i++;
      } 
      JOptionPane.showMessageDialog(null, "Pengambilan Gambar BMP dari Orthanc berhasil, silahkan lihat di dalam folder Aplikasi..!!");
    } catch (Exception e) {
      System.out.println("Notifikasi : " + e);
      JOptionPane.showMessageDialog(null, "Gagal mengambil Gambar BMP dari Orthanc, silahkan hubungi administrator ..!!");
    } 
    return root;
  }
  
  public JsonNode AmbilDcm(String NoRawat, String Series) {
    System.out.println("Percobaan Mengambil Gambar DCM : " + NoRawat + ", Series : " + Series);
    try {
      headers = new HttpHeaders();
      System.out.println("Auth : " + authEncrypt);
      headers.add("Authorization", "Basic " + authEncrypt);
      requestEntity = new HttpEntity((MultiValueMap)headers);
      System.out.println("URL : " + koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/series/" + Series);
      requestJson = (String)getRest().exchange(koneksiDB.URLORTHANC() + ":" + koneksiDB.URLORTHANC() + "/series/" + koneksiDB.PORTORTHANC(), HttpMethod.GET, requestEntity, String.class, new Object[0]).getBody();
      System.out.println("Result JSON : " + requestJson);
      root = mapper.readTree(requestJson);
      i = 1;
      for (JsonNode list : root.path("Instances")) {
        System.out.println("Mengambil Gambar DCM " + koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/instances/" + list.asText() + "/file");
        headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + authEncrypt);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
        headers.setAccept(Collections.singletonList(MediaType.IMAGE_JPEG));
        HttpEntity<String> entity = new HttpEntity((MultiValueMap)headers);
        ResponseEntity<byte[]> response = getRest().exchange(koneksiDB.URLORTHANC() + ":" + koneksiDB.URLORTHANC() + "/instances/" + koneksiDB.PORTORTHANC() + "/file", HttpMethod.GET, entity, byte[].class, new Object[0]);
        Files.write(Paths.get("./gambarradiologi/" + NoRawat + i + ".dcm", new String[0]), (byte[])response.getBody(), new java.nio.file.OpenOption[0]);
        i++;
      } 
      JOptionPane.showMessageDialog(null, "Pengambilan Gambar DCM dari Orthanc berhasil, silahkan lihat di dalam folder Aplikasi..!!");
    } catch (Exception e) {
      System.out.println("Notifikasi : " + e);
      JOptionPane.showMessageDialog(null, "Gagal mengambil Gambar DCM dari Orthanc, silahkan hubungi administrator ..!!");
    } 
    return root;
  }
  
  public String UbahAccession(String studyId, String accessionBaru) {
    System.out.println("Modify AccessionNumber Study : " + studyId);
    try {
      headers = new HttpHeaders();
      headers.add("Authorization", "Basic " + authEncrypt);
      headers.setContentType(MediaType.APPLICATION_JSON);
      requestJson = "{\"Replace\": {\"AccessionNumber\": \"" + accessionBaru + "\"},\"Force\": true,\"Synchronous\": true,\"KeepSource\": false}";
      System.out.println("Request JSON : " + requestJson);
      requestEntity = new HttpEntity(requestJson, (MultiValueMap)headers);
      String url = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/studies/" + studyId + "/modify";
      System.out.println("URL : " + url);
      String response = (String)getRest().exchange(url, HttpMethod.POST, requestEntity, String.class, new Object[0]).getBody();
      System.out.println("Response : " + response);
      JsonNode root = mapper.readTree(response);
      String studyIdBaru = root.path("ID").asText();
      if (studyIdBaru == null || studyIdBaru.isEmpty()) {
        System.out.println("Modify sukses tapi Study ID baru kosong, pakai Study ID lama.");
        return studyId;
      } 
      System.out.println("Study ID setelah modify: " + studyIdBaru);
      return studyIdBaru;
    } catch (Exception e) {
      System.out.println("Notifikasi : " + e);
      JOptionPane.showMessageDialog(null, "Gagal mengubah Accession Number di Orthanc..!!");
      return null;
    } 
  }
  
  public boolean kirimKeModality(String studyId){
        System.out.println("Kirim Study ke Modality : " + studyId);
        try{
            headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + authEncrypt);
            headers.setContentType(MediaType.APPLICATION_JSON);
            requestJson = "[\"" + studyId + "\"]";
            requestEntity = new HttpEntity(requestJson, headers);
            System.out.println("URL : " + koneksiDB.URLORTHANC()+":"+koneksiDB.PORTORTHANC()+"/modalities/DCMROUTER/store");
            System.out.println("Request JSON : " + requestJson);
            String response = getRest().exchange(koneksiDB.URLORTHANC()+":"+koneksiDB.PORTORTHANC()+"/modalities/DCMROUTER/store",HttpMethod.POST,requestEntity,String.class).getBody();
            System.out.println("Response : " + response);
            System.out.println("Proses kirim ke Modality selesai..!!");
            return true;
        }catch(Exception e){
            System.out.println("Notifikasi : " + e);
            JOptionPane.showMessageDialog(null,"Gagal kirim ke Modality..!!");
            return false;
        }
    }
  
  public RestTemplate getRest() throws NoSuchAlgorithmException, KeyManagementException {
    sslContext = SSLContext.getInstance("SSL");
    TrustManager[] trustManagers = { new X509TrustManager() {
          public X509Certificate[] getAcceptedIssuers() {
            return null;
          }
          
          public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
          
          public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
        } };
    sslContext.init(null, trustManagers, new SecureRandom());
    sslFactory = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
    scheme = new Scheme("https", 443, (SchemeSocketFactory)sslFactory);
    factory = new HttpComponentsClientHttpRequestFactory();
    factory.getHttpClient().getConnectionManager().getSchemeRegistry().register(scheme);
    return new RestTemplate((ClientHttpRequestFactory)factory);
  }
}
