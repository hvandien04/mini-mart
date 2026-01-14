/**
 * Export Receipt Create Page
 * Form tạo phiếu xuất kho
 * 
 * QUAN TRỌNG: FE chỉ gửi productId và quantity
 * Backend tự động xử lý:
 * - FIFO + hạn sử dụng (ưu tiên lô có hạn sử dụng gần nhất)
 * - Nếu 1 lô không đủ → tự động tách sang lô tiếp theo
 * - Tự động tạo nhiều ExportReceiptItem nếu cần
 * - Validate tồn kho
 */

import { useState, useMemo } from 'react';
import { Form, Input, Select, InputNumber, DatePicker, Button, Table, Space, Card, message, Tag, Alert } from 'antd';
import type { Dayjs } from 'dayjs';
import { PlusOutlined, DeleteOutlined, SaveOutlined, WarningOutlined } from '@ant-design/icons';
import { useCreateExportReceipt } from '@/hooks/useExport';
import { useCustomers } from '@/hooks/useCustomer';
import { useActiveProducts } from '@/hooks/useProduct';
import { useCurrentStock } from '@/hooks/useReport';
import type { ExportReceiptItemRequest } from '@/types/export';
import { useNavigate } from 'react-router-dom';
import dayjs from 'dayjs';

const ExportReceiptCreatePage = () => {
  const navigate = useNavigate();
  const createMutation = useCreateExportReceipt();
  const { data: customers } = useCustomers();
  const { data: products } = useActiveProducts();
  const { data: stockData } = useCurrentStock();
  const [form] = Form.useForm();
  const [items, setItems] = useState<ExportReceiptItemRequest[]>([]);

  // Tạo map stock theo productId
  const stockMap = new Map(
    stockData?.map((item) => [item.productId, item.totalStock]) || []
  );

  // Kiểm tra xem có item nào có số lượng > tồn kho không
  const hasInsufficientStock = useMemo(() => {
    return items.some((item: ExportReceiptItemRequest) => {
      if (!item.productId || item.quantity <= 0) return false;
      const stock = stockMap.get(item.productId) || 0;
      const stockNum = typeof stock === 'number' ? stock : 0;
      return item.quantity > stockNum;
    });
  }, [items, stockMap]);

  // Lấy danh sách các sản phẩm không đủ tồn kho
  const insufficientStockItems = useMemo(() => {
    return items.filter((item: ExportReceiptItemRequest) => {
      if (!item.productId || item.quantity <= 0) return false;
      const stock = stockMap.get(item.productId) || 0;
      const stockNum = typeof stock === 'number' ? stock : 0;
      return item.quantity > stockNum;
    });
  }, [items, stockMap]);

  const handleAddItem = () => {
    setItems([
      ...items,
      {
        productId: 0,
        quantity: 0,
        sellingPrice: 0,
        note: '',
      },
    ]);
  };

  const handleRemoveItem = (index: number) => {
    setItems(items.filter((_, i) => i !== index));
  };

  const handleItemChange = (index: number, field: keyof ExportReceiptItemRequest, value: unknown) => {
    const newItems = [...items];
    newItems[index] = { ...newItems[index], [field]: value };
    setItems(newItems);
  };

  const handleSubmit = (values: { customerId: number; exportDate: Dayjs; note?: string }) => {
    if (items.length === 0) {
      message.warning('Vui lòng thêm ít nhất một mặt hàng!');
      return;
    }

    // Validate items
    const invalidItems = items.filter(
      (item) => !item.productId || item.quantity <= 0 || item.sellingPrice <= 0
    );
    if (invalidItems.length > 0) {
      message.warning('Vui lòng điền đầy đủ thông tin cho tất cả mặt hàng!');
      return;
    }

    // Validate tồn kho (FE chỉ check tổng, backend sẽ validate chi tiết)
    const insufficientStock = items.filter((item: ExportReceiptItemRequest) => {
      const stock = stockMap.get(item.productId) || 0;
      const stockNum = typeof stock === 'number' ? stock : 0;
      return item.quantity > stockNum;
    });
    if (insufficientStock.length > 0) {
      const productNames = insufficientStock
        .map((item) => {
          const product = products?.find((p: { id: number }) => p.id === item.productId);
          const stock = stockMap.get(item.productId) || 0;
          const stockNum = typeof stock === 'number' ? stock : 0;
          return `${product?.name || 'Sản phẩm'} (Cần: ${item.quantity}, Có: ${stockNum})`;
        })
        .join(', ');
      message.error(`Không đủ tồn kho cho: ${productNames}`);
      return;
    }

    const request = {
      customerId: values.customerId,
      exportDate: values.exportDate.toISOString(),
      items: items.map((item) => ({
        productId: item.productId,
        quantity: item.quantity,
        sellingPrice: item.sellingPrice,
        note: item.note || undefined,
      })),
      note: values.note || undefined,
    };

    createMutation.mutate(request, {
      onSuccess: (response: { result?: { items?: unknown[] } }) => {
        // Hiển thị thông tin các lô đã được xuất
        if (response.result?.items) {
          message.success(
            `Xuất kho thành công! Đã tạo ${response.result.items.length} item(s) từ các lô khác nhau.`
          );
        }
        navigate('/exports');
      },
      onError: (error) => {
        // Lỗi từ backend (ví dụ: insufficient stock) đã được xử lý trong useCreateExportReceipt
        // Nhưng có thể hiển thị thêm thông tin chi tiết nếu cần
        console.error('Export receipt creation error:', error);
      },
    });
  };

  const itemColumns = [
    {
      title: 'Sản phẩm',
      key: 'product',
      render: (_: unknown, _record: unknown, index: number) => {
        return (
          <Select
            style={{ width: 250 }}
            placeholder="Chọn sản phẩm"
            value={items[index]?.productId || undefined}
            onChange={(value) => handleItemChange(index, 'productId', value)}
            showSearch
            filterOption={(input, option) => {
              const label = typeof option?.label === 'string' ? option.label : String(option?.label ?? '');
              return label.toLowerCase().includes(input.toLowerCase());
            }}
          >
            {products?.map((p: { id: number; name: string; sku: string }) => (
              <Select.Option key={p.id} value={p.id} label={p.name}>
                {p.name} ({p.sku})
              </Select.Option>
            ))}
          </Select>
        );
      },
    },
    {
      title: 'Tồn kho',
      key: 'stock',
      render: (_: unknown, _record: unknown, index: number) => {
        const stock = stockMap.get(items[index]?.productId || 0) || 0;
        const stockNum = typeof stock === 'number' ? stock : 0;
        return <Tag color={stockNum > 0 ? 'green' : 'red'}>{stockNum}</Tag>;
      },
    },
    {
      title: 'Số lượng xuất',
      key: 'quantity',
      render: (_: unknown, _record: unknown, index: number) => {
        const item = items[index];
        const stock = stockMap.get(item?.productId || 0) || 0;
        const stockNum = typeof stock === 'number' ? stock : 0;
        const quantity = item?.quantity || 0;
        const isInsufficient = item?.productId && quantity > stockNum;
        
        return (
          <div>
            <InputNumber
              style={{ width: 120 }}
              placeholder="Số lượng"
              min={1}
              max={stockNum > 0 ? stockNum : undefined}
              value={quantity || undefined}
              onChange={(value) => handleItemChange(index, 'quantity', value || 0)}
              status={isInsufficient ? 'error' : undefined}
            />
            {isInsufficient && (
              <div style={{ marginTop: 4, color: '#ff4d4f', fontSize: 12 }}>
                <WarningOutlined /> Không đủ tồn kho (Có: {stockNum})
              </div>
            )}
          </div>
        );
      },
    },
    {
      title: 'Giá bán',
      key: 'sellingPrice',
      render: (_: unknown, _record: unknown, index: number) => (
        <InputNumber
          style={{ width: 150 }}
          placeholder="Giá bán"
          min={0}
          value={items[index]?.sellingPrice || undefined}
          onChange={(value) => handleItemChange(index, 'sellingPrice', value || 0)}
          formatter={((value?: string | number) => {
            if (value === undefined || value === null) return '';
            return String(value).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
          }) as any}
          parser={((value?: string) => {
            return Number((value ?? '').replace(/\$\s?|(,*)/g, '')) || 0;
          }) as any}
        />
      ),
    },
    {
      title: 'Ghi chú',
      key: 'note',
      render: (_: unknown, _record: unknown, index: number) => (
        <Input
          placeholder="Ghi chú"
          value={items[index]?.note || ''}
          onChange={(e) => handleItemChange(index, 'note', e.target.value)}
        />
      ),
    },
    {
      title: 'Thao tác',
      key: 'action',
      render: (_: unknown, _record: unknown, index: number) => (
        <Button
          type="link"
          danger
          icon={<DeleteOutlined />}
          onClick={() => handleRemoveItem(index)}
        >
          Xóa
        </Button>
      ),
    },
  ];

  return (
    <div>
      <h2>Tạo Phiếu Xuất Kho</h2>
      <div style={{ marginBottom: 16, padding: 16, background: '#e6f7ff', borderRadius: 4 }}>
        <strong>Lưu ý:</strong> Hệ thống tự động xử lý FIFO + hạn sử dụng. Bạn chỉ cần chọn sản phẩm
        và số lượng cần xuất. Hệ thống sẽ tự động chọn các lô có hạn sử dụng gần nhất trước.
      </div>

      <Card style={{ marginBottom: 16 }}>
        <Form form={form} layout="vertical" onFinish={handleSubmit}>
          <Form.Item
            label="Khách hàng"
            name="customerId"
            rules={[{ required: true, message: 'Vui lòng chọn khách hàng!' }]}
          >
            <Select 
              placeholder="Chọn khách hàng"
              showSearch
              filterOption={(input, option) => {
                const label = typeof option?.children === 'string' ? option.children : String(option?.children ?? '');
                return label.toLowerCase().includes(input.toLowerCase());
              }}
            >
              {customers?.map((c: { id: number; fullName: string; phone?: string }) => (
                <Select.Option key={c.id} value={c.id}>
                  {c.fullName} {c.phone ? `(${c.phone})` : ''}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            label="Ngày xuất"
            name="exportDate"
            rules={[{ required: true, message: 'Vui lòng chọn ngày xuất!' }]}
            initialValue={dayjs()}
          >
            <DatePicker showTime style={{ width: '100%' }} format="DD/MM/YYYY HH:mm" />
          </Form.Item>

          <Form.Item label="Ghi chú" name="note">
            <Input.TextArea rows={3} placeholder="Nhập ghi chú" />
          </Form.Item>
        </Form>
      </Card>

      <Card
        title="Danh sách mặt hàng"
        extra={
          <Button type="primary" icon={<PlusOutlined />} onClick={handleAddItem}>
            Thêm mặt hàng
          </Button>
        }
      >
        {hasInsufficientStock && (
          <Alert
            message="Cảnh báo: Một số sản phẩm không đủ tồn kho"
            description={
              <div>
                {insufficientStockItems.map((item: ExportReceiptItemRequest) => {
                  const product = products?.find((p: { id: number }) => p.id === item.productId);
                  const stock = stockMap.get(item.productId) || 0;
                  const stockNum = typeof stock === 'number' ? stock : 0;
                  return (
                    <div key={item.productId}>
                      • <strong>{product?.name || 'Sản phẩm'}</strong>: Cần {item.quantity}, Có {stockNum}
                    </div>
                  );
                })}
              </div>
            }
            type="error"
            showIcon
            style={{ marginBottom: 16 }}
          />
        )}
        <Table
          columns={itemColumns}
          dataSource={items}
          rowKey={(_, index) => index ?? 0}
          pagination={false}
          locale={{ emptyText: 'Chưa có mặt hàng nào. Vui lòng thêm mặt hàng.' }}
        />
        <div style={{ marginTop: 16, textAlign: 'right' }}>
          <Space>
            <Button onClick={() => navigate('/exports')}>Hủy</Button>
            <Button
              type="primary"
              icon={<SaveOutlined />}
              loading={createMutation.isPending}
              disabled={hasInsufficientStock || items.length === 0}
              onClick={() => form.submit()}
            >
              Tạo phiếu xuất
            </Button>
          </Space>
        </div>
      </Card>
    </div>
  );
};

export default ExportReceiptCreatePage;

