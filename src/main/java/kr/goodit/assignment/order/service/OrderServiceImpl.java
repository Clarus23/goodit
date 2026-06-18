package kr.goodit.assignment.order.service;

import kr.goodit.assignment.order.dto.DailySalesDto;
import kr.goodit.assignment.order.dto.OrderStatsResponse;
import kr.goodit.assignment.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public OrderStatsResponse getStats() {
        LocalDate startDate = LocalDate.now().minusDays(6);

        List<DailySalesDto> dailySales = orderRepository.findDailySalesFrom(startDate);

        Long totalAmount = dailySales.stream()
                .mapToLong(DailySalesDto::totalAmount)
                .sum();

        Long totalOrderCount = dailySales.stream()
                .mapToLong(DailySalesDto::orderCount)
                .sum();

        return new OrderStatsResponse(totalAmount, totalOrderCount, dailySales);
    }
}
