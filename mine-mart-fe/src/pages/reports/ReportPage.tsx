/**
 * Report Page
 * - Tồn kho hiện tại (group theo product)
 * - Danh sách hàng sắp hết hạn
 * - Tổng nhập - xuất theo khoảng thời gian
 */

import { useState } from 'react';
import { Card, Table, DatePicker, Button, Space, Tag, Statistic, Row, Col, Select, Spin } from 'antd';
import { ReloadOutlined } from '@ant-design/icons';
import { useCurrentStock, useExpiringItems, useImportExportSummary, useTopExportingUsers, useRevenueReport } from '@/hooks/useReport';
import { StockBarChart } from '@/components/charts/StockBarChart';
import { ImportExportLineChart } from '@/components/charts/ImportExportLineChart';
import { RevenueChart } from '@/components/charts/RevenueChart';
import dayjs, { Dayjs } from 'dayjs';

const ReportPage = () => {
  const [expiringBeforeDate, setExpiringBeforeDate] = useState<string | undefined>(
    dayjs().add(30, 'day').format('YYYY-MM-DD')
  );
  const [startDate, setStartDate] = useState<Dayjs>(dayjs().startOf('month'));
  const [endDate, setEndDate] = useState<Dayjs>(dayjs().endOf('month'));

  const { data: stockData, isLoading: stockLoading, refetch: refetchStock } = useCurrentStock();
  const {
    data: expiringData,
    isLoading: expiringLoading,
    refetch: refetchExpiring,
  } = useExpiringItems(expiringBeforeDate);
  const {
    data: summaryData,
    refetch: refetchSummary,
  } = useImportExportSummary(startDate.toISOString(), endDate.toISOString());
  
  const {
    data: topUsersData,
    isLoading: topUsersLoading,
    refetch: refetchTopUsers,
  } = useTopExportingUsers(startDate.toISOString(), endDate.toISOString(), 10);
  
  const [revenueGroupBy, setRevenueGroupBy] = useState<'DAY' | 'MONTH'>('DAY');
  const {
    data: revenueData,
    isLoading: revenueLoading,
    refetch: refetchRevenue,
  } = useRevenueReport(startDate.toISOString(), endDate.toISOString(), revenueGroupBy);

  const stockColumns = [
    {
      title: 'Mã sản phẩm',
      dataIndex: 'productSku',
      key: 'productSku',
    },
    {
      title: 'Tên sản phẩm',
      dataIndex: 'productName',
      key: 'productName',
    },
    {
      title: 'Tồn kho',
      dataIndex: 'totalStock',
      key: 'totalStock',
      render: (stock: number) => (
        <Tag color={stock > 0 ? 'green' : 'red'}>{stock.toLocaleString('vi-VN')}</Tag>
      ),
    },
  ];

  const expiringColumns = [
    {
      title: 'Mã sản phẩm',
      dataIndex: 'productSku',
      key: 'productSku',
    },
    {
      title: 'Tên sản phẩm',
      dataIndex: 'productName',
      key: 'productName',
    },
    {
      title: 'Số lượng còn lại',
      dataIndex: 'remainingQuantity',
      key: 'remainingQuantity',
    },
    {
      title: 'Hạn sử dụng',
      dataIndex: 'expireDate',
      key: 'expireDate',
      render: (date: string) => dayjs(date).format('DD/MM/YYYY'),
    },
    {
      title: 'Còn lại (ngày)',
      dataIndex: 'daysUntilExpiry',
      key: 'daysUntilExpiry',
      render: (days: number) => {
        if (days < 0) return <Tag color="red">Đã hết hạn</Tag>;
        if (days <= 7) return <Tag color="red">Còn {days} ngày</Tag>;
        if (days <= 30) return <Tag color="orange">Còn {days} ngày</Tag>;
        return <Tag color="green">Còn {days} ngày</Tag>;
      },
    },
  ];

  return (
    <div>
      <h2>Báo Cáo & Thống Kê</h2>

      {/* Tồn kho hiện tại - Bar Chart */}
      <Card
        title="Tồn Kho Hiện Tại"
        extra={
          <Button icon={<ReloadOutlined />} onClick={() => refetchStock()}>
            Làm mới
          </Button>
        }
        style={{ marginBottom: 16 }}
      >
        {stockData && stockData.length > 0 && (
          <StockBarChart data={stockData} />
        )}
        <Table
          columns={stockColumns}
          dataSource={stockData}
          rowKey="productId"
          loading={stockLoading}
          pagination={{ pageSize: 10 }}
          style={{ marginTop: 16 }}
        />
      </Card>

      {/* Hàng sắp hết hạn */}
      <Card
        title="Hàng Sắp Hết Hạn"
        extra={
          <Space>
            <DatePicker
              placeholder="Chọn ngày"
              value={expiringBeforeDate ? dayjs(expiringBeforeDate) : null}
              onChange={(date) =>
                setExpiringBeforeDate(date ? date.format('YYYY-MM-DD') : undefined)
              }
            />
            <Button icon={<ReloadOutlined />} onClick={() => refetchExpiring()}>
              Làm mới
            </Button>
          </Space>
        }
        style={{ marginBottom: 16 }}
      >
        <Table
          columns={expiringColumns}
          dataSource={expiringData}
          rowKey="importReceiptItemId"
          loading={expiringLoading}
          pagination={{ pageSize: 10 }}
        />
      </Card>

      {/* Tổng nhập - xuất */}
      <Card
        title="Tổng Nhập - Xuất Theo Thời Gian"
        extra={
          <Button icon={<ReloadOutlined />} onClick={() => refetchSummary()}>
            Làm mới
          </Button>
        }
      >
        <Space style={{ marginBottom: 16 }}>
          <DatePicker
            placeholder="Từ ngày"
            showTime
            value={startDate}
            onChange={(date) => date && setStartDate(date)}
            format="DD/MM/YYYY HH:mm"
          />
          <DatePicker
            placeholder="Đến ngày"
            showTime
            value={endDate}
            onChange={(date) => date && setEndDate(date)}
            format="DD/MM/YYYY HH:mm"
          />
        </Space>

        {summaryData && (
          <>
            <Row gutter={16}>
              <Col span={6}>
                <Statistic
                  title="Tổng phiếu nhập"
                  value={summaryData.totalImportReceipts}
                  suffix="phiếu"
                />
              </Col>
              <Col span={6}>
                <Statistic
                  title="Tổng phiếu xuất"
                  value={summaryData.totalExportReceipts}
                  suffix="phiếu"
                />
              </Col>
              <Col span={6}>
                <Statistic
                  title="Tổng số lượng nhập"
                  value={summaryData.totalImportQuantity}
                  suffix="đơn vị"
                />
              </Col>
              <Col span={6}>
                <Statistic
                  title="Tổng số lượng xuất"
                  value={summaryData.totalExportQuantity}
                  suffix="đơn vị"
                />
              </Col>
              <Col span={12} style={{ marginTop: 16 }}>
                <Statistic
                  title="Tổng giá trị nhập"
                  value={summaryData.totalImportValue}
                  precision={0}
                  suffix="đ"
                  formatter={(value) => `${Number(value).toLocaleString('vi-VN')}`}
                />
              </Col>
              <Col span={12} style={{ marginTop: 16 }}>
                <Statistic
                  title="Tổng giá trị xuất"
                  value={summaryData.totalExportValue}
                  precision={0}
                  suffix="đ"
                  formatter={(value) => `${Number(value).toLocaleString('vi-VN')}`}
                />
              </Col>
            </Row>
            
            {/* Line Charts: Nhập/Xuất theo số lượng và giá trị */}
            <Row gutter={16} style={{ marginTop: 24 }}>
              <Col span={12}>
                <Card title="So sánh Nhập - Xuất (Số lượng)" size="small">
                  <ImportExportLineChart data={summaryData} type="quantity" />
                </Card>
              </Col>
              <Col span={12}>
                <Card title="So sánh Nhập - Xuất (Giá trị)" size="small">
                  <ImportExportLineChart data={summaryData} type="value" />
                </Card>
              </Col>
            </Row>
          </>
        )}
      </Card>

      {/* Top nhân viên xuất kho */}
      <Card
        title="Top Nhân Viên Xuất Kho"
        extra={
          <Space>
            <DatePicker
              placeholder="Từ ngày"
              showTime
              value={startDate}
              onChange={(date) => date && setStartDate(date)}
              format="DD/MM/YYYY HH:mm"
            />
            <DatePicker
              placeholder="Đến ngày"
              showTime
              value={endDate}
              onChange={(date) => date && setEndDate(date)}
              format="DD/MM/YYYY HH:mm"
            />
            <Button icon={<ReloadOutlined />} onClick={() => refetchTopUsers()}>
              Làm mới
            </Button>
          </Space>
        }
        style={{ marginTop: 16 }}
      >
        <Table
          columns={[
            {
              title: 'STT',
              key: 'index',
              width: 60,
              render: (_: unknown, __: unknown, index: number) => index + 1,
            },
            {
              title: 'Nhân viên',
              dataIndex: 'fullName',
              key: 'fullName',
            },
            {
              title: 'Tổng số lượng xuất',
              dataIndex: 'totalExportQuantity',
              key: 'totalExportQuantity',
              align: 'right' as const,
              render: (qty: number) => qty.toLocaleString('vi-VN'),
            },
            {
              title: 'Tổng giá trị xuất',
              dataIndex: 'totalExportAmount',
              key: 'totalExportAmount',
              align: 'right' as const,
              render: (amount: number) =>
                new Intl.NumberFormat('vi-VN', {
                  style: 'currency',
                  currency: 'VND',
                }).format(amount),
            },
          ]}
          dataSource={topUsersData}
          rowKey="userId"
          loading={topUsersLoading}
          pagination={false}
        />
      </Card>

      {/* Báo cáo doanh thu */}
      <Card
        title="Báo Cáo Doanh Thu"
        extra={
          <Space>
            <DatePicker
              placeholder="Từ ngày"
              showTime
              value={startDate}
              onChange={(date) => date && setStartDate(date)}
              format="DD/MM/YYYY HH:mm"
            />
            <DatePicker
              placeholder="Đến ngày"
              showTime
              value={endDate}
              onChange={(date) => date && setEndDate(date)}
              format="DD/MM/YYYY HH:mm"
            />
            <Select
              style={{ width: 120 }}
              value={revenueGroupBy}
              onChange={(value) => setRevenueGroupBy(value)}
            >
              <Select.Option value="DAY">Theo ngày</Select.Option>
              <Select.Option value="MONTH">Theo tháng</Select.Option>
            </Select>
            <Button icon={<ReloadOutlined />} onClick={() => refetchRevenue()}>
              Làm mới
            </Button>
          </Space>
        }
        style={{ marginTop: 16 }}
      >
        {revenueLoading ? (
          <div style={{ textAlign: 'center', padding: 50 }}>
            <Spin size="large" />
          </div>
        ) : revenueData ? (
          <>
            <Row gutter={16} style={{ marginBottom: 24 }}>
              <Col span={24}>
                <Statistic
                  title="Tổng doanh thu"
                  value={revenueData.totalRevenue}
                  precision={0}
                  suffix="đ"
                  formatter={(value) =>
                    new Intl.NumberFormat('vi-VN', {
                      style: 'currency',
                      currency: 'VND',
                    }).format(Number(value))
                  }
                />
              </Col>
            </Row>
            <RevenueChart data={revenueData} />
          </>
        ) : (
          <div>Không có dữ liệu</div>
        )}
      </Card>
    </div>
  );
};

export default ReportPage;

