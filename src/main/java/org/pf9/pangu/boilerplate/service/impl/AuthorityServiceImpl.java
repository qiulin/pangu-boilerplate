package org.pf9.pangu.boilerplate.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.pf9.pangu.boilerplate.entity.Authority;
import org.pf9.pangu.boilerplate.mapper.AuthorityMapper;
import org.pf9.pangu.boilerplate.service.AbstractEntityService;
import org.pf9.pangu.boilerplate.service.AuthorityService;
import org.pf9.pangu.boilerplate.util.DataTablesUtil;
import org.pf9.pangu.framework.auth.security.Constants;
import org.pf9.pangu.framework.common.exception.PanguException;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesOrder;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesQuery;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesResult;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesSearch;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class AuthorityServiceImpl extends AbstractEntityService<Authority, AuthorityMapper> implements AuthorityService {

    @Autowired
    private AuthorityMapper baseMapper;

    //PostConstruct 注释用于在依赖关系注入完成之后需要执行的方法上，以执行任何初始化。
    @PostConstruct
    private void init() {
        super.setBaseMapper(baseMapper);
    }

    @Override
    public List<Authority> findAuthoritiesByUserId(Long uid) {
        List<Authority> authorities = this.baseMapper.findAuthoritiesByUserId(uid);
        if (authorities.size() == 0) {
            authorities.add(newAnonymousAuthority());
        }
        return authorities;
    }


    @Override
    public List<Authority> findAll() {
        return baseMapper.selectAll();
    }

    @Override
    public DataTablesResult<Authority> getTable(DataTablesQuery query) {
        int pageIndex = query.getStart() / query.getLength() + 1;
        int pageSize = query.getLength();
        List<DataTablesOrder> orders = query.getOrder();
        DataTablesSearch search = query.getSearch();
        Condition condition = new Condition(Authority.class);
        String searchText = search.getValue();
        if (!StringUtils.isEmpty(searchText))
            condition.createCriteria().andLike("code", "%" + searchText + "%")
                    .orLike("name", "%" + searchText + "%")
                    .orLike("description", "%" + searchText + "%");
        for (DataTablesOrder order : orders) {
            condition.setOrderByClause(query.getColumns().get(order.getColumn()).getData() + " " + order.getDir());
        }
        Page<Authority> rolesPaged = PageHelper.startPage(pageIndex, pageSize)
                .doSelectPage(() -> baseMapper.selectByCondition(condition));
        return DataTablesUtil.wrap(rolesPaged, query);
    }


//    public Page<Authority> findAll(PaginationQuery query) {
//        Condition where = new Condition(Menu.class);
//        PageHelper.startPage(query.getPageNum(), query.getPageSize());
//        return PageHelper.startPage(query.getPageNum(), query.getPageSize())
//                .doSelectPage(() -> this.baseMapper.selectByCondition(where));
//    }

    private Authority newAnonymousAuthority() {
        Authority authority = new Authority();
        authority.setName(Constants.ANONYMOUS_AUTHORITY);
        return authority;
    }

    @Override
    public long addAuthority(Authority authority) {
        Condition condition = new Condition(Authority.class);
        condition.createCriteria().andEqualTo("code", authority.getCode());

        int num = baseMapper.selectCountByCondition(condition);
        if (num > 0) {
            return 2;
        }
        return baseMapper.insert(authority);
    }

    @Override
    public int deleteAuthorityById(String code) {
        return baseMapper.deleteByPrimaryKey(code);
    }

    @Override
    public void defaultCreate() throws PanguException {
//
//        // 清空现有权限
//        Condition condition = new Condition(Authority.class);
//        condition.createCriteria().andCondition("1=1");
//        baseMapper.deleteByCondition(condition);
//
////        List<HashMap> authorityDefs = AuthoritiesFactory.get();
//        for (HashMap def : authorityDefs) {
//
//            Authority authority = new Authority();
//            authority.setCode(def.get("code").toString());
//            authority.setName(def.get("name").toString());
////            authority.setCategory(def.get("category").toString());
//            authority.setDescription(def.get("description").toString());
//
//            baseMapper.insertSelective(authority);
//        }
    }
}
