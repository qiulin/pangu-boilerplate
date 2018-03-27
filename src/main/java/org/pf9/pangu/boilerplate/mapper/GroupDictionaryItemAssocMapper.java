package org.pf9.pangu.boilerplate.mapper;

import org.pf9.pangu.boilerplate.entity.DictionaryItem;
import org.pf9.pangu.boilerplate.entity.GroupDictionaryItemAssoc;
import org.pf9.pangu.boilerplate.util.MapperBase;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupDictionaryItemAssocMapper extends MapperBase<GroupDictionaryItemAssoc>{
        List<DictionaryItem> getProductByGroup(String code);
}
