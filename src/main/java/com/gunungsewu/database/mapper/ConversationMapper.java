package com.gunungsewu.database.mapper;

import com.gunungsewu.database.entity.Chat;
import com.gunungsewu.database.entity.CheckOrderChat;
import com.gunungsewu.database.entity.KeluhanChat;
import com.gunungsewu.database.entity.ReorderChat;

public interface ConversationMapper {
	
	Integer newCheckOrder(CheckOrderChat chat);
	Integer newReorder(ReorderChat chat);
	Integer newKeluhan(KeluhanChat chat);
	
	void saveChat(Chat chat);
	void updCheckType(CheckOrderChat chat);
	void updCheckPo(CheckOrderChat chat);

	void updReorderPo(ReorderChat chat);
	void updReorderNewPo(ReorderChat chat);
	void updReorderStatus(ReorderChat chat);
	
	void updKelPo(KeluhanChat chat);
	void updKelOrderId(KeluhanChat chat);
	void updKelType(KeluhanChat chat);
	void updKelDetail(KeluhanChat chat);
	void updKelTicket(KeluhanChat chat);
}
