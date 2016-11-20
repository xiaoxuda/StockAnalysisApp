/**
 * 
 */
package com.kimi.stockanalysis.catcher.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.kimi.stockanalysis.catcher.BaseCatcher;
import com.kimi.stockanalysis.catcher.enums.TaskTypeEnum;
import com.kimi.stockanalysis.dao.StockInfoDao;
import com.kimi.stockanalysis.entity.StockInfo;

/**
 * 任务生成器，按照不同任务的时间策略定时生成数据抓取任务
 * 
 * @author kimi
 */
public class TaskGenerateService implements ApplicationContextAware {
	private final Logger LOGGER = LoggerFactory.getLogger(TaskGenerateService.class);

	@Autowired
	private StockInfoDao stockInfoDao;

	@Autowired
	private TaskQueueService taskQueueService;

	/** 生成任务执行开关 **/
	private boolean isContinue = true;
	/** 调度周期 ，毫秒 **/
	private long time_gap = 5 * 60 * 1000L;
	/** 上次调度时间 **/
	private Map<TaskTypeEnum, Date> scheduleMap = new HashMap<TaskTypeEnum, Date>();

	/** 注册爬虫 **/
	private Map<TaskTypeEnum, BaseCatcher> catcherMap = new HashMap<TaskTypeEnum, BaseCatcher>();

	@PostConstruct
	public void init() {
		// FIXME 考虑将调度时间落入数据库，启动时从数据库读取
	}

	/**
	 * 自动注册已设置的爬虫
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// TODO Auto-generated method stub
		Map<String, BaseCatcher> beansMap = applicationContext.getBeansOfType(BaseCatcher.class);
		if (beansMap != null && beansMap.size() > 0) {
			for (BaseCatcher catcher : beansMap.values()) {
				catcherMap.put(catcher.getTaskType(), catcher);
			}
		}
	}

	/** 启动生成器 **/
	public void startGenerator() {

		Thread thread = new Thread("taskGenerate") {
			@Override
			public void run() {
				while (true) {
					if (isContinue) {
						// commitCatchTaskAll(null);
						commitCatchTaskAll(TaskTypeEnum.SINAJS_PRICE);
					}

					try {
						Thread.sleep(time_gap);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						LOGGER.error("taskGenerate,异常信息{}", e.getMessage());
					}

				}
			}
		};
		thread.start();
	}

	public void reStartGenerator() {
		this.isContinue = true;
	}

	public void closeGenerator() {
		this.isContinue = false;
	}

	/**
	 * 提交爬虫任务
	 * 
	 * @param typeEnum
	 * @param stockInfo
	 */
	public void commitCatchTask(TaskTypeEnum typeEnum, StockInfo stockInfo) {

		CatchTask task = catcherMap.get(typeEnum).generateTask(stockInfo);

		taskQueueService.commitTask(typeEnum, task);
	}

	/**
	 * 生成爬虫任务,上市公司列表的任务除外
	 * 
	 * @param typeEnum
	 *            可为null,为null时提交所有已注册爬虫任务
	 */
	public void commitCatchTaskAll(TaskTypeEnum typeEnum) {
		// 生成股票列表抓取任务
		if (refreshCycle(TaskTypeEnum.JUCAONET_COMPANY_LIST)) {
			commitCatchTask(TaskTypeEnum.JUCAONET_COMPANY_LIST, null);
		}

		// 跳过股票列表爬虫
		if (TaskTypeEnum.JUCAONET_COMPANY_LIST.equals(typeEnum)) {
			return;
		}

		// 若没有查询到股票信息则清除调度信息
		List<StockInfo> taskInfoList = stockInfoDao.selectList(new StockInfo());
		if (taskInfoList == null || taskInfoList.size() == 0) {
			return;
		}
		for (Map.Entry<TaskTypeEnum, BaseCatcher> entry : catcherMap.entrySet()) {
			// 跳过股票列表爬虫
			if (TaskTypeEnum.JUCAONET_COMPANY_LIST.equals(entry.getKey())) {
				continue;
			}
			// 爬虫与指定不一致
			if (null != typeEnum && !entry.getKey().equals(typeEnum)) {
				continue;
			}

			if (refreshCycle(entry.getKey())) {
				for (StockInfo stockInfo : taskInfoList) {
					commitCatchTask(entry.getKey(), stockInfo);
				}
			}
		}
	}

	/**
	 * 若在下一个调度周期内则刷新调度时间，若不在当前调度周期内则返回false
	 * 
	 * @return
	 */
	public boolean refreshCycle(TaskTypeEnum typeEnum) {
		// 判断是否在调度周期内
		if (!typeEnum.isInNextCycle(scheduleMap.get(typeEnum))) {
			return false;
		}
		// 更新调度时间
		scheduleMap.put(typeEnum, new Date());
		return true;
	}
}
