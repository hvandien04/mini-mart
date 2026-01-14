/**
 * Import Receipt Detail Page
 * Xem chi tiết phiếu nhập kho
 */

import { useParams, useNavigate } from 'react-router-dom';
import { Card, Descriptions, Table, Button, Tag, Spin, message, Select } from 'antd';
import { ArrowLeftOutlined } from '@ant-design/icons';
import { useImportReceipt, useImportReceiptItemsWithFilter } from '@/hooks/useImport';
import dayjs from 'dayjs';
import type { ImportReceiptItemResponse } from '@/types/import';
import { useState } from 'react';

const ImportReceiptDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const receiptId = id ? parseInt(id, 10) : 0;
  const { data: receipt, isLoading, error } = useImportReceipt(receiptId);
  const [filterStatus, setFilterStatus] = useState<boolean | undefined>(undefined);
  
  // Fetch items với filter
  const { data: filteredReceipt, isLoading: isLoadingFiltered } = useImportReceiptItemsWithFilter(
    receiptId,
    filterStatus
  );
  
  // Sử dụng filtered data nếu có filter, otherwise dùng receipt gốc
  const displayReceipt = filterStatus !== undefined ? filteredReceipt : receipt;
  const isLoadingItems = filterStatus !== undefined ? isLoadingFiltered : false;

  if (error) {
    message.error('Không thể tải chi tiết phiếu nhập kho');
  }

  const itemColumns = [
    {
      title: 'Sản phẩm',
      key: 'product',
      render: (_: unknown, record: ImportReceiptItemResponse) => (
        <div>
          <div>{record.productName}</div>
          <div style={{ fontSize: 12, color: '#999' }}>SKU: {record.productSku}</div>
        </div>
      ),
    },
    {
      title: 'Số lượng nhập',
      dataIndex: 'quantity',
      key: 'quantity',
      align: 'right' as const,
    },
    {
      title: 'Số lượng còn lại',
      dataIndex: 'remainingQuantity',
      key: 'remainingQuantity',
      align: 'right' as const,
      render: (quantity: number, record: ImportReceiptItemResponse) => (
        <Tag color={quantity > 0 ? 'green' : 'red'}>
          {quantity} / {record.quantity}
        </Tag>
      ),
    },
    {
      title: 'Giá nhập',
      dataIndex: 'importPrice',
      key: 'importPrice',
      align: 'right' as const,
      render: (price: number) => `${price.toLocaleString('vi-VN')} đ`,
    },
    {
      title: 'Hạn sử dụng',
      dataIndex: 'expireDate',
      key: 'expireDate',
      render: (date: string | undefined) =>
        date ? dayjs(date).format('DD/MM/YYYY') : <Tag color="default">Không có</Tag>,
    },
    {
      title: 'Ghi chú',
      dataIndex: 'note',
      key: 'note',
    },
  ];

  if (isLoading) {
    return (
      <div style={{ textAlign: 'center', padding: 50 }}>
        <Spin size="large" />
      </div>
    );
  }

  if (!receipt) {
    return (
      <div>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/imports')} style={{ marginBottom: 16 }}>
          Quay lại
        </Button>
        <Card>Không tìm thấy phiếu nhập kho</Card>
      </div>
    );
  }

  return (
    <div>
      <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/imports')} style={{ marginBottom: 16 }}>
        Quay lại
      </Button>

      <Card title={`Chi tiết Phiếu Nhập Kho #${receipt.id}`} style={{ marginBottom: 16 }}>
        <Descriptions column={2} bordered>
          <Descriptions.Item label="ID">{receipt.id}</Descriptions.Item>
          <Descriptions.Item label="Ngày nhập">
            {dayjs(receipt.importDate).format('DD/MM/YYYY HH:mm')}
          </Descriptions.Item>
          <Descriptions.Item label="Nhà cung cấp">{receipt.supplierName}</Descriptions.Item>
          <Descriptions.Item label="Người nhập">{receipt.userName}</Descriptions.Item>
          <Descriptions.Item label="Ngày tạo">
            {dayjs(receipt.createdAt).format('DD/MM/YYYY HH:mm')}
          </Descriptions.Item>
          {receipt.note && <Descriptions.Item label="Ghi chú" span={2}>{receipt.note}</Descriptions.Item>}
        </Descriptions>
      </Card>

      <Card 
        title="Danh sách mặt hàng"
        extra={
          <Select
            style={{ width: 150 }}
            placeholder="Lọc theo trạng thái"
            allowClear
            value={filterStatus === undefined ? undefined : filterStatus ? 'available' : 'out_of_stock'}
            onChange={(value) => {
              if (value === undefined || value === null) {
                setFilterStatus(undefined);
              } else {
                setFilterStatus(value === 'available');
              }
            }}
          >
            <Select.Option value="available">Còn hàng</Select.Option>
            <Select.Option value="out_of_stock">Hết hàng</Select.Option>
          </Select>
        }
      >
        <Table
          columns={itemColumns}
          dataSource={displayReceipt?.items || []}
          rowKey="id"
          pagination={false}
          loading={isLoadingItems}
          summary={(pageData) => {
            const totalQuantity = pageData.reduce((sum, item) => sum + item.quantity, 0);
            const totalRemaining = pageData.reduce((sum, item) => sum + item.remainingQuantity, 0);
            const totalValue = pageData.reduce(
              (sum, item) => sum + item.quantity * item.importPrice,
              0
            );

            return (
              <Table.Summary fixed>
                <Table.Summary.Row>
                  <Table.Summary.Cell index={0}>
                    <strong>Tổng cộng</strong>
                  </Table.Summary.Cell>
                  <Table.Summary.Cell index={1} align="right">
                    <strong>{totalQuantity}</strong>
                  </Table.Summary.Cell>
                  <Table.Summary.Cell index={2} align="right">
                    <strong>{totalRemaining}</strong>
                  </Table.Summary.Cell>
                  <Table.Summary.Cell index={3} align="right">
                    <strong>{totalValue.toLocaleString('vi-VN')} đ</strong>
                  </Table.Summary.Cell>
                  <Table.Summary.Cell index={4} colSpan={2}></Table.Summary.Cell>
                </Table.Summary.Row>
              </Table.Summary>
            );
          }}
        />
      </Card>
    </div>
  );
};

export default ImportReceiptDetailPage;

