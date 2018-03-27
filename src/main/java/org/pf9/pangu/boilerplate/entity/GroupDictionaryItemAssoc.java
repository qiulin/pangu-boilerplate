package org.pf9.pangu.boilerplate.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="pg_adm_group_dictionaryitem_assoc")
public class GroupDictionaryItemAssoc {

    @Id
    private Long id;

    @Column(name="g_code")
    private String groupCode;

    @Column(name = "d_id")
    private Long dictionaryItemId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public Long getDictionaryItemId() {
        return dictionaryItemId;
    }

    public void setDictionaryItemId(Long dictionaryItemId) {
        this.dictionaryItemId = dictionaryItemId;
    }
}
