/**
 *
 */
package com.orditech.stockanalysis.controller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.orditech.stockanalysis.catcher.service.CatcherManageService;
import com.orditech.stockanalysis.catcher.service.TaskGenerateService;

/**
 * @author kimi
 *         用于在系统启动后启动爬虫系统
 */
@Component
public class CatcherController {

    @Autowired
    private CatcherManageService catcherManageService;

    @Autowired
    private TaskGenerateService taskGenerateService;

    @PostConstruct
    public void starter () {
        //开启爬虫监控
        catcherManageService.startCatcherMonitor ();
        //启动任务生成器
        taskGenerateService.startGenerator ();
    }


}
