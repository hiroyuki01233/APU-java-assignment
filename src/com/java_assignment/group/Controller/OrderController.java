package com.java_assignment.group.Controller;

import com.java_assignment.group.Model.*;

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

    public OrderController() throws IOException {
        orderRepository = new TxtModelRepository<>("src/Data/order.txt", Order::fromCsv, Order::toCsv);
        orderItemRepository = new TxtModelRepository<>("src/Data/order_item.txt", OrderItem::fromCsv, OrderItem::toCsv);
        cartItemRepository = new TxtModelRepository<>("src/Data/cart_item.txt", CartItem::fromCsv, CartItem::toCsv);
        deliveryRunnerTxtModelRepository = new TxtModelRepository<>("src/Data/delivery_runner.txt", DeliveryRunner::fromCsv, DeliveryRunner::toCsv);
        menuController = new MenuController();
        notificationController = new NotificationController();
        authController = new AuthController();
        baseUser = authController.getCurrentUser();
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
            String createdAt = LocalDateTime.now().toString();

            Double totalPrice = 0.0;
            for (CartItem item : cartItems) {
                totalPrice += item.getEachPrice() * item.getAmount();
            }
            totalPrice = Math.floor(totalPrice * 100) / 100;

            Double commission = Math.floor((totalPrice * 0.1) * 100) / 100;
            Double tax = Math.floor((totalPrice * 0.08) * 100) / 100;
            Double venderPayout = Math.floor((totalPrice - commission - tax) * 100) / 100;
            Double deliveryFee = orderType.equals("Delivery") ? 0.0 : 5.0;


            Order newOrder = new Order(
                    orderId,
                    userId,
                    venderId,
                    "",
                    orderType,
                    address,
                    "NEW",
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
                if(orders.get(i).getUserId().equals(userId)) {
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
            String createdAt = LocalDateTime.now().toString();

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
                    "NEW",
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
                        Notification newNotification = new Notification();
                        newNotification.setBaseUserId(order.getUserId());
                        newNotification.setTitle("Your order is placed");
                        newNotification.setDescription("Your order is currently preparing");
                        newNotification.setPageName("OrderProgressPage");

                        notificationController.addNotification(newNotification);

                        DeliveryRunner runner = new DeliveryRunner();
                        for (DeliveryRunner runnerItem : deliveryRunnerTxtModelRepository.readAll()){
                            Boolean available = true;
                            for (Order orderItem : orders){
                                if (
                                        (
                                                orderItem.getCurrentStatus().equals("NEW") ||
                                                        orderItem.getCurrentStatus().equals("Preparing") ||
                                                        orderItem.getCurrentStatus().equals("Ready")
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

                    if (baseUser.getUserType().equals("vender") && newStatus.equals("Declined")){
                        String newId = UUID.randomUUID().toString();
                        LocalDateTime createdAt = LocalDateTime.now();

                        Notification newNotification = new Notification(
                                newId, order.getUserId(), "Your order is Declined", "Your order is Declined, please try to order at other time.",
                                "OrderProgressPage", createdAt);
                        notificationController.addNotification(newNotification);
                    }

                    if (baseUser.getUserType().equals("vender") && newStatus.equals("Ready")){
                        String newId = UUID.randomUUID().toString();
                        LocalDateTime createdAt = LocalDateTime.now();

                        Notification newNotification = new Notification(
                                newId, order.getUserId(), "Your order is on delivery", "Your order is on delivery, please be ready to take your food.",
                                "OrderProgressPage", createdAt);
                        notificationController.addNotification(newNotification);
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
}
