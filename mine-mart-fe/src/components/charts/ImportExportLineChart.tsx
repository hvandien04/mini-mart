/**
 * Import/Export Pie Chart Component
 * So sánh nhập và xuất theo số lượng hoặc giá trị
 * Dùng Pie chart vì chỉ có 2 giá trị so sánh, trực quan hơn
 */

import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import type { ImportExportSummaryResponse } from '@/types/report';

interface ImportExportPieChartProps {
  data: ImportExportSummaryResponse;
  type: 'quantity' | 'value';
}

const COLORS = {
  quantity: ['#1890ff', '#52c41a'],
  value: ['#1890ff', '#52c41a'],
};

export const ImportExportLineChart = ({ data, type }: ImportExportPieChartProps) => {
  const importValue = type === 'quantity' ? data.totalImportQuantity : data.totalImportValue;
  const exportValue = type === 'quantity' ? data.totalExportQuantity : data.totalExportValue;
  
  const chartData = [
    {
      name: 'Nhập',
      value: importValue,
    },
    {
      name: 'Xuất',
      value: exportValue,
    },
  ];

  const renderLabel = (entry: { name: string; value: number; percent: number }) => {
    return `${entry.name}: ${type === 'value' 
      ? `${Number(entry.value).toLocaleString('vi-VN')} đ`
      : `${Number(entry.value).toLocaleString('vi-VN')}`}`;
  };

  return (
    <ResponsiveContainer width="100%" height={300}>
      <PieChart>
        <Pie
          data={chartData}
          cx="50%"
          cy="50%"
          labelLine={false}
          label={renderLabel}
          outerRadius={100}
          fill="#8884d8"
          dataKey="value"
        >
          {chartData.map((_entry, index) => (
            <Cell key={`cell-${index}`} fill={COLORS[type][index % COLORS[type].length]} />
          ))}
        </Pie>
        <Tooltip 
          formatter={(value: number) => 
            type === 'value' 
              ? `${Number(value).toLocaleString('vi-VN')} đ`
              : `${Number(value).toLocaleString('vi-VN')}`
          } 
        />
        <Legend />
      </PieChart>
    </ResponsiveContainer>
  );
};


