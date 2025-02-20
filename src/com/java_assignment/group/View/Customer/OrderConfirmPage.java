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
            cart.getCartItems().get(1).getMenuId();

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

        JLabel titleLabel = new JLabel("Order Confirm Page");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(titleLabel, BorderLayout.NORTH);


        // ----------------------------
        // 上半分：ItemCardリスト（スクロール可能）
        // ----------------------------
        JPanel menuListPanel = new JPanel();
        menuListPanel.setLayout(new BoxLayout(menuListPanel, BoxLayout.Y_AXIS));
        if (cart != null && cart.getCartItems() != null) {
            for (CartItem item : cart.getCartItems()) {
                ItemCard card = new ItemCard(mainFrame, item);
                card.setMaximumSize(new Dimension(Short.MAX_VALUE, 66));
                menuListPanel.add(card);
                menuListPanel.add(Box.createVerticalStrut(5));
            }
        } else {
            menuListPanel.add(new JLabel("No items in cart."));
        }
        menuListPanel.add(Box.createVerticalGlue());
        JScrollPane itemScrollPane = new JScrollPane(menuListPanel);
        itemScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        itemScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        itemScrollPane.getVerticalScrollBar().setUnitIncrement(10);
        itemScrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(itemScrollPane, BorderLayout.CENTER);

        // ----------------------------
        // 下半分：オーダー詳細（デリバリー／ピックアップ、税金、デリバリー料金、サービス料金、合計、オーダーボタン）
        // ----------------------------
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ① オーダータイプ選択パネル
        JPanel orderTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JToggleButton pickupButton = new JToggleButton("Pick up at store");
        JToggleButton deliveryButton = new JToggleButton("Delivery", true); // デフォルトはDelivery選択
        ButtonGroup orderTypeGroup = new ButtonGroup();
        orderTypeGroup.add(pickupButton);
        orderTypeGroup.add(deliveryButton);
        orderTypePanel.add(pickupButton);
        orderTypePanel.add(deliveryButton);
        bottomPanel.add(orderTypePanel);
        bottomPanel.add(Box.createVerticalStrut(10));

        JPanel taxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel taxLabel = new JLabel("Tax: RM");
        taxPanel.add(taxLabel);
        bottomPanel.add(taxPanel);
        bottomPanel.add(Box.createVerticalStrut(10));

        // ③ デリバリー料金（Delivery選択時のみ表示）
        JPanel deliveryFeePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel deliveryFeeLabel = new JLabel("Delivery Fee: RM");
        deliveryFeePanel.add(deliveryFeeLabel);
        bottomPanel.add(deliveryFeePanel);
        bottomPanel.add(Box.createVerticalStrut(10));

        // ④ サービス料金表示パネル
        JPanel serviceChargePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel serviceChargeLabel = new JLabel("Service Charge: RM");
        serviceChargePanel.add(serviceChargeLabel);
        bottomPanel.add(serviceChargePanel);
        bottomPanel.add(Box.createVerticalStrut(10));

        // ⑤ 合計金額表示パネル
        JPanel totalPricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel totalPriceLabel = new JLabel("Total Price: RM");
        totalPricePanel.add(totalPriceLabel);
        bottomPanel.add(totalPricePanel);
        bottomPanel.add(Box.createVerticalStrut(10));

        JPanel currentBalancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel currentBalanceLabel = new JLabel("Current Balance: RM");
        currentBalancePanel.add(currentBalanceLabel);
        bottomPanel.add(currentBalancePanel);
        bottomPanel.add(Box.createVerticalStrut(10));

        JPanel balanceAfterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel balanceAfterLabel = new JLabel("Balance After: RM");
        balanceAfterPanel.add(balanceAfterLabel);
        bottomPanel.add(balanceAfterPanel);
        bottomPanel.add(Box.createVerticalStrut(10));

        JPanel addressPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel addressLabel = new JLabel("Address: ");
        JTextArea addressField = new JTextArea(3, 20); // 3行 x 20列
        addressField.setLineWrap(true);
        addressField.setWrapStyleWord(true);
        addressPanel.add(addressLabel);
        addressPanel.add(new JScrollPane(addressField)); // スクロール可能に
        bottomPanel.add(addressPanel);
        bottomPanel.add(Box.createVerticalStrut(10));

        // ⑥ オーダーボタンパネル
        JPanel orderButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton orderButton = new JButton("Place Order");
        JButton goBackButton = new JButton("Go back & Edit");
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

            double deliveryFee = deliveryButton.isSelected() ? 5.0 : 0.0;
            double serviceCharge = totalPriceOfCart * 0.1;
            double taxFee = (totalPriceOfCart + deliveryFee + serviceCharge) * 0.1;
            double totalPrice = totalPriceOfCart + deliveryFee + serviceCharge + taxFee;
            double currentBalance = authController.getCurrentUser().getWallet().getBalance();

            taxLabel.setText("Tax: RM" + taxFee);
            deliveryFeeLabel.setText("Delivery Fee: RM" + deliveryFee);
            serviceChargeLabel.setText("Service Charge: RM" + serviceCharge);
            totalPriceLabel.setText("Total Price: RM" + totalPrice);
            currentBalanceLabel.setText("Current Balance: RM" + currentBalance);
            if ((currentBalance-totalPrice) < 0){
                balanceAfterLabel.setText("No Balance!!! Please Ask Admin to Top-up");
            }else{
                balanceAfterLabel.setText("Balance After: RM"+(currentBalance-totalPrice));
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
        splitPane.setResizeWeight(0.5); // 上下をほぼ半々に
        splitPane.setDividerSize(5);
        splitPane.setOneTouchExpandable(true);

        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
    }
}


class ItemCard extends JPanel{
    private MainFrame mainFrame;

    public ItemCard(MainFrame mainFrame, CartItem item){
        this.mainFrame = mainFrame;

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
}