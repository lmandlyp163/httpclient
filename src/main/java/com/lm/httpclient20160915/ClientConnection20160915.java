/**  
 * Project Name:httpclient_study20160915  
 * File Name:ClientConnection20160915.java  
 * Package Name:com.lm.httpclient20160915  
 * Date:2016年9月15日上午10:56:00  
 * Copyright (c) 2016,  All Rights Reserved.  
 *  
*/  
  
package com.lm.httpclient20160915;

import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;

/**  
 * ClassName:ClientConnection20160915 <br/>  
 * Date:     2016年9月15日 上午10:56:00 <br/>  
 * @author   hzlimao  
 * @version    
 * @since    JDK 1.8 
 * @see        
 */
/*
 * 4.2.1可以用。4.3.1已放弃
 */
public class ClientConnection20160915 {
	static final int TIMEOUT = 20000;//连接超时时间
	static final int SO_TIMEOUT = 60000;//数据传输超时
	
	@SuppressWarnings("deprecation")
	public static DefaultHttpClient getHttpClient(){
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(
				new Scheme("http",80,PlainSocketFactory.getSocketFactory()));
		schemeRegistry.register(
				new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
		
		PoolingClientConnectionManager  cm = new PoolingClientConnectionManager(schemeRegistry);
		cm.setMaxTotal(200);
		cm.setDefaultMaxPerRoute(20);
		
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,TIMEOUT);
		params.setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
		
		DefaultHttpClient client = new DefaultHttpClient(cm,params);
		return client;
	}
}
  
