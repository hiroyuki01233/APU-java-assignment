package com.java_assignment.group.View.Customer;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.Controller.CartController;
import com.java_assignment.group.Controller.OrderController;
import com.java_assignment.group.Controller.VenderController;
import com.java_assignment.group.MainFrame;
import com.java_assignment.group.Model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class OrderConfirmPage extends JPanel {
    private JLabel taxLabel;
    private JLabel deliveryFeeLabel;
    private JLabel serviceChargeLabel;
    private JLabel totalPriceLabel;
    private JLabel currentBalanceLabel;
    private JLabel balanceAfterLabel;
    private JPanel deliveryFeePanel;
    private MainFrame mainFrame;
    private VenderController venderController;
    private AuthController authController;
    private OrderController orderController;
    private CartController cartController;
    private List<Vender> venders;
    private Vender vender;
    private Cart cart;
    private BaseUser baseUser;

    private void onLoadDashboard() {
        try {
            venderController = new VenderController();
            authController = new AuthController();
            orderController = new OrderController();
            cartController = new CartController();
            baseUser = authController.getCurrentUser();
            cart = cartController.getCartByUserIdAndVenderId(baseUser.getId(), vender.getId());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(mainFrame, "Error loading: " + ex.getMessage());
        }
    }

    public void onPageDisplayed() {
        this.onLoadDashboard();
        System.out.println("Customer dashboard loaded");
    }

    public OrderConfirmPage(MainFrame frame, Vender vender) {
        this.mainFrame = frame;
        this.vender = vender;
        onPageDisplayed();
        setBackground(Color.WHITE);

        // Modern header panel with light blue color scheme
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(100, 149, 237));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JLabel titleLabel = new JLabel("Order Confirmation");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Enhanced menu list panel with better spacing and visual hierarchy
        JPanel menuListPanel = new JPanel();
        menuListPanel.setLayout(new BoxLayout(menuListPanel, BoxLayout.Y_AXIS));
        menuListPanel.setBackground(Color.WHITE);
        menuListPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        if (cart != null && cart.getCartItems() != null) {
            for (CartItem item : cart.getCartItems()) {
                ItemCard card = new ItemCard(mainFrame, item);
                card.setMaximumSize(new Dimension(Short.MAX_VALUE, 66));
                menuListPanel.add(card);
                menuListPanel.add(Box.createVerticalStrut(8));
            }
        } else {
            JLabel emptyLabel = new JLabel("No items in cart.");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            emptyLabel.setForeground(new Color(108, 117, 125));
            menuListPanel.add(emptyLabel);
        }

        menuListPanel.add(Box.createVerticalGlue());
        JScrollPane itemScrollPane = new JScrollPane(menuListPanel);
        itemScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        itemScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        itemScrollPane.getVerticalScrollBar().setUnitIncrement(10);
        itemScrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        topPanel.add(itemScrollPane, BorderLayout.CENTER);

        // Enhanced bottom panel with improved visual organization
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(222, 226, 230)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        bottomPanel.setBackground(Color.WHITE);

        // Order type selection panel with modern toggle buttons
        JPanel orderTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        orderTypePanel.setBackground(Color.WHITE);
        JToggleButton pickupButton = createStyledToggleButton("Pick up at store");
        JToggleButton deliveryButton = createStyledToggleButton("Delivery");
        deliveryButton.setSelected(true);
        ButtonGroup orderTypeGroup = new ButtonGroup();
        orderTypeGroup.add(pickupButton);
        orderTypeGroup.add(deliveryButton);
        orderTypePanel.add(pickupButton);
        orderTypePanel.add(deliveryButton);
        bottomPanel.add(orderTypePanel);
        bottomPanel.add(Box.createVerticalStrut(15));

        // Cost breakdown panels with consistent styling
        deliveryFeePanel = createCostPanel("Delivery Fee", deliveryFeeLabel = new JLabel("Delivery Fee: RM"));
        JPanel[] costPanels = {
            createCostPanel("Tax", taxLabel = new JLabel("Tax: RM")),
            createCostPanel("Delivery Fee", deliveryFeeLabel = new JLabel("Delivery Fee: RM")),
            createCostPanel("Service Charge", serviceChargeLabel = new JLabel("Service Charge: RM")),
            createCostPanel("Total Price", totalPriceLabel = new JLabel("Total Price: RM")),
            createCostPanel("Current Balance", currentBalanceLabel = new JLabel("Current Balance: RM")),
            createCostPanel("Balance After", balanceAfterLabel = new JLabel("Balance After: RM"))
        };

        for (JPanel panel : costPanels) {
            bottomPanel.add(panel);
            bottomPanel.add(Box.createVerticalStrut(8));
        }

        // Address input panel with improved styling
        JPanel addressPanel = new JPanel(new BorderLayout());
        addressPanel.setBackground(Color.WHITE);
        JLabel addressLabel = new JLabel("Delivery Address:");
        addressLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JTextArea addressField = new JTextArea(3, 20);
        addressField.setLineWrap(true);
        addressField.setWrapStyleWord(true);
        addressField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        addressPanel.add(addressLabel, BorderLayout.NORTH);
        addressPanel.add(new JScrollPane(addressField), BorderLayout.CENTER);
        bottomPanel.add(addressPanel);
        bottomPanel.add(Box.createVerticalStrut(20));

        // Action buttons panel with enhanced styling
        JPanel orderButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        orderButtonPanel.setBackground(Color.WHITE);
        orderButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JButton orderButton = createStyledButton("Place Order", new Color(40, 167, 69));
        JButton goBackButton = createStyledButton("Go back & Edit", new Color(100, 149, 237));

        // Set preferred size for consistent button width
        Dimension buttonSize = new Dimension(150, 40);
        orderButton.setPreferredSize(buttonSize);
        goBackButton.setPreferredSize(buttonSize);

        // Add padding to buttons
        orderButton.setMargin(new Insets(8, 15, 8, 15));
        goBackButton.setMargin(new Insets(8, 15, 8, 15));
        goBackButton.addActionListener(e -> {
            VenderStorePage storePage = new VenderStorePage(frame, vender);
            frame.addPanel("VenderStorePage", storePage);
            frame.switchTo("VenderStorePage");
        });
        orderButton.addActionListener(e -> {
            try {
                orderController.createOrderFromCart(
                        baseUser.getId(),
                        vender.getId(),
                        deliveryButton.isSelected() ? "Delivery" : "PickUp",
                        addressField.getText()
                );
                // Create a custom option for the dialog
                String[] options = {"View Order"};
                int choice = JOptionPane.showOptionDialog(
                        mainFrame,
                        "Order placed successfully!",
                        "Order Status",
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
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        mainFrame,
                        "Order failed: " + ex.getMessage(),
                        "Order Status",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        orderButtonPanel.add(orderButton);
        orderButtonPanel.add(goBackButton);
        bottomPanel.add(orderButtonPanel);

        Runnable updateFees = () -> {
            double totalPriceOfCart = 0.0;
            // カート内アイテムの合計金額を算出（Menu.getPrice()はdouble型と仮定）
            for (CartItem item : cart.getCartItems()){
                totalPriceOfCart += item.getMenu().getPrice() * item.getAmount();
            }

            totalPriceOfCart = Math.floor(totalPriceOfCart * 100) / 100;

            double deliveryFee = deliveryButton.isSelected() ? 5.0 : 0.0;
            double serviceCharge = totalPriceOfCart * 0.1;
            double taxFee = (totalPriceOfCart + deliveryFee + serviceCharge) * 0.1;
            double totalPrice = totalPriceOfCart + deliveryFee + serviceCharge + taxFee;
            double currentBalance = authController.getCurrentUser().getWallet().getBalance();
            double afterBalance = currentBalance-totalPrice;

            deliveryFee = Math.floor(deliveryFee * 100) / 100;
            serviceCharge = Math.floor(serviceCharge * 100) / 100;
            taxFee = Math.floor(taxFee * 100) / 100;
            totalPrice = Math.floor(totalPrice * 100) / 100;
            afterBalance = Math.floor(afterBalance * 100) / 100;

            taxLabel.setText("Tax: RM" + taxFee);
            deliveryFeeLabel.setText("Delivery Fee: RM" + deliveryFee);
            serviceChargeLabel.setText("Service Charge: RM" + serviceCharge);
            totalPriceLabel.setText("Total Price: RM" + totalPrice);
            currentBalanceLabel.setText("Current Balance: RM" + currentBalance);
            if ((afterBalance) < 0){
                balanceAfterLabel.setText("No Balance!!! Please Ask Admin to Top-up");
            }else{
                balanceAfterLabel.setText("Balance After: RM"+(afterBalance));
            }

            // 配達ボタン選択時のみ配送料金パネルを表示
            deliveryFeePanel.setVisible(deliveryButton.isSelected());
        };

        // 初回の料金更新
        updateFees.run();

        // deliveryButtonおよびpickupButtonの選択変更時に料金を再計算するリスナーを追加
        deliveryButton.addActionListener(e -> updateFees.run());
        pickupButton.addActionListener(e -> updateFees.run());

        // ----------------------------
        // 全体レイアウト：上下をJSplitPaneで分割（上半分＝ItemCardリスト、下半分＝オーダー詳細）
        // ----------------------------
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, bottomPanel);
        splitPane.setResizeWeight(0.4);
        splitPane.setDividerSize(4);
        splitPane.setBorder(null);

        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createCostPanel(String label, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel(label);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(valueLabel, BorderLayout.EAST);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        return panel;
    }

    private JToggleButton createStyledToggleButton(String text) {
        JToggleButton button = new JToggleButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(100, 149, 237));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(8, 15, 8, 15));
        button.setPreferredSize(new Dimension(150, 40));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!button.isSelected()) {
                    button.setBackground(new Color(100, 149, 237).darker());
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!button.isSelected()) {
                    button.setBackground(new Color(100, 149, 237));
                }
            }
        });

        // Update change listener to use green color for selected state
        button.addChangeListener(e -> {
            if (button.isSelected()) {
                button.setBackground(new Color(40, 167, 69)); // Green color when selected
            } else {
                button.setBackground(new Color(100, 149, 237));
            }
        });

        return button;
    }

    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(baseColor.darker(), 1));
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(8, 15, 8, 15));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor);
            }
        });
        return button;
    }
}


class ItemCard extends JPanel {
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = Color.LIGHT_GRAY;
    private MainFrame mainFrame;

    public ItemCard(MainFrame mainFrame, CartItem item){
        this.mainFrame = mainFrame;

        JPanel mainPanel = new JPanel();
        mainPanel.setPreferredSize(new Dimension(340,66));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
//        mainPanel.setBorder(new EmptyBorder(0, 20,0,0));

        // 商品画像（画像パスから ImageIcon を生成。必要に応じてリサイズ）
        ImageIcon icon = new ImageIcon("src/Data/Image/test.png");
        Image scaledImage = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        mainPanel.add(imageLabel, BorderLayout.CENTER);


        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.X_AXIS));

        JLabel menuName = new JLabel();
        menuName.setText(item.getMenu().getName());
        menuName.setBorder(new EmptyBorder(0, 30,5,0));

        JLabel amount = new JLabel();
        amount.setText("x "+item.getAmount().toString());
        amount.setBorder(new EmptyBorder(0, 30,5,0));

        JLabel menuPrice = new JLabel();
        menuPrice.setText("RM" + item.getMenu().getPrice());
        menuPrice.setBorder(new EmptyBorder(0, 30,5,0));

        rightPanel.add(menuName);
        rightPanel.add(amount);
        rightPanel.add(menuPrice);

        // `rightPanel` を `mainPanel` の中央に配置
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        // ボタンを右寄せするためのパネル
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        add(mainPanel);
    }

    private JToggleButton createStyledToggleButton(String text) {
        JToggleButton button = new JToggleButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLUE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!button.isSelected()) {
                    button.setBackground(button.getBackground().darker());
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!button.isSelected()) {
                    button.setBackground(Color.BLUE);
                }
            }
        });
        return button;
    }

    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor);
            }
        });
        return button;
    }
}