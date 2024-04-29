package org.example.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.hutool.json.JSONObject;
import org.apache.http.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

/**
 * @author Joe_yoy
 *
 */
public class HttpUtil {

    private static final CloseableHttpClient httpclient = HttpClients.createDefault();
    // 继续
    /**
     * 发送HttpGet请求
     * @param url
     * @return
     */
    public static String sendGet(String url) {

        HttpGet httpget = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpget);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        String result = null;
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    /**
     * 发送HttpGet带参请求
     * @param url
     * @param header
     * @return
     */
    public static String sendGet(String url, Map<String, String> header) {
        HttpGet httpGet = new HttpGet(url);


        //设置头部
        for(Map.Entry entry:header.entrySet()){

            httpGet.setHeader(entry.getKey().toString(),entry.getValue().toString());
        }


        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        String result = null;
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    /**
     * 发送不带参数的HttpPost请求
     * @param url
     * @return
     */
    public static String sendPost(String url) {
        HttpPost httppost = new HttpPost(url);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity = response.getEntity();
        String result = null;
        try {
            result = EntityUtils.toString(entity);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 发送HttpPost请求，参数为map
     * @param url
     * @param map
     * @return
     */
    public static String sendPost(String url, Map<String,String> map) {

        JSONObject json = new JSONObject(map);
        StringEntity entity = new StringEntity(json.toString(), Consts.UTF_8);
        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(entity);     //设置请求体
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity1 = response.getEntity();
        String result = null;
        try {
            result = EntityUtils.toString(entity1);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static String sendPost(String url, com.alibaba.fastjson.JSONObject json) {

        StringEntity entity = new StringEntity(json.toString(), Consts.UTF_8);
        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(entity);     //设置请求体
        System.out.println(entity);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity1 = response.getEntity();
        String result = null;
        try {
            result = EntityUtils.toString(entity1);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String sendPostWithJson(String url, String jsonStr, HashMap<String,String> headers) {
        // 返回的结果
        String jsonResult = "";
        try {
            HttpClient client = new HttpClient();
            // 连接超时
            client.getHttpConnectionManager().getParams().setConnectionTimeout(3*1000);
            // 读取数据超时
            client.getHttpConnectionManager().getParams().setSoTimeout(3*60*1000);
            client.getParams().setContentCharset("UTF-8");
            PostMethod postMethod = new PostMethod(url);

            postMethod.setRequestHeader("content-type", headers.get("content-type"));

            // 非空
            if (null != jsonStr && !"".equals(jsonStr)) {
                StringRequestEntity requestEntity = new StringRequestEntity(jsonStr, headers.get("content-type"), "UTF-8");
                postMethod.setRequestEntity(requestEntity);
            }
            int status = client.executeMethod(postMethod);
            if (status == HttpStatus.SC_OK) {
                jsonResult = postMethod.getResponseBodyAsString();
            } else {
                throw new RuntimeException("接口连接失败！");
            }
        } catch (Exception e) {
            throw new RuntimeException("接口连接失败！");
        }
        return jsonResult;
    }
}


