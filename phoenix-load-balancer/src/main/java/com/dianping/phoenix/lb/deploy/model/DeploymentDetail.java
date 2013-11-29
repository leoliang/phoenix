package com.dianping.phoenix.lb.deploy.model;

import java.io.Serializable;
import java.util.Date;

public class DeploymentDetail implements Serializable {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column deployment_detail.id
     *
     * @mbggenerated
     */
    private Integer id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column deployment_detail.deploy_id
     *
     * @mbggenerated
     */
    private Integer deployId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column deployment_detail.ip_address
     *
     * @mbggenerated
     */
    private String ipAddress;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column deployment_detail.status
     *
     * @mbggenerated
     */
    private Integer status;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column deployment_detail.begin_date
     *
     * @mbggenerated
     */
    private Date beginDate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column deployment_detail.end_date
     *
     * @mbggenerated
     */
    private Date endDate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column deployment_detail.creation_date
     *
     * @mbggenerated
     */
    private Date creationDate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column deployment_detail.last_modified_date
     *
     * @mbggenerated
     */
    private Date lastModifiedDate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column deployment_detail.raw_log
     *
     * @mbggenerated
     */
    private String rawLog;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table deployment_detail
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column deployment_detail.id
     *
     * @return the value of deployment_detail.id
     *
     * @mbggenerated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column deployment_detail.id
     *
     * @param id the value for deployment_detail.id
     *
     * @mbggenerated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column deployment_detail.deploy_id
     *
     * @return the value of deployment_detail.deploy_id
     *
     * @mbggenerated
     */
    public Integer getDeployId() {
        return deployId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column deployment_detail.deploy_id
     *
     * @param deployId the value for deployment_detail.deploy_id
     *
     * @mbggenerated
     */
    public void setDeployId(Integer deployId) {
        this.deployId = deployId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column deployment_detail.ip_address
     *
     * @return the value of deployment_detail.ip_address
     *
     * @mbggenerated
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column deployment_detail.ip_address
     *
     * @param ipAddress the value for deployment_detail.ip_address
     *
     * @mbggenerated
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress == null ? null : ipAddress.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column deployment_detail.status
     *
     * @return the value of deployment_detail.status
     *
     * @mbggenerated
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column deployment_detail.status
     *
     * @param status the value for deployment_detail.status
     *
     * @mbggenerated
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column deployment_detail.begin_date
     *
     * @return the value of deployment_detail.begin_date
     *
     * @mbggenerated
     */
    public Date getBeginDate() {
        return beginDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column deployment_detail.begin_date
     *
     * @param beginDate the value for deployment_detail.begin_date
     *
     * @mbggenerated
     */
    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column deployment_detail.end_date
     *
     * @return the value of deployment_detail.end_date
     *
     * @mbggenerated
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column deployment_detail.end_date
     *
     * @param endDate the value for deployment_detail.end_date
     *
     * @mbggenerated
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column deployment_detail.creation_date
     *
     * @return the value of deployment_detail.creation_date
     *
     * @mbggenerated
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column deployment_detail.creation_date
     *
     * @param creationDate the value for deployment_detail.creation_date
     *
     * @mbggenerated
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column deployment_detail.last_modified_date
     *
     * @return the value of deployment_detail.last_modified_date
     *
     * @mbggenerated
     */
    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column deployment_detail.last_modified_date
     *
     * @param lastModifiedDate the value for deployment_detail.last_modified_date
     *
     * @mbggenerated
     */
    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column deployment_detail.raw_log
     *
     * @return the value of deployment_detail.raw_log
     *
     * @mbggenerated
     */
    public String getRawLog() {
        return rawLog;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column deployment_detail.raw_log
     *
     * @param rawLog the value for deployment_detail.raw_log
     *
     * @mbggenerated
     */
    public void setRawLog(String rawLog) {
        this.rawLog = rawLog == null ? null : rawLog.trim();
    }
}