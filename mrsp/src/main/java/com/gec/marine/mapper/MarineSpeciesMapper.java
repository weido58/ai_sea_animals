package com.gec.marine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gec.marine.entity.MarineSpecies;
import org.apache.ibatis.annotations.Mapper;

/**
 * 海洋生物数据访问层
 */
@Mapper
public interface MarineSpeciesMapper extends BaseMapper<MarineSpecies> {
}
