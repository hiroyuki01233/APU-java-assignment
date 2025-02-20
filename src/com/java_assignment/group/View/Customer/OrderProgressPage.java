package com.java_assignment.group.View.Customer;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.Controller.OrderController;
import com.java_assignment.group.MainFrame;
import com.java_assignment.group.Model.BaseUser;
import com.java_assignment.group.Model.Order;
import com.java_assignment.group.Model.OrderItem;
import com.java_assignment.group.Model.Vender;

import javax.swing.*;

import java.awt.*;
import java.io.IOException;
import java.util.jar.JarEntry;
import javax.swing.border.EmptyBorder;

public class OrderProgressPage extends JPanel {
    private MainFrame mainFrame;
    private OrderController orderController;
    private AuthController authController;
    private Order order;
    private BaseUser baseUser;

    public OrderProgressPage(MainFrame frame) {
        this.mainFrame = frame;
    }

    public void onPageDisplayed() {
        initUI();
        System.out.println("Order progress page loaded");
    }

    private void initUI() {
        removeAll();

        // 全体のレイアウトは縦方向の BoxLayout を使用
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        try{
            this.authController = new AuthController();
            this.baseUser = authController.getCurrentUser();
            this.orderController = new OrderController();
            this.order = orderController.getCurrentOrder(baseUser.getId());

            if (order == null) {
                return;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // ① 注文ステータスの表示パネル
        JPanel statusPanel = createStatusPanel();
        add(statusPanel);
        add(Box.createVerticalStrut(20)); // パネル間の余白

        // ② 金額詳細の表示パネル
        JPanel costPanel = createCostPanel();
        add(costPanel);
        add(Box.createVerticalStrut(20));

        // ③ 配送情報の表示パネル（配送先住所、ドライバーへのコンタクト）
        JPanel infoPanel = createInfoPanel();
        add(infoPanel);
        add(Box.createVerticalStrut(40));

        JPanel bottomButtonPanel = new JPanel();
        JButton goBackButton = new JButton();
        goBackButton.setText("go back");
        goBackButton.addActionListener(e -> mainFrame.switchTo("CustomerDashboard"));
        bottomButtonPanel.add(goBackButton);

        if(order.getCurrentStatus().equals("NEW")){
            JButton cancelButton = new JButton();
            cancelButton.setText("Cancel Order");
            cancelButton.addActionListener(e -> {
                orderController.updateOrderStatus(order.getId(), "Cancel");
            });
            bottomButtonPanel.add(cancelButton);
        }

        add(bottomButtonPanel, BorderLayout.SOUTH);
    }

    // 注文ステータスのパネル作成
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel();
//        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Order Status"));

        // ステータスの配列
        String[] steps = {"発注済み", "vender accept済み", "Vender調理完了", "配達中", "配達完了"};
        // ダミーの現在進捗（例：インデックス2までは完了）
        int currentStep = 2;

        JLabel currentStatus = new JLabel();
        currentStatus.setText(order.getCurrentStatus());
        panel.add(currentStatus);

        return panel;
    }

    // 金額詳細のパネル作成
    private JPanel createCostPanel() {
        Integer rowCount = 6;
        rowCount += order.getItems().size();
        if (order.getOrderType().equals("Delivery")) {rowCount++;};
        System.out.println(rowCount);

        JPanel panel = new JPanel(new GridLayout(rowCount, 3, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Order Detail"));

        for (OrderItem item: order.getItems()){
            panel.add(new JLabel(item.getMenu().getName()+" : "));
            panel.add(new JLabel("x" + item.getAmount()));
            panel.add(new JLabel("RM"+item.getEachPrice()*item.getAmount()));
        }

        panel.add(new JLabel("Tax:"));
        panel.add(new JLabel());
        panel.add(new JLabel("RM"+ order.getTax()));

        if (order.getOrderType().equals("Delivery")){
            panel.add(new JLabel("Delivery:"));
            panel.add(new JLabel());
            panel.add(new JLabel("RM"+ order.getCommission()));
        }

        panel.add(new JLabel("Charge fee:"));
        panel.add(new JLabel());
        panel.add(new JLabel("RM"+ order.getCommission()));

        panel.add(new JLabel("Total : "));
        panel.add(new JLabel());
        panel.add(new JLabel("RM"+order.getTotalPriceAllIncludes()));

        panel.add(new JLabel("Address : "));
        panel.add(new JLabel());
        panel.add(new JLabel(order.getAddress()));

        panel.add(new JLabel("Order type:"));
        panel.add(new JLabel());
        panel.add(new JLabel(order.getOrderType()));


        return panel;
    }

    // 配送情報のパネル作成（配送先住所、ドライバーへのコンタクト）
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("配送情報"));

        // ダミーの配送先住所とドライバーコンタクト
        JLabel addressLabel = new JLabel("配送先住所: 東京都渋谷区〇〇-〇〇-〇〇");
        addressLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JLabel contactLabel = new JLabel("ドライバーへのコンタクト: 090-1234-5678");
        contactLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        panel.add(addressLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(contactLabel);

        return panel;
    }
}
