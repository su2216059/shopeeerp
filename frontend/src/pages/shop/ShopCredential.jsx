import React, { useState, useEffect } from 'react'
import { 
  Form, Input, Button, Card, message, Space, Typography, 
  Descriptions, Tag, Alert, Divider, Spin 
} from 'antd'
import { 
  ArrowLeftOutlined, SaveOutlined, CheckCircleOutlined,
  KeyOutlined, EyeOutlined, EyeInvisibleOutlined 
} from '@ant-design/icons'
import { useNavigate, useParams } from 'react-router-dom'
import { shopApi } from '../../api'

const { Title, Text } = Typography

const ShopCredential = () => {
  const navigate = useNavigate()
  const { shopId } = useParams()
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [submitting, setSubmitting] = useState(false)
  const [verifying, setVerifying] = useState(false)
  const [shop, setShop] = useState(null)
  const [credential, setCredential] = useState(null)
  const [showApiKey, setShowApiKey] = useState(false)

  useEffect(() => {
    fetchData()
  }, [shopId])

  const fetchData = async () => {
    setLoading(true)
    try {
      const [shopRes, credRes] = await Promise.all([
        shopApi.getById(shopId),
        shopApi.getCredential(shopId).catch(() => ({ data: null })),
      ])
      setShop(shopRes.data)
      setCredential(credRes.data)
      
      if (credRes.data) {
        form.setFieldsValue({
          clientId: credRes.data.clientId,
        })
      }
    } catch (error) {
      message.error('获取数据失败')
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = async (values) => {
    setSubmitting(true)
    try {
      await shopApi.saveCredential(shopId, values)
      message.success('凭证保存成功')
      fetchData()
    } catch (error) {
      message.error('保存失败')
    } finally {
      setSubmitting(false)
    }
  }

  const handleVerify = async () => {
    setVerifying(true)
    try {
      const res = await shopApi.verifyCredential(shopId)
      if (res.data?.valid) {
        message.success('凭证验证通过')
      } else {
        message.warning('凭证验证失败，请检查配置')
      }
      fetchData()
    } catch (error) {
      message.error('验证请求失败')
    } finally {
      setVerifying(false)
    }
  }

  const statusColors = {
    active: 'success',
    expired: 'warning',
    revoked: 'error',
  }

  const statusLabels = {
    active: '有效',
    expired: '已过期',
    revoked: '已撤销',
  }

  return (
    <div>
      <Spin spinning={loading}>
        <Card>
          <div style={{ display: 'flex', alignItems: 'center', marginBottom: 24 }}>
            <Button 
              icon={<ArrowLeftOutlined />} 
              onClick={() => navigate('/shops')}
              style={{ marginRight: 16 }}
            >
              返回
            </Button>
            <Title level={4} style={{ margin: 0 }}>
              <KeyOutlined style={{ marginRight: 8 }} />
              API凭证管理
            </Title>
          </div>

          {shop && (
            <Alert
              message={`店铺: ${shop.shopName} (${shop.shopCode})`}
              description={`平台: ${shop.platform?.toUpperCase()} | 市场: ${shop.market}`}
              type="info"
              showIcon
              style={{ marginBottom: 24 }}
            />
          )}

          {credential && (
            <>
              <Divider orientation="left">当前凭证状态</Divider>
              <Descriptions bordered column={2} style={{ marginBottom: 24 }}>
                <Descriptions.Item label="状态">
                  <Tag color={statusColors[credential.status]}>
                    {statusLabels[credential.status] || credential.status}
                  </Tag>
                </Descriptions.Item>
                <Descriptions.Item label="凭证类型">
                  {credential.credentialType === 'api_key' ? 'API Key' : credential.credentialType}
                </Descriptions.Item>
                <Descriptions.Item label="Client ID">
                  {credential.clientId || '-'}
                </Descriptions.Item>
                <Descriptions.Item label="API Key">
                  <Space>
                    <Text code>{showApiKey ? credential.apiKey : credential.apiKey}</Text>
                    <Button 
                      type="text" 
                      size="small"
                      icon={showApiKey ? <EyeInvisibleOutlined /> : <EyeOutlined />}
                      onClick={() => setShowApiKey(!showApiKey)}
                    />
                  </Space>
                </Descriptions.Item>
                <Descriptions.Item label="最后使用时间">
                  {credential.lastUsedAt ? new Date(credential.lastUsedAt).toLocaleString('zh-CN') : '-'}
                </Descriptions.Item>
                <Descriptions.Item label="最后验证时间">
                  {credential.lastVerifiedAt ? new Date(credential.lastVerifiedAt).toLocaleString('zh-CN') : '-'}
                </Descriptions.Item>
                <Descriptions.Item label="请求限制 (每分钟)">
                  {credential.rateLimitPerMinute || 60}
                </Descriptions.Item>
                <Descriptions.Item label="请求限制 (每天)">
                  {credential.rateLimitPerDay || 10000}
                </Descriptions.Item>
              </Descriptions>

              <Button 
                type="primary"
                icon={<CheckCircleOutlined />}
                loading={verifying}
                onClick={handleVerify}
                style={{ marginBottom: 24 }}
              >
                验证凭证有效性
              </Button>
            </>
          )}

          <Divider orientation="left">
            {credential ? '更新凭证' : '配置凭证'}
          </Divider>

          <Form
            form={form}
            layout="vertical"
            onFinish={handleSubmit}
            style={{ maxWidth: 500 }}
          >
            <Form.Item
              name="clientId"
              label="Client ID"
              rules={[{ required: true, message: '请输入Client ID' }]}
              tooltip="平台分配的客户端标识"
            >
              <Input placeholder="如: 3207535" />
            </Form.Item>

            <Form.Item
              name="apiKey"
              label="API Key"
              rules={[{ required: !credential, message: '请输入API Key' }]}
              tooltip={credential ? '留空表示不修改' : '平台分配的API密钥'}
            >
              <Input.Password 
                placeholder={credential ? '留空表示不修改' : '输入API Key'}
                visibilityToggle
              />
            </Form.Item>

            <Form.Item
              name="apiSecret"
              label="API Secret (可选)"
              tooltip="部分平台需要，如Shopee"
            >
              <Input.Password 
                placeholder="如有需要请输入"
                visibilityToggle
              />
            </Form.Item>

            <Form.Item>
              <Space>
                <Button 
                  type="primary" 
                  htmlType="submit" 
                  loading={submitting}
                  icon={<SaveOutlined />}
                >
                  保存凭证
                </Button>
                <Button onClick={() => navigate('/shops')}>
                  取消
                </Button>
              </Space>
            </Form.Item>
          </Form>

          <Alert
            message="安全提示"
            description={
              <ul style={{ margin: 0, paddingLeft: 20 }}>
                <li>API Key 会加密存储在数据库中</li>
                <li>请勿将 API Key 泄露给他人</li>
                <li>定期更换 API Key 以确保安全</li>
                <li>如发现异常使用，请立即撤销并重新生成</li>
              </ul>
            }
            type="warning"
            showIcon
            style={{ marginTop: 24 }}
          />
        </Card>
      </Spin>
    </div>
  )
}

export default ShopCredential
