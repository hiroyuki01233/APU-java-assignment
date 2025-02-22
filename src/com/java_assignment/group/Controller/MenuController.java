package com.java_assignment.group.Controller;

import com.java_assignment.group.Model.Menu;
import com.java_assignment.group.Model.TxtModelRepository;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MenuController {
    private TxtModelRepository<Menu> menuRepository;

    public MenuController() throws IOException {
        menuRepository = new TxtModelRepository<>("Data/menu.txt", Menu::fromCsv, Menu::toCsv);
    }

    public List<Menu> getMenus(){
        try{
            List<Menu> allMenus = menuRepository.readAll();
            return allMenus;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * VenderごとのMenu一覧取得
     */
    public List<Menu> getMenusByVender(String venderId) throws IOException {
        List<Menu> allMenus = menuRepository.readAll();
        return allMenus.stream()
                .filter(menu -> menu.getVenderId().equals(venderId))
                .collect(Collectors.toList());
    }

    public Menu getMenuById(String menuId) {
        try {
            for (Menu menu : menuRepository.readAll()){
                if (menu.getId().equals(menuId)){
                    return menu;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Menuの追加
     */
    public boolean addMenu(Menu menu) {
        try {
            List<Menu> menus = menuRepository.readAll();
            String menuId = UUID.randomUUID().toString();
            menu.setId(menuId);
            menus.add(menu);
            menuRepository.writeAll(menus, false);
            return true;
        } catch(IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Menuの編集
     */
    public boolean updateMenu(Menu updatedMenu) {
        try {
            List<Menu> menus = menuRepository.readAll();
            for (int i = 0; i < menus.size(); i++) {
                if (menus.get(i).getId().equals(updatedMenu.getId())) {
                    menus.set(i, updatedMenu);
                    break;
                }
            }
            menuRepository.writeAll(menus, false);
            return true;
        } catch(IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Menuの削除
     */
    public boolean deleteMenu(String menuId) {
        try {
            List<Menu> menus = menuRepository.readAll();
            menus = menus.stream()
                    .filter(menu -> !menu.getId().equals(menuId))
                    .collect(Collectors.toList());
            menuRepository.writeAll(menus, false);
            return true;
        } catch(IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
