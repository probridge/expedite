package com.probridge.expedite.dao.expdb;

import com.probridge.expedite.model.expdb.UserRoles;
import com.probridge.expedite.model.expdb.UserRolesExample;
import com.probridge.expedite.model.expdb.UserRolesKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserRolesMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_ROLES
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    int countByExample(UserRolesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_ROLES
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    int deleteByExample(UserRolesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_ROLES
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    int deleteByPrimaryKey(UserRolesKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_ROLES
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    int insert(UserRoles record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_ROLES
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    int insertSelective(UserRoles record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_ROLES
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    List<UserRoles> selectByExample(UserRolesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_ROLES
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    UserRoles selectByPrimaryKey(UserRolesKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_ROLES
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    int updateByExampleSelective(@Param("record") UserRoles record, @Param("example") UserRolesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_ROLES
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    int updateByExample(@Param("record") UserRoles record, @Param("example") UserRolesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_ROLES
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    int updateByPrimaryKeySelective(UserRoles record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_ROLES
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    int updateByPrimaryKey(UserRoles record);
}