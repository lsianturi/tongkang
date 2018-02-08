package com.gunungsewu.database.service;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gunungsewu.database.MyBatisSqlSessionFactory;
import com.gunungsewu.database.entity.Chat;
import com.gunungsewu.database.entity.CheckOrderChat;
import com.gunungsewu.database.entity.KeluhanChat;
import com.gunungsewu.database.entity.ReorderChat;
import com.gunungsewu.database.mapper.ConversationMapper;

public class ConverstationService {

	private Logger logger = LoggerFactory.getLogger(ConverstationService.class);
	
	private static volatile ConverstationService instance;

	public static ConverstationService getInstance() {
		final ConverstationService currentInstance;
		if (instance == null) {
			synchronized (ConverstationService.class) {
				if (instance == null) {
					instance = new ConverstationService();
				}
				currentInstance = instance;
			}
		} else {
			currentInstance = instance;
		}
		return currentInstance;
	}
	
	
	public void saveChat(Long chatId, Integer userId, String name, String type, String text) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			Chat chat = new Chat(chatId, userId, name, type, text);
			ConversationMapper mapper = sqlSession.getMapper(ConversationMapper.class);
			mapper.saveChat(chat);
			sqlSession.commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
	}
	

	public CheckOrderChat newCheckOrder(CheckOrderChat chat) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			ConversationMapper wsMapper = sqlSession.getMapper(ConversationMapper.class);
			wsMapper.newCheckOrder(chat);
			sqlSession.commit();
			return chat;
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
		return null;
	}
	
	public ReorderChat newReorder(ReorderChat chat) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			ConversationMapper wsMapper = sqlSession.getMapper(ConversationMapper.class);
			wsMapper.newReorder(chat);
			sqlSession.commit();
			return chat;
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
		return null;
	}
	
	public KeluhanChat newKeluhan(KeluhanChat chat) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			ConversationMapper wsMapper = sqlSession.getMapper(ConversationMapper.class);
			wsMapper.newKeluhan(chat);
			sqlSession.commit();
			return chat;
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
		return null;
	}
	
	
	public void updCheckType(CheckOrderChat chat) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			ConversationMapper wsMapper = sqlSession.getMapper(ConversationMapper.class);
			wsMapper.updCheckType(chat);
			sqlSession.commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
	}
	
	public void updCheckPo(CheckOrderChat chat) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			ConversationMapper wsMapper = sqlSession.getMapper(ConversationMapper.class);
			wsMapper.updCheckPo(chat);
			sqlSession.commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
	}
	
	
	public void updReorderPo(ReorderChat chat) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			ConversationMapper wsMapper = sqlSession.getMapper(ConversationMapper.class);
			wsMapper.updReorderPo(chat);
			sqlSession.commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
	}
	
	public void updKelOrderId(KeluhanChat chat) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			ConversationMapper wsMapper = sqlSession.getMapper(ConversationMapper.class);
			wsMapper.updKelOrderId(chat);
			sqlSession.commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
	}
	
	public void updReorderNewPo(ReorderChat chat) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			ConversationMapper wsMapper = sqlSession.getMapper(ConversationMapper.class);
			wsMapper.updReorderNewPo(chat);
			sqlSession.commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
	}
	
	public void updReorderStatus(ReorderChat chat) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			ConversationMapper wsMapper = sqlSession.getMapper(ConversationMapper.class);
			wsMapper.updReorderStatus(chat);
			sqlSession.commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
	}
	
	public void updKelPo(KeluhanChat chat) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			ConversationMapper wsMapper = sqlSession.getMapper(ConversationMapper.class);
			wsMapper.updKelPo(chat);
			sqlSession.commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
	}
	
	public void updKelType(KeluhanChat chat) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			ConversationMapper wsMapper = sqlSession.getMapper(ConversationMapper.class);
			wsMapper.updKelType(chat);
			sqlSession.commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
	}
	
	public void updKelDetail(KeluhanChat chat) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			ConversationMapper wsMapper = sqlSession.getMapper(ConversationMapper.class);
			wsMapper.updKelDetail(chat);
			sqlSession.commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
	}
	
	public void updKelTicket(KeluhanChat chat) {
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		try {
			ConversationMapper wsMapper = sqlSession.getMapper(ConversationMapper.class);
			wsMapper.updKelTicket(chat);
			sqlSession.commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			sqlSession.close();
		}
	}
	

	public static void main(String[] args) {
		/*
		ConverstationService svc = ConverstationService.getInstance();

		
		CheckOrderChat chat = new CheckOrderChat(1, "check");
		chat = svc.newCheckOrder(chat);
		System.out.println("id: " + chat.getId());
		
		chat.setPoNo("123");
		chat.setType("choose po");
		svc.updCheckPo(chat);
		svc.updCheckType(chat);
		
		chat = new CheckOrderChat(2, "check");
		chat = svc.newCheckOrder(chat);
		System.out.println("id: " + chat.getId());
		
		chat.setPoNo("123");
		chat.setType("top 5");
		svc.updCheckPo(chat);
		svc.updCheckType(chat);
		
		
		ReorderChat rchat = new ReorderChat(1, "reorder");
		rchat = svc.newReorder(rchat);
		System.out.println("id: " + rchat.getId());
		
		rchat.setNewPo("123123");
		rchat.setOrderId("ord1");
		rchat.setPoNo("no 123");
		rchat.setStatus(1);
		
		svc.updReorderNewPo(rchat);
		svc.updReorderOrderId(rchat);
		svc.updReorderPo(rchat);
		svc.updReorderStatus(rchat);
		
		
		KeluhanChat kchat = new KeluhanChat(1, "keluhan");
		kchat = svc.newKeluhan(kchat);
		System.out.println("id: " + kchat.getId());
		
		kchat.setDetailKeluhan("keluhin");
		kchat.setPoNo("12314");
		kchat.setTicketId(13213);
		kchat.setTypeKeluhan("Rusak");
		
		svc.updKelPo(kchat);
		svc.updKelPoDetail(kchat);
		svc.updKelPoTicket(kchat);
		svc.updKelType(kchat);
		
		
		kchat = new KeluhanChat(1, "habis air");
		kchat = svc.newKeluhan(kchat);
		System.out.println("id: " + kchat.getId());
		
		kchat.setDetailKeluhan("keluhin");
		kchat.setPoNo("12314");
		kchat.setTicketId(13213);
		kchat.setTypeKeluhan("Haus");
		
		svc.updKelPo(kchat);
		svc.updKelPoDetail(kchat);
		svc.updKelPoTicket(kchat);
		svc.updKelType(kchat);
		*/
	}
}