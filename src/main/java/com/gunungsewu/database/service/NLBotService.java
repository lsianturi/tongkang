package com.gunungsewu.database.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gunungsewu.database.MyBatisSqlSessionFactory;
import com.gunungsewu.database.entity.Issue;
import com.gunungsewu.database.entity.Jtrac;
import com.gunungsewu.database.entity.MQConfig;
import com.gunungsewu.database.entity.MQName;
import com.gunungsewu.database.entity.NLUser;
import com.gunungsewu.database.entity.Topic;
import com.gunungsewu.database.entity.WsConfig;
import com.gunungsewu.database.mapper.ConfigMapper;
import com.gunungsewu.database.mapper.IssueMapper;
import com.gunungsewu.database.mapper.JtracMapper;

public class NLBotService {

	private Logger logger = LoggerFactory.getLogger(NLBotService.class);
	
	private static volatile NLBotService instance;

	public static NLBotService getInstance() {
		final NLBotService currentInstance;
		if (instance == null) {
			synchronized (NLBotService.class) {
				if (instance == null) {
					instance = new NLBotService();
				}
				currentInstance = instance;
			}
		} else {
			currentInstance = instance;
		}
		return currentInstance;
	}

	public WsConfig getWsConfig(String name) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			ConfigMapper wsMapper = sqlSession.getMapper(ConfigMapper.class);
			return wsMapper.getWsConfig(name);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
		return null;
	}
	
	public MQConfig getMQConfig() {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			ConfigMapper wsMapper = sqlSession.getMapper(ConfigMapper.class);
			return wsMapper.getMQConfig();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
		return null;
	}
	
	
	public List<MQName> getQueueNames() {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			ConfigMapper wsMapper = sqlSession.getMapper(ConfigMapper.class);
			return wsMapper.getQueueNames();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
		return null;
	}

	public NLUser getUserByMsisdn(String msisdn) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			ConfigMapper wsMapper = sqlSession.getMapper(ConfigMapper.class);
			return wsMapper.getUser(msisdn);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
		return null;
	}

	public NLUser getUserByChatId(Integer chatId) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			ConfigMapper wsMapper = sqlSession.getMapper(ConfigMapper.class);
			return wsMapper.getUserByChatId(chatId);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
		return null;
	}
	
	public void updateUserChatId(String msisdn, Integer chatId) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			ConfigMapper wsMapper = sqlSession.getMapper(ConfigMapper.class);
			NLUser user = new NLUser();
			user.setMsisdn(msisdn);
			user.setName(msisdn);
			user.setChatId(chatId);
			wsMapper.updateUserChatId(user);
			sqlSession.commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
	}
	
	/*
	public List<POType> getPOTypes(String type, String poNumber, String chatId) {
		WsConfig ws = NLBotService.getInstance().getWsConfig(type);

		if (ws.getWsUrl() != null && !ws.getWsUrl().trim().equals("")) {
			try {
				return NlWsUtil.getInstance().getPOTypes(ws.getWsUrl(), poNumber, chatId);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return null;

	}
	

	public List<OrderItem> getOrder(String type, String poNumber, String chatId) {
		WsConfig ws = null;
		if (type.contains("Woven")) {
			ws = NLBotService.getInstance().getWsConfig(Constant.GET_PO_WOVEN);
		} else {
			ws = NLBotService.getInstance().getWsConfig(Constant.GET_PO_PRINTED);
		}

		if (ws.getWsUrl() != null && !ws.getWsUrl().trim().equals("")) {
			try {
				return NlWsUtil.getInstance().getPOItem(ws.getWsUrl(), poNumber, chatId);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return null;

	}
	
	public List<OrderItem> getReOrder(String type, String poNumber, String chatId) {
		WsConfig ws = null;
		if (type.contains("Woven")) {
			ws = NLBotService.getInstance().getWsConfig(Constant.GET_REORDER_PO_WOVEN);
		} else {
			ws = NLBotService.getInstance().getWsConfig(Constant.GET_REORDER_PO_PRINTED);
		}

		if (ws.getWsUrl() != null && !ws.getWsUrl().trim().equals("")) {
			try {
				return NlWsUtil.getInstance().getPOItem(ws.getWsUrl(), poNumber, chatId);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return null;

	}
	
	
	public List<Order> getTop5PO(String chatId) {
		WsConfig ws = NLBotService.getInstance().getWsConfig(Constant.GET_TOP_5_PO);
		if (ws.getWsUrl() != null && !ws.getWsUrl().trim().equals("")) {
			try {
				return NlWsUtil.getInstance().getTop5PO(ws.getWsUrl(), chatId);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return null;
	}
	
	*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public List<String> getIssueTopics() {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			IssueMapper issueMapper = sqlSession.getMapper(IssueMapper.class);
			return issueMapper.getIssueTopics();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
		return null;
	}

	public List<String> getDefectList(String topic) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			IssueMapper issueMapper = sqlSession.getMapper(IssueMapper.class);
			return issueMapper.getDefectList(topic);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
		return null;
	}
	
	
	
	
	

	/*
	 * public boolean addUserForIssue(Integer userId, int status) { SqlSession
	 * sqlSession = MyBatisSqlSessionFactory.getSqlSession(); try { IssueMapper
	 * issueMapper = sqlSession.getMapper(IssueMapper.class); Issue issue = new
	 * Issue(); issue.setId(userId); issue.setStatus(status); boolean result=
	 * issueMapper.addUserForIssue(issue); sqlSession.commit(); return result; }
	 * catch (Exception e) { logger.debug(e.getMessage()); } finally {
	 * sqlSession.close(); } return false; }
	 */

	/*
	 * public boolean deleteUserForIssue(Integer userId) { SqlSession sqlSession
	 * = MyBatisSqlSessionFactory.getSqlSession(); try { IssueMapper issueMapper
	 * = sqlSession.getMapper(IssueMapper.class); boolean result=
	 * issueMapper.deleteUserForIssue(userId); sqlSession.commit(); return
	 * result; } catch (Exception e) { logger.debug(e.getMessage()); } finally {
	 * sqlSession.close(); } return false; }
	 */

	public Integer updateIssueJtracId(Integer issueNo, Integer jtracNo) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			IssueMapper issueMapper = sqlSession.getMapper(IssueMapper.class);
			Issue issue = new Issue();
			issue.setId(issueNo);
			issue.setJtracNo(jtracNo);
			Integer result = issueMapper.updateIssue(issue);
			sqlSession.commit();
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
		return 0;
	}

	/*
	 * public int getUserStatusForIssue(Integer userId) { SqlSession sqlSession
	 * = MyBatisSqlSessionFactory.getSqlSession(); try { IssueMapper issueMapper
	 * = sqlSession.getMapper(IssueMapper.class); return
	 * issueMapper.getUserStatusForIssue(userId); } catch (Exception e) {
	 * logger.debug(e.getMessage()); } finally { sqlSession.close(); } return 0;
	 * }
	 */

	public String getLastIssueStatus(Integer userId) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		StringBuilder sb = new StringBuilder();
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		try {
			IssueMapper issueMapper = sqlSession.getMapper(IssueMapper.class);
			List<Issue> issues = issueMapper.getLastIssueStatus(userId);
			if (issues != null) {
				sb.append("No").append("\t").append("Desc").append("\t").append("Status").append("\t").append("Date")
						.append("\n");
				for (Issue i : issues) {
					sb.append(i.getJtracNo()).append("\t");
					sb.append(i.getDetail()).append("\t");
					sb.append(i.getTopic()).append("\t");
					sb.append(df.format(i.getCreateTime())).append("\n");
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
		return sb.toString();
	}

	public Issue findIssueById(Integer id) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			IssueMapper issueMapper = sqlSession.getMapper(IssueMapper.class);
			return issueMapper.findIssueById(id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
		return null;
	}

	public Issue findIssueByJtracNo(Integer jtracNo) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			IssueMapper issueMapper = sqlSession.getMapper(IssueMapper.class);
			return issueMapper.findIssueByJtracNo(jtracNo);
			// return
			// sqlSession.selectOne("com.mybatis3.IssueMapper.findStudentById",
			// studId);
		} finally {
			sqlSession.close();
		}
	}

	public String getIssueStatus(Integer jtracNo) {
		StringBuilder sb = new StringBuilder();
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		try {
			Issue i = findIssueByJtracNo(jtracNo);
			if (i != null) {
				sb.append("No").append("\t").append("Desc").append("\t").append("Status").append("\t").append("Date")
						.append("\n");
				sb.append(i.getJtracNo()).append("\t");
				sb.append(i.getDetail()).append("\t");
				sb.append(i.getTopic()).append("\t");
				sb.append(df.format(i.getCreateTime())).append("\n");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return sb.toString();
	}

	

	public Integer addIssue(String msisdn, Integer userId, long chatId, String userName) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			IssueMapper issueMapper = sqlSession.getMapper(IssueMapper.class);
			Issue i = new Issue();
			i.setMsisdn(msisdn);
			i.setUserId(userId);
			i.setChatId(chatId);
			i.setUserName(userName);

			issueMapper.addIssue(i);
			sqlSession.commit();
			return i.getId();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
		return 0;
	}

	public Integer updateIssue(Issue issue) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			IssueMapper issueMapper = sqlSession.getMapper(IssueMapper.class);
			int result = issueMapper.updateIssue(issue);
			sqlSession.commit();
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
		return 0;
	}

	public Jtrac getJtracUser() {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			JtracMapper jtracMapper = sqlSession.getMapper(JtracMapper.class);
			return jtracMapper.getJtracUser();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			sqlSession.close();
		}
		return null;
	}

	public Topic getTopic(String topic, String subTopic) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			JtracMapper jtracMapper = sqlSession.getMapper(JtracMapper.class);
			Map<String, String> param = new HashMap<>();
			param.put("topic", topic);
			param.put("subTopic", subTopic);
			return jtracMapper.getTopic(param);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
		return null;
	}

	public Integer updateIssueLocation(Integer issueNo, String location) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			IssueMapper issueMapper = sqlSession.getMapper(IssueMapper.class);
			Issue issue = new Issue();
			issue.setId(issueNo);
			Integer result = issueMapper.updateIssue(issue);
			sqlSession.commit();
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
		return 0;
	}

	public Integer updateIssueTopic(Integer issueNo, String topic) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			IssueMapper issueMapper = sqlSession.getMapper(IssueMapper.class);
			Issue issue = new Issue();
			issue.setId(issueNo);
			issue.setTopic(topic);
			Integer result = issueMapper.updateIssue(issue);
			sqlSession.commit();
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
		return 0;
	}

	public Integer updateIssueSubTopic(Integer issueNo, String subTopic) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			IssueMapper issueMapper = sqlSession.getMapper(IssueMapper.class);
			Issue issue = new Issue();
			issue.setId(issueNo);
			Integer result = issueMapper.updateIssue(issue);
			sqlSession.commit();
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
		return 0;
	}

	public Integer updateIssueDetail(Integer issueNo, String detail) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			IssueMapper issueMapper = sqlSession.getMapper(IssueMapper.class);
			Issue issue = new Issue();
			issue.setId(issueNo);
			issue.setDetail(detail);
			Integer result = issueMapper.updateIssue(issue);
			sqlSession.commit();
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
		return 0;
	}

	public Integer updateIssueAttachment1(Integer issueNo, String path) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			IssueMapper issueMapper = sqlSession.getMapper(IssueMapper.class);
			Issue issue = new Issue();
			issue.setId(issueNo);
			issue.setAttachment1(path);
			Integer result = issueMapper.updateIssue(issue);
			sqlSession.commit();
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
		return 0;
	}

	public Integer updateIssueAttachment2(Integer issueNo, String path) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			IssueMapper issueMapper = sqlSession.getMapper(IssueMapper.class);
			Issue issue = new Issue();
			issue.setId(issueNo);
			issue.setAttachment2(path);
			Integer result = issueMapper.updateIssue(issue);
			sqlSession.commit();
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
		return 0;
	}

	public Integer updateIssueAttachment3(Integer issueNo, String path) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			IssueMapper issueMapper = sqlSession.getMapper(IssueMapper.class);
			Issue issue = new Issue();
			issue.setId(issueNo);
			issue.setAttachment3(path);
			Integer result = issueMapper.updateIssue(issue);
			sqlSession.commit();
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
		return 0;
	}

	/*
	 * public List<Store> getStoreLocations() { SqlSession sqlSession =
	 * MyBatisSqlSessionFactory.getSqlSession(); try { ConfigMapper
	 * storeMapper = sqlSession.getMapper(ConfigMapper.class); return
	 * storeMapper.getStoreLocations(); } catch (Exception e) {
	 * logger.debug(e.getMessage()); } finally { sqlSession.close(); } return
	 * null; } public String getStoreLocation(String shortname) { SqlSession
	 * sqlSession = MyBatisSqlSessionFactory.getSqlSession(); try {
	 * ConfigMapper storeMapper = sqlSession.getMapper(ConfigMapper.class);
	 * return storeMapper.getStoreLocation(shortname); } catch (Exception e) {
	 * logger.debug(e.getMessage()); } finally { sqlSession.close(); } return
	 * null; }
	 * 
	 * public List<Customer> getUsers() { SqlSession sqlSession =
	 * MyBatisSqlSessionFactory.getSqlSession(); try { ConfigMapper
	 * storeMapper = sqlSession.getMapper(ConfigMapper.class); return
	 * storeMapper.getUsers(); } catch (Exception e) {
	 * logger.debug(e.getMessage()); } finally { sqlSession.close(); } return
	 * null; }
	 * 
	 * 
	 * 
	 */
	//
	// boolean ;
	//
	// boolean ;
	//
	// boolean ;
	//
	// boolean ;
	//
	// boolean updateIssueAttachment2(long issueNo, String path);
	//
	// boolean updateIssueAttachment3(long issueNo, String path);
	/*
	 * public List<Issue> findAllIssues() { SqlSession sqlSession =
	 * MyBatisSqlSessionFactory.getSqlSession(); try { IssueMapper issueMapper =
	 * sqlSession.getMapper(IssueMapper.class); return
	 * issueMapper.findAllIssues(); } finally { sqlSession.close(); } }
	 * 
	 * public Issue findIssueById(Integer studId) {
	 * logger.debug("Select Issue By ID :{}", studId); SqlSession sqlSession =
	 * MyBatisSqlSessionFactory.getSqlSession(); try { IssueMapper issueMapper =
	 * sqlSession.getMapper(IssueMapper.class); return
	 * issueMapper.findIssueById(studId); //return
	 * sqlSession.selectOne("com.mybatis3.IssueMapper.findStudentById", studId);
	 * } finally { sqlSession.close(); } }
	 * 
	 * public void createIssue(Issue issue) { SqlSession sqlSession =
	 * MyBatisSqlSessionFactory.getSqlSession(); try { IssueMapper issueMapper =
	 * sqlSession.getMapper(IssueMapper.class); issueMapper.insertIssue(issue);
	 * sqlSession.commit(); } finally { sqlSession.close(); } }
	 * 
	 * public void updateIssue(Issue issue) { SqlSession sqlSession =
	 * MyBatisSqlSessionFactory.getSqlSession(); try { IssueMapper issueMapper =
	 * sqlSession.getMapper(IssueMapper.class); issueMapper.updateIssue(issue);
	 * sqlSession.commit(); } finally { sqlSession.close(); } }
	 */

	public static void main(String[] args) {
		NLBotService svc = NLBotService.getInstance();

//		boolean stat = svc.updateUserChatId("6281315791700", 0);
//		System.out.println(stat);
		
		/*List<POType> types = svc.getPOTypes("001/NL/ACC/VI/16");
		for (POType t: types ) {
			System.out.println(t.getName());
		}*/
		
		/*List<OrderItem> items = svc.getOrder("Woven Label", "99999");
		for (OrderItem i : items) {
			System.out.println(i.getDescription());
			System.out.println(i.getImage());
		}*/
		/*List<MQName> ques = svc.getQueueNames();
		Map<String, String> map = ques.stream().collect(Collectors.toMap(MQName::getId, MQName::getName));
		
		System.out.println(map.get("TEL_ORDER_TO_NLIS"));*/
		
		/*List<Order> orders = svc.getTop5PO("375474385");
		for (Order ord : orders) {
			System.out.println(ord.getPoNumber());
		}*/
		
	}
}