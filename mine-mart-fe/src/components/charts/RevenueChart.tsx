/**
 * Revenue Chart Component
 * Hiển thị doanh thu theo thời gian với Line chart và Bar chart
 */

import { useState } from 'react';
import { Radio, Space } from 'antd';
import {
  LineChart,
  Line,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts';
import type { RevenueReportResponse } from '@/types/report';

interface RevenueChartProps {
  data: RevenueReportResponse;
}

export const RevenueChart = ({ data }: RevenueChartProps) => {
  const [chartType, setChartType] = useState<'line' | 'bar'>('line');

  // Format data cho chart
  const chartData = data.dataPoints.map((point) => ({
    date: point.date,
    revenue: Number(point.revenue),
    quantity: point.quantity,
  }));

  // Format tooltip
  const formatTooltip = (value: number, name: string) => {
    if (name === 'revenue') {
      return [
        new Intl.NumberFormat('vi-VN', {
          style: 'currency',
          currency: 'VND',
        }).format(value),
        'Doanh thu',
      ];
    }
    return [value.toLocaleString('vi-VN'), 'Số lượng'];
  };

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Radio.Group
          value={chartType}
          onChange={(e) => setChartType(e.target.value)}
          buttonStyle="solid"
        >
          <Radio.Button value="line">Line Chart</Radio.Button>
          <Radio.Button value="bar">Bar Chart</Radio.Button>
        </Radio.Group>
      </Space>

      <ResponsiveContainer width="100%" height={400}>
        {chartType === 'line' ? (
          <LineChart data={chartData}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis
              dataKey="date"
              angle={-45}
              textAnchor="end"
              height={100}
              interval={0}
            />
            <YAxis
              yAxisId="revenue"
              orientation="left"
              tickFormatter={(value) =>
                new Intl.NumberFormat('vi-VN', {
                  notation: 'compact',
                  style: 'currency',
                  currency: 'VND',
                }).format(value)
              }
            />
            <Tooltip formatter={formatTooltip} />
            <Legend />
            <Line
              yAxisId="revenue"
              type="monotone"
              dataKey="revenue"
              fill="#1890ff"
              name="Doanh thu"
              stroke="#1890ff"
            />
          </LineChart>
        ) : (
          <BarChart data={chartData}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis
              dataKey="date"
              angle={-45}
              textAnchor="end"
              height={100}
              interval={0}
            />
            <YAxis
              yAxisId="revenue"
              orientation="left"
              tickFormatter={(value) =>
                new Intl.NumberFormat('vi-VN', {
                  notation: 'compact',
                  style: 'currency',
                  currency: 'VND',
                }).format(value)
              }
            />
            <Tooltip formatter={formatTooltip} />
            <Legend />
            <Bar
              yAxisId="revenue"
              dataKey="revenue"
              fill="#1890ff"
              name="Doanh thu"
            />
          </BarChart>
        )}
      </ResponsiveContainer>
    </div>
  );
};


