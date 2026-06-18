package kr.goodit.assignment.order.repository;

import kr.goodit.assignment.order.domain.Order;
import kr.goodit.assignment.order.dto.DailySalesDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("SELECT new kr.goodit.assignment.order.dto.DailySalesDto(o.orderDate, SUM(o.price), COUNT(o)) " +
            "FROM Order o WHERE o.orderDate >= :startDate GROUP BY o.orderDate ORDER BY o.orderDate ASC")
    List<DailySalesDto> findDailySalesFrom(@Param("startDate") LocalDate startDate);
}
