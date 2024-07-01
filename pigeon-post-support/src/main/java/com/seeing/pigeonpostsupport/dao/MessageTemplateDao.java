package com.seeing.pigeonpostsupport.dao;

import com.seeing.pigeonpostsupport.domain.MessageTemplate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 消息模板Dao
 * @author zengxw
 */
public interface MessageTemplateDao extends JpaRepository<MessageTemplate, Long> {


    /**
     * 查询 列表（分页)
     * @param deleted  0：未删除 1：删除
     * @param pageable 分页对象
     * @return
     */
    List<MessageTemplate> findAllByIsDeletedEquals(Integer deleted, Pageable pageable);


    /**
     * 统计未删除的条数
     * @param deleted
     * @return
     */
    Long countByIsDeletedEquals(Integer deleted);

}
