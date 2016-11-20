/**
 * 
 */
package com.kimi.stockanalysis.catcher.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.kimi.stockanalysis.catcher.BaseCatcher;
import com.kimi.stockanalysis.catcher.enums.TaskTypeEnum;

/**
 * 爬虫管理器
 * 
 * @author kimi
 *
 */
public class CatcherManageService implements ApplicationContextAware{
	
	public final Logger LOGGER = LoggerFactory.getLogger(CatcherManageService.class);
	
	//爬虫检测时间间隔
	private Long checkTimeGap = 10*1000L;
	
	private Map<TaskTypeEnum, BaseCatcher> catcherMap = new HashMap<TaskTypeEnum, BaseCatcher>();

	@Autowired
	private TaskQueueService taskQueueService;
	
	/**
	 * 自动注册已设置的爬虫
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// TODO Auto-generated method stub
		Map<String,BaseCatcher> beansMap = applicationContext.getBeansOfType(BaseCatcher.class);
		if(beansMap != null && beansMap.size()>0){
			for(BaseCatcher catcher:beansMap.values()){
				catcherMap.put(catcher.getTaskType(), catcher);
			}
		}
	}
	
	/**
	 * 启动已注册的爬虫
	 * 
	 * @author kimi
	 */
	public void startCatcher() {
		for (TaskTypeEnum type : catcherMap.keySet()) {
			BaseCatcher catcher = catcherMap.get(type);
			if (!catcher.isRunning()) {
				catcher.start();
			}
		}
	}

	/**
	 * 爬虫监控,按任务类型读取任务队列，唤醒/重启对应的爬虫
	 */
	public void startCatcherMonitor() {
		Thread thread = new Thread("catcherMonitor") {
			@Override
			public void run() {
				while (true) {
					catcherStateCheck();
					try {
						Thread.sleep(checkTimeGap);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						LOGGER.error("catcherMonitor,异常信息{}", e.getMessage());
					}
				}
			}
		};
		thread.start();
	}

	/**
	 * 重启爬虫任务,暂时没有实现睡眠唤醒功能
	 */
	public void catcherStateCheck(){
		for(TaskTypeEnum type:taskQueueService.getTypeSet()){
			startCatcher(type);
		}
	}
	
	/**
	 * 启动已注册的选定key的爬虫
	 * 
	 * @author kimi
	 */
	public void startCatcher(TaskTypeEnum type) {
		BaseCatcher catcher = catcherMap.get(type);
		if (null != catcher && !catcher.isRunning()) {
			LOGGER.info("{}:爬虫重新启动",type);
			catcher.start();
		}
	}

}
