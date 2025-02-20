package com.java_assignment.group;

import com.java_assignment.group.Model.*;

import java.io.IOException;
import java.util.List;
import javax.swing.SwingUtilities;

public class Main {
//    public static void main(String[] args) {
////        // SwingのイベントディスパッチスレッドでGUIを実行
////        SwingUtilities.invokeLater(() -> {
////            MainFrame mainFrame = new MainFrame();
////            mainFrame.setVisible(true);
////        });
//
//        String filePath = "src\\Data\\base_user.txt";
//
//        // Create repository for BaseUser using CSV conversion methods.
//        TxtModelRepository<BaseUser> repository = new TxtModelRepository<>(
//                filePath,
//                BaseUser::fromCsv,   // parser: convert CSV line to BaseUser
//                BaseUser::toCsv      // serializer: convert BaseUser to CSV line
//        );
//
//        try {
//            // Append a new BaseUser record
//            BaseUser user = new BaseUser("bu001", "0123456789", "pass123", "regular", "2025-02-11", "1");
//            repository.append(user);
//            System.out.println("Appended: " + user);
//
//            // Read all BaseUser records from the file
//            List<BaseUser> users = repository.readAll();
//            System.out.println("All BaseUser records:");
//            for (BaseUser u : users) {
//                System.out.println(u);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
