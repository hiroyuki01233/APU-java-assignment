package com.java_assignment.group.Component;

import com.java_assignment.group.MainFrame;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class HeaderPanel extends JPanel {
    public HeaderPanel(MainFrame mainFrame) {
        this.setLayout(new BoxLayout(this, 1));
        this.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));
        JButton homeButton = new JButton("Icon");
        homeButton.addActionListener((e) -> mainFrame.switchTo("CustomerDashboard"));
        this.add(homeButton);
    }
}
