package cn.fh.httpclient;

import cn.fh.httpclient.exception.ConnectionException;
import cn.fh.httpclient.exception.constant.Method;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * HTTP客户端. 支持GET和POST请求
 */
public class HttpClient {
    private String url;
    private String queryString;
    private Method method;
    private HttpURLConnection conn;
    private Map<String, List<String>> headers;
    private String resp;

    private String contentType;

    /**
     * 是否是HTTPS请求
     */
    private boolean isHttps = false;

    /**
     * 构造一个HttpClient对象并指定URL和请求方式.
     * 默认为非HTTPS请求
     * @param url
     * @param method
     */
    public HttpClient(String url, Method method) {
        this.url = url;
        this.method = method;
        this.isHttps = false;
    }

    public HttpClient(String url, Method method, boolean isHttps) {
        this.url = url;
        this.method = method;

        this.isHttps = isHttps;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setHttps(boolean https) {
        this.isHttps = https;
    }

    /**
     * 设置query string.
     * 如果是GET请求则会加到URL之后，如果是POST则放到请求body中。
     * @param queryString
     */
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public void setContentType(String type) {
        this.contentType = type;
    }
    /**
     * 发起HTTP请求并得到响应信息。
     * 调用完成后连接会马上断开。
     * @throws ConnectionException
     */
    public void connect() throws ConnectionException {
        try {
            String url = this.url;

            URL u = new URL(url);
            this.conn = buildConnection(u);

            conn.setRequestMethod(method.toString());

            // 如果是GET请求
            if (Method.GET == method) {
                // 在请求后面加上queryString
                if (null != queryString) {
                    url = url + "?" + queryString;
                }
            } else {
                conn.setDoOutput(true);
            }

            if (null != this.contentType) {
                conn.setRequestProperty("Content-Type", this.contentType);
            }

            conn.connect();

            // 如果是POST请求
            if (Method.POST == method) {
                // 向服务器写入POST数据
                if (null == queryString) {
                    throw new IllegalStateException("No query string!");
                }

                conn.getOutputStream().write(queryString.getBytes());
                conn.getOutputStream().flush();
            }

            // 读取响应数据
            InputStream in = conn.getInputStream();
            this.resp = stream2String(in, "UTF-8");
            this.headers = conn.getHeaderFields();

            this.conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new ConnectionException("invalid URL");
        } catch (IOException e) {
            e.printStackTrace();
            throw new ConnectionException("HTTP响应状态码非200");
        }
    }

    /**
     * 得到请求头
     * @return
     */
    public Map<String, List<String>> getHeaders() {
        return this.headers;
    }

    /**
     * 得到响应数据
     * @return
     */
    public String getResponse() {
        return this.resp;
    }

    /**
     * 重置HttpClient，使得对象可以重复使用
     */
    public void reset() {
        this.conn = null;
        this.resp = null;
        this.method = null;
        this.url = null;
        this.queryString = null;
        this.headers = null;
        this.contentType = null;
        this.isHttps = false;
    }

    /**
     * 将stream转换成String对象
     * @param in
     * @param charSet
     * @return
     * @throws IOException
     */
    private String stream2String(InputStream in, String charSet) throws IOException {
        InputStreamReader reader = new InputStreamReader(in, charSet);
        char[] buf = new char[200];

        int len = 0;
        StringBuilder sb = new StringBuilder();
        while ( (len = reader.read(buf)) != -1 ) {
            sb.append(buf, 0, len);
        }

        return sb.toString();
    }

    /**
     * 创建一个{@link HttpURLConnection}对象。
     * 如果URL中的Schema字段为HTTPS，则返回{@link HttpsURLConnection}对象
     *
     * @param u
     * @return
     * @throws ConnectionException
     * @throws IOException
     */
    protected HttpURLConnection buildConnection(URL u) throws ConnectionException, IOException {
        HttpURLConnection httpConn = null;

        URLConnection urlConn = u.openConnection();

        // 判断是不是一个HTTP连接
        if (false == urlConn instanceof HttpURLConnection) {
            throw new ConnectionException("Invalid HTTP URL");
        }
        httpConn = (HttpURLConnection) urlConn;

        // 是否设置了HTTPS标记位
        if (isHttps) {
            // 判断打开的连接是否是HTTPS连接
            if (false == urlConn instanceof HttpsURLConnection) {
                throw new ConnectionException("URL不符合HTTPS格式");
            }

        }

        return httpConn;
    }

    public static void main(String[] args) throws Exception {
        // GET
        HttpClient client = new HttpClient("http://www.baidu.com", Method.GET);
        client.connect();

        String resp = client.getResponse();
        Map<String, List<String >> headerList = client.getHeaders();

        //System.out.println(resp);

        Set<Map.Entry<String, List<String>>> entrySet = headerList.entrySet();
        entrySet.forEach(entry -> {
            //System.out.println("header:" + entry.getKey());
            //entry.getValue().forEach(val -> System.out.println("val = " + val));
        });



        client.reset();
        // POST
        client.setUrl("http://120.24.218.56/api/user/name");
        client.setQueryString("username=hanxinxin");
        client.setMethod(Method.POST);
        client.connect();
        resp = client.getResponse();
        //System.out.println(resp);



        // HTTPS Test
        client.reset();
        // POST
        //client.setUrl("https://kyfw.12306.cn/otn/leftTicket/init");
        client.setUrl("https://m.jdpay.com/wepay/query");
        client.setQueryString("username=hanxinxin");
        client.setMethod(Method.POST);
        client.connect();
        resp = client.getResponse();
        System.out.println(resp);

    }
}
