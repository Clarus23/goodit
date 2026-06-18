package kr.goodit.assignment;

import kr.goodit.assignment.order.domain.Order;
import kr.goodit.assignment.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInit implements CommandLineRunner {

    private final OrderRepository orderRepository;

    @Override
    public void run(String... args) {
        if(orderRepository.count() > 0) return;

        LocalDate today = LocalDate.now();
        List<Order> orders = new ArrayList<>();

        String[] products = {"노트북", "키보드", "마우스", "모니터", "헤드셋"};
        long[] prices = {1800000L, 50000L, 35000L, 400000L, 120000L};

        for(int day=0; day<14; ++day) {
            LocalDate date = today.minusDays(day);
            int orderCount = (day%3)+1;

            for(int i=0; i<orderCount; ++i) {
                int idx = (day+i) % products.length;
                orders.add(Order.builder()
                        .productName(products[idx])
                        .price(prices[idx])
                        .orderDate(date)
                        .build());
            }
        }

        orderRepository.saveAll(orders);
    }
}
