package com.java_assignment.group.Controller;

import com.java_assignment.group.Model.*;

import java.awt.event.WindowListener;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderController {
    private TxtModelRepository<Order> orderRepository;
    private TxtModelRepository<OrderItem> orderItemRepository;
    private TxtModelRepository<CartItem> cartItemRepository; // cartからorder作成時に利用
    private MenuController menuController;
    private NotificationController notificationController;
    private AuthController authController;
    private BaseUser baseUser;
    private TxtModelRepository<DeliveryRunner> deliveryRunnerTxtModelRepository;
    private WalletController walletController;
    private BaseUser admin;

    public OrderController() throws IOException {
        this.orderRepository = new TxtModelRepository<>("src/Data/order.txt", Order::fromCsv, Order::toCsv);
        this.orderItemRepository = new TxtModelRepository<>("src/Data/order_item.txt", OrderItem::fromCsv, OrderItem::toCsv);
        this.cartItemRepository = new TxtModelRepository<>("src/Data/cart_item.txt", CartItem::fromCsv, CartItem::toCsv);
        this.deliveryRunnerTxtModelRepository = new TxtModelRepository<>("src/Data/delivery_runner.txt", DeliveryRunner::fromCsv, DeliveryRunner::toCsv);
        this.menuController = new MenuController();
        this.notificationController = new NotificationController();
        this.authController = new AuthController();
        this.baseUser = authController.getCurrentUser();
        this.walletController = new WalletController();
        List<BaseUser> users = authController.getAllBaseusers();
        for (BaseUser user: users){
            if (user.getUserType().equals("admin")){
                this.admin = user;
            }
        }

    }

    public List<Order> getOrders(){
        try{
            return orderRepository.readAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * (1) cartからorder, order_itemのデータ作成
     * ユーザーのcart_itemから新たなOrderおよびOrderItem群を作成し、
     * 作成後は該当ユーザーのcart_itemをクリアする実装例です。
     */
    public boolean createOrderFromCart(String userId, String venderId, String orderType, String address) {
        try {
            // ユーザーのcart_itemを取得（必要に応じvenderIdでフィルタ可）
            List<CartItem> cartItems = cartItemRepository.readAll().stream()
                    .filter(item -> item.getUserId().equals(userId))
                    .collect(Collectors.toList());

            if(cartItems.isEmpty()) {
                return false;
            }

            // 新規Order作成
            List<Order> orders = orderRepository.readAll();
            String orderId = UUID.randomUUID().toString();
            LocalDateTime createdAt = LocalDateTime.now();

            Double totalPrice = 0.0;
            for (CartItem item : cartItems) {
                totalPrice += item.getEachPrice() * item.getAmount();
            }
            totalPrice = Math.floor(totalPrice * 100) / 100;

            Double commission = Math.floor((totalPrice * 0.1) * 100) / 100;
            Double tax = Math.floor((totalPrice * 0.08) * 100) / 100;
            Double venderPayout = Math.floor((totalPrice - commission - tax) * 100) / 100;
            Double deliveryFee = orderType.equals("Delivery") ? 5.0 : 0.0;


            Order newOrder = new Order(
                    orderId,
                    userId,
                    venderId,
                    "",
                    orderType,
                    address,
                    "Ordered",
                    createdAt,
                    totalPrice,
                    commission,
                    tax,
                    venderPayout,
                    deliveryFee
            );
            orders.add(newOrder);
            orderRepository.writeAll(orders, false);

            // 対応するOrderItem作成
            List<OrderItem> orderItems = orderItemRepository.readAll();
            List<OrderItem> newOrderItems = cartItems.stream().map(cartItem -> {
                String orderItemId = UUID.randomUUID().toString();
                return new OrderItem(orderItemId, orderId, cartItem.getMenuId(), cartItem.getAmount(), createdAt, cartItem.getMenu().getPrice());
            }).collect(Collectors.toList());

            orderItems.addAll(newOrderItems);
            orderItemRepository.writeAll(orderItems, false);

            // 対象ユーザーのcart_itemをクリア
            List<CartItem> remainingCartItems = cartItemRepository.readAll().stream()
                    .filter(item -> !item.getUserId().equals(userId))
                    .collect(Collectors.toList());
            cartItemRepository.writeAll(remainingCartItems, false);

            return true;
        } catch(IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public Order getCurrentOrder(String userId){
        try{
            List<Order> orders = orderRepository.readAll();
            for (int i = 0; i < orders.size(); i++) {
                if(orders.get(i).getUserId().equals(userId) && (
                    "Ordered".equals(orders.get(i).getCurrentStatus()) ||
                    "Preparing".equals(orders.get(i).getCurrentStatus()) ||
                    "Preparing-runnerWaiting".equals(orders.get(i).getCurrentStatus()) ||
                    "ReadyToPickup".equals(orders.get(i).getCurrentStatus()) ||
                    "OnDelivery".equals(orders.get(i).getCurrentStatus())
                )) {
                    return orders.get(i);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * (2) order内のorder_itemの編集
     */
    public boolean updateOrder(Order updatedOrder) {
        try {
            List<Order> order = orderRepository.readAll();
            for (int i = 0; i < order.size(); i++) {
                if(order.get(i).getId().equals(updatedOrder.getId())) {
                    order.set(i, updatedOrder);
                    break;
                }
            }
            orderRepository.writeAll(order, false);
            return true;
        } catch(IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * (2) order内のorder_itemの削除
     */
    public boolean deleteOrderItem(String orderItemId) {
        try {
            List<OrderItem> orderItems = orderItemRepository.readAll();
            orderItems = orderItems.stream()
                    .filter(item -> !item.getId().equals(orderItemId))
                    .collect(Collectors.toList());
            orderItemRepository.writeAll(orderItems, false);
            return true;
        } catch(IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public List<Order> getOrdersByVender(String venderId) throws IOException {
        List<Order> orders = orderRepository.readAll();
        return orders.stream()
                .filter(order -> order.getVenderId().equals(venderId))
                .collect(Collectors.toList());
    }

    /**
     * (3) userごとの過去order一覧取得
     */
    public List<Order> getOrdersByUser(String userId) throws IOException {
        List<Order> orders = orderRepository.readAll();
        return orders.stream()
                .filter(order -> order.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    /**
     * (4) orderごとのitem一覧取得
     */
    public List<OrderItem> getOrderItemsByOrder(String orderId) throws IOException {
        List<OrderItem> orderItems = orderItemRepository.readAll();
        return orderItems.stream()
                .filter(item -> item.getOrderId().equals(orderId))
                .collect(Collectors.toList());
    }

    /**
     * (5) 過去orderからのreorder（以前の注文内容を元に新規Order, OrderItemを作成）
     */
    public boolean reorder(String previousOrderId) {
        try {
            // 以前のOrderを検索
            List<Order> orders = orderRepository.readAll();
            Order previousOrder = orders.stream()
                    .filter(order -> order.getId().equals(previousOrderId))
                    .findFirst().orElse(null);
            if(previousOrder == null) {
                return false;
            }

            // 以前のOrderItemを取得
            List<OrderItem> prevOrderItems = orderItemRepository.readAll().stream()
                    .filter(item -> item.getOrderId().equals(previousOrderId))
                    .collect(Collectors.toList());
            if(prevOrderItems.isEmpty()) {
                return false;
            }

            String newOrderId = UUID.randomUUID().toString();
            LocalDateTime createdAt = LocalDateTime.now();

            Double totalPrice = 0.0;
            for (OrderItem prevItem : prevOrderItems) {
                Menu menu = menuController.getMenuById(prevItem.getMenuId());
                totalPrice += menu.getPrice() * prevItem.getAmount();
            }
            totalPrice = Math.floor(totalPrice * 100) / 100;

            Double commission = Math.floor((totalPrice * 0.1) * 100) / 100;
            Double tax = Math.floor((totalPrice * 0.08) * 100) / 100;
            Double venderPayout = Math.floor((totalPrice - commission - tax) * 100) / 100;
            Double deliveryFee = previousOrder.getOrderType().equals("Delivery") ? 0.0 : 5.0;


            Order newOrder = new Order(
                    newOrderId,
                    previousOrder.getUserId(),
                    previousOrder.getVenderId(),
                    "",
                    previousOrder.getOrderType(),
                    previousOrder.getAddress(),
                    "Ordered",
                    createdAt,
                    totalPrice,
                    commission,
                    tax,
                    venderPayout,
                    deliveryFee
            );
            orders.add(newOrder);
            orderRepository.writeAll(orders, false);

            // 新規OrderItem作成
            List<OrderItem> orderItems = orderItemRepository.readAll();
            List<OrderItem> newOrderItems = prevOrderItems.stream().map(prevItem -> {
                String newOrderItemId = UUID.randomUUID().toString();
                Menu menu = menuController.getMenuById(prevItem.getMenuId());
                return new OrderItem(newOrderItemId, newOrderId, prevItem.getMenuId(), prevItem.getAmount(), createdAt, menu.getPrice());
            }).collect(Collectors.toList());
            orderItems.addAll(newOrderItems);
            orderItemRepository.writeAll(orderItems, false);

            return true;
        } catch(IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * (6) Orderの削除（関連するOrderItemも削除）
     */
    public boolean deleteOrder(String orderId) {
        try {
            List<Order> orders = orderRepository.readAll();
            orders = orders.stream()
                    .filter(order -> !order.getId().equals(orderId))
                    .collect(Collectors.toList());
            orderRepository.writeAll(orders, false);

            // 関連するOrderItemの削除
            List<OrderItem> orderItems = orderItemRepository.readAll();
            orderItems = orderItems.stream()
                    .filter(item -> !item.getOrderId().equals(orderId))
                    .collect(Collectors.toList());
            orderItemRepository.writeAll(orderItems, false);

            return true;
        } catch(IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * (7) Orderのステータス変更
     */
    public boolean updateOrderStatus(String orderId, String newStatus) {
        try {
            List<Order> orders = orderRepository.readAll();
            for (int i = 0; i < orders.size(); i++) {
                if(orders.get(i).getId().equals(orderId)) {
                    Order order = orders.get(i);
                    order.setCurrentStatus(newStatus);

                    if (baseUser.getUserType().equals("vender") && newStatus.equals("Preparing")){
                        DeliveryRunner runner = new DeliveryRunner();
                        for (DeliveryRunner runnerItem : deliveryRunnerTxtModelRepository.readAll()){
                            Boolean available = true;
                            for (Order orderItem : orders){
                                if (
                                        (
                                                orderItem.getCurrentStatus().equals("Ordered") ||
                                                orderItem.getCurrentStatus().equals("Preparing") ||
                                                orderItem.getCurrentStatus().equals("Preparing-runnerWaiting") ||
                                                orderItem.getCurrentStatus().equals("ReadyToPickup")
                                        ) &&
                                                orderItem.getDeliveryRunnerId().equals(runnerItem.getId())
                                ){
                                    available = false;
                                }
                            }

                            if(available){
                                runner = runnerItem;
                            }
                        }
                        order.setDeliveryRunnerId(runner.getId());
                        this.updateOrder(order);
                    }

                    LocalDateTime createdAt = LocalDateTime.now();

                    if (newStatus.equals("ForceCancelled")){
                        Notification newNotification = new Notification(
                                UUID.randomUUID().toString(), order.getUserId(), "The Order is cancelled", "Please reorder other restaurant again.",
                                "OrderProgressPage", createdAt);
                        notificationController.addNotification(newNotification);

                        Notification newNotificationForVender = new Notification(
                                UUID.randomUUID().toString(), order.getVenderId(), "The Order is cancelled", "Order was cancelled.",
                                "VenderDashboard", createdAt);
                        notificationController.addNotification(newNotificationForVender);

                        if(order.getOrderType().equals("Delivery")){
                            Notification newNotificationForRunner = new Notification(
                                    UUID.randomUUID().toString(), order.getDeliveryRunnerId(), "The Order is cancelled", "Order was cancelled.",
                                    "DeliveryRunnerDashboard", createdAt);
                            notificationController.addNotification(newNotificationForRunner);
                        }
                    }

                    if (newStatus.equals("Preparing")){
                        Notification newNotification = new Notification(
                                UUID.randomUUID().toString(), order.getUserId(), "Your food is in kitchen", "Your order is been preparing.",
                                "OrderProgressPage", createdAt);
                        notificationController.addNotification(newNotification);

                        if(order.getOrderType().equals("Delivery")){
                            Notification newNotificationForRunner = new Notification(
                                    UUID.randomUUID().toString(), order.getDeliveryRunnerId(), "You received new delivery", "You have received new delivery please accept or decline.",
                                    "DeliveryRunnerDashboard", createdAt);
                            notificationController.addNotification(newNotificationForRunner);
                        }
                    }

                    if (newStatus.equals("Declined")){
                        Notification newNotification = new Notification(
                                UUID.randomUUID().toString(), order.getUserId(), "Your order is Declined", "Your order is Declined please try to order at other time.",
                                "OrderProgressPage", createdAt);
                        notificationController.addNotification(newNotification);
                    }

                    if (newStatus.equals("ReadyToPickup")){
                        if(order.getOrderType().equals("Delivery")){
                            Notification newNotification = new Notification(
                                    UUID.randomUUID().toString(), order.getUserId(), "Your order is on delivery", "Your order is on delivery please be ready to take your food.",
                                    "OrderProgressPage", createdAt);
                            notificationController.addNotification(newNotification);

                            Notification newNotificationForRunner = new Notification(
                                    UUID.randomUUID().toString(), order.getDeliveryRunnerId(), "Food is ready", "Food is ready to pickup please delivery it.",
                                    "DeliveryRunnerDashboard", createdAt);
                            notificationController.addNotification(newNotificationForRunner);
                        }else{
                            order.setCurrentStatus("Completed");
                            this.updateOrder(order);

                            Notification newNotification = new Notification(
                                    UUID.randomUUID().toString(), order.getUserId(), "Your order is ready", "Your food is ready on restaurant please take it",
                                    "OrderProgressPage", createdAt);
                            notificationController.addNotification(newNotification);

                            this.processOrderPayment(order);
                        }
                    }

                    if(newStatus.equals("Completed")){
                        Notification newNotification = new Notification(
                                UUID.randomUUID().toString(), order.getUserId(), "Your order is completed", "Thank you for using our platform.",
                                "CustomerDashboard", createdAt);
                        notificationController.addNotification(newNotification);

                        this.processOrderPayment(order);
                    }
                    break;
                }
            }
            orderRepository.writeAll(orders, false);
            return true;
        } catch(IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public List<Order> getOrdersByDeliveryRunner(String runnderId){
        try{
            List<Order> orders = orderRepository.readAll();
            List<Order> resultOrders = new java.util.ArrayList<>(List.of());
            for (int i = 0; i < orders.size(); i++) {
                if (orders.get(i).getDeliveryRunnerId().equals(runnderId)) {
                    resultOrders.add(orders.get(i));
                }
            }
            return resultOrders;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processOrderPayment(Order order){
        String sourceUserId = order.getUserId();
        String venderId = order.getVenderId();
        String runnerId = order.getDeliveryRunnerId();

        Double amountToVender = order.getVenderPayout();
        Double amountToRunner = order.getDeliveryFee();
        Double amountToAdmin = order.getCommission() + order.getTax();

        walletController.transferFunds(sourceUserId, venderId, amountToVender, "FoodFee", order.getId(), null);
        walletController.transferFunds(sourceUserId, admin.getId(), amountToAdmin, "Comission", order.getId(), null);

        if(order.getOrderType().equals("Delivery")){
            walletController.transferFunds(sourceUserId, runnerId, amountToRunner, "DeliveryFee", order.getId(), null);
        }
    }
}
