package org.pf9.pangu.boilerplate.service;

import com.github.pagehelper.Page;
import org.pf9.pangu.boilerplate.entity.Menu;
import org.pf9.pangu.boilerplate.mapper.MenuMapper;
import org.pf9.pangu.boilerplate.service.dto.MenuDTO;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesQuery;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesResult;
import org.pf9.pangu.framework.data.mybatis.dto.PaginationQuery;
import org.pf9.pangu.framework.data.service.CreateDefaultService;

import java.util.List;

public interface MenuService extends CreateDefaultService, EntityService<Menu, MenuMapper> {

    MenuDTO getTree(String username, boolean filterDisabled);

    MenuDTO getTree(String username, String parentCode, boolean filterDisabled);

    MenuDTO getDTOTree(String roleCode);

    Page<Menu> getPagedMenuList(PaginationQuery query);

    DataTablesResult<Menu> getTable(DataTablesQuery req);

    int saveOrUpdate(Menu menu);

    void setDisabledById(Long id, boolean disabled);

    List<MenuDTO> getAllMenuByRole(String code);

    MenuDTO getAppMenusByCode(String username, String appCode, boolean filterDisabled);

    void processMenuTree();

    List<Menu> getMenuList(String parentCode, int depth);
}
