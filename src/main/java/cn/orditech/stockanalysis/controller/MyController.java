package cn.orditech.stockanalysis.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cn.orditech.stockanalysis.catcher.enums.TaskTypeEnum;
import cn.orditech.stockanalysis.catcher.service.TaskGenerateService;
import cn.orditech.stockanalysis.entity.StockInfo;
import cn.orditech.stockanalysis.query.StockInfoQuery;
import cn.orditech.stockanalysis.service.StockDataQueryService;
import cn.orditech.stockanalysis.service.StockDataShowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/*
 * @author kimi
 */
@Controller
public class MyController {
    @Autowired
    private StockDataQueryService stockDataQueryService;
    @Autowired
    private StockDataShowService stockDataShowService;
    @Autowired
    private TaskGenerateService taskGenerateService;
    @Value ("${generate.lock.Key}")
    private String generateLockKey;

    private static Logger logger = LoggerFactory.getLogger (MyController.class);

    @RequestMapping(value = "/index")
    public String index () {
        logger.info ("欢迎访问kimi的网站！");

        return "index";
    }

    @RequestMapping(value = "/stockcurve")
    public String businessCurve () {
        return "stockcurve";
    }

    @RequestMapping(value = "/stocksearch", method = RequestMethod.POST)
    @ResponseBody
    public String searchStock (@RequestParam("keyword") String keyword) {
        StockInfoQuery query = new StockInfoQuery ();
        query.setKeyword (keyword);
        List<StockInfo> stockInfoList = stockDataQueryService.fuzzySearchByCodeOrName (query);
        Gson gson = new Gson ();
        return gson.toJson (stockInfoList);
    }

    @RequestMapping(value = "/curvedata")
    @ResponseBody
    public String getCureData (@RequestParam("code") String code) {
        Map<String, Object> result = stockDataShowService.generateBussinessCurveData (code,
                16 + (int)Math.ceil(Calendar.getInstance ().get (Calendar.MONTH)/3.0));

        Gson gson = new GsonBuilder ().serializeNulls ().create ();
        return gson.toJson (result);
    }

    @RequestMapping(value = "/generatetask")
    public String generateTask (Model model) {
        model.addAttribute ("catcherSet", taskGenerateService.getCatcherMap ().keySet ());
        return "generatetask";
    }

    @RequestMapping(value = "/generatetaskcommit", method = RequestMethod.POST)
    @ResponseBody
    public String generateTaskCommit (@RequestParam String typeEnumCode, @RequestParam String generateLockKey) {
        if (TaskTypeEnum.getByCode (typeEnumCode) == null) {
            return "任务类型错误！";
        }
        if (!this.generateLockKey.equals (generateLockKey)) {
            return "任务锁错误！";
        }
        taskGenerateService.commitCatchTask (TaskTypeEnum.getByCode (typeEnumCode), false);
        return null;
    }
}
