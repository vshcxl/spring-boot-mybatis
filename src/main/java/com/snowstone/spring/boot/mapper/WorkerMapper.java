package com.snowstone.spring.boot.mapper;

import java.util.List;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.snowstone.spring.boot.model.Worker;
import com.snowstone.spring.boot.model.Worker.WorkStatus;

public interface WorkerMapper {
	int deleteByPrimaryKey(Integer id);

    int insert(Worker record);

    int insertSelective(Worker record);

    Worker selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Worker record);

    int updateByPrimaryKey(Worker record);
    
	List<Worker> findByStatus(WorkStatus status);
	
	Worker findOne(Integer id);
	
	
}
