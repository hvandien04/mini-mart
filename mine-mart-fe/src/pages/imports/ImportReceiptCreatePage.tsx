/**
 * Import Receipt Create Page
 * Form tạo phiếu nhập kho
 * Mỗi item sẽ tạo thành một lô hàng (batch) với quantity, remainingQuantity, expireDate
 * 
 * NGHIỆP VỤ BẮT BUỘC VỀ SUPPLIER - PRODUCT:
 * - Khi tạo phiếu nhập kho với Supplier A, CHỈ ĐƯỢC nhập các sản phẩm thuộc Supplier A
 * - KHÔNG được phép nhập sản phẩm của nhà cung cấp khác
 * - Khi chọn Supplier: CHỈ load danh sách product của supplier đó
 * - Khi đổi supplier: Reset toàn bộ product đã chọn
 * - Frontend phải enforce nghiệp vụ này, nhưng backend vẫn validate để đảm bảo an toàn
 * 
 * Ràng buộc dữ liệu:
 * - Product có FK supplier_id -> mỗi product thuộc về một supplier
 * - Backend validate tất cả products phải thuộc supplier đã chọn
 */

import { useState } from 'react';
import { Form, Input, Select, InputNumber, DatePicker, Button, Table, Space, Card, message } from 'antd';
import type { Dayjs } from 'dayjs';
import { PlusOutlined, DeleteOutlined, SaveOutlined } from '@ant-design/icons';
import { useCreateImportReceipt } from '@/hooks/useImport';
import { useSuppliers } from '@/hooks/useSupplier';
import { useProductsBySupplier } from '@/hooks/useProduct';
import type { ImportReceiptItemRequest } from '@/types/import';
import { useNavigate } from 'react-router-dom';
import dayjs from 'dayjs';

const ImportReceiptCreatePage = () => {
  const navigate = useNavigate();
  const createMutation = useCreateImportReceipt();
  const { data: suppliers } = useSuppliers();
  const [form] = Form.useForm();
  const [items, setItems] = useState<ImportReceiptItemRequest[]>([]);
  const [selectedSupplierId, setSelectedSupplierId] = useState<number | undefined>(undefined);
  
  // Load products theo supplier đã chọn
  const { data: products } = useProductsBySupplier(selectedSupplierId || 0);

  const handleAddItem = () => {
    setItems([
      ...items,
      {
        productId: 0,
        quantity: 0,
        importPrice: 0,
        expireDate: undefined,
        note: '',
      },
    ]);
  };

  const handleRemoveItem = (index: number) => {
    setItems(items.filter((_, i) => i !== index));
  };

  // Khi đổi supplier: reset toàn bộ items đã chọn
  const handleSupplierChange = (supplierId: number) => {
    setSelectedSupplierId(supplierId);
    // Reset toàn bộ items khi đổi supplier
    setItems([]);
    form.setFieldsValue({ supplierId });
  };

  const handleItemChange = (index: number, field: keyof ImportReceiptItemRequest, value: unknown) => {
    const newItems = [...items];
    newItems[index] = { ...newItems[index], [field]: value };
    setItems(newItems);
  };

  const handleSubmit = (values: { supplierId: number; importDate: Dayjs; note?: string }) => {
    if (items.length === 0) {
      message.warning('Vui lòng thêm ít nhất một mặt hàng!');
      return;
    }

    // Validate items
    const invalidItems = items.filter(
      (item) => !item.productId || item.quantity <= 0 || item.importPrice <= 0
    );
    if (invalidItems.length > 0) {
      message.warning('Vui lòng điền đầy đủ thông tin cho tất cả mặt hàng!');
      return;
    }

    const request = {
      supplierId: values.supplierId,
      importDate: values.importDate.toISOString(),
      items: items.map((item) => ({
        productId: item.productId,
        quantity: item.quantity,
        importPrice: item.importPrice,
        expireDate: item.expireDate ? dayjs(item.expireDate).format('YYYY-MM-DD') : undefined,
        note: item.note || undefined,
      })),
      note: values.note || undefined,
    };

    createMutation.mutate(request, {
      onSuccess: () => {
        navigate('/imports');
      },
    });
  };

  const itemColumns = [
    {
      title: 'Sản phẩm',
      key: 'product',
      render: (_: unknown, _record: unknown, index: number) => (
        <Select
          style={{ width: 200 }}
          placeholder="Chọn sản phẩm"
          value={items[index]?.productId || undefined}
          onChange={(value) => handleItemChange(index, 'productId', value)}
          showSearch
          filterOption={(input, option) => {
            const label = typeof option?.label === 'string' ? option.label : String(option?.label ?? '');
            return label.toLowerCase().includes(input.toLowerCase());
          }}
          disabled={!selectedSupplierId}
        >
          {products && products.length > 0 ? (
            products.map((p: { id: number; name: string; sku: string }) => (
              <Select.Option key={p.id} value={p.id} label={p.name}>
                {p.name} ({p.sku})
              </Select.Option>
            ))
          ) : (
            <Select.Option disabled value={0}>
              {selectedSupplierId ? 'Không có sản phẩm' : 'Vui lòng chọn nhà cung cấp trước'}
            </Select.Option>
          )}
        </Select>
      ),
    },
    {
      title: 'Số lượng',
      key: 'quantity',
      render: (_: unknown, _record: unknown, index: number) => (
        <InputNumber
          style={{ width: 120 }}
          placeholder="Số lượng"
          min={1}
          value={items[index]?.quantity || undefined}
          onChange={(value) => handleItemChange(index, 'quantity', value || 0)}
        />
      ),
    },
    {
      title: 'Giá nhập',
      key: 'importPrice',
      render: (_: unknown, _record: unknown, index: number) => (
        <InputNumber
          style={{ width: 150 }}
          placeholder="Giá nhập"
          min={0}
          value={items[index]?.importPrice || undefined}
          onChange={(value) => handleItemChange(index, 'importPrice', value || 0)}
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
      title: 'Hạn sử dụng',
      key: 'expireDate',
      render: (_: unknown, _record: unknown, index: number) => (
        <DatePicker
          style={{ width: 150 }}
          placeholder="Chọn ngày"
          value={items[index]?.expireDate ? dayjs(items[index].expireDate) : null}
          onChange={(date) =>
            handleItemChange(index, 'expireDate', date ? date.format('YYYY-MM-DD') : undefined)
          }
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
      <h2>Tạo Phiếu Nhập Kho</h2>
      <Card style={{ marginBottom: 16 }}>
        <Form form={form} layout="vertical" onFinish={handleSubmit}>
          <Form.Item
            label="Nhà cung cấp"
            name="supplierId"
            rules={[{ required: true, message: 'Vui lòng chọn nhà cung cấp!' }]}
          >
            <Select 
              placeholder="Chọn nhà cung cấp"
              onChange={handleSupplierChange}
              showSearch
              filterOption={(input, option) => {
                const label = typeof option?.children === 'string' ? option.children : String(option?.children ?? '');
                return label.toLowerCase().includes(input.toLowerCase());
              }}
            >
              {suppliers?.map((s: { id: number; name: string; phone?: string }) => (
                <Select.Option key={s.id} value={s.id}>
                  {s.name} {s.phone ? `(${s.phone})` : ''}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            label="Ngày nhập"
            name="importDate"
            rules={[{ required: true, message: 'Vui lòng chọn ngày nhập!' }]}
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
        <Table
          columns={itemColumns}
          dataSource={items}
          rowKey={(_, index) => index ?? 0}
          pagination={false}
          locale={{ emptyText: 'Chưa có mặt hàng nào. Vui lòng thêm mặt hàng.' }}
        />
        <div style={{ marginTop: 16, textAlign: 'right' }}>
          <Space>
            <Button onClick={() => navigate('/imports')}>Hủy</Button>
            <Button
              type="primary"
              icon={<SaveOutlined />}
              loading={createMutation.isPending}
              onClick={() => form.submit()}
            >
              Tạo phiếu nhập
            </Button>
          </Space>
        </div>
      </Card>
    </div>
  );
};

export default ImportReceiptCreatePage;

