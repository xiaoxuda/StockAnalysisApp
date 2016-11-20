package com.kimi.stockanalysis.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kimi.stockanalysis.entity.StockInfo;
import com.kimi.stockanalysis.query.StockInfoQuery;
import com.kimi.stockanalysis.service.StockDataQueryService;
import com.kimi.stockanalysis.service.StockDataShowService;

/*
 * @author kimi
 */
@Controller
public class MyController {
	@Autowired
	private StockDataQueryService stockDataQueryService;
	@Autowired
	private StockDataShowService stockDataShowService;

	private static Logger logger = LoggerFactory.getLogger(MyController.class);

	@RequestMapping(value = "/index")
	public String index() {
		logger.info("欢迎访问kimi的网站！");

		return "index";
	}

	@RequestMapping(value = "/stockcurve")
	public String businessCurve() {
		return "stockcurve";
	}

	@RequestMapping(value = "/stocksearch", method = RequestMethod.POST)
	@ResponseBody
	public String searchStock(@RequestParam("keyword") String keyword) {
		StockInfoQuery query = new StockInfoQuery();
		query.setKeyword(keyword);
		List<StockInfo> stockInfoList = stockDataQueryService.fuzzySearchByCodeOrName(query);
		Gson gson = new Gson();
		return gson.toJson(stockInfoList);
	}

	@RequestMapping(value = "/curvedata", method = RequestMethod.POST)
	@ResponseBody
	public String getCureData(@RequestParam("code") String code) {
		Map<String, Object> result = 
				stockDataShowService.generateBussinessCurveData(code,20);
		
		Gson gson = new GsonBuilder().serializeNulls().create();
		return gson.toJson(result);
	}
}
