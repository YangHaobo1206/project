package com.example.fooddelivery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private Long userCount;
    private List<ChartItem> dishStats;
    private List<ChartItem> orderStats;
    private List<ChartItem> orderByDate;
}
