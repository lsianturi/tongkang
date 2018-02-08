package com.gunungsewu.database.mapper;

import java.util.List;

import com.gunungsewu.database.entity.MQConfig;
import com.gunungsewu.database.entity.MQName;
import com.gunungsewu.database.entity.NLUser;
import com.gunungsewu.database.entity.WsConfig;

public interface ConfigMapper {
	
	WsConfig getWsConfig(String name);
	
	MQConfig getMQConfig();
	
	List<MQName> getQueueNames();
	
	NLUser getUser(String msisdn);
	
	NLUser getUserByChatId(Integer chatId);
	
	void updateUserChatId(NLUser user);

}
