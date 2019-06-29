package cn.orditech.stockanalysis.catcher.service;

import cn.orditech.stockanalysis.entity.WechatPublic;
import cn.orditech.stockanalysis.service.StockDataService;
import cn.orditech.stockanalysis.service.WechatPublicService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/*
 * @author kimi
 * @see 抓取上市公司财务报表
 */
@Service
public class WechatPublicCatcher {

    @Autowired
    private WechatPublicService wechatPublicService;

    @PostConstruct
    public void init() throws IOException {
        new Thread() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep (60L);
                } catch (InterruptedException e) {
                    e.printStackTrace ();
                }
                try {
                    rankExtractAll();
                } catch (IOException e) {
                    e.printStackTrace ();
                }
            }
        }.start ();
    }

    public void searchWechatPublicData(){
        String[] tags = {"电视","电台","民生","政务","财金","媒体"};
        for (String tag : tags)
        {
            for (int i = 1; i < 50; i++) {
                JSONArray jsonArr = null;
                try {
                    jsonArr = extractSearchData (URLEncoder.encode (tag, "utf-8"), i);
                } catch (IOException e) {
                    e.printStackTrace ();
                }
                if (jsonArr == null || jsonArr.size () == 0) {
                    continue;
                }
                for (int index = 0; index < jsonArr.size (); index++) {
                    saveSearchData (jsonArr.getJSONObject (index));
                }

            }
        }
    }

    public JSONArray extractSearchData(String keyword, Integer page) throws IOException {
        URL url = new URL ("https://api.newrank.cn/api/sync/weixin/account/search?keyword="+keyword+"&&page="+page+"&size=1");
        HttpURLConnection con = (HttpURLConnection) url.openConnection ();
        con.setRequestMethod ("POST");
        con.setRequestProperty ("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        con.setRequestProperty ("key", "df9516ecdae74dad845c8ad71");
        InputStream inputStream = con.getInputStream ();
        InputStreamReader inputStreamReader = new InputStreamReader (inputStream);
        BufferedReader reader = new BufferedReader (inputStreamReader);
        StringBuilder builder = new StringBuilder ();
        // 将html文档格式化为单行文本
        String line = null;
        while ((line = reader.readLine ()) != null) {
            builder.append (line);
        }
        JSONObject json = JSONObject.parseObject (builder.toString ());
        return json.getJSONArray ("data");
    }

    public void saveSearchData(JSONObject object){
        WechatPublic wechatPublic = new WechatPublic ();
        wechatPublic.setName (object.getString("name"));
        wechatPublic.setCertifiedText (object.getString("certifiedText"));
        wechatPublic.setAccount (object.getString("account"));
        wechatPublic.setTags (object.getString("tags"));
        wechatPublic.setType (object.getString("type"));
        wechatPublic.setCodeImageUrl (object.getString ("codeImageUrl"));
        wechatPublic.setDescription (object.getString("description"));
        wechatPublic.setHeadImageUrl (object.getString("headImageUrl"));
        wechatPublic.setWxid (object.getString("wxId"));
        WechatPublic old = wechatPublicService.selectByName (wechatPublic.getName ());
        if(old != null){
            wechatPublic.setId(old.getId());
            wechatPublicService.updateSelective (wechatPublic);
        } else {

            wechatPublicService.insert (wechatPublic);
        }
    }

    public void rankExtractAll() throws IOException {
        String[] rankNameArr = {"民生","时事","政务"};
        //rankExtractByMonth (rankNameArr);
        rankExtractByWeek (rankNameArr);
        rankExtractByDay(rankNameArr);
    }

    public void rankExtractByMonth(String[] rankNameArr)throws IOException {
        String start;
        String end;
        DateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance ();
        for(int i=1;i<4;i++) {
            calendar.add (Calendar.MONTH, -1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            start = dateFormat.format (calendar.getTime ());
            calendar.set (Calendar.DAY_OF_MONTH, calendar.getActualMaximum (Calendar.DAY_OF_MONTH));
            end = dateFormat.format (calendar.getTime ());
            for (String rankName : rankNameArr) {
                String url = "https://www.newrank.cn/xdnphb/list/month/rank?end=" + end + "&&rank_name=" + URLEncoder.encode (rankName, "utf-8") + "&&rank_name_group=" + URLEncoder.encode ("资讯", "utf-8") + "&&start=" + start + "&&nonce=11e8e228b&&xyz=ce317fc97632c42c1a6a79f1b64580b1";
                rankExtract (url, rankName);
            }
        }
    }

    public void rankExtractByWeek(String[] rankNameArr)throws IOException {
        String start;
        String end;
        DateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance ();
        calendar.set(Calendar.DAY_OF_WEEK, 2);
        for(int i=1;i<5;i++) {
            calendar.add (Calendar.DAY_OF_MONTH, -7);
            start = dateFormat.format (calendar.getTime ());
            calendar.add (Calendar.DAY_OF_MONTH, 6);
            end = dateFormat.format (calendar.getTime ());
            calendar.add (Calendar.DAY_OF_MONTH, -6);
            for (String rankName : rankNameArr) {
                String url = "https://www.newrank.cn/xdnphb/list/week/rank?end=" + end + "&&rank_name=" + URLEncoder.encode (rankName, "utf-8") + "&&rank_name_group=" + URLEncoder.encode ("资讯", "utf-8") + "&&start=" + start + "&&nonce=11e8e228b&&xyz=ce317fc97632c42c1a6a79f1b64580b1";
                rankExtract (url, rankName);
            }
        }
    }

    public void rankExtractByDay(String[] rankNameArr)throws IOException {
        String start;
        String end;
        DateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance ();
        calendar.add (Calendar.DAY_OF_MONTH, -1);
        for(int i=1;i<8;i++) {
            calendar.add (Calendar.DAY_OF_MONTH, -1);
            start = dateFormat.format (calendar.getTime ());
            end = start;
            for (String rankName : rankNameArr) {
                String url = "https://www.newrank.cn/xdnphb/list/day/rank?end=" + end + "&&rank_name=" + URLEncoder.encode (rankName, "utf-8") + "&&rank_name_group=" + URLEncoder.encode ("资讯", "utf-8") + "&&start=" + start + "&&nonce=11e8e228b&&xyz=ce317fc97632c42c1a6a79f1b64580b1";
                rankExtract (url, rankName);
            }
        }
    }

    public void rankExtract(String urlStr, String rankName) throws IOException {
        URL url = new URL (urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection ();
        con.setRequestMethod ("POST");
        con.setRequestProperty ("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        InputStream inputStream = con.getInputStream ();
        InputStreamReader inputStreamReader = new InputStreamReader (inputStream);
        BufferedReader reader = new BufferedReader (inputStreamReader);
        StringBuilder builder = new StringBuilder ();
        // 将html文档格式化为单行文本
        String line;
        while ((line = reader.readLine ()) != null) {
            builder.append (line);
        }
        JSONObject res = JSONObject.parseObject (builder.toString ());
        if("[]".equals (res.getString ("value"))){
            return;
        }
        JSONObject value = res.getJSONObject ("value");
        if(value == null || value.size () == 0){
            return;
        }
        JSONArray data = value.getJSONArray ("datas");
        if(data == null || data.size () == 0){
            return;
        }
        for(int j=0;j<data.size ();j++){
            saveDataForRank (rankName, data.getJSONObject (j));
        }
    }

    public void saveDataForRank(String type, JSONObject object){
        WechatPublic wechatPublic = new WechatPublic ();
        wechatPublic.setName (object.getString("name"));
        wechatPublic.setAccount (object.getString("account"));
        wechatPublic.setTags ("[\""+type+"\"]");
        wechatPublic.setType (type);
        wechatPublic.setCodeImageUrl ("https://open.weixin.qq.com/qr/code?username="+wechatPublic.getAccount ());
        wechatPublic.setHeadImageUrl (object.getString("head_image_url"));
        WechatPublic old = wechatPublicService.selectByName (wechatPublic.getName ());
        if(old != null){
            wechatPublic.setId(old.getId());
            wechatPublicService.updateSelective (wechatPublic);
        } else {

            wechatPublicService.insert (wechatPublic);
        }
    }

}
