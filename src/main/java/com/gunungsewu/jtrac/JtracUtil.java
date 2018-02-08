package com.gunungsewu.jtrac;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gunungsewu.database.entity.Issue;
import com.gunungsewu.database.entity.Jtrac;
import com.gunungsewu.database.entity.Topic;
import com.gunungsewu.database.service.NLBotService;

public class JtracUtil {
	private CloseableHttpClient client = null;
	private final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
	private static Logger log = LoggerFactory.getLogger(JtracUtil.class);
	private Jtrac jtrac=null;
	private HttpClientContext context=null;

	private static volatile JtracUtil instance;
	public static JtracUtil getInstance() {
		final JtracUtil currentInstance;
		if (instance == null) {
			synchronized (JtracUtil.class) {
				if (instance == null) {
					instance = new JtracUtil();
				}
				currentInstance = instance;
			}
		} else {
			currentInstance = instance;
		}
		return currentInstance;
	}
	
	public List<Cookie> getCookies(){
		CookieStore store = (CookieStore) context.getAttribute(HttpClientContext.COOKIE_STORE);
		return store.getCookies();
	}
	
	private String getCookie() {
		String cookie="";
		List<Cookie> cookies = getCookies();
		for (Cookie c:cookies) {
			cookie = cookie.length()>0 ? ";"+c.getName() + "=" +c.getValue() : c.getName() + "=" +c.getValue();
		}
		
		return cookie;
	}

	public JtracUtil() {
//        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.DEFAULT).build();
		BasicCookieStore cookieStore = new BasicCookieStore();
        context = new HttpClientContext ();
        context.setCookieStore(cookieStore);
        client = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).disableRedirectHandling().build();
	}

//	public Integer postToJtrac(String summary, String detail, Integer topic, Integer assignTo, String[] cc, String attachment, String attachment2, String attachment3 ) throws Exception {
	public Integer postToJtrac(Issue issue, Topic topic) throws Exception {
		Integer ticketNo = 0;
		
		jtrac = NLBotService.getInstance().getJtracUser();
		
		String url = jtrac.getLoginUrl(); // "http://aplikasi.terbanggi.ggp/tracking/app/login";
		String loginUrl = jtrac.getAuthenticateUrl(); //"http://aplikasi.terbanggi.ggp/tracking/app/login/wicket:interface/%3A1%3Aform%3A%3AIFormSubmitListener%3A%3A/";
		String loginUrl2 = jtrac.getAppUrl(); //"http://aplikasi.terbanggi.ggp/tracking/app/";
		
		String page = getLoginPage(url, context);
		
		List<NameValuePair> postParams = getLoginFormParams(page, jtrac.getUsername(), jtrac.getPassword());

		// open the login page
		page = login(jtrac.getHost(), loginUrl, url, postParams, context);
		
		//get the new ticket link
		String newlink = getNewTicketLink(page);
		//open the new ticket page
		page = getNewTicketPage(jtrac.getHost(), loginUrl2+newlink, loginUrl2, context);
		
		//get the action url for new ticket
		String actionUrl = getNewIssueFormParams(page);
		
		File folder = new File(System.getProperty("user.home") + File.separator+ "nlbot");
		if (!folder.isDirectory())
			folder.mkdir();
		
		//post the new ticket 
		Map<String, Object> map = null;
		String referrer="";
		File file = null;
		if (issue.getAttachment1() != null && !issue.getAttachment1().trim().equals("")) {
			file = new File(folder.getPath() + File.separator + getFileName(issue.getAttachment1()));
			FileUtils.copyURLToFile(new URL(issue.getAttachment1()), file);
		}
//			file = new File(attachment);
		map = sendNewPost(loginUrl2 +actionUrl, context, issue.getSummary(), issue.getDetail(), topic.getTopicJtracId(), topic.getPicUser(), topic.getCcUser().split(","),  file);
		
		referrer = (String) map.get("location");
//		System.out.println("new tiket link:" + referrer);
		String[] tmp = referrer.split("-");
		ticketNo = Integer.parseInt(tmp[1].replaceAll("/", ""));
		
		String tag="";
		Map<String, String> map2;
		if (issue.getAttachment2() != null && !issue.getAttachment2().trim().equals("")) {
			File file2 = null;
			file2 = new File(folder.getPath() + File.separator + getFileName(issue.getAttachment2()));
			
			FileUtils.copyURLToFile(new URL(issue.getAttachment2()), file2);
			map2 = getAttachmentAction((String)map.get("html"));
			actionUrl = (String) map2.get("action");
			tag = (String) map2.get("form");
			
			//post additional attachment
			page = addAttachment(loginUrl2 +actionUrl, context, referrer, tag, issue.getAtt2Detail(), file2, topic.getPicUser(), topic.getCcUser().split(","));
		}
		
		if (issue.getAttachment3() != null && !issue.getAttachment3().trim().equals("")) {
			File file3 = null;
			file3 = new File(folder.getPath() + File.separator + getFileName(issue.getAttachment3()));
			FileUtils.copyURLToFile(new URL(issue.getAttachment3()), file3);
			map2 = getAttachmentAction(page);
			actionUrl = (String) map2.get("action");
			tag = (String) map2.get("form");
			
			//post additional attachment
			page = addAttachment(loginUrl2 +actionUrl, context, referrer, tag, issue.getAtt3Detail(), file3, topic.getPicUser(), topic.getCcUser().split(","));
		}
		
		/*try {
			client.close();
		} catch (Exception e){}*/
		return ticketNo;
	}
	
	public String getLoginPage(String url, HttpClientContext context) throws Exception {
		HttpGet httpGet = new HttpGet(url);
		StringBuffer result=  new StringBuffer("");
		CloseableHttpResponse response = null;
		try {
			response = client.execute(httpGet, context);
			
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			EntityUtils.consume(response.getEntity());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			response.close();
		}

		return result.toString();
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
	
	public String login(String host, String url, String referer, List<NameValuePair> postParams, HttpClientContext context) throws Exception {

		HttpPost post = new HttpPost(url);
		StringBuffer result = new StringBuffer();
		
		// add header
		post.setHeader("Referer", referer);
		post.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		post.setHeader("Accept-Encoding","gzip, deflate");
		post.setHeader("Accept-Language","en-US,en;q=0.8");
		post.setHeader("Cache-Control","max-age=0");
		post.setHeader("Connection","keep-alive");
		post.setHeader("Content-Type","application/x-www-form-urlencoded");
		post.setHeader("Host",host);
		post.setHeader("Origin","http://" + host);
		post.setHeader("Upgrade-Insecure-Requests","1");
		post.setHeader("User-Agent",USER_AGENT);
		post.setHeader("Cookie", getCookie());
		post.setEntity(new UrlEncodedFormEntity(postParams));

		CloseableHttpResponse response = null;
		try {
			response = client.execute(post, context);
			log.debug("Context: "+ context);
			if (response.getStatusLine().getStatusCode() == 302) {
				String redirectURL = jtrac.getAppUrl() + response.getFirstHeader("Location").getValue().replaceAll("../", "");

				HttpGet httpGet = new HttpGet(redirectURL);
				httpGet.setHeader("Referer", referer);
				httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
				httpGet.setHeader("Accept-Encoding","gzip, deflate");
				httpGet.setHeader("Accept-Language","en-US,en;q=0.8");
				httpGet.setHeader("Cache-Control","max-age=0");
				httpGet.setHeader("Connection","keep-alive");
				httpGet.setHeader("Host",host);
				httpGet.setHeader("Origin","http://"+host);
				httpGet.setHeader("Upgrade-Insecure-Requests","1");
				httpGet.setHeader("Cookie", getCookie());
				httpGet.setHeader("User-Agent",USER_AGENT);
				
				HttpResponse response2 = client.execute(httpGet, context);
		
				BufferedReader rd = new BufferedReader(new InputStreamReader(response2.getEntity().getContent()));
	
				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			EntityUtils.consume(response.getEntity());
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
        	   break;
           }
        }
		return newLink;
	}
	
	public String getNewTicketPage(String host, String url, String referer, HttpClientContext context) throws Exception {
		HttpGet httpGet = new HttpGet(url);
		
		httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		httpGet.setHeader("Accept-Encoding","gzip, deflate");
		httpGet.setHeader("Accept-Language","en-US,en;q=0.8");
		httpGet.setHeader("Connection","keep-alive");
		httpGet.setHeader("Cookie", getCookie());
		httpGet.setHeader("Host",host);
		httpGet.setHeader("Referer", referer);
		httpGet.setHeader("Upgrade-Insecure-Requests","1");
		httpGet.setHeader("User-Agent",USER_AGENT);
		
		StringBuffer result=  new StringBuffer("");
		CloseableHttpResponse response = null;
		try {
			response = client.execute(httpGet, context);
			if (response.getStatusLine().getStatusCode() == 302) {
				String redirectURL = response.getFirstHeader("Location").getValue();

				httpGet.setURI(new URI(referer+redirectURL));
				httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
				httpGet.setHeader("Accept-Encoding","gzip, deflate");
				httpGet.setHeader("Accept-Language","en-US,en;q=0.8");
				httpGet.setHeader("Connection","keep-alive");
				httpGet.setHeader("Host",host);
				httpGet.setHeader("Referer", referer);
				httpGet.setHeader("Upgrade-Insecure-Requests","1");
				httpGet.setHeader("Cookie", getCookie());
				httpGet.setHeader("User-Agent",USER_AGENT);
				
				HttpResponse response2 = client.execute(httpGet, context);
		
				BufferedReader rd = new BufferedReader(new InputStreamReader(response2.getEntity().getContent()));
	
				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
			} else {
			
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	
				result = new StringBuffer();
				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			response.close();
		}

		return result.toString();
	}
	
	public Map<String, Object> sendNewPost(String requestUrl, HttpClientContext context, String summary, String detail, Integer topic, Integer assignTo, String[] cc, File attachment) throws Exception {
		String[] strs = requestUrl.split("/");
		StringBuffer result = new StringBuffer();
		Map<String, Object> map = new HashMap<String, Object>();
		
		HttpPost httpPost = new HttpPost(requestUrl);
		
		HttpEntity entity = null;
		MultipartEntityBuilder m = null;
		if (attachment != null) {
			m = MultipartEntityBuilder
			    .create()
			    .addTextBody("summary", summary)
			    .addTextBody("detail", detail)
			    .addTextBody("fields:fields:0:field:border:field", ""+topic)  // Topic : defect list, marketing, etc
//			    .addTextBody("fields:fields:1:field:field", store)  //store location
			    .addTextBody("hideAssignedTo:border:assignedTo", ""+assignTo)  //assign to
			    .addTextBody("Content-Type", "application/octet-stream")
			    .addTextBody("form24_hf_0", "")
			    .addTextBody("sendNotifications", "on")
			    .addBinaryBody("hideNotifyList:file", attachment, ContentType.create("application/octet-stream"), attachment.getName());
			
			if (cc.length>0) {
				for (int i=0; i<cc.length; i++){
					m.addTextBody("hideNotifyList:itemUsers", ""+cc[i]);
				}
			}
			entity = m.build();
		} else {
			m = MultipartEntityBuilder
				    .create()
				    .addTextBody("summary", summary)
				    .addTextBody("detail", detail)
				    .addTextBody("fields:fields:0:field:border:field", ""+topic)  // Topic : defect list, marketing, etc
//				    .addTextBody("fields:fields:1:field:field", store)  //store location
				    .addTextBody("hideAssignedTo:border:assignedTo", ""+assignTo)  //assign to
				    .addTextBody("Content-Type", "application/octet-stream")
				    .addTextBody("form24_hf_0", "")
				    .addTextBody("sendNotifications", "on")
				    .addTextBody("hideNotifyList:file", "filename=\"\"");
			
			if (cc.length>0) {
				for (int i=0; i<cc.length; i++){
					m.addTextBody("hideNotifyList:itemUsers", ""+cc[i]);
				}
			}
			entity = m.build();
		}
		httpPost.setHeader("User-Agent", USER_AGENT);
		httpPost.setHeader("Origin", "http://" + strs[2]);
		httpPost.setHeader("Connection", "keep-alive");
		httpPost.setHeader("Upgrade-Insecure-Requests", "1");
		httpPost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		httpPost.setHeader("Accept-Encoding", "gzip, deflate, sdch");
		httpPost.setHeader("Accept-Language", "en-US,en;q=0.8");
		httpPost.setHeader("Connection","keep-alive");
		httpPost.setHeader("Content-Type", entity.getContentType().getValue());
		httpPost.setHeader("Referer", "http://"+strs[2]+"/tracking/app/?wicket:bookmarkablePage=%3Ainfo.jtrac.wicket.ItemFormPage");
		httpPost.setEntity(entity);
		
		HttpResponse response = null;
		try {
			response = client.execute(httpPost, context);
			EntityUtils.consume(response.getEntity());
			if (response.getStatusLine().getStatusCode() == 302) {
				String redirectURL = jtrac.getAppUrl() + response.getFirstHeader("Location").getValue();
				map.put("location", redirectURL);

				// no auto-redirecting at client side, need manual send the request.
				HttpGet httpGet = new HttpGet( redirectURL);
				response = client.execute(httpGet, context);
		
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				
				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
				EntityUtils.consume(response.getEntity());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		map.put("html", result.toString());
		
		return map;
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
		String action = form.attr("action").replaceAll("../", "");
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
	
	public String addAttachment(String requestUrl, HttpClientContext context, String referrer, String tag, String comment, File attachment, Integer assignTo, String[] cc) throws Exception {
		String[] strs = requestUrl.split("/");
		StringBuffer result = new StringBuffer();
		
		HttpPost httpPost = new HttpPost(requestUrl);
		
		HttpEntity entity = null;
		MultipartEntityBuilder m = null;
		m = MultipartEntityBuilder
			    .create()
			    .addTextBody("comment", comment)
			    .addTextBody("status", "")  // Topic : defect list, marketing, etc
			    .addTextBody("Content-Type", "application/octet-stream")
			    .addTextBody(tag, "")
			    .addTextBody("sendNotifications", "on")
			    .addBinaryBody("file", attachment, ContentType.create("application/octet-stream"), attachment.getName());
		
		if (assignTo != null) m.addTextBody("itemUsers", ""+assignTo);
		if (cc.length>0) {
			for (int i=0; i<cc.length; i++){
				m.addTextBody("itemUsers", ""+cc[i]);
			}
		}
		entity = m.build();

		httpPost.setHeader("User-Agent", USER_AGENT);
		httpPost.setHeader("Origin", "http://" + strs[2]);
		httpPost.setHeader("Connection", "keep-alive");
		httpPost.setHeader("Upgrade-Insecure-Requests", "1");
		httpPost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		httpPost.setHeader("Accept-Encoding", "gzip, deflate, sdch");
		httpPost.setHeader("Accept-Language", "en-US,en;q=0.8");
		httpPost.setHeader("Connection","keep-alive");
		httpPost.setHeader("Content-Type", entity.getContentType().getValue());
		httpPost.setHeader("Referer", referrer);
		httpPost.setEntity(entity);
		
		HttpResponse response = null;
		try {
			response = client.execute(httpPost, context);
			if (response.getStatusLine().getStatusCode() == 302) {
				String redirectURL = jtrac.getAppUrl() + response.getFirstHeader("Location").getValue();

				// no auto-redirecting at client side, need manual send the request.
				HttpGet httpGet = new HttpGet( redirectURL);
				response = client.execute(httpGet, context);
		
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				
				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
				EntityUtils.consume(response.getEntity());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}
	
	private String getFileName(String url) {
		String[] s=url.split("/");
		return s[s.length-1];
	}
	
	public static void main(String[] args) throws Exception{
		NLBotService issueSvc = NLBotService.getInstance();
		Issue issue = issueSvc.findIssueById(1);
		Topic topic = issueSvc.getTopic(issue.getTopic(), null);
		JtracUtil util = new JtracUtil();
		Integer no = util.postToJtrac(issue, topic);
		System.out.println("tiket : " + no);
	}

}
