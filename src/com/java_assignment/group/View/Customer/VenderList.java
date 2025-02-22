package com.java_assignment.group.View.Customer;

import com.java_assignment.group.Controller.CartController;
import com.java_assignment.group.MainFrame;
import com.java_assignment.group.Model.Cart;
import com.java_assignment.group.Model.Vender;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import com.java_assignment.group.Model.Menu;

public class VenderList extends JPanel {
    private MainFrame mainFrame;
    private CartController cartController;
    private Cart cart;
    private static final int GRID_COLUMNS = 2;
    private static final int CARD_HEIGHT = 120;
    private static final int H_GAP = 15;
    private static final int V_GAP = 15;

    public VenderList(List<Vender> venders, MainFrame frame) {
        JPanel mainPanel = new JPanel();
        setLayout(new BorderLayout());

        if (venders != null) {
            // Calculate number of rows needed
            int rows = (int) Math.ceil((double) venders.size() / GRID_COLUMNS);
            
            // Use GridLayout for the main container
            mainPanel.setLayout(new GridLayout(rows, GRID_COLUMNS, H_GAP, V_GAP));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            mainPanel.setBackground(Color.WHITE);

            // Add vendor cards to the grid
            for (Vender vender : venders) {
                JPanel cardWrapper = new JPanel(new BorderLayout());
                cardWrapper.setBackground(Color.WHITE);
                
                VendorCard card = new VendorCard(vender, frame);
                card.setPreferredSize(new Dimension(0, CARD_HEIGHT));
                
                cardWrapper.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                cardWrapper.add(card, BorderLayout.CENTER);
                
                mainPanel.add(cardWrapper);
            }
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(scrollPane, BorderLayout.CENTER);
    }
}

class VendorCard extends JPanel {
    public VendorCard(Vender vender, MainFrame frame) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(280, 200));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        setBackground(Color.WHITE);

        // Vendor Image Panel
        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(new Color(248, 249, 250));
        imagePanel.setPreferredSize(new Dimension(250, 100));
        
        // Load and scale the vendor image
        ImageIcon icon = new ImageIcon("src/Data/Image/test.png");
        Image scaledImage = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        imagePanel.add(imageLabel);

        // Vendor Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        
        JLabel nameLabel = new JLabel(vender.getStoreName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton orderButton = new JButton("Order Now");
        orderButton.setBackground(new Color(0, 123, 255));
        orderButton.setForeground(Color.WHITE);
        orderButton.setFocusPainted(false);
        orderButton.setBorderPainted(false);
        orderButton.setOpaque(true);
        orderButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        orderButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        orderButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                Color originalColor = orderButton.getBackground();
                orderButton.setBackground(originalColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                orderButton.setBackground(new Color(0, 123, 255));
            }
        });
        
        orderButton.addActionListener(e -> {
            VenderStorePage storePage = new VenderStorePage(frame, vender);
            frame.addPanel("VenderStorePage", storePage);
            frame.switchTo("VenderStorePage");
        });

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(orderButton);

        add(imagePanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);
    }
}

/**
 * ProductCard は商品の画像、名前、料金を表示するカードです。
 * ・上部に商品の画像
 * ・下部に、左寄せの名前と右寄せの料金を表示
 */
class ProductCard extends JPanel {
    public ProductCard(Menu menu) {
        setLayout(new BorderLayout());
//        setPreferredSize(new Dimension(120, 100));

        JLabel header = new JLabel("tsetestset");
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.setBorder(new EmptyBorder(10, 10, 0, 10));
        setOpaque(false);
        add(header, BorderLayout.NORTH);

        // 商品画像（画像パスから ImageIcon を生成。必要に応じてリサイズ）
        ImageIcon icon = new ImageIcon("src/Data/Image/test.png");
        // ここでは 100×100 ピクセルにスケーリング
        Image scaledImage = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        add(imageLabel, BorderLayout.CENTER);

        // 下部情報パネル：商品名（左寄せ）と料金（右寄せ）
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setPreferredSize(new Dimension(120, 30));
        JLabel nameLabel = new JLabel(menu.getName());
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        nameLabel.setForeground(Color.BLACK);
        JLabel priceLabel = new JLabel("RM" + menu.getPrice());
        priceLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        priceLabel.setForeground(Color.BLACK);
        infoPanel.add(nameLabel, BorderLayout.WEST);
        infoPanel.add(priceLabel, BorderLayout.EAST);
        // 少しのパディングを追加
        infoPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        infoPanel.setOpaque(false);
        add(infoPanel, BorderLayout.SOUTH);

        // 推奨サイズ（必要に応じて調整）
        setPreferredSize(new Dimension(120, 100));
    }
}