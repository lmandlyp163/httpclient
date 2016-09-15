package cn.fh.httpclient.test;

import cn.fh.httpclient.HttpClient;
import cn.fh.httpclient.exception.constant.Method;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by whf on 8/9/15.
 */
public class ClientTest {
    @Test
    public void testGET() throws Exception {
        // GET
        HttpClient client = new HttpClient("http://www.baidu.com", Method.GET);
        client.connect();

        String resp = client.getResponse();
        Map<String, List<String >> headerList = client.getHeaders();

        System.out.println(resp);

        Set<Map.Entry<String, List<String>>> entrySet = headerList.entrySet();
        entrySet.forEach(entry -> {
            System.out.println("header:" + entry.getKey());
            entry.getValue().forEach(val -> System.out.println("val = " + val));
        });




    }

    @Test
    public void testPost() throws Exception {
        HttpClient client = new HttpClient("http://www.baidu.com", Method.GET);

        // POST
        client.setUrl("http://120.24.218.56/api/user/name");
        client.setQueryString("username=hanxinxin");
        client.setMethod(Method.POST);
        client.connect();
        String resp = client.getResponse();
        System.out.println(resp);

    }

    public void testHTTPS() throws Exception {
        HttpClient client = new HttpClient("http://www.baidu.com", Method.GET);
        // POST
        //client.setUrl("https://kyfw.12306.cn/otn/leftTicket/init");
        client.setUrl("https://m.jdpay.com/wepay/query");
        client.setQueryString("username=hanxinxin");
        client.setMethod(Method.POST);
        client.connect();
        String resp = client.getResponse();
        System.out.println(resp);

    }
}
