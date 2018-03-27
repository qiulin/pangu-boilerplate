package org.pf9.pangu.boilerplate.service.dto;

import org.pf9.pangu.boilerplate.entity.Menu;

import java.util.LinkedList;
import java.util.List;

public class MenuDTO {

    private Long id;

    private String title;

    private String code;

    private String iconCls;

    private String href;

    private String parameters;

    private boolean disabled;

    private Menu parentMenu;

    private Long parentId;

    private Integer level;

    private Integer sortNo;

    private String description;

    private List<MenuDTO> children;

    private boolean checked = false;

    public MenuDTO() {
    }

    public MenuDTO(Menu menu) {
        this.id = menu.getId();
        this.title = menu.getTitle();
        this.code = menu.getCode();
        this.iconCls = menu.getIconCls();
        this.href = menu.getHref();
        this.parameters = menu.getParameters();
        this.disabled = menu.isDisabled();
        this.description = menu.getDescription();
        this.level = menu.getTreeLevel();
        this.sortNo = menu.getSortNo();
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

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

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public Menu getParentMenu() {
        return parentMenu;
    }

    public void setParentMenu(Menu parentMenu) {
        this.parentMenu = parentMenu;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }

    public List<MenuDTO> getChildren() {
        return children;
    }

    public void setChildren(List<MenuDTO> children) {
        this.children = children;
    }

    public void addChild(MenuDTO menu) {

        if (this.children == null) {
            this.children = new LinkedList<>();
        }
        this.children.add(menu);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
