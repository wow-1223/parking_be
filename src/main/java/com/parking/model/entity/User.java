package com.parking.model.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String openId;
    private String nickName;
    private String avatarUrl;
    private String phone;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 