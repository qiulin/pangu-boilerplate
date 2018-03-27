package org.pf9.pangu.boilerplate.util;

import tk.mybatis.mapper.common.BaseMapper;
import tk.mybatis.mapper.common.ConditionMapper;
import tk.mybatis.mapper.common.RowBoundsMapper;

public interface MapperBase<T> extends BaseMapper<T>, ConditionMapper<T>, RowBoundsMapper<T> {
}
