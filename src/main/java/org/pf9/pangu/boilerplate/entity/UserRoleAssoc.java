package org.pf9.pangu.boilerplate.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "pg_adm_user_role_assoc")
public class UserRoleAssoc {

    @Id
    private long id;

    @Column(name = "role_code")
    private String roleCode;

    @Column(name = "user_id")
    private Long uid;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "UserRoleAssoc{" +
                "id=" + id +
                ", roleCode=" + roleCode +
                ", uid=" + uid +
                '}';
    }
}
