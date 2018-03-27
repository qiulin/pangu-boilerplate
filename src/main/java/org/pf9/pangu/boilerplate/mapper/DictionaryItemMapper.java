package org.pf9.pangu.boilerplate.mapper;

import org.pf9.pangu.boilerplate.entity.DictionaryItem;
import org.pf9.pangu.boilerplate.util.MapperBase;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DictionaryItemMapper extends MapperBase<DictionaryItem>{

    List<DictionaryItem> getProductByGroupCode(String groupCode);
}
