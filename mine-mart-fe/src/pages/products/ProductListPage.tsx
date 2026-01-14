/**
 * Product Management Page
 * Danh sách sản phẩm, lọc theo danh mục, xem tồn kho
 */

import { useState, useMemo } from 'react';
import { Button, Table, Space, Modal, Form, Input, InputNumber, Select, Switch, Popconfirm, Tag, Input as AntInput } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined } from '@ant-design/icons';
import {
  useProducts,
  useCreateProduct,
  useUpdateProduct,
  useDeleteProduct,
} from '@/hooks/useProduct';
import { useActiveCategories } from '@/hooks/useCategory';
import { useSuppliers } from '@/hooks/useSupplier';
import { useCurrentStock } from '@/hooks/useReport';
import { useAuthContext } from '@/contexts/AuthContext';
import type { ProductCreateRequest, ProductUpdateRequest } from '@/types/product';

const ProductListPage = () => {
  const { data: products, isLoading } = useProducts();
  const { data: categories } = useActiveCategories();
  const { data: suppliers } = useSuppliers();
  const { data: stockData } = useCurrentStock();
  const { isAdmin } = useAuthContext();
  const createMutation = useCreateProduct();
  const updateMutation = useUpdateProduct();
  const deleteMutation = useDeleteProduct();

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingProduct, setEditingProduct] = useState<number | null>(null);
  const [selectedCategoryId, setSelectedCategoryId] = useState<number | undefined>(undefined);
  const [searchKeyword, setSearchKeyword] = useState<string>('');
  const [stockFilter, setStockFilter] = useState<'all' | 'in-stock' | 'out-of-stock'>('all');
  const [form] = Form.useForm();

  // Tạo map stock theo productId để hiển thị tồn kho
  const stockMap = new Map(
    stockData?.map((item: { productId: number; totalStock: number }) => [item.productId, item.totalStock]) || []
  );

  const handleCreate = () => {
    setEditingProduct(null);
    form.resetFields();
    setIsModalOpen(true);
  };

  const handleEdit = (product: {
    id: number;
    categoryId: number;
    supplierId: number;
    sku: string;
    name: string;
    brand?: string;
    description?: string;
    imageUrl?: string;
    unit?: string;
    sellingPrice: number;
    isActive: boolean;
  }) => {
    setEditingProduct(product.id);
    form.setFieldsValue({
      categoryId: product.categoryId,
      supplierId: product.supplierId,
      sku: product.sku,
      name: product.name,
      brand: product.brand,
      description: product.description,
      imageUrl: product.imageUrl,
      unit: product.unit,
      sellingPrice: product.sellingPrice,
      isActive: product.isActive,
    });
    setIsModalOpen(true);
  };

  const handleDelete = (id: number) => {
    deleteMutation.mutate(id);
  };

  const handleSubmit = (values: ProductCreateRequest | ProductUpdateRequest) => {
    if (editingProduct) {
      updateMutation.mutate(
        { id: editingProduct, request: values as ProductUpdateRequest },
        {
          onSuccess: () => {
            setIsModalOpen(false);
            form.resetFields();
          },
        }
      );
    } else {
      createMutation.mutate(values as ProductCreateRequest, {
        onSuccess: () => {
          setIsModalOpen(false);
          form.resetFields();
        },
      });
    }
  };

  // Lọc products theo category, search keyword và tồn kho
  const filteredProducts = useMemo(() => {
    let filtered = products || [];
    
    // Filter theo category
    if (selectedCategoryId) {
      filtered = filtered.filter((p) => p.categoryId === selectedCategoryId);
    }
    
    // Filter theo search keyword (tên sản phẩm)
    if (searchKeyword.trim()) {
      const keyword = searchKeyword.trim().toLowerCase();
      filtered = filtered.filter((p) => 
        p.name.toLowerCase().includes(keyword) ||
        p.sku.toLowerCase().includes(keyword)
      );
    }
    
    // Filter theo tồn kho
    if (stockFilter === 'in-stock') {
      filtered = filtered.filter((p) => {
        const stock = stockMap.get(p.id) || 0;
        return stock > 0;
      });
    } else if (stockFilter === 'out-of-stock') {
      filtered = filtered.filter((p) => {
        const stock = stockMap.get(p.id) || 0;
        return stock === 0;
      });
    }
    
    return filtered;
  }, [products, selectedCategoryId, searchKeyword, stockFilter, stockMap]);

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: 'SKU',
      dataIndex: 'sku',
      key: 'sku',
    },
    {
      title: 'Tên sản phẩm',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: 'Danh mục',
      dataIndex: 'categoryName',
      key: 'categoryName',
    },
    {
      title: 'Giá bán',
      dataIndex: 'sellingPrice',
      key: 'sellingPrice',
      render: (price: number) => `${price.toLocaleString('vi-VN')} đ`,
    },
    {
      title: 'Tồn kho',
      key: 'stock',
      render: (_: unknown, record: { id: number }) => {
        const stock = stockMap.get(record.id) || 0;
        const stockNum = typeof stock === 'number' ? stock : 0;
        return <Tag color={stockNum > 0 ? 'green' : 'red'}>{stockNum}</Tag>;
      },
    },
    {
      title: 'Trạng thái',
      dataIndex: 'isActive',
      key: 'isActive',
      render: (isActive: boolean) => (
        <Tag color={isActive ? 'green' : 'red'}>{isActive ? 'Hoạt động' : 'Không hoạt động'}</Tag>
      ),
    },
    {
      title: 'Thao tác',
      key: 'action',
      render: (_: unknown, record: {
        id: number;
        categoryId: number;
        supplierId: number;
        sku: string;
        name: string;
        brand?: string;
        description?: string;
        imageUrl?: string;
        unit?: string;
        sellingPrice: number;
        isActive: boolean;
      }) => (
        <Space>
          {isAdmin ? (
            <Button
              type="link"
              icon={<EditOutlined />}
              onClick={() => handleEdit(record)}
            >
              Sửa
            </Button>
          ) : (
            <Button
              type="link"
              icon={<EditOutlined />}
              disabled
              title="Chỉ quản trị viên mới có thể sửa sản phẩm"
            >
              Sửa
            </Button>
          )}
          {isAdmin && (
            <Popconfirm
              title="Xóa sản phẩm"
              description="Bạn có chắc chắn muốn xóa sản phẩm này?"
              onConfirm={() => handleDelete(record.id)}
              okText="Xóa"
              cancelText="Hủy"
            >
              <Button type="link" danger icon={<DeleteOutlined />}>
                Xóa
              </Button>
            </Popconfirm>
          )}
        </Space>
      ),
    },
  ];

  return (
    <div>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2>Quản lý Sản phẩm</h2>
        <Space>
          <AntInput
            placeholder="Tìm kiếm theo tên sản phẩm..."
            prefix={<SearchOutlined />}
            allowClear
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
            style={{ width: 250 }}
          />
          <Select
            style={{ width: 200 }}
            placeholder="Lọc theo danh mục"
            allowClear
            value={selectedCategoryId}
            onChange={setSelectedCategoryId}
          >
            {categories?.map((cat: { id: number; name: string }) => (
              <Select.Option key={cat.id} value={cat.id}>
                {cat.name}
              </Select.Option>
            ))}
          </Select>
          <Select
            style={{ width: 180 }}
            placeholder="Lọc theo tồn kho"
            value={stockFilter}
            onChange={setStockFilter}
          >
            <Select.Option value="all">Tất cả</Select.Option>
            <Select.Option value="in-stock">Còn tồn kho</Select.Option>
            <Select.Option value="out-of-stock">Hết tồn kho</Select.Option>
          </Select>
          <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
            Thêm sản phẩm
          </Button>
        </Space>
      </div>

      <Table
        columns={columns}
        dataSource={filteredProducts}
        rowKey="id"
        loading={isLoading}
      />

      <Modal
        title={editingProduct ? 'Sửa sản phẩm' : 'Thêm sản phẩm'}
        open={isModalOpen}
        onCancel={() => {
          setIsModalOpen(false);
          form.resetFields();
        }}
        onOk={() => form.submit()}
        confirmLoading={createMutation.isPending || updateMutation.isPending}
        width={600}
      >
        <Form form={form} layout="vertical" onFinish={handleSubmit}>
          <Form.Item
            label="Danh mục"
            name="categoryId"
            rules={[{ required: true, message: 'Vui lòng chọn danh mục!' }]}
          >
            <Select placeholder="Chọn danh mục">
              {categories?.map((cat) => (
                <Select.Option key={cat.id} value={cat.id}>
                  {cat.name}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            label="Nhà cung cấp"
            name="supplierId"
            rules={[{ required: true, message: 'Vui lòng chọn nhà cung cấp!' }]}
          >
            <Select placeholder="Chọn nhà cung cấp">
              {suppliers?.map((sup) => (
                <Select.Option key={sup.id} value={sup.id}>
                  {sup.name}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            label="SKU"
            name="sku"
            rules={[{ required: true, message: 'Vui lòng nhập SKU!' }]}
          >
            <Input placeholder="Nhập SKU" />
          </Form.Item>

          <Form.Item
            label="Tên sản phẩm"
            name="name"
            rules={[{ required: true, message: 'Vui lòng nhập tên sản phẩm!' }]}
          >
            <Input placeholder="Nhập tên sản phẩm" />
          </Form.Item>

          <Form.Item label="Thương hiệu" name="brand">
            <Input placeholder="Nhập thương hiệu" />
          </Form.Item>

          <Form.Item label="Mô tả" name="description">
            <Input.TextArea rows={3} placeholder="Nhập mô tả" />
          </Form.Item>

          <Form.Item label="URL hình ảnh" name="imageUrl">
            <Input placeholder="Nhập URL hình ảnh" />
          </Form.Item>

          <Form.Item label="Đơn vị" name="unit">
            <Input placeholder="Nhập đơn vị (ví dụ: cái, kg, lít)" />
          </Form.Item>

          <Form.Item
            label="Giá bán"
            name="sellingPrice"
            rules={[{ required: true, message: 'Vui lòng nhập giá bán!' }]}
          >
            <InputNumber
              style={{ width: '100%' }}
              placeholder="Nhập giá bán"
              min={0}
              formatter={((value?: string | number) => {
                if (value === undefined || value === null) return '';
                return String(value).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
              }) as any}
              parser={((value?: string) => {
                return Number((value ?? '').replace(/\$\s?|(,*)/g, '')) || 0;
              }) as any}
            />
          </Form.Item>

          {editingProduct && (
            <Form.Item label="Trạng thái" name="isActive" valuePropName="checked">
              <Switch />
            </Form.Item>
          )}
        </Form>
      </Modal>
    </div>
  );
};

export default ProductListPage;

