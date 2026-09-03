package com.lekha.travel_planner.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lekha.travel_planner.dto.CreateOrderRequest;
import com.lekha.travel_planner.dto.OrderItemResponse;
import com.lekha.travel_planner.dto.OrderResponse;
import com.lekha.travel_planner.dto.UpdateOrderStatusRequest;
import com.lekha.travel_planner.entity.Order;
import com.lekha.travel_planner.entity.OrderItem;
import com.lekha.travel_planner.entity.OrderStatus;
import com.lekha.travel_planner.entity.Product;
import com.lekha.travel_planner.entity.User;
import com.lekha.travel_planner.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final AuthService authService;

    public OrderService(OrderRepository orderRepository, ProductService productService, AuthService authService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.authService = authService;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        User user = authService.getCurrentUserEntity();
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        BigDecimal total = BigDecimal.ZERO;

        for (var itemRequest : request.items()) {
            Product product = productService.findProduct(itemRequest.productId());
            if (!product.isActive()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is inactive: " + product.getName());
            }
            if (product.getStockQuantity() < itemRequest.quantity()) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Insufficient stock for: " + product.getName()
                );
            }

            product.setStockQuantity(product.getStockQuantity() - itemRequest.quantity());

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemRequest.quantity());
            item.setUnitPrice(product.getPrice());
            order.addItem(item);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity())));
        }

        order.setTotalAmount(total);
        return toResponse(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> listOrdersForCurrentUser() {
        User user = authService.getCurrentUserEntity();
        boolean isAdmin = user.getRoles().stream()
            .anyMatch(role -> role.getName().name().equals("ADMIN"));

        List<Order> orders = isAdmin
            ? orderRepository.findAllWithItems()
            : orderRepository.findByUserIdWithItems(user.getId());

        return orders.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long id) {
        Order order = findOrder(id);
        User user = authService.getCurrentUserEntity();
        boolean isAdmin = user.getRoles().stream()
            .anyMatch(role -> role.getName().name().equals("ADMIN"));

        if (!isAdmin && !order.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return toResponse(order);
    }

    @Transactional
    public OrderResponse updateStatus(Long id, UpdateOrderStatusRequest request) {
        Order order = findOrder(id);
        order.setStatus(request.status());
        return toResponse(orderRepository.save(order));
    }

    private Order findOrder(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
            .map(item -> new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            ))
            .toList();

        return new OrderResponse(
            order.getId(),
            order.getUser().getId(),
            order.getUser().getEmail(),
            order.getStatus(),
            order.getTotalAmount(),
            items,
            order.getCreatedAt(),
            order.getUpdatedAt()
        );
    }
}
