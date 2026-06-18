package kr.goodit.assignment.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record DailySalesDto(
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        Long totalAmount,
        Long orderCount
) {}
