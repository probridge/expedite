package com.probridge.expedite.dao.expdb;

import com.probridge.expedite.model.expdb.RolesExample;
import com.probridge.expedite.model.expdb.RolesKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RolesMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_ROLES
     *
     * @mbggenerated Mon Feb 10 23:16:09 CST 2014
     */
    int countByExample(RolesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_ROLES
     *
     * @mbggenerated Mon Feb 10 23:16:09 CST 2014
     */
    int deleteByExample(RolesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_ROLES
     *
     * @mbggenerated Mon Feb 10 23:16:09 CST 2014
     */
    int deleteByPrimaryKey(RolesKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_ROLES
     *
     * @mbggenerated Mon Feb 10 23:16:09 CST 2014
     */
    int insert(RolesKey record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_ROLES
     *
     * @mbggenerated Mon Feb 10 23:16:09 CST 2014
     */
    int insertSelective(RolesKey record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_ROLES
     *
     * @mbggenerated Mon Feb 10 23:16:09 CST 2014
     */
    List<RolesKey> selectByExample(RolesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_ROLES
     *
     * @mbggenerated Mon Feb 10 23:16:09 CST 2014
     */
    int updateByExampleSelective(@Param("record") RolesKey record, @Param("example") RolesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_ROLES
     *
     * @mbggenerated Mon Feb 10 23:16:09 CST 2014
     */
    int updateByExample(@Param("record") RolesKey record, @Param("example") RolesExample example);
}