package com.dianping.phoenix.lb.deploy.dao;

import com.dianping.phoenix.lb.deploy.model.Deployment;
import com.dianping.phoenix.lb.deploy.model.DeploymentExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface DeploymentMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table deployment
     *
     * @mbggenerated
     */
    int countByExample(DeploymentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table deployment
     *
     * @mbggenerated
     */
    int deleteByExample(DeploymentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table deployment
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table deployment
     *
     * @mbggenerated
     */
    int insert(Deployment record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table deployment
     *
     * @mbggenerated
     */
    int insertSelective(Deployment record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table deployment
     *
     * @mbggenerated
     */
    List<Deployment> selectByExampleWithRowbounds(DeploymentExample example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table deployment
     *
     * @mbggenerated
     */
    List<Deployment> selectByExample(DeploymentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table deployment
     *
     * @mbggenerated
     */
    Deployment selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table deployment
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") Deployment record, @Param("example") DeploymentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table deployment
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") Deployment record, @Param("example") DeploymentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table deployment
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(Deployment record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table deployment
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(Deployment record);
}