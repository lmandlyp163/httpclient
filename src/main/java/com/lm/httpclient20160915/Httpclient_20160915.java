/**  
 * Project Name:httpclient_study20160915  
 * File Name:Httpclient_20160915.java  
 * Package Name:com.lm.httpclient  
 * Date:2016年9月15日上午10:51:12  
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
 * Date:     2016年9月15日 上午10:51:12 <br/>  
 * @author   hzlimao  
 * @version    
 * @since    JDK 1.8 
 * @see        
 */
public class Httpclient_20160915 {

    /**  
     * HttpClient连接SSL，需要导入证书的方法  
     * 若SSL需要导入信任的证书，使用该方法  
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
                // 加载keyStore d:\\tomcat.keystore  
                trustStore.load(instream, "123456".toCharArray());  
            } catch (CertificateException e) {  
                e.printStackTrace();  
            } finally {  
                try {  
                    instream.close();  
                } catch (Exception ignore) {  
                }  
            }  
            // 相信自己的CA和所有自签名的证书  
            SSLContext sslcontext = SSLContexts  
                    .custom()  
                    .loadTrustMaterial(trustStore,  
                            new TrustSelfSignedStrategy()).build();  
            // 只允许使用TLSv1协议  
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(  
                    sslcontext,  
                    new String[] { "TLSv1" },  
                    null,  
                    SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);  
            httpclient = HttpClients.custom().setSSLSocketFactory(sslsf)  
                    .build();  
            // 创建http请求(get方式)  
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
     * 不需要导入证书，SSL信任所有证书，使用该方法  
     *   
     * @return  
     */  
    public static CloseableHttpClient createSSLClientDefault() {  
        try {  
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(  
                    null, new TrustStrategy() {  
                        // 信任所有证书  
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
     * HttpClient连接SSL，不需要导入证书  
     * 信任所有证书，跳过证书验证  
     * @param url  
     */  
  
    public static void ssl2(String url) {  
        CloseableHttpClient httpclient = null;  
        try {  
  
            httpclient = createSSLClientDefault();  
            // 创建http请求(get方式)  
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
     * 发送 get请求  
     */  
    public static void get(String url) {  
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        try {  
            // 创建httpget.  
            HttpGet httpget = new HttpGet(url);  
            System.out.println("executing request " + httpget.getURI());  
            // 执行get请求.  
            CloseableHttpResponse response = httpclient.execute(httpget);  
            try {  
                // 获取响应实体  
                HttpEntity entity = response.getEntity();  
                System.out.println("--------------------------------------");  
                // 打印响应状态  
                System.out.println(response.getStatusLine());  
                if (entity != null) {  
                    // 打印响应内容长度  
                    System.out.println("Response content length: "  
                            + entity.getContentLength());  
                    // 打印响应内容  
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
            // 关闭连接,释放资源  
            try {  
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
  
    /**  
     * 发送 post请求 访问本地应用并根据传递参数不同返回不同结果  
     */  
    public static void post(String url) {  
        // 创建默认的httpClient实例.  
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        // 创建httppost  
        HttpPost httppost = new HttpPost(url);  
        // 创建参数队列  
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
            // 关闭连接,释放资源  
            try {  
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
  
    /**  
     * post方式提交表单（模拟用户登录请求）  
     */  
    public static void postForm(String url) {  
        // 创建默认的httpClient实例.  
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        // 创建httppost  
        HttpPost httppost = new HttpPost(url);  
        // 创建参数队列  
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
            // 关闭连接,释放资源  
            try {  
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
  
    /**  
     * 上传文件  
     */  
    public static void upload(String url) {  
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        try {  
            HttpPost httppost = new HttpPost(url);  
            FileBody bin = new FileBody(new File(  
                    "C:\\Users\\zhangwenchao\\Desktop\\jinzhongzi.jpg"));  
            // StringBody name = new StringBody("这个一测试",  
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
                    // 响应长度  
                    System.out.println("Response content length: "  
                            + resEntity.getContentLength());  
                    // 打印响应内容  
                    System.out.println("Response content: "  
                            + EntityUtils.toString(resEntity));  
                }  
                // 销毁  
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
     * 文件下载  
     */  
    public static void download(String url) {  
        // 生成一个httpclient对象  
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        try {  
            HttpGet httpget = new HttpGet(url);  
            CloseableHttpResponse response = httpclient.execute(httpget);  
            HttpEntity resEntity = response.getEntity();  
            if (resEntity != null) {  
                // 响应长度  
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
                        // 注意这里如果用OutputStream.write(buff)的话，图片会失真，大家可以试试  
                    }  
                    fout.flush();  
                    fout.close();  
                } finally {  
                    // 关闭低层流。  
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
  
        // Httpclient_20160915.ssl2("https://www.baidu.com?word=刘德华"); //模拟访问HTTPS  
        // Httpclient_20160915.get("http://www.baidu.com?word=刘德华"); //模拟访问get  
        // Httpclient_20160915.post("http://localhost:8080/BCP/system/user/checkName");//模拟访问post  
        // Httpclient_20160915.postForm("http://localhost:8080/BCP/j_spring_security_check");//模拟访问post form表单提交  
        Httpclient_20160915.upload("http://localhost:8080/BCP/all/test/upload"); // 模拟文件上传  
        // Httpclient_20160915.download("http://localhost:8080/BCP/images/crops/tongyong.jpg");//模拟文件上传  
  
    }  
}
  
