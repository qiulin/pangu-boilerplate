package org.pf9.pangu.boilerplate.util;

import org.pf9.pangu.boilerplate.entity.Menu;
import org.pf9.pangu.boilerplate.service.dto.MenuDTO;
import org.pf9.pangu.framework.auth.security.SecurityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MenuUtil {

    public static Optional<Menu> getMenu(List<Menu> all, Long id) {

        return all.stream().filter(m -> m.getId().equals(id)).findFirst();
    }

    public static Optional<Menu> getMenu(List<Menu> all, String code) {

        return all.stream().filter(m -> m.getCode().equals(code)).findFirst();
    }

    public static Optional<MenuDTO> getMenuDTO(List<MenuDTO> all, Long id) {

        return all.stream().filter(m -> m.getId().equals(id)).findFirst();
    }

    public static List<Menu> getChildren(List<Menu> all, Long id) {
        return all.stream().filter(m -> m.getParentId().equals(id)).collect(Collectors.toList());
    }

    public static List<MenuDTO> getDTOChildren(List<MenuDTO> all, Long id) {
        return all.stream().filter(m -> m.getParentId().equals(id)).collect(Collectors.toList());
    }

    public static List<Menu> getBrothers(List<Menu> all, Long id) {

        Optional<Menu> _current = all.stream().filter(m -> m.getId().equals(id)).findFirst();

        List<Menu> brothers = new LinkedList<>();
        Menu current;
        if (_current.isPresent()) {
            current = _current.get();
            Long parentId = current.getParentId();

            brothers = all.stream()
                    .filter(m -> m.getParentId().equals(parentId))
                    .sorted(new Comparator<Menu>() {
                        @Override
                        public int compare(Menu o1, Menu o2) {
                            return o1.getSortNo() - o2.getSortNo();
                        }
                    })
                    .collect(Collectors.toList());
        }

        return brothers;
    }

    public static Optional<Menu> getNextBrother(List<Menu> all, Long id) {
        List<Menu> brothers = getBrothers(all, id);
        Optional<Menu> nextBrother = Optional.empty();
        Optional<Menu> _current = getMenu(all, id);
        Menu current;
        if (brothers.size() > 1 && _current.isPresent()) {

            current = _current.get();
            nextBrother = brothers.stream().filter(o -> o.getSortNo() > current.getSortNo()).findFirst();
        }
        return nextBrother;
    }

    public static boolean hasChildren(List<Menu> all, Long id) {
        return getChildren(all, id).size() > 0;
    }

    public static boolean hasBrothers(List<Menu> all, Long id) {
        return getBrothers(all, id).size() > 1;
    }

    public static boolean hasNextBrother(List<Menu> all, Long id) {
        return getNextBrother(all, id).isPresent();
    }

    public static MenuDTO convertToDTO(Menu menu) {
        return new MenuDTO(menu);
    }

    public static Menu convertToEntity(MenuDTO dto) {

        Menu menu = new Menu();

        if (dto.getParentMenu() != null) {
            menu.setParentId(dto.getParentMenu().getId());
        }
        menu.setSortNo(dto.getSortNo());
        menu.setLastModifiedDate(Instant.now());
        menu.setLastModifiedBy(SecurityUtils.getCurrentUserLogin());
        menu.setParameters(dto.getParameters());
        menu.setTitle(dto.getTitle());
        menu.setIconCls(dto.getIconCls());
        menu.setHref(dto.getHref());
        menu.setCode(dto.getCode());
        menu.setId(dto.getId());
        menu.setParentId(dto.getParentId());

        return menu;
    }

    /**
     * 递归获取菜单树
     *
     * @param all
     * @param nodes
     */
    public static void getMenuTree(List<Menu> all, List<MenuDTO> nodes) {

        for (MenuDTO node : nodes) {

            List<Menu> children = getChildren(all, node.getId());
            children.forEach(o -> node.addChild(new MenuDTO(o)));
            if (node.getChildren() != null && node.getChildren().size() > 0) {
                getMenuTree(all, node.getChildren());
            }
        }
    }

    /**
     * 递归获取菜单树（ MenuDTO 版）
     */
    public static void getMenuDTOTree(List<MenuDTO> all, List<MenuDTO> nodes) {
        for (MenuDTO node : nodes) {
            List<MenuDTO> children = getDTOChildren(all, node.getId());
            children.forEach(c -> node.addChild(c));

            if (node.getChildren() != null && node.getChildren().size() > 0) {
                getMenuDTOTree(all, node.getChildren());
            }
        }
    }

    public static Menu getMenuFromJSONObject(JSONObject menuJson) throws JSONException {
        try {
            Menu menu = new Menu();
            menu.setCode(menuJson.getString("code"));
            menu.setDescription(menuJson.getString("description"));
            menu.setDisabled(false);
            menu.setHref(menuJson.getString("href"));
            menu.setIconCls(menuJson.getString("iconCls"));
            menu.setParameters(menuJson.getString("parameters"));
            menu.setTitle(menuJson.getString("title"));
            menu.setSortNo(menuJson.getInt("sortNo"));
            menu.setLastModifiedBy("root");
            menu.setCreatedBy("root");
            menu.setLastModifiedDate(Instant.now());
            menu.setCreatedDate(Instant.now());

            return menu;
        } catch (JSONException ex) {
            throw ex;
        }
    }
}
