package org.pf9.pangu.boilerplate.rest;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import org.pf9.pangu.boilerplate.entity.Menu;
import org.pf9.pangu.boilerplate.service.MenuService;
import org.pf9.pangu.boilerplate.service.dto.MenuDTO;
import org.pf9.pangu.boilerplate.util.MenuUtil;
import org.pf9.pangu.framework.auth.security.SecurityUtils;
import org.pf9.pangu.framework.common.exception.PanguException;
import org.pf9.pangu.framework.data.domain.ResponseResult;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesQuery;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesResult;
import org.pf9.pangu.framework.data.mybatis.dto.PaginationQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/menu")
public class MenuResource {

    @Autowired
    private MenuService menuService;

    private Logger log = LoggerFactory.getLogger(MenuResource.class);

    @GetMapping("init")
    public ResponseEntity initMenu() {
        try {
            menuService.defaultCreate();
            return ResponseEntity.ok().build();
        } catch (PanguException ex) {
            return ResponseEntity.badRequest().body(ex);
        }
    }

    @GetMapping("tree")
    public ResponseEntity<MenuDTO> treeMenu(@RequestParam(value = "disabled", required = false, defaultValue = "false") boolean filterDisabled,
                                            @RequestParam(value = "root", required = false, defaultValue = "root") String root) {
        String username = SecurityUtils.getCurrentUserLogin();
        MenuDTO tree = menuService.getTree(username, root, filterDisabled);

        log.debug(tree.toString());

        return ResponseEntity.ok().body(tree);
    }

    //get  
    @GetMapping("list")
    public ResponseEntity list(@ModelAttribute @Valid PaginationQuery query) {
        Page<Menu> pagedMenu = menuService.getPagedMenuList(query);

        return ResponseEntity.ok().body(new PageInfo<>(pagedMenu));
    }

    @GetMapping("getByParent")
    public ResponseEntity getByParent(@RequestParam(value = "parentCode", defaultValue = "root") String parentCode,
                                      @RequestParam(value = "treeLevel", defaultValue = "100") int treeLevel) {
        List<Menu> menuList = menuService.getMenuList(parentCode, treeLevel);

        return ResponseEntity.ok(menuList);
    }

    @GetMapping("table")
    public DataTablesResult table(@ModelAttribute @Valid DataTablesQuery query) {

        return menuService.getTable(query);
    }

    @PostMapping("save")
    public ResponseEntity save(MenuDTO menuDTO) {

        Menu menu = MenuUtil.convertToEntity(menuDTO);

        try {

            int result = menuService.saveOrUpdate(menu);

            menuService.processMenuTree();

            return ResponseEntity.ok().body(ResponseResult.success());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.ok().body(ResponseResult.failure().message("保存菜单失败"));
        }
    }

    @PostMapping("delete")
    public ResponseEntity delete(@RequestBody MenuDTO dto) {

        Menu menu = MenuUtil.convertToEntity(dto);

        try {

            Optional<Menu> old = menuService.findById(menu.getId());
            if (old.isPresent())
                menuService.deleteById(menu.getId());

            return ResponseEntity.ok().body(ResponseResult.success());
        } catch (Exception ex) {
            return ResponseEntity.ok().body(ResponseResult.failure().message("删除菜单失败"));
        }
    }

    @PostMapping("disable")
    public ResponseEntity disable(@RequestBody HashMap req) {

        try {
//            menuService.setDisabledById(id, true);
            menuService.setDisabledById(Long.valueOf(req.get("id").toString()), true);

            return ResponseEntity.ok().body(ResponseResult.success());
        } catch (Exception ex) {
            return ResponseEntity.ok().body(ResponseResult.failure().message("停用菜单失败"));
        }
    }

    @PostMapping("enable")
    public ResponseEntity enable(@RequestBody HashMap req) {

        try {
            menuService.setDisabledById(Long.valueOf(req.get("id").toString()), false);

            return ResponseEntity.ok().body(ResponseResult.success());
        } catch (Exception ex) {
            return ResponseEntity.ok().body(ResponseResult.failure().message("启用菜单失败"));
        }
    }

    @GetMapping("processMenuTree")
    public ResponseResult processMenuTree() {
        menuService.processMenuTree();

        return ResponseResult.success();
    }

    @GetMapping("get")
    public ResponseEntity get(@RequestParam(value = "id", required = false) Long id,
                              @RequestParam(value = "code", required = false) String code) {

        if (id != null) {
            Optional<Menu> _menu = menuService.findById(id);

            return _menu.<ResponseEntity>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        }

        if (code != null && !code.equals(StringUtils.EMPTY)) {

            List<Menu> _menu = menuService.findByCode(code);
            return ResponseEntity.ok(_menu.get(0));
        }

        return ResponseEntity.notFound().build();
    }
}
