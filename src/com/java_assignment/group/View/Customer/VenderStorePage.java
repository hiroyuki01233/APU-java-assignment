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
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        setLayout(new BorderLayout(20, 20));
        onLoadDashboard();
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
        removeAll();

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setBackground(Color.WHITE);
        JLabel storeLabel = new JLabel(vender.getStoreName());
        storeLabel.setFont(new Font("Arial", Font.BOLD, 32));
        storeLabel.setForeground(new Color(33, 37, 41));
        centerPanel.add(storeLabel);

        JButton backButton = new JButton("Back");
        styleButton(backButton, new Color(108, 117, 125));
        backButton.addActionListener(e -> mainFrame.switchTo("CustomerDashboard"));

        headerPanel.add(centerPanel, BorderLayout.CENTER);
        headerPanel.add(backButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Menu Items Panel
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(248, 249, 250));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        Consumer<Void> refreshPage = (Void) -> updatePageUI();

        for (Menu menu : vender.getItems()) {
            MenuItemCard card = new MenuItemCard(mainFrame, vender, menu, cartController, cart, refreshPage);
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
            menuPanel.add(card);
            menuPanel.add(Box.createVerticalStrut(15));
        }

        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(new Color(248, 249, 250));
        scrollPane.getViewport().setBackground(new Color(248, 249, 250));

        add(scrollPane, BorderLayout.CENTER);

        // Footer Panel with Cart Summary
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(222, 226, 230)),
            BorderFactory.createEmptyBorder(20, 0, 0, 0)
        ));

        JButton checkoutButton = new JButton("Checkout (" + cart.getAllAmountOfItems() + ")");
        styleButton(checkoutButton, new Color(40, 167, 69));
        checkoutButton.addActionListener(e -> handleCheckout());

        footerPanel.add(checkoutButton, BorderLayout.EAST);
        add(footerPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private void handleCheckout() {
        Order currentOrder = orderController.getCurrentOrder(user.getId());

        if (currentOrder == null) {
            mainFrame.addPanel("OrderConfirmPage", new OrderConfirmPage(mainFrame, vender));
            mainFrame.switchTo("OrderConfirmPage");
        } else {
            String[] options = {"View Order"};
            int choice = JOptionPane.showOptionDialog(
                mainFrame,
                "You have an active order!",
                "Active Order",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
            );
            if (choice == 0) {
                mainFrame.switchTo("OrderProgressPage");
            }
        }
    }

    private void styleButton(JButton button, Color baseColor) {
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Arial", Font.BOLD, 14));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor);
            }
        });
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
    ) {
        this.mainframe = mainframe;
        this.vender = vender;
        this.menu = menu;
        this.cartController = cartController;
        this.cart = cart;
        this.refreshPage = refreshPage;
        this.item = cart.getCartItemByMenuId(menu.getId());

        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Left Panel - Image
        ImageIcon icon = new ImageIcon("src/Data/Image/test.png");
        Image scaledImage = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        imageLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        add(imageLabel, BorderLayout.WEST);

        // Center Panel - Menu Info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(menu.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(new Color(33, 37, 41));

        JLabel priceLabel = new JLabel(String.format("RM %.2f", menu.getPrice()));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        priceLabel.setForeground(new Color(40, 167, 69));

        JLabel descLabel = new JLabel(menu.getDescription());
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(new Color(108, 117, 125));

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(descLabel);

        add(infoPanel, BorderLayout.CENTER);

        // Right Panel - Quantity Controls
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        quantityPanel.setBackground(Color.WHITE);

        if (item != null) {
            JButton minusButton = createQuantityButton("-", new Color(220, 53, 69));
            JLabel quantityLabel = new JLabel(String.valueOf(item.getAmount()));
            quantityLabel.setFont(new Font("Arial", Font.BOLD, 16));
            quantityLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            JButton plusButton = createQuantityButton("+", new Color(40, 167, 69));

            minusButton.addActionListener(e -> changeQuantity(item.getAmount() - 1));
            plusButton.addActionListener(e -> changeQuantity(item.getAmount() + 1));

            quantityPanel.add(minusButton);
            quantityPanel.add(quantityLabel);
            quantityPanel.add(plusButton);
        } else {
            JButton addButton = createQuantityButton("Add", new Color(0, 123, 255));
            addButton.addActionListener(e -> {
                CartItem cartItem = new CartItem();
                cartItem.setMenuId(menu.getId());
                cartItem.setUserId(cart.getUserId());
                cartItem.setCartId(cart.getId());
                cartItem.setAmount(1);
                cartController.addCartItem(cartItem);
                refreshPage.accept(null);
            });
            quantityPanel.add(addButton);
        }

        add(quantityPanel, BorderLayout.EAST);
    }

    private JButton createQuantityButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(40, 30));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
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