package com.dianping.phoenix.lb.deploy.model;

import java.io.Serializable;
import java.util.Date;

public class DeployTask implements Serializable {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column deploy_task.id
     *
     * @mbggenerated
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column deploy_task.name
     *
     * @mbggenerated
     */
    private String name;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column deploy_task.agent_batch
     *
     * @mbggenerated
     */
    private AgentBatch agentBatch;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column deploy_task.error_policy
     *
     * @mbggenerated
     */
    private ErrorPolicy errorPolicy;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column deploy_task.auto_continue
     *
     * @mbggenerated
     */
    private Boolean autoContinue;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column deploy_task.deploy_interval
     *
     * @mbggenerated
     */
    private Integer deployInterval;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column deploy_task.status
     *
     * @mbggenerated
     */
    private DeployTaskStatus status;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column deploy_task.deployed_by
     *
     * @mbggenerated
     */
    private String deployedBy;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column deploy_task.begin_date
     *
     * @mbggenerated
     */
    private Date beginDate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column deploy_task.end_date
     *
     * @mbggenerated
     */
    private Date endDate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column deploy_task.creation_date
     *
     * @mbggenerated
     */
    private Date creationDate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column deploy_task.last_modified_date
     *
     * @mbggenerated
     */
    private Date lastModifiedDate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column deploy_task.state_action
     *
     * @mbggenerated
     */
    private StateAction stateAction;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table deploy_task
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column deploy_task.id
     *
     * @return the value of deploy_task.id
     *
     * @mbggenerated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column deploy_task.id
     *
     * @param id the value for deploy_task.id
     *
     * @mbggenerated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column deploy_task.name
     *
     * @return the value of deploy_task.name
     *
     * @mbggenerated
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column deploy_task.name
     *
     * @param name the value for deploy_task.name
     *
     * @mbggenerated
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column deploy_task.agent_batch
     *
     * @return the value of deploy_task.agent_batch
     *
     * @mbggenerated
     */
    public AgentBatch getAgentBatch() {
        return agentBatch;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column deploy_task.agent_batch
     *
     * @param agentBatch the value for deploy_task.agent_batch
     *
     * @mbggenerated
     */
    public void setAgentBatch(AgentBatch agentBatch) {
        this.agentBatch = agentBatch;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column deploy_task.error_policy
     *
     * @return the value of deploy_task.error_policy
     *
     * @mbggenerated
     */
    public ErrorPolicy getErrorPolicy() {
        return errorPolicy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column deploy_task.error_policy
     *
     * @param errorPolicy the value for deploy_task.error_policy
     *
     * @mbggenerated
     */
    public void setErrorPolicy(ErrorPolicy errorPolicy) {
        this.errorPolicy = errorPolicy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column deploy_task.auto_continue
     *
     * @return the value of deploy_task.auto_continue
     *
     * @mbggenerated
     */
    public Boolean getAutoContinue() {
        return autoContinue;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column deploy_task.auto_continue
     *
     * @param autoContinue the value for deploy_task.auto_continue
     *
     * @mbggenerated
     */
    public void setAutoContinue(Boolean autoContinue) {
        this.autoContinue = autoContinue;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column deploy_task.deploy_interval
     *
     * @return the value of deploy_task.deploy_interval
     *
     * @mbggenerated
     */
    public Integer getDeployInterval() {
        return deployInterval;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column deploy_task.deploy_interval
     *
     * @param deployInterval the value for deploy_task.deploy_interval
     *
     * @mbggenerated
     */
    public void setDeployInterval(Integer deployInterval) {
        this.deployInterval = deployInterval;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column deploy_task.status
     *
     * @return the value of deploy_task.status
     *
     * @mbggenerated
     */
    public DeployTaskStatus getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column deploy_task.status
     *
     * @param status the value for deploy_task.status
     *
     * @mbggenerated
     */
    public void setStatus(DeployTaskStatus status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column deploy_task.deployed_by
     *
     * @return the value of deploy_task.deployed_by
     *
     * @mbggenerated
     */
    public String getDeployedBy() {
        return deployedBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column deploy_task.deployed_by
     *
     * @param deployedBy the value for deploy_task.deployed_by
     *
     * @mbggenerated
     */
    public void setDeployedBy(String deployedBy) {
        this.deployedBy = deployedBy == null ? null : deployedBy.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column deploy_task.begin_date
     *
     * @return the value of deploy_task.begin_date
     *
     * @mbggenerated
     */
    public Date getBeginDate() {
        return beginDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column deploy_task.begin_date
     *
     * @param beginDate the value for deploy_task.begin_date
     *
     * @mbggenerated
     */
    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column deploy_task.end_date
     *
     * @return the value of deploy_task.end_date
     *
     * @mbggenerated
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column deploy_task.end_date
     *
     * @param endDate the value for deploy_task.end_date
     *
     * @mbggenerated
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column deploy_task.creation_date
     *
     * @return the value of deploy_task.creation_date
     *
     * @mbggenerated
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column deploy_task.creation_date
     *
     * @param creationDate the value for deploy_task.creation_date
     *
     * @mbggenerated
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column deploy_task.last_modified_date
     *
     * @return the value of deploy_task.last_modified_date
     *
     * @mbggenerated
     */
    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column deploy_task.last_modified_date
     *
     * @param lastModifiedDate the value for deploy_task.last_modified_date
     *
     * @mbggenerated
     */
    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column deploy_task.state_action
     *
     * @return the value of deploy_task.state_action
     *
     * @mbggenerated
     */
    public StateAction getStateAction() {
        return stateAction;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column deploy_task.state_action
     *
     * @param stateAction the value for deploy_task.state_action
     *
     * @mbggenerated
     */
    public void setStateAction(StateAction stateAction) {
        this.stateAction = stateAction;
    }
}