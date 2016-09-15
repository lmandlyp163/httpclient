/**  
 * Project Name:httpclient_study20160915  
 * File Name:InfoLoad.java  
 * Package Name:com.lm.httpclient20160915  
 * Date:2016��9��15������11:46:49  
 * Copyright (c) 2016,  All Rights Reserved.  
 *  
*/  
  
package com.lm.httpclient20160915;  
/**  
 * ClassName:InfoLoad <br/>   
 * Date:     2016��9��15�� ����11:46:49 <br/>  
 * @author   hzlimao  
 * @version    
 * @since    JDK 1.8 
 * @see        
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.ByteArrayBuffer;  
  
 
  
  
/** 
 * ������ ���ػ����࣬����ء����ص�ģ��ʹ��  
 */  
public class InfoLoad {  
	public static final int HTTPCLIENT_CONNECTION_COUNT=200;//���������
	public static final int HTTPCLIENT_MAXPERROUTE_COUNT=20;
	public static final int HTTPCLIENT_CONNECT_TIMEOUT = 20000;//���ӳ�ʱʱ��
	public static final int HTTPCLIENT_SOCKET_TIMEOUT = 60000;//���ݴ��䳬ʱ
	
    // ����httpclient���ӳ�  
    private PoolingHttpClientConnectionManager httpClientConnectionManager = null;  
      
    /******����ģʽ������ʼ******/  
    //���ʼ��ʱ���Զ�ʵ��������������ģʽ  
    private static final InfoLoad infoLoad = new InfoLoad();  
    /** 
     *  
     * ��������getInfoLoadInstance 
     * �����������ľ�̬����������InfoLoad��ʵ�� 
     * @return 
     */  
    public static InfoLoad getInfoLoadInstance(){  
        return infoLoad;  
    }  
    /******����ģʽ��������******/  
    /** 
     * ˽�еĹ��캯�� 
     */  
    private InfoLoad(){  
        //��ʼ��httpClient  
        initHttpClient();  
    }  
    /** 
     *  
     * ��������initHttpClient 
     * ���ߣ�zhouyh 
     * ����ʱ�䣺2015-10-14 ����11:00:30 
     * ����������httpclient���ӳأ�����ʼ��httpclient 
     */  
    public void initHttpClient(){  
        //����httpclient���ӳ�  
        httpClientConnectionManager = new PoolingHttpClientConnectionManager();  
        //�������ӳ��������  
        httpClientConnectionManager.setMaxTotal(HTTPCLIENT_CONNECTION_COUNT);  
        //���õ���·�������������  
        httpClientConnectionManager.setDefaultMaxPerRoute(HTTPCLIENT_MAXPERROUTE_COUNT);  
    }  
    //�������Ի���  
    HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {  
        public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {  
            if (executionCount >= 3) {  
                // ��������������������  
                return false;  
            }  
            if (exception instanceof InterruptedIOException) {  
                // Timeout  
                return false;  
            }  
            if (exception instanceof UnknownHostException) {  
                // Unknown host  
                return false;  
            }  
            if (exception instanceof ConnectTimeoutException) {  
                // Connection refused  
                return false;             
            }  
            if (exception instanceof SSLException) {  
                // SSL handshake exception  
                return false;  
            }  
            HttpClientContext clientContext = HttpClientContext.adapt(context);  
            HttpRequest request = clientContext.getRequest();  
            boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);  
            if (idempotent) {  
                // Retry if the request is considered idempotent  
                return true;  
            }  
            return false;  
        }  
    };  
    /** 
     *  
     * ��������getHttpClient 
     * ���������̵߳���ʱ����Ҫ�����Լ���httpclient 
     * @return 
     */  
    public CloseableHttpClient getHttpClient(){       
        // ����ȫ�ֵ�requestConfig  
        RequestConfig requestConfig = RequestConfig.custom()  
                .setConnectTimeout(HTTPCLIENT_CONNECT_TIMEOUT)  
                .setSocketTimeout(HTTPCLIENT_SOCKET_TIMEOUT)  
                .setCookieSpec(CookieSpecs.BEST_MATCH).build();  
        // �����ض�����Զ���  
        LaxRedirectStrategy redirectStrategy = new LaxRedirectStrategy();  
          
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(httpClientConnectionManager)  
                                                    .setDefaultRequestConfig(requestConfig)  
                                                    .setRedirectStrategy(redirectStrategy)  
                                                    .setRetryHandler(myRetryHandler)  
                                                    .build();   
        return httpClient;   
    }  
      
    /** 
     *  
     * ��������loadForString 
     * ���������ݴ����url��ȡ������Ϣ 
     * @param url 
     * @param type 
     * @return 
     */  
    public static String loadForString(String urlString, int type){  
        String src = "";  
        if(null==urlString || urlString.isEmpty() || !urlString.startsWith("http")){//���urlStringΪnull����urlStringΪ�գ���urlString��http��ͷ������src��ֵ  
            return src;  
        }  
        //����response  
        CloseableHttpResponse response = null;  
        HttpGet httpGet = null;  
        CloseableHttpClient httpClient=null;
        urlString = urlString.trim();//��ֹ�����urlString��β�пո�  
        //ת��String urlΪURI,���url�а��������ַ������  
        try {  //URI��ʾ�����������·����������ôһ����Դ����URLͬʱ˵��Ҫ��η��������Դ��http://��ÿ�� URL ���� URI������һ��ÿ�� URI ���� URL��������Ϊ URI ������һ�����࣬��ͳһ��Դ���� (URN)
        	
//        	URI ���ܻ�ȥ��һ��port��һЩ��������ݣ����API�ӿ�ȷ�������ʹ��String��uri
//            URL url = new URL(urlString);  //http://10.165.124.24:8210/sd/service/query?index=beauty_product&stype=1&abtest=0_unknown&q=11  ��ͳһ��Դ��λ��
//            URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(),null);  //http://10.165.124.24/sd/service/query?index=beauty_product&stype=1&abtest=0_unknown&q=11   ͳһ��Դ��ʶ��
//            httpGet = new HttpGet(uri);  
            httpGet = new HttpGet(urlString);

            
            //��������ͷ  
            httpGet.addHeader("Accept","*/*");  
            httpGet.addHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");  
            httpGet.addHeader("Connection","keep-alive");  
            httpGet.addHeader("Accept-Encoding", "gzip, deflate");  
//              
            //����USER_AGENT  
            Random random = new Random();  
            int randomInt = random.nextInt(4);  
            System.err.println(randomInt);  
              
//          httpGet.addHeader("User-Agent", Constant.USER_AGENT[randomInt]);  
            //�˴��Ĵ�����ʱע��  
//          String[] proxys = Constant.HTTPCLIENT_PROXY[randomInt].split("\\s+");  
//          //��Ӵ���  
//          HttpHost proxy = new HttpHost(proxys[0].trim(), Integer.parseInt(proxys[1].trim()), "http");  
//          RequestConfig config = RequestConfig.custom().setProxy(proxy).build();  
//          httpGet.setConfig(config);    
            
            
            //ִ������        
            try {  
                if(urlString.startsWith("https")){//���https  
                    System.setProperty ("jsse.enableSNIExtension", "false"); 
                    httpClient=createSSLClientDefault();
                    response =httpClient.execute(httpGet);  
                }else{  
                	httpClient=infoLoad.getHttpClient();
                    response = httpClient.execute(httpGet);  
                }  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
              
            //�õ���Ӧ״̬��  
            int statuCode = response.getStatusLine().getStatusCode();  
            //����״̬������߼�����  
            switch (statuCode){  
            case 200:  
                src = getResultData(response, urlString); 
                break;  
            case 400:  
                System.out.println("����400������룬��������﷨����" + urlString);  
                //TODO Ҫ�����ж����б�ҳ��������ҳ���أ���ȥ�޸����ݿ⣬��ͬ  
                //TODO �˴���Ӷ�mongodb���ݿ�Ĳ���������url��isStart��Ϊ0����ʱ���ڽ��м�أ���������ģ��״̬Ϊ0�Ľ����޸�  
                break;  
            case 403:  
                System.out.println("����403������룬��Դ������" + urlString);                
                //TODO �˴���Ӷ�mongodb���ݿ�Ĳ���������url��isStart��Ϊ0����ʱ���ڽ��м�أ���������ģ��״̬Ϊ0�Ľ����޸�  
                break;  
            case 404:  
                System.out.println("����404������룬�޷��ҵ�ָ����Դ��ַ" + urlString);  
                //TODO �˴���Ӷ�mongodb���ݿ�Ĳ���������url��isStart��Ϊ0����ʱ���ڽ��м�أ���������ģ��״̬Ϊ0�Ľ����޸�  
                break;  
            case 503:  
                System.out.println("����503������룬���񲻿���" + urlString);  
                //TODO �˴���Ӷ�mongodb���ݿ�Ĳ���������url��isStart��Ϊ0����ʱ���ڽ��м�أ���������ģ��״̬Ϊ0�Ľ����޸�  
                break;  
            case 504:  
                System.out.println("����504������룬���س�ʱ" + urlString);  
                //TODO �˴���Ӷ�mongodb���ݿ�Ĳ���������url��isStart��Ϊ0����ʱ���ڽ��м�أ���������ģ��״̬Ϊ0�Ľ����޸�  
                break;  
            }  
                  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally{  
            if(response != null){  
                try {//response �ر�  
                    response.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }
            if(httpClient!=null){//�����õ����ӳأ����Բ����Լ��رգ��رջ�������
					System.out.println("httpClient �رմ�ӡ����ʵ�ʲ�û�ֶ��ر�");
            }
            if(httpGet!=null){
            	httpGet.abort();    //������ر�httpGet����  
            } 
        }  
          
        return src;  
    }  
    /** 
     *  
     * ��������createSSLClientDefault 
     * ���������https����SSL�ķ�ʽ����httpclient 
     * @return 
     */  
    public static CloseableHttpClient createSSLClientDefault(){       
        try {             
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy(){  
            //��������  
            public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {  
                return true;  
            }}).build();  
  
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);  
  
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();  
  
        } catch (KeyManagementException e) {  
            e.printStackTrace();  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (KeyStoreException e) {  
            e.printStackTrace();  
        }  
              
        return  HttpClients.createDefault();  
    }  
    /** 
     *  
     * ��������getCharsetFromMetaTag 
     * ��������meta��ǩ�л�ȡ�����ʽ 
     * @param buffer 
     * @param url 
     * @return 
     */  
    public static String getCharsetFromMetaTag(ByteArrayBuffer buffer,String url){  
        String charset = null;  
        String regEx = "";  
        Pattern p = Pattern.compile(regEx,  
                Pattern.CASE_INSENSITIVE);  
        Matcher m = p.matcher(new String(buffer.toByteArray()));  
        boolean result = m.find();  
        if (result) {  
            if (m.groupCount() == 1) {  
                charset = m.group(1);  
            }   
            System.err.println("��ҳ �еı���:" + charset + "\t url:" + url);  
        } else {  
            //����δƥ��ı��룬�ȸ�ֵΪgbk  
            charset = "gbk";  
            System.out.println("�ַ�����δƥ�䵽 : " + url);  
        }  
        return charset;  
    }  
    /** 
     *  
     * ��������replaceStr 
     * �������滻ԭ��ҳ�е������ַ� 
     * @param src 
     * @return 
     */  
    public static String replaceStr(String src){  
        if (src == null || "".equals(src)) return null;  
        src = src.replaceAll("<!--", "");  
        src = src.replaceAll("-->", "");  
        src = src.replaceAll("<", "<");  
        src = src.replaceAll(">", ">");  
        src = src.replaceAll("\"", "\"");  
        src = src.replaceAll(" ", " ");  
        src = src.replaceAll("&", "&");  
        return src;  
    }  
      
    /*
     * ����API�еķ�������
     */
    
    public static String getResultData(CloseableHttpResponse response,String urlString){
        try {
			//�����Ӧʵ��  
			HttpEntity entity = response.getEntity();  
			/**  
			 * ���������ȡ��ҳ����  
			 * ��������ȴ�content-type��charset����Ӧͷ��Ϣ���л�ȡ���룬  
			 * �����ȡ���ˣ�����meta��HTML��Ĵ��룩�л�ȡcharset�ı���ֵ  
			 */  
			//��һ��-->������ҳ�ַ�����  
			String charset = null;  
			ContentType contentType = null;  
			contentType = ContentType.getOrDefault(entity);  
			Charset charsets = contentType.getCharset();  
			if(null != charsets){  
			    charset = charsets.toString();  
			}  
			//�жϷ��ص��������Ƿ������gzipѹ��  
			Header header = entity.getContentEncoding();  
			boolean isGzip = false;  
			if(null != header){  
			    for(HeaderElement headerElement : header.getElements()){  
			        if(headerElement.getName().equalsIgnoreCase("gzip")){  
			            isGzip = true;  
			        }  
			    }  
			}  
			//�����Ӧ��  
			InputStream inputStream = entity.getContent();  
			ByteArrayBuffer buffer = new ByteArrayBuffer(4096000);  
			byte[] tmp = new byte[4096];  
			int count;  
			if(isGzip){//���������Gzipѹ���������gizpѹ������  
			    GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);  
			    while((count=gzipInputStream.read(tmp)) != -1){  
			        buffer.append(tmp, 0, count);  
			    }  
			}else{//�����gzip��ʽ������  
			    while((count=inputStream.read(tmp)) != -1){  
			        buffer.append(tmp, 0, count);  
			    }  
			}  
			//�ڶ���--->�����һ��contenttypδ��ȡ�����룬�����meta��ǩ�л�ȡ  
			if(null==charset || "".equals(charset) || "null".equals(charset)   
			        || "zh-cn".equalsIgnoreCase(charset)){  
			    charset = getCharsetFromMetaTag(buffer, urlString);  
			}  
			//���ݻ�ȡ���ַ�����תΪstring����  
			String src = new String(buffer.toByteArray(),StringUtils.isBlank(charset)?"UTF-8":charset);  
			//�滻�������  
			src = replaceStr(src);  
			//ת��Unicode�����ʽ]  
    //      src = Common.decodeUnicode(src);  
			System.out.println(src);
			return src;
		} catch (Exception e) {
			e.printStackTrace();  
		}
        return "";
    }
    /** 
     * ��������main 
     * ������main���� 
     * @param args 
     */  
    @SuppressWarnings("static-access")
	public static void main(String[] args) { 
    	String[] urisToGet = {
    			"http://10.165.124.24:8210/sd/service/query?index=beauty_product&stype=1&abtest=0_unknown&q=11",
    			"http://10.165.124.24:8210/sd/service/query?index=beauty_product&stype=1&abtest=0_unknown&q=22",
    			"http://10.165.124.24:8210/sd/service/query?index=beauty_product&stype=1&abtest=0_unknown&q=333",
    			"http://10.165.124.24:8210/sd/service/query?index=beauty_product&stype=1&abtest=0_unknown&q=444"
    			};
    	
    	for (int i=0;i<10;i++) {
    		Random random=new Random();
    		String string=urisToGet[random.nextInt(3)];
			Thread thread=new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println(string);
					InfoLoad.getInfoLoadInstance().loadForString(string, 0); 
				}
			});
			thread.start();
		}

        InfoLoad.getInfoLoadInstance().loadForString("https://www.baidu.com", 0);  
    }  
  
    
    
}  
  
