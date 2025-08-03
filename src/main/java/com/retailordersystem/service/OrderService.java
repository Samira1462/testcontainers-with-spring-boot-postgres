package com.retailordersystem.service;

import java.util.List;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.retailordersystem.model.Order;
import com.retailordersystem.repository.OrderRepository;

@Service
@Transactional
public class OrderService {
	
	private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;

    
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

	public Order createOrder(Order order) {
    	Order savedOrder = orderRepository.save(order);
    	logger.info("Order {} saved to database:", savedOrder);
        return savedOrder;
    }

    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order updateOrder(Long id, Order order) {
        Order existingOrder = getOrder(id);
        existingOrder.setStatus(order.getStatus());
        return orderRepository.save(existingOrder);
    }

    public void deleteOrder(Long id) {
        Order order = getOrder(id);
        orderRepository.delete(order);
    }
}
