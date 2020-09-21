package com.baomidou.mybatis3.domain;


import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Accessors(chain = true)
@Data
@Table(name = "t_blog")
public class JpaBlog {
    @Id
   private Long id;
   private String title;
   private String content;
   private BigDecimal money;
   private Integer age;
   @Column(name = "create_time")
   private Date createTime;



}
