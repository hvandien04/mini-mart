/**
 * Export Receipt Detail Page
 * Xem chi tiết phiếu xuất kho
 */

import { useParams, useNavigate } from 'react-router-dom';
import { Card, Descriptions, Table, Button, Spin, message } from 'antd';
import { ArrowLeftOutlined } from '@ant-design/icons';
import { useExportReceipt } from '@/hooks/useExport';
import dayjs from 'dayjs';
import type { ExportReceiptItemResponse } from '@/types/export';

const ExportReceiptDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const receiptId = id ? parseInt(id, 10) : 0;
  const { data: receipt, isLoading, error } = useExportReceipt(receiptId);

  if (error) {
    message.error('Không thể tải chi tiết phiếu xuất kho');
  }

  const itemColumns = [
    {
      title: 'Sản phẩm',
      key: 'product',
      render: (_: unknown, record: ExportReceiptItemResponse) => (
        <div>
          <div>{record.productName}</div>
          <div style={{ fontSize: 12, color: '#999' }}>SKU: {record.productSku}</div>
        </div>
      ),
    },
    {
      title: 'Số lượng xuất',
      dataIndex: 'quantity',
      key: 'quantity',
      align: 'right' as const,
    },
    {
      title: 'Giá bán',
      dataIndex: 'sellingPrice',
      key: 'sellingPrice',
      align: 'right' as const,
      render: (price: number) => `${price.toLocaleString('vi-VN')} đ`,
    },
    {
      title: 'Thành tiền',
      key: 'total',
      align: 'right' as const,
      render: (_: unknown, record: ExportReceiptItemResponse) =>
        `${(record.quantity * record.sellingPrice).toLocaleString('vi-VN')} đ`,
    },
    {
      title: 'Lô hàng (Import Item ID)',
      dataIndex: 'importReceiptItemId',
      key: 'importReceiptItemId',
      align: 'center' as const,
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
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/exports')} style={{ marginBottom: 16 }}>
          Quay lại
        </Button>
        <Card>Không tìm thấy phiếu xuất kho</Card>
      </div>
    );
  }

  return (
    <div>
      <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/exports')} style={{ marginBottom: 16 }}>
        Quay lại
      </Button>

      <Card title={`Chi tiết Phiếu Xuất Kho #${receipt.id}`} style={{ marginBottom: 16 }}>
        <Descriptions column={2} bordered>
          <Descriptions.Item label="ID">{receipt.id}</Descriptions.Item>
          <Descriptions.Item label="Ngày xuất">
            {dayjs(receipt.exportDate).format('DD/MM/YYYY HH:mm')}
          </Descriptions.Item>
          <Descriptions.Item label="Khách hàng">{receipt.customerName}</Descriptions.Item>
          <Descriptions.Item label="Người xuất">{receipt.userName}</Descriptions.Item>
          <Descriptions.Item label="Ngày tạo">
            {dayjs(receipt.createdAt).format('DD/MM/YYYY HH:mm')}
          </Descriptions.Item>
          {receipt.note && <Descriptions.Item label="Ghi chú" span={2}>{receipt.note}</Descriptions.Item>}
        </Descriptions>
      </Card>

      <Card title="Danh sách mặt hàng">
        <Table
          columns={itemColumns}
          dataSource={receipt.items}
          rowKey="id"
          pagination={false}
          summary={(pageData) => {
            const totalQuantity = pageData.reduce((sum, item) => sum + item.quantity, 0);
            const totalValue = pageData.reduce(
              (sum, item) => sum + item.quantity * item.sellingPrice,
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
                  <Table.Summary.Cell index={2} colSpan={2}></Table.Summary.Cell>
                  <Table.Summary.Cell index={4} align="right" colSpan={2}>
                    <strong>Tổng tiền: {totalValue.toLocaleString('vi-VN')} đ</strong>
                  </Table.Summary.Cell>
                </Table.Summary.Row>
              </Table.Summary>
            );
          }}
        />
      </Card>
    </div>
  );
};

export default ExportReceiptDetailPage;

