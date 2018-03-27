package org.pf9.pangu.boilerplate.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.pf9.pangu.boilerplate.entity.Menu;
import org.pf9.pangu.boilerplate.entity.Role;
import org.pf9.pangu.boilerplate.entity.User;
import org.pf9.pangu.boilerplate.mapper.MenuMapper;
import org.pf9.pangu.boilerplate.mapper.RoleMapper;
import org.pf9.pangu.boilerplate.mapper.UserMapper;
import org.pf9.pangu.boilerplate.seq.MenuSeq;
import org.pf9.pangu.boilerplate.service.AbstractEntityService;
import org.pf9.pangu.boilerplate.service.MenuService;
import org.pf9.pangu.boilerplate.service.dto.MenuDTO;
import org.pf9.pangu.boilerplate.util.DataTablesUtil;
import org.pf9.pangu.boilerplate.util.MenuUtil;
import org.pf9.pangu.framework.auth.security.SecurityUtils;
import org.pf9.pangu.framework.common.exception.PanguException;
import org.pf9.pangu.framework.common.util.JSONUtils;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesOrder;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesQuery;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesResult;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesSearch;
import org.pf9.pangu.framework.data.mybatis.dto.PaginationQuery;
import org.pf9.pangu.framework.data.util.SequenceGenerator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl extends AbstractEntityService<Menu, MenuMapper> implements MenuService {


    @Autowired
    @Qualifier("pgSequenceGenerator")
    private SequenceGenerator sequenceGenerator;

    private Logger logger = LoggerFactory.getLogger(MenuServiceImpl.class);

    @Autowired
    protected MenuMapper baseMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private MenuSeq menuSeq;

    @Value("classpath:menus.json")
    private Resource menuConf;

    @PostConstruct
    protected void init() {
        logger.debug("Menu BaseMapper is " + baseMapper.toString());
        super.setBaseMapper(baseMapper);
    }

    @Cacheable(value = "menu-all", key = "${userId}")
    public List<Menu> getAllMenu(String username, boolean filterDisabled) {

        User user = userMapper.findUserByLogin(username);

        List<Role> roles = roleMapper.getRoleListByUser(user.getId());

        List<Role> adminRole = roles.stream().filter(r -> r.getCode().equals("ROLE_ADMIN")).collect(Collectors.toList());

        // 如果是管理员显示所有的菜单
        if (adminRole.size() > 0 || user.getLogin().equals("admin")) {

            // 把已停用的过滤掉
            Condition condition = new Condition(Menu.class);
            if (filterDisabled)
                condition.createCriteria().andEqualTo("disabled", false);

            return this.baseMapper.selectByCondition(condition).stream().sorted(new Comparator<Menu>() {
                @Override
                public int compare(Menu o1, Menu o2) {
                    // 防止 sortNo 为空报空指针错误
                    if (o1.getSortNo() == null)
                        o1.setSortNo(9999);
                    if (o2.getSortNo() == null)
                        o2.setSortNo(9999);

                    return o1.getSortNo() - o2.getSortNo();
                }
            }).collect(Collectors.toList());
        } else {
            List<Menu> commonMenu = baseMapper.getAllMenusByRole(roles.get(0).getCode()).stream().sorted(new Comparator<Menu>() {
                @Override
                public int compare(Menu o1, Menu o2) {
                    // 防止 sortNo 为空报空指针错误
                    if (o1.getSortNo() == null)
                        o1.setSortNo(9999);
                    if (o2.getSortNo() == null)
                        o2.setSortNo(9999);

                    return o1.getSortNo() - o2.getSortNo();
                }
            }).collect(Collectors.toList());

            return commonMenu;
        }
    }


    @Override
    public MenuDTO getTree(String username, boolean filterDisabled) {
        return getTree(username, "root", filterDisabled); // ROOT
    }

    @Override
    public MenuDTO getTree(String username, String parentCode, boolean filterDisabled) {

        List<Menu> allMenu = getAllMenu(username, filterDisabled);
        MenuDTO tree = new MenuDTO();
        //得到菜单根节点
        Optional<Menu> rootMenu = MenuUtil.getMenu(allMenu, parentCode);
        if (rootMenu.isPresent()) {

            tree = new MenuDTO(rootMenu.get());
            //得到根节点下面的值
            List<Menu> children = MenuUtil.getChildren(allMenu, rootMenu.get().getId());
            for (Menu c : children) {
                MenuDTO child = new MenuDTO(c);
                child.setParentMenu(getMenuById(c.getParentId()));
                tree.addChild(child);
            }
            getMenuTree(allMenu, tree.getChildren());
        }


        return tree;
    }

    public void getMenuTree(List<Menu> all, List<MenuDTO> nodes) {

        for (MenuDTO node : nodes) {

            List<Menu> children = getChildren(all, node.getId());
            for (Menu m : children) {
                MenuDTO dto = new MenuDTO(m);
                dto.setParentMenu(getMenuById(m.getParentId()));
                node.addChild(dto);
            }
            if (node.getChildren() != null && node.getChildren().size() > 0) {
                getMenuTree(all, node.getChildren());
            }
        }
    }

    public List<Menu> getChildren(List<Menu> all, Long id) {
        return all.stream().filter(m -> m.getParentId().equals(id)).collect(Collectors.toList());
    }

    public Menu getMenuById(Long id) {
        return baseMapper.selectByPrimaryKey(id);
    }


    @Override
    public MenuDTO getDTOTree(String roleCode) {

        List<MenuDTO> all = baseMapper.getAllMenuByRole(roleCode);

        MenuDTO tree = new MenuDTO();

        Optional<MenuDTO> _current = MenuUtil.getMenuDTO(all, 0L); // 0L: ROOT
        if (_current.isPresent()) {
            tree = _current.get();
            List<MenuDTO> children = MenuUtil.getDTOChildren(all, 0L);
//            children.forEach(menuDTO ->tree.addChild(menuDTO) );
            for (MenuDTO m : children) {
                tree.addChild(m);
            }
            MenuUtil.getMenuDTOTree(all, tree.getChildren());
        }

        return tree;
    }

    public Page<Menu> getPagedMenuList(PaginationQuery query) {
        Condition where = new Condition(Menu.class);
        if (query.getQuery() == null) {
            where.createCriteria().andEqualTo("parentId", 0);
        } else {

            where.createCriteria().andEqualTo("parentId", 0)
                    .andLike("title", "%" + query.getQuery() + "%");
        }
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        return PageHelper.startPage(query.getPageNum(), query.getPageSize())
                .doSelectPage(() -> this.baseMapper.selectByCondition(where));
    }


    @Override
    public List<MenuDTO> getAllMenuByRole(String code) {
        return baseMapper.getAllMenuByRole(code);
    }

    /**
     * 结合 datatables 的分页
     *
     * @param req
     * @return
     */
    @Override
    public DataTablesResult<Menu> getTable(DataTablesQuery req) {

        int pageNum = req.getStart() / req.getLength();

        int pageSize = req.getLength();

        List<DataTablesOrder> orders = req.getOrder();

        DataTablesSearch search = req.getSearch();

        Condition condition = new Condition(Menu.class);

        condition.createCriteria().andLike("title", "%" + search.getValue() + "%");
        for (DataTablesOrder order : orders) {
            condition.setOrderByClause(req.getColumns().get(order.getColumn()).getData() + " " + order.getDir());
        }

        Page<Menu> menuPaged = PageHelper.startPage(pageNum, pageSize)
                .doSelectPage(() -> baseMapper.selectByCondition(condition));

        return DataTablesUtil.wrap(menuPaged, req);
    }

    /**
     * 保存或更新实体
     *
     * @param menu
     */
    @Transactional
    @Override
    @CacheEvict("menu-all")
    public int saveOrUpdate(Menu menu) {


        if (menu.getId() == null) {
            // 新增
            menu.setId(menuSeq.nextValue());
            menu.setLastModifiedBy(SecurityUtils.getCurrentUserLogin());
            menu.setLastModifiedDate(Instant.now());
            menu.setCreatedDate(Instant.now());
            menu.setCreatedBy(SecurityUtils.getCurrentUserLogin());

            if (menu.getParentId() == null)
                menu.setParentId(0L);

            return baseMapper.insert(menu);
        } else {
            // 修改
            menu.setLastModifiedBy(SecurityUtils.getCurrentUserLogin());
            menu.setLastModifiedDate(Instant.now());

            return baseMapper.updateByPrimaryKey(menu);
        }
    }

    @Transactional
    @Override
    @CacheEvict("menu-all")
    public void setDisabledById(Long id, boolean disabled) {

        Menu menu = new Menu();
        menu.setId(id);
        menu.setDisabled(disabled);
        menu.setLastModifiedBy(SecurityUtils.getCurrentUserLogin());
        menu.setLastModifiedDate(Instant.now());

        baseMapper.updateByPrimaryKeySelective(menu);
    }

    /**
     * 生成默认的菜单列表
     * <p>
     * 读取 menus.json 的内容，并生成菜单列表
     */
    @Override
    @CacheEvict("menu-all")
    public void defaultCreate() throws PanguException {

        Condition condition = new Condition(Menu.class);
        condition.createCriteria().andCondition("1=1");
        baseMapper.deleteByCondition(condition);

        String jsonStr = StringUtils.EMPTY;
        try {
            jsonStr = IOUtils.toString(menuConf.getInputStream(), Charset.defaultCharset());

            JSONObject menuJson = JSONUtils.readValueAsJSONObject(jsonStr);

            try {
                Menu currentMenu = MenuUtil.getMenuFromJSONObject(menuJson);
                long id = menuSeq.nextValue();
                currentMenu.setId(id);
                currentMenu.setParentId(-1L);
                baseMapper.insertSelective(currentMenu);

                if (!menuJson.isNull("children") && !menuJson.getString("children").equals("")
                        && !menuJson.getString("children").equals("null")) {
                    JSONArray childrenJson = menuJson.getJSONArray("children");

                    visitMenuJsonTree(childrenJson, id);
                }
                processMenuTree();

            } catch (Exception ex) {

                logger.warn("解析菜单出错", ex);
                throw new PanguException("解析菜单出错", ex);
            }
        } catch (IOException ioEx) {
            logger.warn("找不到菜单配置JSON文件");
            throw new PanguException("解析菜单文件出错", ioEx);
        }
    }


    private void visitMenuJsonTree(JSONArray children, Long parentId) {
        int len = children.length();
        int i = 0;
        while (i < len) {
            try {
                JSONObject current = children.getJSONObject(i);
                Menu menu = MenuUtil.getMenuFromJSONObject(current);
                if (menu.getCode().equals("admin_group")) {
                    logger.debug("admin_group");
                }
                menu.setParentId(parentId);
                long id = menuSeq.nextValue();
                menu.setId(id);
                baseMapper.insertSelective(menu);
                if (!current.isNull("children") && !current.getString("children").equals("")
                        && !current.getString("children").equals("null")) {
                    JSONArray childrenJson = current.getJSONArray("children");
                    visitMenuJsonTree(childrenJson, id);
                }
            } catch (JSONException ex) {
                logger.warn("解析菜单JSON树失败", ex);
            } finally {
                i++;
            }
        }
    }

    /**
     * 根据用户名和应用编码获取菜单
     *
     * @param username
     * @param appCode
     * @param filterDisabled 是否过滤停用的菜单
     * @return
     */
    @Override
    public MenuDTO getAppMenusByCode(String username, String appCode, boolean filterDisabled) {
        MenuDTO root = getTree(username, filterDisabled);
        Optional<MenuDTO> _app = root.getChildren().stream().filter(o -> o.getCode().equals(appCode)).findFirst();
        return _app.orElse(null);
    }

    private List<Menu> getMenuByParentId(Long parentId) {
        Condition condition = new Condition(Menu.class);

        condition.createCriteria().andEqualTo("parentId", parentId);

        return baseMapper.selectByCondition(condition);
    }

    @Override
    public void processMenuTree() {
//        List<Menu> all = this.getAllMenu("admin", false);

        List<Menu> rootList = this.findByCode("root");
        if (rootList.size() > 0) {

            Menu root = rootList.get(0);

            root.setTreePath(root.getId().toString());

            root.setTreeLevel(0);
            this.updateByPrimaryKeySelective(root);

            Menu parent = root;
            List<Menu> children = this.getMenuByParentId(parent.getId());
            processSubMenuTree(parent, children);
        }
    }

    private void processSubMenuTree(Menu parent, List<Menu> children) {

        for (Menu child : children) {
            child.setTreeLevel(parent.getTreeLevel() + 1);
            child.setTreePath(parent.getTreePath() + "," + child.getId().toString());

            this.updateByPrimaryKeySelective(child);

            processSubMenuTree(child, getMenuByParentId(child.getId()));
        }
    }

    @Override
    public List<Menu> getMenuList(String parentCode, int depth) {
        Menu parent = this.findByCode(parentCode).get(0);
        Condition condition = new Condition(Menu.class);
        condition.createCriteria().andEqualTo("parentId", parent.getId())
                .andLessThan("treeLevel", depth + parent.getTreeLevel());
        condition.orderBy("treePath");

        return baseMapper.selectByCondition(condition);

    }
}
