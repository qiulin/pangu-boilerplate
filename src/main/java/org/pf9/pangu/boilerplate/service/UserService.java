package org.pf9.pangu.boilerplate.service;

import org.pf9.pangu.boilerplate.entity.User;
import org.pf9.pangu.boilerplate.entity.UserRoleAssoc;
import org.pf9.pangu.boilerplate.mapper.UserMapper;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesQuery;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesResult;
import org.pf9.pangu.framework.data.service.CreateDefaultService;

import java.util.List;
import java.util.Optional;

public interface UserService extends CreateDefaultService, EntityService<User, UserMapper> {

    //根据当前用户，获得该用户对应的组长
    List<User> getAdminUsers();

    Optional<User> findUserByLogin(String login);

    Optional<User> findUserByEmail(String email);

    DataTablesResult<User> findAll(DataTablesQuery query);


    User createUser(String login, String password, String firstName,
                    String lastName, String email,
                    String imageUrl, String langKey);

    User createUser(User user);

    int findLogin(String login);

    User getCurrentUser();

    User updateUser(String firstName, String lastName, String email, String langKey);

    User updateUser(User user);

    void changePassword(String password);

    int deleteUserById(long id);

    List<UserRoleAssoc> findUserRoleAssocByUid(long uid);

    void assignRoleToUser(List<UserRoleAssoc> assocList);
}
