package com.probridge.expedite.dao.expdb;

import com.probridge.expedite.model.expdb.Users;
import com.probridge.expedite.model.expdb.UsersExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UsersMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_USERS
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    int countByExample(UsersExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_USERS
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    int deleteByExample(UsersExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_USERS
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    int deleteByPrimaryKey(String userName);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_USERS
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    int insert(Users record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_USERS
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    int insertSelective(Users record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_USERS
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    List<Users> selectByExample(UsersExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_USERS
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    Users selectByPrimaryKey(String userName);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_USERS
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    int updateByExampleSelective(@Param("record") Users record, @Param("example") UsersExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_USERS
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    int updateByExample(@Param("record") Users record, @Param("example") UsersExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_USERS
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    int updateByPrimaryKeySelective(Users record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_USERS
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    int updateByPrimaryKey(Users record);
}