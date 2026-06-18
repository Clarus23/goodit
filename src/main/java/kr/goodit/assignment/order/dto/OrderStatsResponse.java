package kr.goodit.assignment.order.dto;

import java.util.List;

public record OrderStatsResponse(
        Long totalAmount,
        Long totalOrderCount,
        List<DailySalesDto> dailySales
) {}
