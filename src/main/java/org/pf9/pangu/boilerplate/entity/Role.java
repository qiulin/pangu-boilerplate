package org.pf9.pangu.boilerplate.entity;


import org.pf9.pangu.framework.data.domain.AbstractAuditingEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "pg_adm_role")
public class Role extends AbstractAuditingEntity {

    @Column
    @NotNull
    private String code;

    @Column
    @NotNull
    private String name;

    @Column(name = "description")
    private String description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
