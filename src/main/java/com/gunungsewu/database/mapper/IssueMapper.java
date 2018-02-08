package com.gunungsewu.database.mapper;

import java.util.List;
import java.util.Map;

import com.gunungsewu.database.entity.Issue;


public interface IssueMapper {
	
	Issue findIssueById(Integer id);
	
	Issue findIssueByJtracNo(Integer jtracNo);

	/*List<Issue> findAllIssues();

	void insertIssue(Issue issue);

	void updateIssue(Issue issue);
	
	Issue getIssue(long issueNo);*/
	
	List<String> getIssueTopics();
	
	List<String> getDefectList(String topic);
	
	boolean addUserForIssue(Issue issue);
	
	boolean deleteUserForIssue(Integer userId);
	
//	boolean updateIssueJtracId(Issue issue);
	
	int getUserStatusForIssue(Integer userId);
	
	List<Issue> getLastIssueStatus(Integer userId);
	
	boolean updateUserChatId(Map<String, Object> param);
	
	Integer addIssue(Issue issue);
	
	Integer updateIssue(Issue issue);
	
//	boolean updateIssueLocation(long issueNo, String location);
//	
//	boolean updateIssueTopic(long issueNo, String topic);
//	
//	boolean updateIssueSubTopic(long issueNo, String defect);
//	
//	boolean updateIssueDetail(long issueNo, String text);
//	
//	boolean updateIssueAttachment1(long issueNo, String path);
//	
//	boolean updateIssueAttachment2(long issueNo, String path);
//	
//	boolean updateIssueAttachment3(long issueNo, String path);

}