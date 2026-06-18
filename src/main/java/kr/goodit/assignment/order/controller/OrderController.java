package kr.goodit.assignment.order.controller;

import kr.goodit.assignment.order.dto.OrderStatsResponse;
import kr.goodit.assignment.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/stats")
    public OrderStatsResponse getStats() {
        return orderService.getStats();
    }
}
