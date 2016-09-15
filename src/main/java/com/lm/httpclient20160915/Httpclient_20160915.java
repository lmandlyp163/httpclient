/**  
 * Project Name:httpclient_study20160915  
 * File Name:Httpclient_20160915.java  
 * Package Name:com.lm.httpclient  
 * Date:2016��9��15������10:51:12  
 * Copyright (c) 2016,  All Rights Reserved.  
 *  
*/  
  
package com.lm.httpclient20160915;  

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;  
/**  
 * ClassName:Httpclient_20160915 <br/>  
 * Date:     2016��9��15�� ����10:51:12 <br/>  
 * @author   hzlimao  
 * @version    
 * @since    JDK 1.8 
 * @see        
 */
public class Httpclient_20160915 {

    /**  
     * HttpClient����SSL����Ҫ����֤��ķ���  
     * ��SSL��Ҫ�������ε�֤�飬ʹ�ø÷���  
     * @param url  
     */  
    public static void ssl(String url) {  
        CloseableHttpClient httpclient = null;  
        try {  
            KeyStore trustStore = KeyStore.getInstance(KeyStore  
                    .getDefaultType());  
            FileInputStream instream = new FileInputStream(new File(  
                    "E:\\tomcat.keystore"));  
            try {  
                // ����keyStore d:\\tomcat.keystore  
                trustStore.load(instream, "123456".toCharArray());  
            } catch (CertificateException e) {  
                e.printStackTrace();  
            } finally {  
                try {  
                    instream.close();  
                } catch (Exception ignore) {  
                }  
            }  
            // �����Լ���CA��������ǩ����֤��  
            SSLContext sslcontext = SSLContexts  
                    .custom()  
                    .loadTrustMaterial(trustStore,  
                            new TrustSelfSignedStrategy()).build();  
            // ֻ����ʹ��TLSv1Э��  
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(  
                    sslcontext,  
                    new String[] { "TLSv1" },  
                    null,  
                    SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);  
            httpclient = HttpClients.custom().setSSLSocketFactory(sslsf)  
                    .build();  
            // ����http����(get��ʽ)  
            HttpGet httpget = new HttpGet(url);  
            System.out.println("executing request" + httpget.getRequestLine());  
            CloseableHttpResponse response = httpclient.execute(httpget);  
            try {  
                HttpEntity entity = response.getEntity();  
                System.out.println("----------------------------------------");  
                System.out.println(response.getStatusLine());  
                if (entity != null) {  
                    System.out.println("Response content length: "  
                            + entity.getContentLength());  
                    System.out.println(EntityUtils.toString(entity));  
                    EntityUtils.consume(entity);  
                }  
            } finally {  
                response.close();  
            }  
        } catch (ParseException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } catch (KeyManagementException e) {  
            e.printStackTrace();  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (KeyStoreException e) {  
            e.printStackTrace();  
        } finally {  
            if (httpclient != null) {  
                try {  
                    httpclient.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
    }  
  
    /**  
     * ����Ҫ����֤�飬SSL��������֤�飬ʹ�ø÷���  
     *   
     * @return  
     */  
    public static CloseableHttpClient createSSLClientDefault() {  
        try {  
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(  
                    null, new TrustStrategy() {  
                        // ��������֤��  
                        public boolean isTrusted(X509Certificate[] chain,  
                                String authType) throws CertificateException {  
                            return true;  
                        }  
                    }).build();  
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(  
                    sslContext);  
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();  
  
        } catch (KeyManagementException e) {  
  
            e.printStackTrace();  
  
        } catch (NoSuchAlgorithmException e) {  
  
            e.printStackTrace();  
  
        } catch (KeyStoreException e) {  
  
            e.printStackTrace();  
  
        }  
        return HttpClients.createDefault();  
  
    }  
  
    /**  
     * HttpClient����SSL������Ҫ����֤��  
     * ��������֤�飬����֤����֤  
     * @param url  
     */  
  
    public static void ssl2(String url) {  
        CloseableHttpClient httpclient = null;  
        try {  
  
            httpclient = createSSLClientDefault();  
            // ����http����(get��ʽ)  
            HttpGet httpget = new HttpGet(url);  
            System.out.println("executing request" + httpget.getRequestLine());  
            CloseableHttpResponse response = httpclient.execute(httpget);  
            try {  
                HttpEntity entity = response.getEntity();  
                System.out.println("----------------------------------------");  
                System.out.println(response.getStatusLine());  
                if (entity != null) {  
                    System.out.println("Response content length: "  
                            + entity.getContentLength());  
                    System.out.println(EntityUtils.toString(entity));  
                    EntityUtils.consume(entity);  
                }  
            } finally {  
                response.close();  
            }  
        } catch (ParseException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (httpclient != null) {  
                try {  
                    httpclient.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
    }  
  
    /**  
     * ���� get����  
     */  
    public static void get(String url) {  
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        try {  
            // ����httpget.  
            HttpGet httpget = new HttpGet(url);  
            System.out.println("executing request " + httpget.getURI());  
            // ִ��get����.  
            CloseableHttpResponse response = httpclient.execute(httpget);  
            try {  
                // ��ȡ��Ӧʵ��  
                HttpEntity entity = response.getEntity();  
                System.out.println("--------------------------------------");  
                // ��ӡ��Ӧ״̬  
                System.out.println(response.getStatusLine());  
                if (entity != null) {  
                    // ��ӡ��Ӧ���ݳ���  
                    System.out.println("Response content length: "  
                            + entity.getContentLength());  
                    // ��ӡ��Ӧ����  
                    System.out.println("Response content: "  
                            + EntityUtils.toString(entity));  
                }  
                System.out.println("------------------------------------");  
            } finally {  
                response.close();  
            }  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (ParseException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            // �ر�����,�ͷ���Դ  
            try {  
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
  
    /**  
     * ���� post���� ���ʱ���Ӧ�ò����ݴ��ݲ�����ͬ���ز�ͬ���  
     */  
    public static void post(String url) {  
        // ����Ĭ�ϵ�httpClientʵ��.  
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        // ����httppost  
        HttpPost httppost = new HttpPost(url);  
        // ������������  
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();  
        formparams.add(new BasicNameValuePair("inputVal", "13301330133"));  
  
        UrlEncodedFormEntity uefEntity;  
        try {  
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");  
            httppost.setEntity(uefEntity);  
            System.out.println("executing request " + httppost.getURI());  
            CloseableHttpResponse response = httpclient.execute(httppost);  
            try {  
                HttpEntity entity = response.getEntity();  
                if (entity != null) {  
                    System.out  
                            .println("--------------------------------------");  
                    System.out.println("Response content: "  
                            + EntityUtils.toString(entity, "UTF-8"));  
                    System.out  
                            .println("--------------------------------------");  
                }  
            } finally {  
                response.close();  
            }  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (UnsupportedEncodingException e1) {  
            e1.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            // �ر�����,�ͷ���Դ  
            try {  
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
  
    /**  
     * post��ʽ�ύ����ģ���û���¼����  
     */  
    public static void postForm(String url) {  
        // ����Ĭ�ϵ�httpClientʵ��.  
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        // ����httppost  
        HttpPost httppost = new HttpPost(url);  
        // ������������  
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();  
        formparams.add(new BasicNameValuePair("j_username", "13301330133"));  
        formparams.add(new BasicNameValuePair("j_password", "330133"));  
        UrlEncodedFormEntity uefEntity;  
        try {  
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");  
            httppost.setEntity(uefEntity);  
            System.out.println("executing request " + httppost.getURI());  
            CloseableHttpResponse response = httpclient.execute(httppost);  
            try {  
                HttpEntity entity = response.getEntity();  
                if (entity != null) {  
                    System.out  
                            .println("--------------------------------------");  
                    System.out.println("Response content: "  
                            + EntityUtils.toString(entity, "UTF-8"));  
                    System.out  
                            .println("--------------------------------------");  
                }  
            } finally {  
                response.close();  
            }  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (UnsupportedEncodingException e1) {  
            e1.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            // �ر�����,�ͷ���Դ  
            try {  
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
  
    /**  
     * �ϴ��ļ�  
     */  
    public static void upload(String url) {  
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        try {  
            HttpPost httppost = new HttpPost(url);  
            FileBody bin = new FileBody(new File(  
                    "C:\\Users\\zhangwenchao\\Desktop\\jinzhongzi.jpg"));  
            // StringBody name = new StringBody("���һ����",  
            // ContentType.TEXT_PLAIN);  
            HttpEntity reqEntity = MultipartEntityBuilder.create()  
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)  
                    .addPart("uploadFile", bin)  
                    .setCharset(CharsetUtils.get("UTF-8")).build();  
            httppost.setEntity(reqEntity);  
            System.out.println("executing request: "  
                    + httppost.getRequestLine());  
            CloseableHttpResponse response = httpclient.execute(httppost);  
  
            // httppost = new  
            // HttpPost(response.getLastHeader("location").getValue());  
            // response = httpclient.execute(httppost);  
            try {  
                System.out.println("----------------------------------------");  
                System.out.println(response.getStatusLine());  
                HttpEntity resEntity = response.getEntity();  
                if (resEntity != null) {  
                    // ��Ӧ����  
                    System.out.println("Response content length: "  
                            + resEntity.getContentLength());  
                    // ��ӡ��Ӧ����  
                    System.out.println("Response content: "  
                            + EntityUtils.toString(resEntity));  
                }  
                // ����  
                EntityUtils.consume(resEntity);  
            } finally {  
                response.close();  
            }  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
  
    /**  
     * �ļ�����  
     */  
    public static void download(String url) {  
        // ����һ��httpclient����  
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        try {  
            HttpGet httpget = new HttpGet(url);  
            CloseableHttpResponse response = httpclient.execute(httpget);  
            HttpEntity resEntity = response.getEntity();  
            if (resEntity != null) {  
                // ��Ӧ����  
                System.out.println("Response content length: "  
                        + resEntity.getContentLength());  
                InputStream in = resEntity.getContent();  
                String fileName = url.substring(url.lastIndexOf("/"));  
                File file = new File("E:\\" + fileName);  
                try {  
                    FileOutputStream fout = new FileOutputStream(file);  
                    int l = -1;  
                    byte[] tmp = new byte[1024];  
                    while ((l = in.read(tmp)) != -1) {  
                        fout.write(tmp, 0, l);  
                        // ע�����������OutputStream.write(buff)�Ļ���ͼƬ��ʧ�棬��ҿ�������  
                    }  
                    fout.flush();  
                    fout.close();  
                } finally {  
                    // �رյͲ�����  
                    in.close();  
                }  
            }  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
  
    }  
  
    public static void main(String[] args) throws Exception {  
  
        // Httpclient_20160915.ssl2("https://www.baidu.com?word=���»�"); //ģ�����HTTPS  
        // Httpclient_20160915.get("http://www.baidu.com?word=���»�"); //ģ�����get  
        // Httpclient_20160915.post("http://localhost:8080/BCP/system/user/checkName");//ģ�����post  
        // Httpclient_20160915.postForm("http://localhost:8080/BCP/j_spring_security_check");//ģ�����post form���ύ  
        Httpclient_20160915.upload("http://localhost:8080/BCP/all/test/upload"); // ģ���ļ��ϴ�  
        // Httpclient_20160915.download("http://localhost:8080/BCP/images/crops/tongyong.jpg");//ģ���ļ��ϴ�  
  
    }  
}
  
