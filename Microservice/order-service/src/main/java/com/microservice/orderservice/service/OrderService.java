package com.microservice.orderservice.service;

import com.microservice.orderservice.entity.Order;
import com.microservice.orderservice.repository.OrderRepository;
import com.microservice.orderservice.vo.Customer;
import com.microservice.orderservice.vo.ResponseTemplateVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public ResponseTemplateVO getOrderWithCustomer(Long orderId) {
        ResponseTemplateVO vo = new ResponseTemplateVO();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        // Call Customer Service using RestTemplate
        Customer customer = restTemplate.getForObject(
                "http://CUSTOMER-SERVICE/customer/" + order.getCustomerId(),
                Customer.class);

        vo.setOrder(order);
        vo.setCustomer(customer);
        return vo;
    }

    public List<Order> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public Order updateOrder(Long orderId, Order orderDetails) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        order.setOrderName(orderDetails.getOrderName());
        order.setOrderAmount(orderDetails.getOrderAmount());
        order.setOrderStatus(orderDetails.getOrderStatus());

        return orderRepository.save(order);
    }

    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        orderRepository.delete(order);
    }
}