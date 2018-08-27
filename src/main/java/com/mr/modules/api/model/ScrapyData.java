package com.mr.modules.api.model;

import com.mr.common.base.model.BaseEntity;
import java.util.Date;
import javax.persistence.*;

@Table(name = "scrapy_data")
public class ScrapyData extends BaseEntity {
    /*@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;*/

    /**
     * url
     */
    private String url;

    /**
     * 数据来源{工信部，商务部，海关}
     */
    private String source;

    /**
     * 本条记录创建时间
     */
    //@Column(name = "created_at")
    private Date createdAt;

    /**
     * url的md5结果（如有附件，则保存在此目录中）
     */
    //@Column(name = "hash_key")
    private String hashKey;

    /**
     * 附件类型（pdf,doc,xls,jpg,tiff...）
     */
    //@Column(name = "attachment_type")
    private String attachmentType;

    /**
     * 正文html
     */
    private String html;

    /**
     * 正文text，提取到的正文
     */
    private String text;

    /**
     * 提取到的关键数据
     */
    private String fields;

    /**
     * @return id
     */
    /*public Integer getId() {
        return id;
    }*/

    /**
     * @param id
     */
    /*public void setId(Integer id) {
        this.id = id;
    }*/

    /**
     * 获取url
     *
     * @return url - url
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置url
     *
     * @param url url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取数据来源{工信部，商务部，海关}
     *
     * @return source - 数据来源{工信部，商务部，海关}
     */
    public String getSource() {
        return source;
    }

    /**
     * 设置数据来源{工信部，商务部，海关}
     *
     * @param source 数据来源{工信部，商务部，海关}
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * 获取本条记录创建时间
     *
     * @return created_at - 本条记录创建时间
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置本条记录创建时间
     *
     * @param createdAt 本条记录创建时间
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取url的md5结果（如有附件，则保存在此目录中）
     *
     * @return hash_key - url的md5结果（如有附件，则保存在此目录中）
     */
    public String getHashKey() {
        return hashKey;
    }

    /**
     * 设置url的md5结果（如有附件，则保存在此目录中）
     *
     * @param hashKey url的md5结果（如有附件，则保存在此目录中）
     */
    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }

    /**
     * 获取附件类型（pdf,doc,xls,jpg,tiff...）
     *
     * @return attachment_type - 附件类型（pdf,doc,xls,jpg,tiff...）
     */
    public String getAttachmentType() {
        return attachmentType;
    }

    /**
     * 设置附件类型（pdf,doc,xls,jpg,tiff...）
     *
     * @param attachmentType 附件类型（pdf,doc,xls,jpg,tiff...）
     */
    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    /**
     * 获取正文html
     *
     * @return html - 正文html
     */
    public String getHtml() {
        return html;
    }

    /**
     * 设置正文html
     *
     * @param html 正文html
     */
    public void setHtml(String html) {
        this.html = html;
    }

    /**
     * 获取正文text，提取到的正文
     *
     * @return text - 正文text，提取到的正文
     */
    public String getText() {
        return text;
    }

    /**
     * 设置正文text，提取到的正文
     *
     * @param text 正文text，提取到的正文
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * 获取提取到的关键数据
     *
     * @return fields - 提取到的关键数据
     */
    public String getFields() {
        return fields;
    }

    /**
     * 设置提取到的关键数据
     *
     * @param fields 提取到的关键数据
     */
    public void setFields(String fields) {
        this.fields = fields;
    }
}