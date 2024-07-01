package com.seeing.pigeonpostsupport.dao;

import com.seeing.pigeonpostsupport.domain.SmsRecord;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SmsRecordDao extends CrudRepository<SmsRecord, Long> {

    /**
     * 通过日期和手机号找到发送记录
     *
     * @param phone
     * @param sendDate
     * @return
     */
    List<SmsRecord> findByPhoneAndSendDate(Long phone, Integer sendDate);
}

