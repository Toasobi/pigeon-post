package com.seeing.pigeon.cron.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.seeing.pigeon.common.enums.RespStatusEnum;
import com.seeing.pigeon.common.vo.BasicResultVO;
import com.seeing.pigeon.cron.service.CronTaskService;
import com.seeing.pigeon.cron.xxl.constants.XxlJobConstant;
import com.seeing.pigeon.cron.xxl.entity.XxlJobGroup;
import com.seeing.pigeon.cron.xxl.entity.XxlJobInfo;
import com.xxl.job.core.biz.model.ReturnT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Slf4j
@Service
public class CronTaskServiceImpl implements CronTaskService {

    @Value("${xxl.job.admin.username}")
    private String xxlUserName;

    @Value("${xxl.job.admin.password}")
    private String xxlPassword;

    @Value("${xxl.job.admin.addresses}")
    private String xxlAddresses;


    @Override
    public BasicResultVO saveCronTask(XxlJobInfo xxlJobInfo) {
        // 将 xxlJobInfo 对象转换为 Map<String, Object> 类型的参数
        Map<String, Object> params = JSON.parseObject(JSON.toJSONString(xxlJobInfo), Map.class);
        // 根据 xxlJobInfo 的 id 判断是插入操作还是更新操作，决定请求的URL
        String path = Objects.isNull(xxlJobInfo.getId()) ? xxlAddresses + XxlJobConstant.INSERT_URL
                : xxlAddresses + XxlJobConstant.UPDATE_URL;

        HttpResponse response;
        ReturnT returnT = null;
        try {
            // 发送 HTTP POST 请求，带上参数和Cookie
            response = HttpRequest.post(path).form(params).cookie(getCookie()).execute();
            // 将响应体解析为 ReturnT 对象
            returnT = JSON.parseObject(response.body(), ReturnT.class);

            // 判断响应是否成功
            // 插入时需要返回Id，而更新时不需要
            if (response.isOk() && ReturnT.SUCCESS_CODE == returnT.getCode()) {
                if (path.contains(XxlJobConstant.INSERT_URL)) {
                    Integer taskId = Integer.parseInt(String.valueOf(returnT.getContent()));
                    return BasicResultVO.success(taskId);
                    // 如果是更新操作，返回成功消息
                } else if (path.contains(XxlJobConstant.UPDATE_URL)) {
                    return BasicResultVO.success();
                }
            }
        } catch (Exception e) {
            log.error("CronTaskService#saveTask fail,e:{},param:{},response:{}", Throwables.getStackTraceAsString(e)
                    , JSON.toJSONString(xxlJobInfo), JSON.toJSONString(returnT));
        }
        // 如果操作失败，返回失败消息
        return BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR, JSON.toJSONString(returnT));
    }

    @Override
    public BasicResultVO deleteCronTask(Integer taskId) {
        // 1. 构建请求 URL
        String path = xxlAddresses + XxlJobConstant.DELETE_URL;

        // 2. 创建请求参数
        HashMap<String, Object> params = MapUtil.newHashMap();
        params.put("id", taskId);

        // 3. 声明响应对象和返回对象
        HttpResponse response;
        ReturnT returnT = null;
        try {
            // 4. 发送 POST 请求
            response = HttpRequest.post(path).form(params).cookie(getCookie()).execute();
            // 5. 解析响应
            returnT = JSON.parseObject(response.body(), ReturnT.class);
            if (response.isOk() && ReturnT.SUCCESS_CODE == returnT.getCode()) {
                return BasicResultVO.success();
            }
        } catch (Exception e) {
            log.error("CronTaskService#deleteCronTask fail,e:{},param:{},response:{}", Throwables.getStackTraceAsString(e)
                    , JSON.toJSONString(params), JSON.toJSONString(returnT));
        }
        return BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR, JSON.toJSONString(returnT));
    }

    @Override
    public BasicResultVO startCronTask(Integer taskId) {
        String path = xxlAddresses + XxlJobConstant.RUN_URL;

        HashMap<String, Object> params = MapUtil.newHashMap();
        params.put("id", taskId);

        HttpResponse response;
        ReturnT returnT = null;
        try {
            response = HttpRequest.post(path).form(params).cookie(getCookie()).execute();
            returnT = JSON.parseObject(response.body(), ReturnT.class);
            if (response.isOk() && ReturnT.SUCCESS_CODE == returnT.getCode()) {
                return BasicResultVO.success();
            }
        } catch (Exception e) {
            log.error("CronTaskService#startCronTask fail,e:{},param:{},response:{}", Throwables.getStackTraceAsString(e)
                    , JSON.toJSONString(params), JSON.toJSONString(returnT));
        }
        return BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR, JSON.toJSONString(returnT));
    }

    @Override
    public BasicResultVO stopCronTask(Integer taskId) {
        String path = xxlAddresses + XxlJobConstant.STOP_URL;

        HashMap<String, Object> params = MapUtil.newHashMap();
        params.put("id", taskId);

        HttpResponse response;
        ReturnT returnT = null;
        try {
            response = HttpRequest.post(path).form(params).cookie(getCookie()).execute();
            returnT = JSON.parseObject(response.body(), ReturnT.class);
            if (response.isOk() && ReturnT.SUCCESS_CODE == returnT.getCode()) {
                return BasicResultVO.success();
            }
        } catch (Exception e) {
            log.error("CronTaskService#stopCronTask fail,e:{},param:{},response:{}", Throwables.getStackTraceAsString(e)
                    , JSON.toJSONString(params), JSON.toJSONString(returnT));
        }
        return BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR, JSON.toJSONString(returnT));
    }

    /**
     * 用于根据应用名称和标题获取任务组（执行器）的 ID
     * @param appName
     * @param title
     * @return
     */
    @Override
    public BasicResultVO getGroupId(String appName, String title) {
        String path = xxlAddresses + XxlJobConstant.JOB_GROUP_PAGE_LIST;

        HashMap<String, Object> params = MapUtil.newHashMap();
        params.put("appname", appName);
        params.put("title", title);

        HttpResponse response = null;
        try {
            response = HttpRequest.post(path).form(params).cookie(getCookie()).execute();
            Integer id = JSON.parseObject(response.body()).getJSONArray("data").getJSONObject(0).getInteger("id");
            if (response.isOk() && Objects.nonNull(id)) {
                return BasicResultVO.success(id);
            }
        } catch (Exception e) {
            log.error("CronTaskService#getGroupId fail,e:{},param:{},response:{}", Throwables.getStackTraceAsString(e)
                    , JSON.toJSONString(params), JSON.toJSONString(response.body()));
        }
        return BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR, JSON.toJSONString(response.body()));
    }

    @Override
    public BasicResultVO createGroup(XxlJobGroup xxlJobGroup) {
        Map<String, Object> params = JSON.parseObject(JSON.toJSONString(xxlJobGroup), Map.class);
        String path = xxlAddresses + XxlJobConstant.JOB_GROUP_INSERT_URL;

        HttpResponse response;
        ReturnT returnT = null;

        try {
            response = HttpRequest.post(path).form(params).cookie(getCookie()).execute();
            returnT = JSON.parseObject(response.body(), ReturnT.class);
            if (response.isOk() && ReturnT.SUCCESS_CODE == returnT.getCode()) {
                return BasicResultVO.success();
            }
        } catch (Exception e) {
            log.error("CronTaskService#createGroup fail,e:{},param:{},response:{}", Throwables.getStackTraceAsString(e)
                    , JSON.toJSONString(params), JSON.toJSONString(returnT));
        }
        return BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR, JSON.toJSONString(returnT));
    }

    /**
     * 获取xxl cookie
     *
     * @return String
     */
    private String getCookie() {
        Map<String, Object> params = MapUtil.newHashMap();
        params.put("userName", xxlUserName);
        params.put("password", xxlPassword);
        params.put("randomCode", IdUtil.fastSimpleUUID());
        //构建请求 URL
        String path = xxlAddresses + XxlJobConstant.LOGIN_URL;
        HttpResponse response = null;
        try {
            response = HttpRequest.post(path).form(params).execute();
            if (response.isOk()) {
                //获取并拼接返回的 Cookie
                List<HttpCookie> cookies = response.getCookies();
                StringBuilder sb = new StringBuilder();
                for (HttpCookie cookie : cookies) {
                    sb.append(cookie.toString());
                }
                return sb.toString();
            }
        } catch (Exception e) {
            log.error("CronTaskService#createGroup getCookie,e:{},param:{},response:{}", Throwables.getStackTraceAsString(e)
                    , JSON.toJSONString(params), JSON.toJSONString(response));
        }
        return null;
    }
}
