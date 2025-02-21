package com.java_assignment.group.View.Customer;

import com.java_assignment.group.Controller.*;
import com.java_assignment.group.MainFrame;
import com.java_assignment.group.Model.*;
import com.java_assignment.group.Model.Menu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class VenderStorePage extends JPanel {
    private MainFrame mainFrame;
    private Vender vender;
    private VenderController venderController;
    private AuthController authController;
    private CartController cartController;
    private MenuController menuController;
    private List<Menu> menuList;
    private Cart cart;
    private OrderController orderController;
    private BaseUser user;

    public VenderStorePage(MainFrame mainFrame, Vender vender) {
        this.mainFrame = mainFrame;
        this.vender = vender;

        this.setBorder(new EmptyBorder(30, 30, 30, 30));
        this.setLayout(new BorderLayout());

        // **ダッシュボードデータのロード**
        onLoadDashboard();

        // **UIを構築**
        updatePageUI();
    }

    private void onLoadDashboard() {
        try {
            this.venderController = new VenderController();
            this.authController = new AuthController();
            this.cartController = new CartController();
            this.menuController = new MenuController();
            this.menuList = menuController.getMenusByVender(vender.getId());
            this.user = authController.getCurrentUser();
            this.orderController = new OrderController();

            String userId = authController.getCurrentUser().getId();
            cart = cartController.getCartByUserIdAndVenderId(userId, vender.getId());

            if (cart == null) {
                cart = new Cart();
                cart.setUserId(userId);
                cart.setVenderId(vender.getId());
                cart = cartController.createCart(cart);
            }

            System.out.println("Dashboard data loaded.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainFrame, "Error loading: " + ex.getMessage());
        }
    }

    private void updatePageUI() {
        onLoadDashboard();

        this.removeAll();
        this.revalidate();
        this.repaint();

        // **ヘッダー**
        JLabel header = new JLabel(vender.getStoreName());
        header.setFont(new Font("SansSerif", Font.BOLD, 26));
        this.add(header, BorderLayout.NORTH);

        // **メニューリストのパネル**
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setAlignmentX(LEFT_ALIGNMENT);

        Consumer<Void> refreshPage = (Void) -> updatePageUI();

        for (Menu menu : vender.getItems()) {
            MenuItemCard card = new MenuItemCard(mainFrame, vender, menu, cartController, cart, refreshPage);
            card.setMaximumSize(new Dimension(Short.MAX_VALUE, 66));
            menuPanel.add(card);
            menuPanel.add(Box.createVerticalStrut(5));
        }

        menuPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        this.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton checkoutButton = new JButton();
        checkoutButton.setText("Check out("+cart.getAllAmountOfItems()+")");
        checkoutButton.addActionListener(e -> {
            Order currentOrder = orderController.getCurrentOrder(user.getId());

            if (currentOrder == null){
                mainFrame.addPanel("OrderConfirmPage", new OrderConfirmPage(mainFrame, vender));
                mainFrame.switchTo("OrderConfirmPage");
            }else{
                String[] options = {"View Order"};
                int choice = JOptionPane.showOptionDialog(
                        mainFrame,
                        "Your have other order currently!",
                        "You have an order",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        options,
                        options[0]
                );
                // If "View Order" is clicked, switch the page
                if (choice == 0) {
                    mainFrame.switchTo("OrderProgressPage");
                }
            }
        });

        JButton goBackButton = new JButton();
        goBackButton.setText("Go back");
        goBackButton.addActionListener(e -> mainFrame.switchTo("CustomerDashboard"));

        buttonPanel.add(checkoutButton);
        buttonPanel.add(goBackButton);
        this.add(buttonPanel, BorderLayout.SOUTH);

        // **画面を更新**
        this.revalidate();
        this.repaint();
    }
}

class MenuItemCard extends JPanel {
    private MainFrame mainframe;
    private Vender vender;
    private Menu menu;
    private CartController cartController;
    private Cart cart;
    private Consumer<Void> refreshPage;
    private CartItem item;

    public MenuItemCard(
            MainFrame mainframe,
            Vender vender,
            Menu menu,
            CartController cartController,
            Cart cart,
            Consumer<Void> refreshPage
    ){
        this.mainframe = mainframe;
        this.vender = vender;
        this.menu = menu;
        this.cartController = cartController;
        this.cart = cart;
        this.refreshPage = refreshPage;
        this.item = cart.getCartItemByMenuId(menu.getId());

        JPanel mainPanel = new JPanel();
        mainPanel.setPreferredSize(new Dimension(340,66));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
//        mainPanel.setBorder(new EmptyBorder(0, 20,0,0));

        // 商品画像（画像パスから ImageIcon を生成。必要に応じてリサイズ）
        ImageIcon icon = new ImageIcon("src/Data/Image/test.png");
        Image scaledImage = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        mainPanel.add(imageLabel, BorderLayout.CENTER);


        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JLabel menuName = new JLabel();
        menuName.setText(menu.getName());
        menuName.setBorder(new EmptyBorder(0, 30,5,0));

        JLabel menuPrice = new JLabel();
        menuPrice.setText("RM" + menu.getPrice());
        menuPrice.setBorder(new EmptyBorder(0, 30,5,0));

        JLabel menuDescription = new JLabel();
        menuDescription.setText(menu.getDescription());
        menuDescription.setBorder(new EmptyBorder(0, 30,0,0));

        rightPanel.add(menuName);
        rightPanel.add(menuPrice);
        rightPanel.add(menuDescription);

        // `rightPanel` を `mainPanel` の中央に配置
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        // ボタンを右寄せするためのパネル
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        if (item != null){
            // `-` ボタン
            JButton minusButton = new JButton("-");
            minusButton.setPreferredSize(new Dimension(40, 30));

            // 数量ラベル
            JLabel quantityLabel = new JLabel(String.valueOf(item.getAmount()));
            quantityLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            quantityLabel.setHorizontalAlignment(JLabel.CENTER);

            // `+` ボタン
            JButton plusButton = new JButton("+");
            plusButton.setPreferredSize(new Dimension(50, 30));

            minusButton.addActionListener(e -> {changeQuantity(item.getAmount()-1);});
            plusButton.addActionListener(e -> {changeQuantity(item.getAmount()+1);});

            // ボタンパネルに追加
            buttonPanel.add(minusButton);
            buttonPanel.add(quantityLabel);
            buttonPanel.add(plusButton);
            buttonPanel.setBorder(new EmptyBorder(0, 0,0,50));
        }else{
            JButton addButton = new JButton("add");
            addButton.addActionListener(e -> {
                CartItem cartItem = new CartItem();
                cartItem.setMenuId(menu.getId());
                cartItem.setUserId(cart.getUserId());
                cartItem.setCartId(cart.getId());
                cartItem.setAmount(1);
                cartController.addCartItem(cartItem);
                refreshPage.accept(null);
            });
            buttonPanel.add(addButton);
            buttonPanel.setBorder(new EmptyBorder(0, 0,0,50));
        }

        // `buttonPanel` を `mainPanel` の右端に配置
        mainPanel.add(buttonPanel, BorderLayout.EAST);

        // `mainPanel` を `this`（MenuItemCard）に追加
        this.add(mainPanel, BorderLayout.CENTER);
    }

    private void changeQuantity(Integer newQuantity){
        if(newQuantity < 1){
            cartController.deleteCartItem(item.getId());
        }else{
            item.setAmount(newQuantity);
            cartController.updateCartItem(item);
        }

        refreshPage.accept(null);
    }
}