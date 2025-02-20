package com.java_assignment.group.View.Customer;

import com.java_assignment.group.Controller.CartController;
import com.java_assignment.group.MainFrame;
import com.java_assignment.group.Model.Cart;
import com.java_assignment.group.Model.Vender;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import com.java_assignment.group.Model.Menu;
import com.java_assignment.group.View.Admin.VenderEditPage;

/**
 * VenderList は複数の VendorCard を縦に並べたコンテナです。
 */
public class VenderList extends JPanel {
    private MainFrame mainFrame;
    private CartController cartController;
    private Cart cart;

    public VenderList(List<Vender> venders, MainFrame frame) {
        JPanel mainPanel = new JPanel();
        setLayout(new BorderLayout());
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

//        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Vender List");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        System.out.println("vender list is here");
        System.out.println(venders);

        if (venders != null) {
            System.out.println("vender is not null");
            for (Vender vender : venders) {
                mainPanel.add(new VendorCard(vender, frame));
                mainPanel.add(Box.createVerticalStrut(10)); // カード間のスペース
            }
        }


        // スクロール可能にする
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10); // スクロール速度を調整
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(scrollPane, BorderLayout.CENTER);
    }
}

/**
 * VendorCard は１つのベンダー情報をカードとして表示します。
 * ・背景は暗めの色で、丸みのある角を描画
 * ・左上ヘッダーにベンダー名を表示
 * ・下部に最大２件の商品の ProductCard を横並びで配置
 */
class VendorCard extends JPanel {
    public VendorCard(Vender vender, MainFrame frame) {
        // レイアウトは BorderLayout でヘッダーと商品の領域に分割
        setLayout(new BorderLayout());
        setOpaque(false); // 背景は paintComponent で描画する
//        setMinimumSize(new Dimension(120, 100));

        JPanel headerComponents = new JPanel();

        // ヘッダー：左上にベンダーの名前を表示
        JLabel header = new JLabel(vender.getStoreName());
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.setBorder(new EmptyBorder(10, 10, 0, 10));

        JButton goToStoreButton = new JButton();
        goToStoreButton.setText("Order here");
        goToStoreButton.addActionListener(e -> {
            VenderStorePage storePage = new VenderStorePage(frame, vender);
            frame.addPanel("VenderStorePage", storePage);
            frame.switchTo("VenderStorePage");
        });

        headerComponents.add(header, BorderLayout.WEST);
        headerComponents.add(goToStoreButton, BorderLayout.EAST);
        headerComponents.setOpaque(false);
        add(headerComponents, BorderLayout.NORTH);

        // 商品パネル：FlowLayout で横並び（左寄せ）
        JPanel productPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        productPanel.setMaximumSize(new Dimension(200, 100));
        productPanel.setOpaque(false); // 背景透過

        // ベンダーに紐付く商品のリストから、最大２件を追加
        List<Menu> menus = vender.getItems();

        System.out.println(menus);
        System.out.println("this is menu items");
        int count = 0;
        for (Menu menu : menus) {
            if (count >= 2) break;
            productPanel.add(new ProductCard(menu));
            count++;
        }

        add(productPanel, BorderLayout.CENTER);

        // パディング
        setBorder(new EmptyBorder(10, 10, 10, 10));
        // 推奨サイズ（実際はレイアウトやコンテナに合わせて調整してください）
        setPreferredSize(new Dimension(300, 250));
    }

    @Override
    protected void paintComponent(Graphics g) {
        // 丸みのある暗い背景を描画
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 背景色（暗めのグレー）
        g2.setColor(Color.WHITE);
        // 角丸の長方形を描画
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        g2.dispose();

        super.paintComponent(g);
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