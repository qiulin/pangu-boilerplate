package org.pf9.pangu.boilerplate.entity;

import org.pf9.pangu.framework.data.mybatis.entity.HierarchicalEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 约定菜单虚拟根为0层
 * 当启用子系统模式时 1 层为子系统，2层为子系统下的第 1 层菜单, 依次类推
 */
@Entity
@Table(name = "pg_adm_menu")
public class Menu extends HierarchicalEntity<Long> {

    private static final long serialVersionUID = -7209083677967362035L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,
            generator = "select nextval('pg_adm_menu_id_seq')")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String title;

    @NotNull
    @Size(max = 50)
    @Column(nullable = false, length = 50, unique = true)
    private String code;

    @Column(name = "icon_cls", length = 50)
    private String iconCls;

    @Column(length = 100)
    private String href;

    @Column(length = 500)
    private String parameters;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "disabled")
    private boolean disabled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIconCls() {
        return iconCls;
    }

    public void setIconCls(String iconCls) {
        this.iconCls = iconCls;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
