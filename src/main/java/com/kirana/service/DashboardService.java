package com.kirana.service;

import com.kirana.dto.DailySalesDto;
import com.kirana.dto.DashboardStatsDto;
import com.kirana.dto.OrderDto;
import com.kirana.dto.TopSellingProductDto;
import com.kirana.dto.UserDto;
import com.kirana.entity.OrderStatus;
import com.kirana.mapper.EntityDtoMapper;
import com.kirana.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final EntityDtoMapper mapper;

    public DashboardService(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            ProductRepository productRepository,
                            InventoryRepository inventoryRepository,
                            UserRepository userRepository,
                            EntityDtoMapper mapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    public DashboardStatsDto getDashboardStatistics() {
        DashboardStatsDto stats = new DashboardStatsDto();

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();

        stats.setTotalOrders(orderRepository.count());
        stats.setTodayOrders(orderRepository.countOrdersToday(startOfDay));
        stats.setTotalRevenue(orderRepository.calculateTotalRevenue());
        stats.setTodayRevenue(orderRepository.calculateTodayRevenue(startOfDay));
        stats.setTotalProducts(productRepository.count());
        stats.setLowStockCount(inventoryRepository.countLowStockItems());
        stats.setTotalCustomers(userRepository.count());
        stats.setPendingOrders(orderRepository.countByOrderStatus(OrderStatus.PLACED));

        List<OrderDto> recentOrders = orderRepository.findAll(PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "createdAt")))
                .getContent()
                .stream()
                .map(mapper::toOrderDto)
                .collect(Collectors.toList());
        stats.setRecentOrders(recentOrders);

        return stats;
    }

    public List<DailySalesDto> getDailySalesChartData() {
        List<Object[]> rawStats = orderRepository.getDailySalesStatistics();
        List<DailySalesDto> list = new ArrayList<>();

        for (Object[] row : rawStats) {
            String date = String.valueOf(row[0]);
            long count = ((Number) row[1]).longValue();
            BigDecimal rev = (BigDecimal) row[2];
            list.add(new DailySalesDto(date, count, rev));
        }

        return list;
    }

    public List<TopSellingProductDto> getTopSellingProducts() {
        List<Object[]> rawList = orderItemRepository.findTopSellingProducts();
        List<TopSellingProductDto> list = new ArrayList<>();

        for (Object[] row : rawList) {
            String prodName = (String) row[0];
            String unit = (String) row[1];
            long qty = ((Number) row[2]).longValue();
            BigDecimal rev = (BigDecimal) row[3];
            list.add(new TopSellingProductDto(prodName, unit, qty, rev));
        }

        return list;
    }

    public List<UserDto> getAllCustomers() {
        return userRepository.findAll().stream()
                .map(mapper::toUserDto)
                .collect(Collectors.toList());
    }
}
