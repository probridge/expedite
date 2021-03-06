package com.probridge.expedite.model.expdb;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class UsersExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table EXPDB.EXPEDITE_USERS
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table EXPDB.EXPEDITE_USERS
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table EXPDB.EXPEDITE_USERS
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_USERS
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    public UsersExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_USERS
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_USERS
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_USERS
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_USERS
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_USERS
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_USERS
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_USERS
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_USERS
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_USERS
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPDB.EXPEDITE_USERS
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table EXPDB.EXPEDITE_USERS
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        protected void addCriterionForJDBCDate(String condition, Date value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            addCriterion(condition, new java.sql.Date(value.getTime()), property);
        }

        protected void addCriterionForJDBCDate(String condition, List<Date> values, String property) {
            if (values == null || values.size() == 0) {
                throw new RuntimeException("Value list for " + property + " cannot be null or empty");
            }
            List<java.sql.Date> dateList = new ArrayList<java.sql.Date>();
            Iterator<Date> iter = values.iterator();
            while (iter.hasNext()) {
                dateList.add(new java.sql.Date(iter.next().getTime()));
            }
            addCriterion(condition, dateList, property);
        }

        protected void addCriterionForJDBCDate(String condition, Date value1, Date value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            addCriterion(condition, new java.sql.Date(value1.getTime()), new java.sql.Date(value2.getTime()), property);
        }

        public Criteria andUserNameIsNull() {
            addCriterion("\"user_name\" is null");
            return (Criteria) this;
        }

        public Criteria andUserNameIsNotNull() {
            addCriterion("\"user_name\" is not null");
            return (Criteria) this;
        }

        public Criteria andUserNameEqualTo(String value) {
            addCriterion("\"user_name\" =", value, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameNotEqualTo(String value) {
            addCriterion("\"user_name\" <>", value, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameGreaterThan(String value) {
            addCriterion("\"user_name\" >", value, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameGreaterThanOrEqualTo(String value) {
            addCriterion("\"user_name\" >=", value, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameLessThan(String value) {
            addCriterion("\"user_name\" <", value, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameLessThanOrEqualTo(String value) {
            addCriterion("\"user_name\" <=", value, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameLike(String value) {
            addCriterion("\"user_name\" like", value, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameNotLike(String value) {
            addCriterion("\"user_name\" not like", value, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameIn(List<String> values) {
            addCriterion("\"user_name\" in", values, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameNotIn(List<String> values) {
            addCriterion("\"user_name\" not in", values, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameBetween(String value1, String value2) {
            addCriterion("\"user_name\" between", value1, value2, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameNotBetween(String value1, String value2) {
            addCriterion("\"user_name\" not between", value1, value2, "userName");
            return (Criteria) this;
        }

        public Criteria andUserPasswordIsNull() {
            addCriterion("\"user_password\" is null");
            return (Criteria) this;
        }

        public Criteria andUserPasswordIsNotNull() {
            addCriterion("\"user_password\" is not null");
            return (Criteria) this;
        }

        public Criteria andUserPasswordEqualTo(String value) {
            addCriterion("\"user_password\" =", value, "userPassword");
            return (Criteria) this;
        }

        public Criteria andUserPasswordNotEqualTo(String value) {
            addCriterion("\"user_password\" <>", value, "userPassword");
            return (Criteria) this;
        }

        public Criteria andUserPasswordGreaterThan(String value) {
            addCriterion("\"user_password\" >", value, "userPassword");
            return (Criteria) this;
        }

        public Criteria andUserPasswordGreaterThanOrEqualTo(String value) {
            addCriterion("\"user_password\" >=", value, "userPassword");
            return (Criteria) this;
        }

        public Criteria andUserPasswordLessThan(String value) {
            addCriterion("\"user_password\" <", value, "userPassword");
            return (Criteria) this;
        }

        public Criteria andUserPasswordLessThanOrEqualTo(String value) {
            addCriterion("\"user_password\" <=", value, "userPassword");
            return (Criteria) this;
        }

        public Criteria andUserPasswordLike(String value) {
            addCriterion("\"user_password\" like", value, "userPassword");
            return (Criteria) this;
        }

        public Criteria andUserPasswordNotLike(String value) {
            addCriterion("\"user_password\" not like", value, "userPassword");
            return (Criteria) this;
        }

        public Criteria andUserPasswordIn(List<String> values) {
            addCriterion("\"user_password\" in", values, "userPassword");
            return (Criteria) this;
        }

        public Criteria andUserPasswordNotIn(List<String> values) {
            addCriterion("\"user_password\" not in", values, "userPassword");
            return (Criteria) this;
        }

        public Criteria andUserPasswordBetween(String value1, String value2) {
            addCriterion("\"user_password\" between", value1, value2, "userPassword");
            return (Criteria) this;
        }

        public Criteria andUserPasswordNotBetween(String value1, String value2) {
            addCriterion("\"user_password\" not between", value1, value2, "userPassword");
            return (Criteria) this;
        }

        public Criteria andUserGroupIsNull() {
            addCriterion("\"user_group\" is null");
            return (Criteria) this;
        }

        public Criteria andUserGroupIsNotNull() {
            addCriterion("\"user_group\" is not null");
            return (Criteria) this;
        }

        public Criteria andUserGroupEqualTo(String value) {
            addCriterion("\"user_group\" =", value, "userGroup");
            return (Criteria) this;
        }

        public Criteria andUserGroupNotEqualTo(String value) {
            addCriterion("\"user_group\" <>", value, "userGroup");
            return (Criteria) this;
        }

        public Criteria andUserGroupGreaterThan(String value) {
            addCriterion("\"user_group\" >", value, "userGroup");
            return (Criteria) this;
        }

        public Criteria andUserGroupGreaterThanOrEqualTo(String value) {
            addCriterion("\"user_group\" >=", value, "userGroup");
            return (Criteria) this;
        }

        public Criteria andUserGroupLessThan(String value) {
            addCriterion("\"user_group\" <", value, "userGroup");
            return (Criteria) this;
        }

        public Criteria andUserGroupLessThanOrEqualTo(String value) {
            addCriterion("\"user_group\" <=", value, "userGroup");
            return (Criteria) this;
        }

        public Criteria andUserGroupLike(String value) {
            addCriterion("\"user_group\" like", value, "userGroup");
            return (Criteria) this;
        }

        public Criteria andUserGroupNotLike(String value) {
            addCriterion("\"user_group\" not like", value, "userGroup");
            return (Criteria) this;
        }

        public Criteria andUserGroupIn(List<String> values) {
            addCriterion("\"user_group\" in", values, "userGroup");
            return (Criteria) this;
        }

        public Criteria andUserGroupNotIn(List<String> values) {
            addCriterion("\"user_group\" not in", values, "userGroup");
            return (Criteria) this;
        }

        public Criteria andUserGroupBetween(String value1, String value2) {
            addCriterion("\"user_group\" between", value1, value2, "userGroup");
            return (Criteria) this;
        }

        public Criteria andUserGroupNotBetween(String value1, String value2) {
            addCriterion("\"user_group\" not between", value1, value2, "userGroup");
            return (Criteria) this;
        }

        public Criteria andUserDescriptionIsNull() {
            addCriterion("\"user_description\" is null");
            return (Criteria) this;
        }

        public Criteria andUserDescriptionIsNotNull() {
            addCriterion("\"user_description\" is not null");
            return (Criteria) this;
        }

        public Criteria andUserDescriptionEqualTo(String value) {
            addCriterion("\"user_description\" =", value, "userDescription");
            return (Criteria) this;
        }

        public Criteria andUserDescriptionNotEqualTo(String value) {
            addCriterion("\"user_description\" <>", value, "userDescription");
            return (Criteria) this;
        }

        public Criteria andUserDescriptionGreaterThan(String value) {
            addCriterion("\"user_description\" >", value, "userDescription");
            return (Criteria) this;
        }

        public Criteria andUserDescriptionGreaterThanOrEqualTo(String value) {
            addCriterion("\"user_description\" >=", value, "userDescription");
            return (Criteria) this;
        }

        public Criteria andUserDescriptionLessThan(String value) {
            addCriterion("\"user_description\" <", value, "userDescription");
            return (Criteria) this;
        }

        public Criteria andUserDescriptionLessThanOrEqualTo(String value) {
            addCriterion("\"user_description\" <=", value, "userDescription");
            return (Criteria) this;
        }

        public Criteria andUserDescriptionLike(String value) {
            addCriterion("\"user_description\" like", value, "userDescription");
            return (Criteria) this;
        }

        public Criteria andUserDescriptionNotLike(String value) {
            addCriterion("\"user_description\" not like", value, "userDescription");
            return (Criteria) this;
        }

        public Criteria andUserDescriptionIn(List<String> values) {
            addCriterion("\"user_description\" in", values, "userDescription");
            return (Criteria) this;
        }

        public Criteria andUserDescriptionNotIn(List<String> values) {
            addCriterion("\"user_description\" not in", values, "userDescription");
            return (Criteria) this;
        }

        public Criteria andUserDescriptionBetween(String value1, String value2) {
            addCriterion("\"user_description\" between", value1, value2, "userDescription");
            return (Criteria) this;
        }

        public Criteria andUserDescriptionNotBetween(String value1, String value2) {
            addCriterion("\"user_description\" not between", value1, value2, "userDescription");
            return (Criteria) this;
        }

        public Criteria andUserTypeIsNull() {
            addCriterion("\"user_type\" is null");
            return (Criteria) this;
        }

        public Criteria andUserTypeIsNotNull() {
            addCriterion("\"user_type\" is not null");
            return (Criteria) this;
        }

        public Criteria andUserTypeEqualTo(String value) {
            addCriterion("\"user_type\" =", value, "userType");
            return (Criteria) this;
        }

        public Criteria andUserTypeNotEqualTo(String value) {
            addCriterion("\"user_type\" <>", value, "userType");
            return (Criteria) this;
        }

        public Criteria andUserTypeGreaterThan(String value) {
            addCriterion("\"user_type\" >", value, "userType");
            return (Criteria) this;
        }

        public Criteria andUserTypeGreaterThanOrEqualTo(String value) {
            addCriterion("\"user_type\" >=", value, "userType");
            return (Criteria) this;
        }

        public Criteria andUserTypeLessThan(String value) {
            addCriterion("\"user_type\" <", value, "userType");
            return (Criteria) this;
        }

        public Criteria andUserTypeLessThanOrEqualTo(String value) {
            addCriterion("\"user_type\" <=", value, "userType");
            return (Criteria) this;
        }

        public Criteria andUserTypeLike(String value) {
            addCriterion("\"user_type\" like", value, "userType");
            return (Criteria) this;
        }

        public Criteria andUserTypeNotLike(String value) {
            addCriterion("\"user_type\" not like", value, "userType");
            return (Criteria) this;
        }

        public Criteria andUserTypeIn(List<String> values) {
            addCriterion("\"user_type\" in", values, "userType");
            return (Criteria) this;
        }

        public Criteria andUserTypeNotIn(List<String> values) {
            addCriterion("\"user_type\" not in", values, "userType");
            return (Criteria) this;
        }

        public Criteria andUserTypeBetween(String value1, String value2) {
            addCriterion("\"user_type\" between", value1, value2, "userType");
            return (Criteria) this;
        }

        public Criteria andUserTypeNotBetween(String value1, String value2) {
            addCriterion("\"user_type\" not between", value1, value2, "userType");
            return (Criteria) this;
        }

        public Criteria andUserEnabledIsNull() {
            addCriterion("\"user_enabled\" is null");
            return (Criteria) this;
        }

        public Criteria andUserEnabledIsNotNull() {
            addCriterion("\"user_enabled\" is not null");
            return (Criteria) this;
        }

        public Criteria andUserEnabledEqualTo(String value) {
            addCriterion("\"user_enabled\" =", value, "userEnabled");
            return (Criteria) this;
        }

        public Criteria andUserEnabledNotEqualTo(String value) {
            addCriterion("\"user_enabled\" <>", value, "userEnabled");
            return (Criteria) this;
        }

        public Criteria andUserEnabledGreaterThan(String value) {
            addCriterion("\"user_enabled\" >", value, "userEnabled");
            return (Criteria) this;
        }

        public Criteria andUserEnabledGreaterThanOrEqualTo(String value) {
            addCriterion("\"user_enabled\" >=", value, "userEnabled");
            return (Criteria) this;
        }

        public Criteria andUserEnabledLessThan(String value) {
            addCriterion("\"user_enabled\" <", value, "userEnabled");
            return (Criteria) this;
        }

        public Criteria andUserEnabledLessThanOrEqualTo(String value) {
            addCriterion("\"user_enabled\" <=", value, "userEnabled");
            return (Criteria) this;
        }

        public Criteria andUserEnabledLike(String value) {
            addCriterion("\"user_enabled\" like", value, "userEnabled");
            return (Criteria) this;
        }

        public Criteria andUserEnabledNotLike(String value) {
            addCriterion("\"user_enabled\" not like", value, "userEnabled");
            return (Criteria) this;
        }

        public Criteria andUserEnabledIn(List<String> values) {
            addCriterion("\"user_enabled\" in", values, "userEnabled");
            return (Criteria) this;
        }

        public Criteria andUserEnabledNotIn(List<String> values) {
            addCriterion("\"user_enabled\" not in", values, "userEnabled");
            return (Criteria) this;
        }

        public Criteria andUserEnabledBetween(String value1, String value2) {
            addCriterion("\"user_enabled\" between", value1, value2, "userEnabled");
            return (Criteria) this;
        }

        public Criteria andUserEnabledNotBetween(String value1, String value2) {
            addCriterion("\"user_enabled\" not between", value1, value2, "userEnabled");
            return (Criteria) this;
        }

        public Criteria andUserExpirationIsNull() {
            addCriterion("\"user_expiration\" is null");
            return (Criteria) this;
        }

        public Criteria andUserExpirationIsNotNull() {
            addCriterion("\"user_expiration\" is not null");
            return (Criteria) this;
        }

        public Criteria andUserExpirationEqualTo(Date value) {
            addCriterionForJDBCDate("\"user_expiration\" =", value, "userExpiration");
            return (Criteria) this;
        }

        public Criteria andUserExpirationNotEqualTo(Date value) {
            addCriterionForJDBCDate("\"user_expiration\" <>", value, "userExpiration");
            return (Criteria) this;
        }

        public Criteria andUserExpirationGreaterThan(Date value) {
            addCriterionForJDBCDate("\"user_expiration\" >", value, "userExpiration");
            return (Criteria) this;
        }

        public Criteria andUserExpirationGreaterThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("\"user_expiration\" >=", value, "userExpiration");
            return (Criteria) this;
        }

        public Criteria andUserExpirationLessThan(Date value) {
            addCriterionForJDBCDate("\"user_expiration\" <", value, "userExpiration");
            return (Criteria) this;
        }

        public Criteria andUserExpirationLessThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("\"user_expiration\" <=", value, "userExpiration");
            return (Criteria) this;
        }

        public Criteria andUserExpirationIn(List<Date> values) {
            addCriterionForJDBCDate("\"user_expiration\" in", values, "userExpiration");
            return (Criteria) this;
        }

        public Criteria andUserExpirationNotIn(List<Date> values) {
            addCriterionForJDBCDate("\"user_expiration\" not in", values, "userExpiration");
            return (Criteria) this;
        }

        public Criteria andUserExpirationBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("\"user_expiration\" between", value1, value2, "userExpiration");
            return (Criteria) this;
        }

        public Criteria andUserExpirationNotBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("\"user_expiration\" not between", value1, value2, "userExpiration");
            return (Criteria) this;
        }

        public Criteria andUserPwdExpirationIsNull() {
            addCriterion("\"user_pwd_expiration\" is null");
            return (Criteria) this;
        }

        public Criteria andUserPwdExpirationIsNotNull() {
            addCriterion("\"user_pwd_expiration\" is not null");
            return (Criteria) this;
        }

        public Criteria andUserPwdExpirationEqualTo(Date value) {
            addCriterionForJDBCDate("\"user_pwd_expiration\" =", value, "userPwdExpiration");
            return (Criteria) this;
        }

        public Criteria andUserPwdExpirationNotEqualTo(Date value) {
            addCriterionForJDBCDate("\"user_pwd_expiration\" <>", value, "userPwdExpiration");
            return (Criteria) this;
        }

        public Criteria andUserPwdExpirationGreaterThan(Date value) {
            addCriterionForJDBCDate("\"user_pwd_expiration\" >", value, "userPwdExpiration");
            return (Criteria) this;
        }

        public Criteria andUserPwdExpirationGreaterThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("\"user_pwd_expiration\" >=", value, "userPwdExpiration");
            return (Criteria) this;
        }

        public Criteria andUserPwdExpirationLessThan(Date value) {
            addCriterionForJDBCDate("\"user_pwd_expiration\" <", value, "userPwdExpiration");
            return (Criteria) this;
        }

        public Criteria andUserPwdExpirationLessThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("\"user_pwd_expiration\" <=", value, "userPwdExpiration");
            return (Criteria) this;
        }

        public Criteria andUserPwdExpirationIn(List<Date> values) {
            addCriterionForJDBCDate("\"user_pwd_expiration\" in", values, "userPwdExpiration");
            return (Criteria) this;
        }

        public Criteria andUserPwdExpirationNotIn(List<Date> values) {
            addCriterionForJDBCDate("\"user_pwd_expiration\" not in", values, "userPwdExpiration");
            return (Criteria) this;
        }

        public Criteria andUserPwdExpirationBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("\"user_pwd_expiration\" between", value1, value2, "userPwdExpiration");
            return (Criteria) this;
        }

        public Criteria andUserPwdExpirationNotBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("\"user_pwd_expiration\" not between", value1, value2, "userPwdExpiration");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table EXPDB.EXPEDITE_USERS
     *
     * @mbggenerated do_not_delete_during_merge Mon Jul 14 16:17:18 CST 2014
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table EXPDB.EXPEDITE_USERS
     *
     * @mbggenerated Mon Jul 14 16:17:18 CST 2014
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}