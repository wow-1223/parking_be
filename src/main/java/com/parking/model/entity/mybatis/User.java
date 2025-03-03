package com.parking.model.entity.mybatis;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.sql.Timestamp;

@Data
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String openId;
    private String phone;
    @TableField(jdbcType = JdbcType.CHAR)
    private String password;
    private String nickName;
    private String avatarUrl;
    private Integer role;
    private Integer status;
    private Integer sourceFrom;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Long deletedAt;
}