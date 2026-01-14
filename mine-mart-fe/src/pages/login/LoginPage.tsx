/**
 * Login Page
 * Form đăng nhập với username và password
 * 
 * Error Handling:
 * - Hiển thị message rõ ràng từ backend (sai username, sai password, tài khoản bị khóa)
 * - Disable nút Login khi đang submit
 * - Có loading state
 * - Không để trạng thái login fail mà không có feedback
 */

import { Form, Input, Button, Card, Typography, Alert } from 'antd';
import { useLogin } from '@/hooks/useAuth';
import type { AuthenticationRequest } from '@/types/auth';
import { getToken } from '@/utils/auth';
import { Navigate } from 'react-router-dom';
import { useState } from 'react';
import { getErrorMessage } from '@/utils/error';

const { Title } = Typography;

const LoginPage = () => {
  const loginMutation = useLogin();
  const token = getToken();
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  // Nếu đã login, redirect về trang chủ
  if (token) {
    return <Navigate to="/" replace />;
  }

  const onFinish = (values: AuthenticationRequest) => {
    // Clear error message trước khi submit
    setErrorMessage(null);
    
    loginMutation.mutate(values, {
      onError: (error: unknown) => {
        // Extract error message từ backend
        const message = getErrorMessage(error);
        setErrorMessage(message);
      },
    });
  };

  return (
    <div
      style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
        background: '#f0f2f5',
      }}
    >
      <Card style={{ width: 400 }}>
        <Title level={2} style={{ textAlign: 'center', marginBottom: 32 }}>
          Mini Mart
        </Title>
        
        {/* Hiển thị error message nếu có */}
        {errorMessage && (
          <Alert
            message={errorMessage}
            type="error"
            showIcon
            closable
            onClose={() => setErrorMessage(null)}
            style={{ marginBottom: 16 }}
          />
        )}

        <Form
          name="login"
          onFinish={onFinish}
          layout="vertical"
          autoComplete="off"
          disabled={loginMutation.isPending}
        >
          <Form.Item
            label="Username"
            name="username"
            rules={[{ required: true, message: 'Vui lòng nhập username!' }]}
          >
            <Input 
              placeholder="Nhập username" 
              disabled={loginMutation.isPending}
            />
          </Form.Item>

          <Form.Item
            label="Password"
            name="password"
            rules={[{ required: true, message: 'Vui lòng nhập password!' }]}
          >
            <Input.Password 
              placeholder="Nhập password" 
              disabled={loginMutation.isPending}
            />
          </Form.Item>

          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              block
              loading={loginMutation.isPending}
              disabled={loginMutation.isPending}
            >
              {loginMutation.isPending ? 'Đang đăng nhập...' : 'Đăng nhập'}
            </Button>
          </Form.Item>
        </Form>
        <div style={{ textAlign: 'center', color: '#999', fontSize: 12 }}>
          Default: admin / admin123
        </div>
      </Card>
    </div>
  );
};

export default LoginPage;

