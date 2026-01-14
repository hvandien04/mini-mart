/**
 * Stock Bar Chart Component
 * Hiển thị tồn kho theo sản phẩm dạng Bar Chart
 */

import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import type { StockReportResponse } from '@/types/report';

interface StockBarChartProps {
  data: StockReportResponse[];
}

export const StockBarChart = ({ data }: StockBarChartProps) => {
  // Chỉ hiển thị top 10 sản phẩm
  const chartData = data.slice(0, 10);

  return (
    <ResponsiveContainer width="100%" height={400}>
      <BarChart data={chartData}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis 
          dataKey="productName" 
          angle={-45} 
          textAnchor="end" 
          height={100}
          interval={0}
        />
        <YAxis />
        <Tooltip />
        <Legend />
        <Bar dataKey="totalStock" fill="#1890ff" name="Tồn kho" />
      </BarChart>
    </ResponsiveContainer>
  );
};


