package org.pf9.pangu.boilerplate.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name="pg_adm_user_group_assoc")
public class UserGroupAssoc implements Serializable{

    private static final long serialVersionUID = 1L;

    private Long uid;

    @Column(name="g_code")
    private String groupCode;

    @Id
    private Long id;


    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
