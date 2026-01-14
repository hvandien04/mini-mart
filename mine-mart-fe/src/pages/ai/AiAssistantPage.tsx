/**
 * AI Assistant Page
 * Chat UI để hỏi đáp về kho và preview xuất kho
 */

import { useState, useRef, useEffect } from 'react';
import { Input, Button, Space, Typography, Tag, Spin, Alert, message as antdMessage, Table, Dropdown } from 'antd';
import { SendOutlined, RobotOutlined, UserOutlined, CheckCircleOutlined, DeleteOutlined, MoreOutlined } from '@ant-design/icons';
import type { MenuProps } from 'antd';
import { useAiAssistant } from '@/hooks/useAi';
import { useCreateExportReceipt } from '@/hooks/useExport';
import type { ExportPreviewData } from '@/types/ai';
import { useNavigate } from 'react-router-dom';
import dayjs from 'dayjs';
import ReactMarkdown from 'react-markdown';

const { Title, Text, Paragraph } = Typography;

interface Message {
  role: 'user' | 'assistant';
  content: string;
  exportPreview?: ExportPreviewData;
  timestamp: Date;
  isConfirmed?: boolean; // Đã xác nhận xuất kho chưa
  exportReceiptId?: number; // ID của export receipt đã tạo
}

const STORAGE_KEY = 'ai_assistant_messages';

const AiAssistantPage = () => {
  const navigate = useNavigate();
  const [message, setMessage] = useState('');
  const [messages, setMessages] = useState<Message[]>(() => {
    // Load messages from localStorage on mount
    try {
      const saved = localStorage.getItem(STORAGE_KEY);
      if (saved) {
        const parsed = JSON.parse(saved);
        return parsed.map((msg: any) => ({
          ...msg,
          timestamp: new Date(msg.timestamp),
        }));
      }
    } catch (e) {
      console.error('Failed to load messages from localStorage', e);
    }
    return [];
  });
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const aiMutation = useAiAssistant();
  const createExportMutation = useCreateExportReceipt();

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  // Save messages to localStorage whenever messages change
  useEffect(() => {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(messages));
    } catch (e) {
      console.error('Failed to save messages to localStorage', e);
    }
  }, [messages]);

  const handleSend = async () => {
    if (!message.trim() || aiMutation.isPending) return;

    const userMessage = message.trim();
    setMessage('');
    
    // Thêm user message vào chat
    const newUserMessage: Message = {
      role: 'user',
      content: userMessage,
      timestamp: new Date(),
    };
    setMessages((prev) => [...prev, newUserMessage]);

    // Gọi AI API
    aiMutation.mutate(
      { message: userMessage },
      {
        onSuccess: (response) => {
          const aiResponse: Message = {
            role: 'assistant',
            content: response.result?.answer || 'Không có phản hồi từ AI',
            exportPreview: response.result?.exportPreview,
            timestamp: new Date(),
          };
          setMessages((prev) => [...prev, aiResponse]);
        },
        onError: () => {
          const errorMessage: Message = {
            role: 'assistant',
            content: 'Xin lỗi, có lỗi xảy ra khi xử lý yêu cầu. Vui lòng thử lại.',
            timestamp: new Date(),
          };
          setMessages((prev) => [...prev, errorMessage]);
        },
      }
    );
  };

  const handleClearHistory = () => {
    if (window.confirm('Bạn có chắc chắn muốn xóa toàn bộ lịch sử hội thoại?')) {
      setMessages([]);
      localStorage.removeItem(STORAGE_KEY);
      antdMessage.success('Đã xóa lịch sử hội thoại');
    }
  };

  const menuItems: MenuProps['items'] = [
    {
      key: 'clear',
      label: 'Xóa lịch sử',
      icon: <DeleteOutlined />,
      danger: true,
      onClick: handleClearHistory,
    },
  ];

  const handleSampleQuestion = async (question: string) => {
    if (aiMutation.isPending) return;
    
    const userMessage = question.trim();
    
    // Thêm user message vào chat
    const newUserMessage: Message = {
      role: 'user',
      content: userMessage,
      timestamp: new Date(),
    };
    setMessages((prev) => [...prev, newUserMessage]);
    
    // Gọi API
    aiMutation.mutate(
      { message: userMessage },
      {
        onSuccess: (response) => {
          const aiResponse: Message = {
            role: 'assistant',
            content: response.result?.answer || 'Không có phản hồi từ AI',
            exportPreview: response.result?.exportPreview,
            timestamp: new Date(),
          };
          setMessages((prev) => [...prev, aiResponse]);
        },
        onError: (error: any) => {
          const errorMessage: Message = {
            role: 'assistant',
            content: error?.response?.data?.message || 'Có lỗi xảy ra khi xử lý yêu cầu. Vui lòng thử lại.',
            timestamp: new Date(),
          };
          setMessages((prev) => [...prev, errorMessage]);
        },
      }
    );
  };

  const handleConfirmExport = (preview: ExportPreviewData, messageIndex: number) => {
    if (!preview) return;

    // Tạo export receipt từ preview
    // Hỗ trợ cả nhiều sản phẩm (items) và 1 sản phẩm (product/quantity - backward compatible)
    const items = preview.items && preview.items.length > 0
      ? preview.items.map(item => ({
          productId: item.product.id,
          quantity: item.quantity,
          sellingPrice: item.unitPrice,
          note: 'Xuất kho từ AI Assistant preview',
        }))
      : preview.product && preview.quantity
      ? [{
          productId: preview.product.id,
          quantity: preview.quantity,
          sellingPrice: preview.unitPrice || 0,
          note: 'Xuất kho từ AI Assistant preview',
        }]
      : [];

    if (items.length === 0) {
      antdMessage.error('Không có sản phẩm nào để xuất kho');
      return;
    }

    const request = {
      customerId: preview.customer.id,
      exportDate: dayjs().toISOString(),
      items,
      note: `Xuất kho từ AI Assistant: ${items.length} sản phẩm`,
    };

    createExportMutation.mutate(request, {
      onSuccess: (response) => {
        // Update message tại index cụ thể để đánh dấu đã xác nhận
        setMessages((prev) => 
          prev.map((msg, idx) => {
            if (idx === messageIndex && msg.exportPreview) {
              return {
                ...msg,
                isConfirmed: true,
                exportReceiptId: response.result?.id,
              };
            }
            return msg;
          })
        );
        
        antdMessage.success('Tạo phiếu xuất kho thành công!');
        // Không navigate nữa, để user có thể tiếp tục chat
        // navigate('/exports');
      },
    });
  };

  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', minHeight: 0, overflow: 'hidden' }}>
        {/* Header với nút xóa lịch sử */}
        {messages.length > 0 && (
          <div style={{
            padding: '12px 20px',
            borderBottom: '1px solid #e0e0e0',
            backgroundColor: '#fff',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            flexShrink: 0,
            boxShadow: '0 2px 4px rgba(0,0,0,0.04)',
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <RobotOutlined style={{ fontSize: 20, color: '#1890ff' }} />
              <Text strong style={{ fontSize: 16, color: '#000' }}>AI Assistant</Text>
            </div>
            <Dropdown menu={{ items: menuItems }} trigger={['click']} placement="bottomRight">
              <Button
                type="text"
                icon={<MoreOutlined />}
                size="small"
                style={{
                  fontSize: 18,
                  padding: '4px 8px',
                  height: 'auto',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                }}
              />
            </Dropdown>
          </div>
        )}
        
        <div style={{ flex: 1, overflowY: 'auto', minHeight: 0, padding: '20px', backgroundColor: '#fafafa' }}>
          {messages.length === 0 ? (
            <div style={{ padding: 20 }}>
              <div style={{ textAlign: 'center', marginBottom: 24 }}>
                <RobotOutlined style={{ fontSize: 48, color: '#1890ff', marginBottom: 16 }} />
                <Title level={4} style={{ marginBottom: 8 }}>Chào bạn! Tôi là AI Assistant</Title>
                <Paragraph style={{ color: '#666', marginBottom: 0 }}>
                  Tôi có thể giúp bạn quản lý kho và trả lời các câu hỏi về tồn kho
                </Paragraph>
              </div>
              
              <div style={{ marginTop: 32 }}>
                <Text strong style={{ fontSize: 16, marginBottom: 16, display: 'block' }}>
                  💡 Câu hỏi mẫu bạn có thể thử:
                </Text>
                <Space direction="vertical" style={{ width: '100%' }} size="small">
                  {[
                    'Tồn kho hiện tại?',
                    'Có bao nhiêu sản phẩm trong kho?',
                    'Sản phẩm nào sắp hết hạn?',
                    'Sản phẩm nào hết hàng?',
                    'Hôm nay xuất nhiều nhất là mặt hàng nào?',
                    'Tồn kho của Bánh Oreo?',
                    'Xuất cho khách Hoàng Văn Diện 5 chai Coca',
                  ].map((question, idx) => (
                    <Button
                      key={idx}
                      type="text"
                      block
                      style={{
                        textAlign: 'left',
                        height: 'auto',
                        padding: '12px 16px',
                        border: '1px solid #e0e0e0',
                        borderRadius: 8,
                        cursor: 'pointer',
                      }}
                      onClick={() => handleSampleQuestion(question)}
                      onMouseEnter={(e) => {
                        e.currentTarget.style.backgroundColor = '#f5f5f5';
                        e.currentTarget.style.borderColor = '#1890ff';
                      }}
                      onMouseLeave={(e) => {
                        e.currentTarget.style.backgroundColor = 'transparent';
                        e.currentTarget.style.borderColor = '#e0e0e0';
                      }}
                    >
                      <Text>{question}</Text>
                    </Button>
                  ))}
                </Space>
              </div>
            </div>
          ) : (
            <Space direction="vertical" style={{ width: '100%' }} size="small">
              {messages.map((msg, msgIndex) => (
                <div key={msgIndex} style={{ width: '100%', marginBottom: 8 }}>
                  <div
                    style={{
                      display: 'flex',
                      alignItems: 'flex-start',
                      justifyContent: msg.role === 'user' ? 'flex-end' : 'flex-start',
                      marginBottom: 8,
                      gap: 8,
                      width: '100%',
                    }}
                  >
                    {msg.role === 'assistant' && (
                      <div
                        style={{
                          width: 36,
                          height: 36,
                          borderRadius: '50%',
                          backgroundColor: '#1890ff',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          flexShrink: 0,
                          boxShadow: '0 2px 4px rgba(24, 144, 255, 0.3)',
                        }}
                      >
                        <RobotOutlined style={{ color: '#fff', fontSize: 18 }} />
                      </div>
                    )}
                    <div
                      style={{
                        maxWidth: '75%',
                        padding: '10px 14px',
                        borderRadius: '18px',
                        backgroundColor: msg.role === 'user' ? '#1890ff' : '#f5f5f5',
                        color: msg.role === 'user' ? '#fff' : '#000',
                        fontSize: 15,
                        lineHeight: 1.6,
                        marginLeft: msg.role === 'user' ? 'auto' : 0,
                        marginRight: msg.role === 'user' ? 0 : 'auto',
                        boxShadow: msg.role === 'user' ? '0 1px 2px rgba(0,0,0,0.1)' : '0 1px 2px rgba(0,0,0,0.05)',
                      }}
                    >
                      {msg.role === 'user' ? (
                        <Text style={{ color: '#fff', whiteSpace: 'pre-wrap' }}>{msg.content}</Text>
                      ) : (
                        <div style={{ color: '#000' }}>
                          <ReactMarkdown
                            components={{
                              p: ({ children }) => <p style={{ margin: '0 0 8px 0', lineHeight: 1.5 }}>{children}</p>,
                              strong: ({ children }) => <strong style={{ fontWeight: 600 }}>{children}</strong>,
                              ul: ({ children }) => <ul style={{ margin: '4px 0', paddingLeft: 20 }}>{children}</ul>,
                              li: ({ children }) => <li style={{ margin: '2px 0' }}>{children}</li>,
                              h1: ({ children }) => <h1 style={{ fontSize: 18, margin: '0 0 8px 0', fontWeight: 600 }}>{children}</h1>,
                              h2: ({ children }) => <h2 style={{ fontSize: 16, margin: '0 0 8px 0', fontWeight: 600 }}>{children}</h2>,
                              h3: ({ children }) => <h3 style={{ fontSize: 14, margin: '0 0 8px 0', fontWeight: 600 }}>{children}</h3>,
                            }}
                          >
                            {msg.content}
                          </ReactMarkdown>
                        </div>
                      )}
                    </div>
                    {msg.role === 'user' && (
                      <div
                        style={{
                          width: 36,
                          height: 36,
                          borderRadius: '50%',
                          backgroundColor: '#1890ff',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          flexShrink: 0,
                          boxShadow: '0 2px 4px rgba(24, 144, 255, 0.3)',
                        }}
                      >
                        <UserOutlined style={{ color: '#fff', fontSize: 18 }} />
                      </div>
                    )}
                  </div>
                  {msg.exportPreview && (() => {
                    const preview = msg.exportPreview;
                    return (
                  <div style={{ 
                    marginTop: 8, 
                    marginBottom: 8, 
                    maxWidth: '100%',
                    marginLeft: msg.role === 'user' ? 'auto' : 0, 
                    marginRight: msg.role === 'user' ? 0 : 'auto' 
                  }}>
                    <div style={{ 
                      padding: '20px', 
                      backgroundColor: '#fff', 
                      borderRadius: 12, 
                      border: '1px solid #e0e0e0',
                      boxShadow: '0 2px 12px rgba(0,0,0,0.08)',
                      color: '#000'
                    }}>
                      {/* Header */}
                      <div style={{ 
                        textAlign: 'center', 
                        marginBottom: 20,
                        borderBottom: '2px solid #1890ff',
                        paddingBottom: 12
                      }}>
                        <Title level={4} style={{ color: '#000', margin: 0, fontWeight: 600, fontSize: 18 }}>
                          PHIẾU XUẤT KHO
                        </Title>
                        <Text type="secondary" style={{ fontSize: 11, color: '#999', display: 'block', marginTop: 4 }}>
                          (Preview)
                        </Text>
                      </div>

                      {/* Customer Info */}
                      {preview.customer && (
                        <div style={{ 
                          marginBottom: 16, 
                          padding: '10px 14px', 
                          backgroundColor: '#f8f9fa', 
                          borderRadius: 6,
                          border: '1px solid #e9ecef'
                        }}>
                          <Text strong style={{ color: '#495057', fontSize: 13 }}>Khách hàng: </Text>
                          <Text style={{ color: '#212529', fontSize: 14, fontWeight: 500 }}>{preview.customer.name || 'N/A'}</Text>
                        </div>
                      )}

                      {/* Items Table */}
                      {preview.items && preview.items.length > 0 ? (
                        <>
                          <div style={{ overflowX: 'auto' }}>
                            <Table
                              columns={[
                                {
                                  title: <span style={{ fontSize: 13, fontWeight: 600 }}>Sản phẩm</span>,
                                  dataIndex: 'product',
                                  key: 'product',
                                  render: (product: any) => (
                                    <div>
                                      <div style={{ fontWeight: 500, color: '#212529', fontSize: 14 }}>{product?.name || 'N/A'}</div>
                                      <div style={{ fontSize: 11, color: '#6c757d', marginTop: 2 }}>{product?.sku || ''}</div>
                                    </div>
                                  ),
                                },
                                {
                                  title: <span style={{ fontSize: 13, fontWeight: 600 }}>Số lượng</span>,
                                  dataIndex: 'quantity',
                                  key: 'quantity',
                                  align: 'right' as const,
                                  width: 90,
                                  render: (qty: number) => <span style={{ color: '#212529', fontSize: 14 }}>{qty}</span>,
                                },
                                {
                                  title: <span style={{ fontSize: 13, fontWeight: 600 }}>Đơn giá</span>,
                                  dataIndex: 'unitPrice',
                                  key: 'unitPrice',
                                  align: 'right' as const,
                                  width: 110,
                                  render: (price: number) => (
                                    <span style={{ color: '#212529', fontSize: 13 }}>
                                      {new Intl.NumberFormat('vi-VN', {
                                        style: 'currency',
                                        currency: 'VND',
                                      }).format(price)}
                                    </span>
                                  ),
                                },
                                {
                                  title: <span style={{ fontSize: 13, fontWeight: 600 }}>Thành tiền</span>,
                                  dataIndex: 'totalPrice',
                                  key: 'totalPrice',
                                  align: 'right' as const,
                                  width: 130,
                                  render: (price: number) => (
                                    <span style={{ color: '#212529', fontWeight: 600, fontSize: 14 }}>
                                      {new Intl.NumberFormat('vi-VN', {
                                        style: 'currency',
                                        currency: 'VND',
                                      }).format(price)}
                                    </span>
                                  ),
                                },
                                {
                                  title: <span style={{ fontSize: 13, fontWeight: 600 }}>Tồn kho</span>,
                                  dataIndex: 'stockStatus',
                                  key: 'stockStatus',
                                  align: 'center' as const,
                                  width: 95,
                                  render: (status: string) => (
                                    <Tag color={status === 'ENOUGH' ? 'green' : 'red'} style={{ fontSize: 12, padding: '2px 8px' }}>
                                      {status === 'ENOUGH' ? 'Đủ' : 'Không đủ'}
                                    </Tag>
                                  ),
                                },
                              ]}
                              dataSource={preview.items.map((item, idx) => ({
                                key: idx,
                                product: item.product,
                                quantity: item.quantity,
                                unitPrice: item.unitPrice,
                                totalPrice: item.totalPrice,
                                stockStatus: item.stockStatus,
                              }))}
                              pagination={false}
                              size="small"
                              style={{ marginBottom: 16, fontSize: 13 }}
                              bordered
                            />
                          </div>
                          
                          {/* Total */}
                          <div style={{ 
                            marginTop: 16, 
                            paddingTop: 16, 
                            borderTop: '2px solid #dee2e6',
                            textAlign: 'right',
                            backgroundColor: '#f8f9fa',
                            marginLeft: -20,
                            marginRight: -20,
                            paddingLeft: 20,
                            paddingRight: 20,
                            borderRadius: '0 0 12px 12px'
                          }}>
                            <div style={{ fontSize: 15 }}>
                              <Text strong style={{ color: '#495057', fontSize: 16 }}>Tổng tiền: </Text>
                              <Text style={{ 
                                color: '#1890ff', 
                                fontSize: 20, 
                                fontWeight: 700 
                              }}>
                                {new Intl.NumberFormat('vi-VN', {
                                  style: 'currency',
                                  currency: 'VND',
                                }).format(preview.totalAmount || preview.totalPrice || 0)}
                              </Text>
                            </div>
                          </div>

                          {/* Note */}
                          {preview.note && (
                            <Alert
                              message={<span style={{ fontSize: 13 }}>{preview.note}</span>}
                              type={preview.overallStockStatus === 'ENOUGH' ? 'success' : 'warning'}
                              showIcon
                              style={{ marginTop: 16, fontSize: 13 }}
                            />
                          )}
                        </>
                      ) : (
                          // Hiển thị 1 sản phẩm (backward compatible)
                          <>
                            {preview.product && (
                              <div>
                                <Text strong>Sản phẩm:</Text> {preview.product.name} ({preview.product.sku})
                              </div>
                            )}
                            {preview.customer && (
                              <div>
                                <Text strong>Khách hàng:</Text> {preview.customer.name || 'N/A'}
                              </div>
                            )}
                            {preview.quantity && (
                              <div>
                                <Text strong>Số lượng:</Text> {preview.quantity}
                              </div>
                            )}
                            {preview.unitPrice && (
                              <div>
                                <Text strong>Giá đơn vị:</Text>{' '}
                                {new Intl.NumberFormat('vi-VN', {
                                  style: 'currency',
                                  currency: 'VND',
                                }).format(preview.unitPrice)}
                              </div>
                            )}
                            {preview.totalPrice && (
                              <div>
                                <Text strong>Tổng tiền:</Text>{' '}
                                <Text style={{ color: '#1890ff', fontSize: 18 }}>
                                  {new Intl.NumberFormat('vi-VN', {
                                    style: 'currency',
                                    currency: 'VND',
                                  }).format(preview.totalPrice)}
                                </Text>
                              </div>
                            )}
                            {preview.stockStatus && (
                              <div>
                                <Text strong>Tồn kho:</Text>{' '}
                                <Tag color={preview.stockStatus === 'ENOUGH' ? 'green' : 'red'}>
                                  {preview.stockStatus === 'ENOUGH' ? 'Đủ' : 'Không đủ'}
                                </Tag>
                              </div>
                            )}
                          </>
                        )}
                      
                      {/* Action Button hoặc Trạng thái đã xác nhận */}
                      {(preview.overallStockStatus === 'ENOUGH' || preview.stockStatus === 'ENOUGH') && (
                        <div style={{ marginTop: 20 }}>
                          {msg.isConfirmed ? (
                            <Alert
                              message="Đã xuất kho"
                              description={
                                msg.exportReceiptId 
                                  ? `Phiếu xuất kho #${msg.exportReceiptId} đã được tạo thành công.`
                                  : 'Phiếu xuất kho đã được tạo thành công.'
                              }
                              type="success"
                              showIcon
                              icon={<CheckCircleOutlined />}
                              action={
                                msg.exportReceiptId ? (
                                  <Button
                                    size="small"
                                    onClick={() => navigate(`/exports/${msg.exportReceiptId}`)}
                                  >
                                    Xem phiếu
                                  </Button>
                                ) : undefined
                              }
                            />
                          ) : (
                            <Button
                              type="primary"
                              icon={<CheckCircleOutlined />}
                              onClick={() => handleConfirmExport(preview, msgIndex)}
                              loading={createExportMutation.isPending}
                              block
                              size="large"
                            >
                              Xác nhận xuất kho
                            </Button>
                          )}
                        </div>
                      )}
                    </div>
                  </div>
                    );
                  })()}
                </div>
              ))}
              {aiMutation.isPending && (
                <div style={{ display: 'flex', alignItems: 'flex-start', gap: 8 }}>
                  <div
                    style={{
                      width: 32,
                      height: 32,
                      borderRadius: '50%',
                      backgroundColor: '#1890ff',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      flexShrink: 0,
                    }}
                  >
                    <RobotOutlined style={{ color: '#fff', fontSize: 16 }} />
                  </div>
                  <div
                    style={{
                      padding: '8px 12px',
                      borderRadius: '18px 18px 18px 4px',
                      backgroundColor: '#f0f0f0',
                    }}
                  >
                    <Space size="small">
                      <Spin size="small" />
                      <Text style={{ fontSize: 14 }}>AI đang suy nghĩ...</Text>
                    </Space>
                  </div>
                </div>
              )}
              <div ref={messagesEndRef} />
            </Space>
          )}
        </div>
        
        <div style={{ 
          padding: '16px 20px', 
          borderTop: '1px solid #e0e0e0',
          backgroundColor: '#fff',
          flexShrink: 0,
          boxShadow: '0 -2px 8px rgba(0,0,0,0.05)',
        }}>
          <Space.Compact style={{ width: '100%' }}>
            <Input
              value={message}
              onChange={(e) => setMessage(e.target.value)}
              onPressEnter={(e) => {
                e.preventDefault();
                if (message.trim() && !aiMutation.isPending) {
                  handleSend();
                }
              }}
              placeholder="Nhập câu hỏi hoặc lệnh của bạn..."
              disabled={aiMutation.isPending}
              size="large"
              style={{ borderRadius: '20px 0 0 20px' }}
            />
            <Button
              type="primary"
              icon={<SendOutlined />}
              onClick={handleSend}
              loading={aiMutation.isPending}
              disabled={!message.trim()}
              size="large"
              style={{ borderRadius: '0 20px 20px 0', minWidth: 60 }}
            />
          </Space.Compact>
        </div>
      </div>
    </div>
  );
};

export default AiAssistantPage;

