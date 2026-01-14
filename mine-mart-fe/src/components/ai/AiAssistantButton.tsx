/**
 * Floating AI Assistant Widget
 * Hiển thị như một widget chat nhỏ ở góc màn hình, giống widget hỗ trợ
 */

import { useState } from 'react';
import { FloatButton } from 'antd';
import { RobotOutlined, CloseOutlined, MinusOutlined } from '@ant-design/icons';
import AiAssistantPage from '@/pages/ai/AiAssistantPage';
import { useAuthContext } from '@/contexts/AuthContext';
import './AiAssistantWidget.css';

const AiAssistantButton = () => {
  const [open, setOpen] = useState(false);
  const [minimized, setMinimized] = useState(false);
  const { user, isAdmin, isStaff } = useAuthContext();

  // Chỉ hiển thị cho ADMIN hoặc STAFF
  if (!user || (!isAdmin && !isStaff)) {
    return null;
  }

  const handleToggle = () => {
    if (open && minimized) {
      setMinimized(false);
    } else {
      setOpen(!open);
      setMinimized(false);
    }
  };

  const handleMinimize = () => {
    setMinimized(true);
  };

  const handleClose = () => {
    setOpen(false);
    setMinimized(false);
  };

  return (
    <>
      {!open && (
        <FloatButton
          icon={<RobotOutlined />}
          type="primary"
          style={{
            right: 24,
            bottom: 24,
            width: 56,
            height: 56,
          }}
          onClick={handleToggle}
          tooltip="AI Assistant"
        />
      )}
      
      {open && (
        <div className={`ai-assistant-widget ${minimized ? 'minimized' : ''}`}>
          <div className="ai-assistant-header">
            <div className="ai-assistant-header-left">
              <RobotOutlined style={{ fontSize: 20, marginRight: 8 }} />
              <span style={{ fontWeight: 600 }}>AI Assistant</span>
            </div>
            <div className="ai-assistant-header-actions">
              {!minimized && (
                <button
                  className="ai-assistant-btn-icon"
                  onClick={handleMinimize}
                  title="Thu nhỏ"
                >
                  <MinusOutlined />
                </button>
              )}
              <button
                className="ai-assistant-btn-icon"
                onClick={handleClose}
                title="Đóng"
              >
                <CloseOutlined />
              </button>
            </div>
          </div>
          
          {!minimized && (
            <div className="ai-assistant-content">
              <AiAssistantPage />
            </div>
          )}
          
          {minimized && (
            <div className="ai-assistant-minimized" onClick={handleToggle}>
              <RobotOutlined style={{ fontSize: 20, marginRight: 8 }} />
              <span>AI Assistant</span>
            </div>
          )}
        </div>
      )}
    </>
  );
};

export default AiAssistantButton;

