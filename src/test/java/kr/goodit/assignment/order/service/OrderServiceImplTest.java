package kr.goodit.assignment.order.service;

import kr.goodit.assignment.order.dto.DailySalesDto;
import kr.goodit.assignment.order.dto.OrderStatsResponse;
import kr.goodit.assignment.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderServiceImpl;

    @Test
    void getStats_주문_있을때_집계_정상_반환() {
        // given
        LocalDate today = LocalDate.now();
        List<DailySalesDto> mockDailySales = List.of(
                new DailySalesDto(today.minusDays(2), 1200000L, 1L),
                new DailySalesDto(today.minusDays(1), 130000L, 2L),
                new DailySalesDto(today, 1800000L, 1L)
        );
        when(orderRepository.findDailySalesFrom(any(LocalDate.class)))
                .thenReturn(mockDailySales);

        // when
        OrderStatsResponse response = orderServiceImpl.getStats();

        // then
        assertThat(response.totalAmount()).isEqualTo(3130000L);
        assertThat(response.totalOrderCount()).isEqualTo(4L);
        assertThat(response.dailySales()).hasSize(3);
        assertThat(response.dailySales()).isEqualTo(mockDailySales);
    }

    @Test
    void getStats_주문_없을때_0_반환() {
        // given
        when(orderRepository.findDailySalesFrom(any(LocalDate.class)))
                .thenReturn(List.of());

        // when
        OrderStatsResponse response = orderServiceImpl.getStats();

        // then
        assertThat(response.totalAmount()).isEqualTo(0L);
        assertThat(response.totalOrderCount()).isEqualTo(0L);
        assertThat(response.dailySales()).isEmpty();
    }

    @Test
    void getStats_startDate가_오늘기준_6일전() {
        // given
        when(orderRepository.findDailySalesFrom(any(LocalDate.class)))
                .thenReturn(List.of());
        ArgumentCaptor<LocalDate> captor = ArgumentCaptor.forClass(LocalDate.class);

        // when
        orderServiceImpl.getStats();

        // then
        verify(orderRepository).findDailySalesFrom(captor.capture());
        assertThat(captor.getValue()).isEqualTo(LocalDate.now().minusDays(6));
    }
}