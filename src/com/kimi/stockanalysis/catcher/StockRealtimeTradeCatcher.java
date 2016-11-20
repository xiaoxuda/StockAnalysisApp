/**
 * 
 */
package com.kimi.stockanalysis.catcher;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import com.kimi.stockanalysis.catcher.enums.TaskTypeEnum;
import com.kimi.stockanalysis.catcher.service.CatchTask;
import com.kimi.stockanalysis.entity.DailyTradeDetail;
import com.kimi.stockanalysis.entity.StockInfo;
import com.kimi.stockanalysis.service.StockDataService;

/**
 * @author kimi
 *
 */
public class StockRealtimeTradeCatcher extends BaseCatcher {

	@Autowired
	private StockDataService stockDataService;

	@Override
	public TaskTypeEnum getTaskType() {
		return TaskTypeEnum.SINAJS_PRICE;
	}

	/**
	 * 抓取股票当天交易信息
	 * 
	 * @author kimi
	 * @see com.kimi.stockanalysis.catcher.BaseCatcher#extract(java.lang.String,
	 *      com.kimi.stockanalysis.service.CatchTask)
	 */
	@Override
	public boolean extract(String src, CatchTask task) {
		if (StringUtils.isBlank(src)) {
			LOGGER.error("{}:{},返回结果为空", task.getType(), task);
			return false;
		}

		String arr[] = src.split(",");

		if (arr.length < 4) {
			LOGGER.error("{}:{},抓取股票当天交易信息获取失败", task.getType(), task);
			return false;
		}

		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {

				String price = StringUtils.isBlank(arr[3]) || Float.valueOf(arr[3]) == 0 ? arr[2] : arr[3];

				StockInfo stockInfo = new StockInfo();
				stockInfo.setCode(task.getInfoValue("code").toString());
				stockInfo.setPrice(Float.valueOf(price));
				stockDataService.siUpdateOrInsert(stockInfo, false);

				// 今日有交易则更新交易信息
				if (StringUtils.isNotBlank(arr[3])) {
					DailyTradeDetail dtd = new DailyTradeDetail();
					dtd.setCode(task.getInfoValue("code").toString());
					dtd.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
					dtd.setStartPrice(Float.valueOf(arr[1]));
					dtd.setLastEndPrice(Float.valueOf(arr[2]));
					dtd.setEndPrice(Float.valueOf(arr[3]));
					dtd.setMaxPrice(Float.valueOf(arr[4]));
					dtd.setMinPrice(Float.valueOf(arr[5]));
					dtd.setTradeVolume(Long.valueOf(arr[8]));
					dtd.setTradeAmt(Double.valueOf(arr[9]));
					stockDataService.dtdUpdateOrInsert(dtd);
				}
			}
		});
		return true;
	}

	@Override
	public CatchTask generateTask(StockInfo stockInfo) {
		CatchTask task = new CatchTask();
		task.addInfo("code", stockInfo.getCode());
		task.addInfo("type", stockInfo.getType());
		task.setType(this.getTaskType().getCode());
		task.setUrl("http://hq.sinajs.cn/list=" + stockInfo.getType().substring(0, 2) + stockInfo.getCode());
		return task;
	}
}
