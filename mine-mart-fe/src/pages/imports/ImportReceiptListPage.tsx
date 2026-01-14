/**
 * Import Receipt List Page
 * Danh sách các phiếu nhập kho
 */

import { useState, useMemo } from 'react';
import { Table, Button, Select, Space } from 'antd';
import { EyeOutlined, PlusOutlined } from '@ant-design/icons';
import { useImportReceipts } from '@/hooks/useImport';
import { useUsers } from '@/hooks/useUser';
import { useNavigate } from 'react-router-dom';
import dayjs from 'dayjs';

const ImportReceiptListPage = () => {
  const { data: receipts, isLoading } = useImportReceipts();
  const { data: users } = useUsers();
  const navigate = useNavigate();
  const [selectedUserId, setSelectedUserId] = useState<number | undefined>(undefined);

  // Filter và sort receipts theo user và thời gian (mới nhất trước)
  const filteredReceipts = useMemo(() => {
    let result = receipts;
    
    // Filter theo user
    if (selectedUserId) {
      result = receipts?.filter((r: { userId: number }) => r.userId === selectedUserId);
    }
    
    // Sort theo importDate (mới nhất trước)
    if (result) {
      result = [...result].sort((a: { importDate: string }, b: { importDate: string }) => {
        const dateA = dayjs(a.importDate).valueOf();
        const dateB = dayjs(b.importDate).valueOf();
        return dateB - dateA; // Descending (mới nhất trước)
      });
    }
    
    return result;
  }, [receipts, selectedUserId]);

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: 'Ngày nhập',
      dataIndex: 'importDate',
      key: 'importDate',
      render: (date: string) => dayjs(date).format('DD/MM/YYYY HH:mm'),
    },
    {
      title: 'Nhà cung cấp',
      dataIndex: 'supplierName',
      key: 'supplierName',
    },
    {
      title: 'Người nhập',
      dataIndex: 'userName',
      key: 'userName',
    },
    {
      title: 'Số mặt hàng',
      key: 'itemCount',
      render: (_: unknown, record: { items?: unknown[] }) => record.items?.length || 0,
    },
    {
      title: 'Ghi chú',
      dataIndex: 'note',
      key: 'note',
    },
    {
      title: 'Thao tác',
      key: 'action',
      render: (_: unknown, record: { id: number }) => (
        <Button
          type="link"
          icon={<EyeOutlined />}
          onClick={() => navigate(`/imports/${record.id}`)}
        >
          Xem chi tiết
        </Button>
      ),
    },
  ];

  return (
    <div>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2>Danh sách Phiếu Nhập Kho</h2>
        <Space>
          <Select
            style={{ width: 200 }}
            placeholder="Lọc theo người nhập"
            allowClear
            value={selectedUserId}
            onChange={(value) => setSelectedUserId(value || undefined)}
          >
            {users?.map((u: { id: number; fullName: string }) => (
              <Select.Option key={u.id} value={u.id}>
                {u.fullName}
              </Select.Option>
            ))}
          </Select>
          <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/imports/create')}>
            Tạo phiếu nhập
          </Button>
        </Space>
      </div>

      <Table
        columns={columns}
        dataSource={filteredReceipts}
        rowKey="id"
        loading={isLoading}
      />
    </div>
  );
};

export default ImportReceiptListPage;

