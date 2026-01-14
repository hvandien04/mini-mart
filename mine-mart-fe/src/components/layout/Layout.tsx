/**
 * Main Layout Component
 * Sidebar navigation + content area
 */

import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Layout as AntLayout, Menu, Button, Space, Typography, Skeleton } from 'antd';
import {
  AppstoreOutlined,
  ShoppingOutlined,
  ImportOutlined,
  ExportOutlined,
  BarChartOutlined,
  LogoutOutlined,
  UserOutlined,
  TeamOutlined,
  ShopOutlined,
  UsergroupAddOutlined,
} from '@ant-design/icons';
import { useLogout } from '@/hooks/useAuth';
import { useAuthContext } from '@/contexts/AuthContext';
import { useState } from 'react';
import AiAssistantButton from '@/components/ai/AiAssistantButton';

const { Text } = Typography;

const { Header, Sider, Content } = AntLayout;

const Layout = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const logout = useLogout();
  const { user, isAdmin } = useAuthContext();
  const [collapsed, setCollapsed] = useState(false);

  const menuItems = [
    {
      key: '/products',
      icon: <ShoppingOutlined />,
      label: 'Sản phẩm',
    },
    {
      key: '/imports',
      icon: <ImportOutlined />,
      label: 'Nhập kho',
    },
    {
      key: '/exports',
      icon: <ExportOutlined />,
      label: 'Xuất kho',
    },
    {
      key: '/reports',
      icon: <BarChartOutlined />,
      label: 'Báo cáo',
    },
    {
      key: '/customers',
      icon: <UsergroupAddOutlined />,
      label: 'Khách hàng',
    },
    ...(isAdmin
      ? [
          {
            key: '/categories',
            icon: <AppstoreOutlined />,
            label: 'Danh mục',
          },
          {
            key: '/suppliers',
            icon: <ShopOutlined />,
            label: 'Nhà cung cấp',
          },
          {
            key: '/users',
            icon: <TeamOutlined />,
            label: 'Quản lý nhân viên',
          },
        ]
      : []),
  ];


  return (
    <AntLayout style={{ height: '100vh', overflow: 'hidden' }}>
      <Sider 
        collapsible 
        collapsed={collapsed} 
        onCollapse={setCollapsed}
        style={{
          position: 'fixed',
          left: 0,
          top: 0,
          bottom: 0,
          height: '100vh',
          overflow: 'auto',
        }}
      >
        <div
          style={{
            height: 32,
            margin: 16,
            background: 'rgba(255, 255, 255, 0.3)',
            borderRadius: 6,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: 'white',
            fontWeight: 'bold',
          }}
        >
          {collapsed ? 'MM' : 'Mini Mart'}
        </div>
        <Menu
          theme="dark"
          selectedKeys={[location.pathname]}
          mode="inline"
          items={menuItems}
          onClick={({ key }: { key: string }) => navigate(key)}
        />
      </Sider>
      <AntLayout style={{ marginLeft: collapsed ? 80 : 200, height: '100vh', display: 'flex', flexDirection: 'column' }}>
        <Header
          style={{
            padding: '0 24px',
            background: '#fff',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            flexShrink: 0,
          }}
        >
          <div></div>
          <Space>
            {user ? (
              <Space>
                <UserOutlined />
                <Text strong>{user.fullName || user.username}</Text>
                <Text type="secondary">({user.role})</Text>
              </Space>
            ) : (
              <Skeleton.Button active size="small" style={{ width: 150, height: 22 }} />
            )}
            <Button type="text" icon={<LogoutOutlined />} onClick={logout}>
              Đăng xuất
            </Button>
          </Space>
        </Header>
        <Content
          style={{
            margin: '24px 16px',
            padding: 24,
            background: '#fff',
            overflow: 'auto',
            flex: 1,
          }}
        >
          <Outlet />
        </Content>
      </AntLayout>
      <AiAssistantButton />
    </AntLayout>
  );
};

export default Layout;

