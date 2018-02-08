package com.gunungsewu.database.mapper;

import java.util.Map;

import com.gunungsewu.database.entity.Jtrac;
import com.gunungsewu.database.entity.Topic;

public interface JtracMapper {
	
	Jtrac getJtracUser();
	
	Topic getTopic(Map<String, String> param);
}
