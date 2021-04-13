package com.baomidou.mybatis3.mapper

import com.baomidou.mybatis3.domain.Blog

interface KotlinMapper {
    fun selectById(id: Long?): Blog?
}
