package org.pf9.pangu.boilerplate.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.pf9.pangu.boilerplate.entity.Role;
import org.pf9.pangu.boilerplate.entity.User;
import org.pf9.pangu.boilerplate.entity.UserGroupAssoc;
import org.pf9.pangu.boilerplate.entity.UserRoleAssoc;
import org.pf9.pangu.boilerplate.mapper.AuthorityMapper;
import org.pf9.pangu.boilerplate.mapper.RoleMapper;
import org.pf9.pangu.boilerplate.mapper.UserGroupAssocMapper;
import org.pf9.pangu.boilerplate.mapper.UserMapper;
import org.pf9.pangu.boilerplate.mapper.UserRoleAssocMapper;
import org.pf9.pangu.boilerplate.seq.RoleMenuAssocSeq;
import org.pf9.pangu.boilerplate.seq.UserRoleAssocSeq;
import org.pf9.pangu.boilerplate.seq.UserSeq;
import org.pf9.pangu.boilerplate.service.AbstractEntityService;
import org.pf9.pangu.boilerplate.service.UserService;
import org.pf9.pangu.boilerplate.util.EntityUtils;
import org.pf9.pangu.framework.auth.security.SecurityUtils;
import org.pf9.pangu.framework.common.exception.PanguException;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesOrder;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesQuery;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesResult;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesSearch;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl extends AbstractEntityService<User, UserMapper> implements UserService {

    @Autowired
    private AuthorityMapper authorityMapper;

    @Autowired
    private UserSeq userSeq;

    @Autowired
    private RoleMenuAssocSeq roleMenuAssocSeq;

    @Autowired
    private UserRoleAssocSeq userRoleAssocSeq;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    protected UserMapper baseMapper;

    @Autowired
    private UserRoleAssocMapper userRoleAssocMapper;

    @Autowired
    private UserGroupAssocMapper userGroupAssocMapper;

    @PostConstruct
    private void init() {
        super.setBaseMapper(baseMapper);
    }

    @Override
    public Optional<User> findUserByLogin(String login) {

        User user = this.baseMapper.findUserByLogin(login);
        if (null != user) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public List<User> getAdminUsers() {
        String groupCode = getCurrentUserGroupCode();
        if (!groupCode.equals("")) {
            return baseMapper.findAdminUsers("admin_" + groupCode);
        } else {
            return baseMapper.selectAll();
        }
    }

    private String getCurrentUserGroupCode() {
        User u = getCurrentUser();
        Condition userCondition = new Condition(UserGroupAssoc.class);
        userCondition.createCriteria().andEqualTo("uid", u.getId());
        if (userGroupAssocMapper.selectByCondition(userCondition).size() != 0) {
            UserGroupAssoc userGroupAssoc = userGroupAssocMapper.selectByCondition(userCondition).get(0);
            return userGroupAssoc.getGroupCode();
        }
        return "";
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        Condition condition = new Condition(User.class);
        condition.createCriteria().andEqualTo("email", email);
        return this.baseMapper.selectByCondition(condition).stream().findFirst();
    }


    @Override
    public DataTablesResult<User> findAll(DataTablesQuery query) {
        int pageIndex = query.getStart() / query.getLength() + 1;
        int pageSize = query.getLength();

        List<DataTablesOrder> orders = query.getOrder();

        DataTablesSearch search = query.getSearch();
        Condition condition = new Condition(User.class);

        String searchText = search.getValue();

        if (!StringUtils.isEmpty(searchText))
            condition.createCriteria().andLike("login", "%" + searchText + "%")
                    .orLike("firstName", "%" + searchText + "%")
                    .orLike("lastName", "%" + searchText + "%");

        for (DataTablesOrder order : orders) {
            condition.setOrderByClause(query.getColumns().get(order.getColumn()).getData() + " " + order.getDir());
        }

        Page<User> rolesPaged = PageHelper.startPage(pageIndex, pageSize)
                .doSelectPage(() -> baseMapper.selectByCondition(condition));
        List<User> users = rolesPaged.getResult();
        users.forEach(a -> a.setRoles(roleMapper.getRoleListByUser(a.getId())));
        DataTablesResult<User> out = new DataTablesResult<>();
        out.setData(users);
        out.setDraw(query.getDraw());
        out.setRecordsTotal(rolesPaged.getTotal());
        out.setRecordsFiltered(rolesPaged.getTotal());
        return out;
    }

    @Override
    @Transactional
    public User createUser(String login, String password, String firstName,
                           String lastName, String email, String imageUrl, String langKey) {
        User user = newEntity();

        user.setId(userSeq.nextValue());
        user.setLogin(login);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setImageUrl(imageUrl);
        user.setLangKey(langKey);
        user.setCreatedDate(Instant.now());
        user.setCreatedBy("0");
        user.setLastModifiedBy("0");
        user.setLastModifiedDate(Instant.now());
        user.setActivated(true);

        this.baseMapper.insert(user);

        return user;
    }


    @Override
    public int findLogin(String login) {
        Condition condition = new Condition(User.class);
        condition.createCriteria().andEqualTo("login", login);
        return baseMapper.selectCountByCondition(condition);
    }

    @Override
    @Transactional
    public User createUser(User user) {
        try {
            user.setId(userSeq.nextValue());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCreatedDate(Instant.now());
            user.setCreatedBy("0");
            user.setLastModifiedBy("0");
            user.setLastModifiedDate(Instant.now());
            user.setActivated(true);
            this.baseMapper.insert(user);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("保存" + user + "发生异常");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
        return user;
    }

    @Override
    public User getCurrentUser() {
        String login = SecurityUtils.getCurrentUserLogin();

        Optional<User> user = findUserByLogin(login);
        return user.orElse(null);
    }

    @Override
    @Transactional
    public User updateUser(String firstName, String lastName, String email, String langKey) {
        User user = getCurrentUser();
        if (user != null) {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setLangKey(langKey);
            EntityUtils.setModifiedAudit(user);

            this.baseMapper.updateByPrimaryKey(user);
        }

        return user;
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        User u = findUserByLogin(user.getLogin()).get();
        u.setFirstName(user.getFirstName());
        u.setLastName(user.getLastName());
        u.setEmail(user.getEmail());
        u.setLangKey(user.getLangKey());
        EntityUtils.setModifiedAudit(u);
        if (user.getPassword() != null && !user.getPassword().trim().equals("")) {
            u.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        this.baseMapper.updateByPrimaryKey(u);

        return u;
    }

    @Override
    public int deleteUserById(long id) {
        //删除用户角色依赖关系
        Condition assocCondition = new Condition(UserRoleAssoc.class);
        assocCondition.createCriteria().andEqualTo("uid", id);
        userRoleAssocMapper.deleteByCondition(assocCondition);
        return baseMapper.deleteUserById(id);
    }

    @Override
    @Transactional
    public void changePassword(String password) {

        User user = getCurrentUser();
        if (user != null) {
            user.setPassword(passwordEncoder.encode(password));
            user.setLastModifiedDate(Instant.now());
            user.setLastModifiedBy(user.getLogin());
            this.baseMapper.updateByPrimaryKey(user);
        }
    }

    /**
     * 初始化管理员用户和权限
     */
    @Transactional
    @Override
    public void defaultCreate() throws PanguException {

        User user = newEntity();

        Optional<User> _user = findUserByLogin("admin");
        if (!_user.isPresent()) {
            user.setId(userSeq.nextValue());
            user.setLogin("admin");
            user.setActivated(true);
            user.setEmail("admin@thothinfo.com");
            user.setFirstName("admin");
            user.setLastName("thothinfo");
            user.setPassword(passwordEncoder.encode("thoth123"));
            user.setCreatedBy("0");
            user.setCreatedDate(Instant.now());

            try {
                this.baseMapper.insert(user);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new PanguException(ex);
            }
        } else {
            user = _user.get();
        }

        // 给用户分配角色
        Condition condition = new Condition(Role.class);
        condition.createCriteria().andEqualTo("code", "ROLE_ADMIN");
        List<Role> roles = roleMapper.selectByCondition(condition);
        if (roles.size() > 0) {
            UserRoleAssoc ur = new UserRoleAssoc();
            ur.setId(userRoleAssocSeq.nextValue());
            ur.setRoleCode(roles.get(0).getCode());
            ur.setUid(user.getId());
            try {
                int pk = userRoleAssocMapper.insertSelective(ur);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new PanguException(ex);
            }
        }


    }

    @Override
    public List<UserRoleAssoc> findUserRoleAssocByUid(long uid) {
        List<UserRoleAssoc> roleList = null;
        Condition condition = new Condition(UserRoleAssoc.class);
        condition.createCriteria().andEqualTo("uid", uid);
        roleList = userRoleAssocMapper.selectByCondition(condition);
        return roleList;
    }

    @Transactional
    @Override
    public void assignRoleToUser(List<UserRoleAssoc> assocList) {
        // 先清除所有的关联，然后重新建立关联，假设参数里面的关联都是同一个角色的
        long uid = assocList.get(0).getUid();

        Condition assocCondition = new Condition(UserRoleAssoc.class);
        assocCondition.createCriteria().andEqualTo("uid", uid);

        userRoleAssocMapper.deleteByCondition(assocCondition);

        for (UserRoleAssoc uac : assocList) {
            uac.setId(roleMenuAssocSeq.nextValue());
            userRoleAssocMapper.insert(uac);
        }

    }


}
