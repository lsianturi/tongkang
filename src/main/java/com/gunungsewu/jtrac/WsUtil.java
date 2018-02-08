package com.gunungsewu.jtrac;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WsUtil {
	private String cookies;
	private CloseableHttpClient client = null;
	private final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
//	private static Logger log = Logger.getLogger(WsUtil.class);
	
	public WsUtil() {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(100);
        client = HttpClients.custom()
//        		.setRedirectStrategy(new LaxRedirectStrategy())
                .setConnectionManager(cm)
                .build();
	}

	public static void main(String[] args) throws Exception {

		String url = "http://rejuveapp.gunungsewu.com/tracking/app/login";
		String loginUrl = "http://rejuveapp.gunungsewu.com/tracking/app/login/wicket:interface/%3A1%3Aform%3A%3AIFormSubmitListener%3A%3A/";
		String loginUrl2 = "http://rejuveapp.gunungsewu.com/tracking/app/";
		
		// Create a local instance of cookie store
        CookieStore cookieStore = new BasicCookieStore();

        // Create local HTTP context
        HttpClientContext localContext = HttpClientContext.create();
        // Bind custom cookie store to the local context
        localContext.setCookieStore(cookieStore);

		// make sure cookies is turn on
		CookieHandler.setDefault(new CookieManager());
		WsUtil http = new WsUtil();

		String page = http.getPageContent(url, null, localContext);
		
		String cookie = http.getCookies();
		cookie = cookie.replace("Set-Cookie: ", "");
		cookie = cookie.replace("; Path=/tracking", "");
		http.setCookies(cookie);
		
		List<NameValuePair> postParams = http.getLoginFormParams(page, "samuel.wijaya", "password");

		// open the login page
		http.sendPost(loginUrl, postParams, localContext);
		//do login action
		page = http.getPageContent(loginUrl2, url, localContext);
		
		//get the new ticket link
		String newlink = http.getNewTicketLink(page);
		
		//open the new ticket page
		page = http.getPageContent(loginUrl2 +newlink, loginUrl2, localContext);
		
		//get the action url for new ticket
		String actionUrl = http.getNewIssueFormParams(page);
		
		//post the new ticket 
		File attachment = new File("C:\\Users\\lambok.sianturi\\rejuve\\71file_43.jpg");
		Map<String, Object> map = http.sendNewPost(loginUrl2 +actionUrl, localContext, "Test dari Bot3", "Bot test detail3", attachment);
		String referrer = (String)map.get("location");
		
		Map<String, String> map2 = http.getAttachmentAction((String)map.get("html"));
		actionUrl = (String) map2.get("action");
		String tag = (String) map2.get("form");
		
		//post additional attachment
		page = http.addAttachment(loginUrl2 +actionUrl, localContext, referrer, tag, "Additional attachment", attachment);
		
	}
	
	public int sendPost(String url, List<NameValuePair> postParams, HttpClientContext context) throws Exception {

		HttpPost post = new HttpPost(url);
		String[] strs = url.split("/");

		// add header
		post.setHeader("Host", strs[2]);
		post.setHeader("NLUser-Agent", USER_AGENT);
		post.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		post.setHeader("Accept-Language", "en-US,en;q=0.5");
		post.setHeader("Cookie", getCookies());
		post.setHeader("Connection", "keep-alive");
		post.setHeader("Referer", url);
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");

		post.setEntity(new UrlEncodedFormEntity(postParams));

		HttpResponse response = client.execute(post, context);

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		return response.getStatusLine().getStatusCode();

	}
	
	public Map<String, Object> sendNewPost(String requestUrl, HttpClientContext context, String summary, String detail, File attachment) throws Exception {
		String[] strs = requestUrl.split("/");
		StringBuffer result = new StringBuffer();
		Map<String, Object> map = new HashMap<String, Object>();
		
//		System.out.println("new ticket url:"+requestUrl);
		HttpPost request = new HttpPost(requestUrl);
		
		HttpEntity entity = null;
		if (attachment != null) {
			entity = MultipartEntityBuilder
			    .create()
			    .addTextBody("summary", summary)
			    .addTextBody("detail", detail)
			    .addTextBody("fields:fields:0:field:border:field", "3")
			    .addTextBody("fields:fields:1:field:border:field", "2")
			    .addTextBody("hideAssignedTo:border:assignedTo", "1633")
			    .addTextBody("Content-Type", "application/octet-stream")
			    .addTextBody("form24_hf_0", "")
			    .addTextBody("sendNotifications", "on")
			    .addBinaryBody("hideNotifyList:file", attachment, ContentType.create("application/octet-stream"), attachment.getName())
			    .build();
		} else {
			entity = MultipartEntityBuilder
				    .create()
				    .addTextBody("summary", summary)
				    .addTextBody("detail", detail)
				    .addTextBody("fields:fields:0:field:border:field", "3")
				    .addTextBody("fields:fields:1:field:border:field", "2")
				    .addTextBody("hideAssignedTo:border:assignedTo", "1633")
				    .addTextBody("Content-Type", "application/octet-stream")
				    .addTextBody("form24_hf_0", "")
				    .addTextBody("sendNotifications", "on")
				    .addTextBody("hideNotifyList:file", "filename=\"\"")
				    .build();
		}
//		System.out.println(entity.getContentType().getValue());
		request.setHeader("NLUser-Agent", USER_AGENT);
		request.setHeader("Origin", "http://"+strs[2]);
		request.setHeader("Connection", "keep-alive");
		request.setHeader("Upgrade-Insecure-Requests", "1");
		request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		request.setHeader("Accept-Encoding", "gzip, deflate, sdch");
		request.setHeader("Accept-Language", "en-US,en;q=0.8");
		request.setHeader("Connection","keep-alive");
		request.setHeader("Content-Type", entity.getContentType().getValue());
		request.setHeader("Cookie", getCookies());
		request.setHeader("Referer", "http://" + strs[2]+ "/tracking/app/?wicket:bookmarkablePage=%3Ainfo.jtrac.wicket.ItemFormPage");
		request.setEntity(entity);
		
		HttpResponse response = null;
		try {
			response = client.execute(request, context);
			if (response.getStatusLine().getStatusCode() == 302) {
				String redirectURL = response.getFirstHeader("Location").getValue();
				map.put("location", redirectURL);

				// no auto-redirecting at client side, need manual send the request.
				HttpGet request2 = new HttpGet(redirectURL);
				HttpResponse response2 = client.execute(request2, context);
		
//				System.out.println(response2.getStatusLine().getStatusCode());
				BufferedReader rd = new BufferedReader(new InputStreamReader(response2.getEntity().getContent()));
	
				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			
		}
		map.put("html", result.toString());
		
		return map;
	}

	public String addAttachment(String requestUrl, HttpClientContext context, String referrer, String tag, String comment, File attachment) throws Exception {
		String[] strs = requestUrl.split("/");
		StringBuffer result = new StringBuffer();
		
//		System.out.println("add attachment url:"+requestUrl);
		HttpPost request = new HttpPost(requestUrl);
		
		HttpEntity entity = MultipartEntityBuilder
			    .create()
			    .addTextBody("comment", comment)
			    .addTextBody("fields:fields:0:field:border:field", "3")
			    .addTextBody("fields:fields:1:field:border:field", "2")
			    .addTextBody("hideAssignedTo:border:assignedTo", "1633")
			    .addTextBody("Content-Type", "application/octet-stream")
			    .addTextBody(tag, "")
			    .addTextBody("sendNotifications", "on")
			    .addBinaryBody("file", attachment, ContentType.create("application/octet-stream"), attachment.getName())
			    .build();

//		System.out.println(entity.getContentType().getValue());
		request.setHeader("NLUser-Agent", USER_AGENT);
		request.setHeader("Origin", "http://" +strs[2]);
		request.setHeader("Connection", "keep-alive");
		request.setHeader("Upgrade-Insecure-Requests", "1");
		request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		request.setHeader("Accept-Encoding", "gzip, deflate, sdch");
		request.setHeader("Accept-Language", "en-US,en;q=0.8");
		request.setHeader("Connection","keep-alive");
		request.setHeader("Content-Type", entity.getContentType().getValue());
		request.setHeader("Cookie", getCookies());
		request.setHeader("Referer", referrer);
		request.setEntity(entity);
		
		HttpResponse response = null;
		try {
			response = client.execute(request, context);
	//		HttpResponse response = client.execute(post, context);
//			System.out.println(response.getStatusLine().getStatusCode());
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
	
			// set cookies
			if (getCookies() == null)
			setCookies(
					response.getFirstHeader("Set-Cookie") == null ? "" : response.getFirstHeader("Set-Cookie").toString());
		} catch (Exception e) {
			e.printStackTrace();
	//	} finally {
	//		response.close();
			
		}
		return result.toString();
	}

	public String getPageContent(String url, String referer, HttpClientContext context) throws Exception {
		
		String[] strs = url.split("/");

		HttpGet request = new HttpGet(url);

		request.setHeader("NLUser-Agent", USER_AGENT);
		request.setHeader("Host", strs[2]);
		request.setHeader("Connection", "keep-alive");
		request.setHeader("Upgrade-Insecure-Requests", "1");
		request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		request.setHeader("Accept-Encoding", "gzip, deflate, sdch");
		request.setHeader("Accept-Language", "en-US,en;q=0.8");
		request.setHeader("Connection","keep-alive");
		request.setHeader("Content-Type", "application/x-www-form-urlencoded");
		
//		UriBuilder builder = new uri
		request.setHeader("Cookie", getCookies());
		if(referer != null) request.setHeader("Referer", referer);
		StringBuffer result=  new StringBuffer("");
		CloseableHttpResponse response = null;
		try {
			response = client.execute(request, context);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			// set cookies
			if (getCookies() == null)
			setCookies(
					response.getFirstHeader("Set-Cookie") == null ? "" : response.getFirstHeader("Set-Cookie").toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			response.close();
		}

		return result.toString();
	}
	
	public String getNewTicketLink(String page) throws Exception {

		Document doc = Jsoup.parse(page);
		
		Elements links = doc.select("a[href]");
		String newLink = null;
        for (Element link : links) {

            // get the value from href attribute
           if (link.attr("href").contains("new::ILinkListener::")) {
        	   newLink = link.attr("href");
           }
        }
		return newLink;
	}

	public List<NameValuePair> getLoginFormParams(String html, String username, String password)
			throws UnsupportedEncodingException {

		Document doc = Jsoup.parse(html);

		// Google form id
		Element loginform = doc.select("form[id=form7]").first();
		Elements inputElements = loginform.getElementsByTag("input");

		List<NameValuePair> paramList = new ArrayList<NameValuePair>();

		for (Element inputElement : inputElements) {
			String key = inputElement.attr("name");
			String value = inputElement.attr("value");

			if (key.equals("loginName"))
				value = username;
			else if (key.equals("password"))
				value = password;
			else
				value = "";

			paramList.add(new BasicNameValuePair(key, value));
		}
		return paramList;
	}
	
	public String getNewIssueFormParams(String html)
			throws UnsupportedEncodingException {

		Document doc = Jsoup.parse(html);

		// Google form id
		Element loginform = doc.select("form[id=form24]").first();
		//Elements inputElements = loginform.getElementsByTag("input");
		String action = loginform.attr("action");

		return action;
	}
	
	public Map<String, String> getAttachmentAction(String html)
			throws UnsupportedEncodingException {

		Map<String, String> map = new HashMap<String, String>();
	
		Document doc = Jsoup.parse(html);

		// Google form id
		Element form = doc.select("form").first();
		String action = form.attr("action").replace("../../", "");
		map.put("action", action);
		
		Elements inputs = doc.select("input");
		for (Element inputElement : inputs) {
			String key = inputElement.attr("name");
			if (key.startsWith("form")) {
				map.put("form", key);
				break;
			}
		}

		return map;
	}

	public String getCookies() {
		return cookies;
	}

	public void setCookies(String cookies) {
		this.cookies = cookies;
	}
	

}
