package org.pf9.pangu.boilerplate.service.dto;

import org.pf9.pangu.boilerplate.entity.Authority;
import org.pf9.pangu.boilerplate.entity.Role;

import java.time.Instant;
import java.util.Set;

public class RoleDto {

    private String code;

    private String name;

    private String description;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private Set<Authority> authoritySet;

    public RoleDto() {
    }

    public RoleDto(Role role) {
    this( role.getCode(), role.getName(), role.getDescription(), role.getCreatedDate(),role.getLastModifiedBy(), role.getLastModifiedDate());


    }


    public RoleDto(String code, String name, String description, Instant createdDate, String lastModifiedBy, Instant lastModifiedDate) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.createdDate = createdDate;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedDate = lastModifiedDate;
        this.authoritySet = authoritySet;
    }

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

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Set<Authority> getAuthoritySet() {
        return authoritySet;
    }

    public void setAuthoritySet(Set<Authority> authoritySet) {
        this.authoritySet = authoritySet;
    }
}
