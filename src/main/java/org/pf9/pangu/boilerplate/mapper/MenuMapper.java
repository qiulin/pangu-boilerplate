package org.pf9.pangu.boilerplate.mapper;


import org.pf9.pangu.boilerplate.entity.Menu;
import org.pf9.pangu.boilerplate.service.dto.MenuDTO;
import org.pf9.pangu.boilerplate.util.MapperBase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuMapper extends MapperBase<Menu> {
    void updateMenuById(Menu menu);


    List<MenuDTO> getAllMenuByRole(@Param("roleCode") String roleCode);

    List<Menu> getAllMenusByRole(@Param("roleCode") String roleCode);
}
