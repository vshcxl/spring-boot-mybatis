package com.snowstone.spring.boot;

import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Connection;
import javax.sql.DataSource;

import org.apache.activemq.jms.pool.PooledConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kdqkl.mq.bo.MqBo;
import com.kdqkl.mq.context.ActiveMQPoolsUtil;
import com.kdqkl.mq.pojo.MqMessage;
import com.kdqkl.mq.service.MqClient;
import com.kdqkl.mq.service.MqComsumer;
import com.snowstone.spring.boot.RedisUtil;
import com.snowstone.spring.boot.listener.RequestMessageListener;
import com.snowstone.spring.boot.mapper.UserMapper;
import com.snowstone.spring.boot.mapper.WorkerMapper;
import com.snowstone.spring.boot.model.User;
import com.snowstone.spring.boot.model.Worker;
import com.snowstone.spring.boot.work.Apply;
import com.snowstone.spring.boot.work.WorkExecutor;

import cn.jszhan.commons.kern.apiext.redis.RedisClient;

@Controller
public class BootController {

	@Autowired
	UserMapper userMapper;
	@Autowired
	RedisUtil redisUtil;
	@Autowired
	DataSource dataSource;
	@Autowired
	RequestMessageListener requestMessageListener;
	
	@Autowired
	MqService mqService;
	
	@Autowired
	private WorkExecutor workExecutor;
	private   String REDIS_KEY = "WORK:ASYN:WORKQUEUE";
	
	@Autowired
	WorkerMapper workerMapper;
	
	@RequestMapping("/ss")
	@ResponseBody
	public void apply() {
//		Worker w=workerMapper.selectByPrimaryKey(71);
//		System.out.println(w.toString());
//		return w;
//		RedisClient.set(REDIS_KEY, "mkmkmk");
//		RedisClient.lpushObjByJson("WORK:ASYN:WORKQUEUE", Apply.class, null);
		workExecutor.submit("m1", "zs", Apply.class);
//		RedisClient.lpushObjByJson("WORK:ASYN:xx", Apply.class, null);
	}
	@RequestMapping("/map")
	@ResponseBody
	public Map map() {
		LinkedHashMap< String, String> link=new LinkedHashMap<>();
		link.put("12", "500");
		link.put("33", "100");
		link.put("11", "300");
		return link;
	}
	
	
	@RequestMapping("/find")
	@ResponseBody
	public User find() {
		try {
			System.out.println(dataSource.getConnection().getMetaData().getURL());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		redisUtil.set("kkpp", "7777");
		return userMapper.selectByPrimaryKey(2);
	}
	
	
	// mq地址
    private static String brokerURL="tcp://116.62.117.88:61616";
    // 数据模板
    private static DataSource dataSource1;
    // 队列名称
    private static String queueName="foo.bar";
    // 消息
    private static String mqMessage="msg msg22";
    // 创建存放消息的表名
    private static String queueTableName="mqmessage";
    // 队列默认大小
    private static Integer queueMaxCount = 500;
    // 是否创建表
    private static Integer isCreateTable = 0;
    Connection connection;
    
    
    
    
    @RequestMapping("/mq")
	@ResponseBody
	public void mq() {
    	mqService.p();
    }
    
    
    //生产者
	@RequestMapping("/mq1")
	@ResponseBody
	public void mq1() {
		try {
			MqClient  mc=new MqClient(brokerURL, dataSource, queueTableName,
					queueName, queueMaxCount, isCreateTable);
			 
			 mc.start();
			 MqMessage mq=new MqMessage();
			 mq.setMessage("msg msg msg222");
			 mq.setQueueName("foo.bar");
			 mq.setSendTime(new Date().getTime());
			 mq.setTableName("mqmessage");
			 mc.sendMessage(mq);
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		System.out.println("---------ok mq1");
	}
	//消费者
	@RequestMapping("/mq2")
	@ResponseBody
	public void mq2() {
		MqBo mb=new MqBo();
		mb.setListener(requestMessageListener);
		mb.setQueueName(queueName);
		MqComsumer t=new MqComsumer(brokerURL);
		t.start();
		t.register(mb);
		System.out.println("---------ok mq2");
	}
}
