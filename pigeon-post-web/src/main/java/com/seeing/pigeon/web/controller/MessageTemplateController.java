package com.seeing.pigeon.web.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.seeing.pigeon.common.enums.RespStatusEnum;
import com.seeing.pigeon.common.vo.BasicResultVO;
import com.seeing.pigeon.service.api.service.SendService;
import com.seeing.pigeon.web.service.MessageTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.seeing.pigeon.web.exception.CommonException;

import java.io.File;
import java.util.HashMap;


@Slf4j
@RestController
@RequestMapping("/messageTemplate")
public class MessageTemplateController {

    @Autowired
    private SendService sendService;

    @Autowired
    private MessageTemplateService messageTemplateService;

    @Value("${pigeon.business.upload.crowd.path}")
    private String dataPath;

    /**
     * 启动模板的定时任务
     */
    @PostMapping("start/{id}")
//    @ApiOperation("/启动模板的定时任务")
    public BasicResultVO start(@RequestBody @PathVariable("id") Long id) {
        return messageTemplateService.startCronTask(id);
    }

    /**
     * 暂停模板的定时任务
     */
    @PostMapping("stop/{id}")
//    @ApiOperation("/暂停模板的定时任务")
    public BasicResultVO stop(@RequestBody @PathVariable("id") Long id) {
        return messageTemplateService.stopCronTask(id);
    }

    /**
     * 上传人群文件
     */
    @PostMapping("upload")
//    @ApiOperation("/上传人群文件")
    public HashMap<Object, Object> upload(@RequestParam("file") MultipartFile file) {
        String filePath = dataPath + IdUtil.fastSimpleUUID() + file.getOriginalFilename();
        try {
            File localFile = new File(filePath);
            if (!localFile.exists()) {
                localFile.mkdirs();
            }
            file.transferTo(localFile);
        } catch (Exception e) {
            log.error("MessageTemplateController#upload fail! e:{},params{}", Throwables.getStackTraceAsString(e), JSON.toJSONString(file));
            throw new CommonException(RespStatusEnum.SERVICE_ERROR);
        }
        return MapUtil.of(new String[][]{{"value", filePath}});
    }
}
