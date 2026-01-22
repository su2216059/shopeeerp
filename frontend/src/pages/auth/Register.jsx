import React, { useState } from 'react'
import { Form, Input, Button, Card, message, Typography, Space, Divider } from 'antd'
import { UserOutlined, LockOutlined, ShopOutlined, CheckCircleOutlined } from '@ant-design/icons'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

const { Title, Text } = Typography

const Register = () => {
  const navigate = useNavigate()
  const { register } = useAuth()
  const [loading, setLoading] = useState(false)

  const onFinish = async (values) => {
    setLoading(true)
    try {
      const result = await register(values.username, values.password, values.confirmPassword)
      if (result.success) {
        message.success('注册成功')
        navigate('/dashboard')
      } else {
        message.error(result.message)
      }
    } catch (error) {
      message.error('注册失败，请稍后重试')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    }}>
      <Card 
        style={{ 
          width: 400, 
          boxShadow: '0 4px 12px rgba(0,0,0,0.15)',
          borderRadius: 8,
        }}
      >
        <div style={{ textAlign: 'center', marginBottom: 32 }}>
          <ShopOutlined style={{ fontSize: 48, color: '#1890ff' }} />
          <Title level={2} style={{ margin: '16px 0 8px' }}>创建账号</Title>
          <Text type="secondary">注册 Shopee ERP 账号</Text>
        </div>

        <Form
          name="register"
          onFinish={onFinish}
          size="large"
          autoComplete="off"
        >
          <Form.Item
            name="username"
            rules={[
              { required: true, message: '请输入用户名' },
              { min: 3, message: '用户名至少3个字符' },
              { max: 20, message: '用户名最多20个字符' },
              { pattern: /^[a-zA-Z0-9_]+$/, message: '只允许字母、数字和下划线' },
            ]}
          >
            <Input 
              prefix={<UserOutlined />} 
              placeholder="用户名" 
            />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[
              { required: true, message: '请输入密码' },
              { min: 6, message: '密码至少6个字符' },
            ]}
          >
            <Input.Password 
              prefix={<LockOutlined />} 
              placeholder="密码 (至少6位)" 
            />
          </Form.Item>

          <Form.Item
            name="confirmPassword"
            dependencies={['password']}
            rules={[
              { required: true, message: '请确认密码' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('password') === value) {
                    return Promise.resolve()
                  }
                  return Promise.reject(new Error('两次密码输入不一致'))
                },
              }),
            ]}
          >
            <Input.Password 
              prefix={<CheckCircleOutlined />} 
              placeholder="确认密码" 
            />
          </Form.Item>

          <Form.Item>
            <Button 
              type="primary" 
              htmlType="submit" 
              loading={loading}
              block
            >
              注册
            </Button>
          </Form.Item>
        </Form>

        <Divider plain>
          <Text type="secondary">已有账号？</Text>
        </Divider>

        <div style={{ textAlign: 'center' }}>
          <Link to="/login">
            <Button type="link">返回登录</Button>
          </Link>
        </div>
      </Card>
    </div>
  )
}

export default Register
