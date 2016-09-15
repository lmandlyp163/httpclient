## HttpClient
### Quick Start
- HTTP请求
```

	// 创建客户端对象
	HttpClient client = new HttpClient("http://www.baidu.com", Method.GET);
	// 设置请求头的Content-Type字段
	client.setContentType("XXX");
	// 发送请求
    client.connect();
    // 得到响应
    String resp = client.getResponse();
    // 得到头信息
    Map<String, List<String >> headerList = client.getHeaders();
    
    // 重置之后可以重复使用HttpClient对象
    client.reset();
    client.setUrl("XXX);
    client.setMethod(Method.POST);
    client.setQueryString("username=hello&age=18");
    client.connect();
    ... ...
```
- HTTPS请求
```
	HttpClient client = new HttpClient("http://www.baidu.com", Method.GET, true);
	client.connect();
	... ...
```


### Build
- 下载项目
```
git clone git@github.com:wanghongfei/httpclient.git
```
- 构建
```
mvn clean install
```
- 添加依赖
```
<dependency>
    <groupId>cn.fh</groupId>
    <artifactId>httpclient</artifactId>
    <version>1.0-RELEASE</version>
</dependency>
```
