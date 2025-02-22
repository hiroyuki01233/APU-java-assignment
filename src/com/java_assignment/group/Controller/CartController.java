package com.java_assignment.group.Controller;

import com.java_assignment.group.Model.Cart;
import com.java_assignment.group.Model.CartItem;
import com.java_assignment.group.Model.OrderItem;
import com.java_assignment.group.Model.TxtModelRepository;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CartController {
    private TxtModelRepository<Cart> cartRepository;
    private TxtModelRepository<CartItem> cartItemRepository;

    public CartController() throws IOException {
        cartRepository = new TxtModelRepository<>("Data/cart.txt", Cart::fromCsv, Cart::toCsv);
        cartItemRepository = new TxtModelRepository<>("Data/cart_item.txt", CartItem::fromCsv, CartItem::toCsv);
    }

    /**
     * userごとのcart_item一覧取得
     */
    public List<CartItem> getCartItemsByUser(String userId) throws IOException {
        List<CartItem> items = cartItemRepository.readAll();
        return items.stream()
                .filter(item -> item.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public Cart getCartByUserIdAndVenderId(String userId, String venderId) {
        try{
            for (Cart cart : cartRepository.readAll()){
                if (cart.getUserId().equals(userId) && cart.getVenderId().equals(venderId)){
                    return cart;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<CartItem> getCartItemsByCartId(String cartId) throws IOException {
        List<CartItem> cartItems = cartItemRepository.readAll();
        return cartItems.stream()
                .filter(item -> item.getCartId().equals(cartId))
                .collect(Collectors.toList());
    }

    public Cart createCart(Cart cart) {
        try {
            List<Cart> items = cartRepository.readAll();
            String cartId = UUID.randomUUID().toString();
            cart.setId(cartId);
            items.add(cart);
            cartRepository.writeAll(items, false);
            return cart;
        } catch(IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * cart_itemの追加
     */
    public boolean addCartItem(CartItem cartItem) {
        try {
            List<CartItem> items = cartItemRepository.readAll();
            String cartItemId = UUID.randomUUID().toString();
            cartItem.setId(cartItemId);
            items.add(cartItem);
            cartItemRepository.writeAll(items, false);
            return true;
        } catch(IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * cart_itemの編集
     */
    public boolean updateCartItem(CartItem updatedCartItem) {
        try {
            List<CartItem> items = cartItemRepository.readAll();
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getId().equals(updatedCartItem.getId())) {
                    items.set(i, updatedCartItem);
                    break;
                }
            }
            cartItemRepository.writeAll(items, false);
            return true;
        } catch(IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * cart_itemの削除
     */
    public boolean deleteCartItem(String cartItemId) {
        try {
            List<CartItem> items = cartItemRepository.readAll();
            items = items.stream()
                    .filter(item -> !item.getId().equals(cartItemId))
                    .collect(Collectors.toList());
            cartItemRepository.writeAll(items, false);
            return true;
        } catch(IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
