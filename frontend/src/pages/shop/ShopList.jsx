import React, { useState, useEffect } from 'react'
import { 
  Table, Button, Space, Card, Tag, message, Popconfirm, 
  Modal, Tooltip, Badge, Typography 
} from 'antd'
import { 
  PlusOutlined, EditOutlined, DeleteOutlined, KeyOutlined,
  UserOutlined, CheckCircleOutlined, SyncOutlined, ShopOutlined
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { shopApi } from '../../api'

const { Title, Text } = Typography

const ShopList = () => {
  const navigate = useNavigate()
  const [shops, setShops] = useState([])
  const [loading, setLoading] = useState(false)
  const [currentShop, setCurrentShop] = useState(null)

  useEffect(() => {
    fetchShops()
    fetchCurrentShop()
  }, [])

  const fetchShops = async () => {
    setLoading(true)
    try {
      const res = await shopApi.list()
      setShops(Array.isArray(res) ? res : (res.data || []))
    } catch (error) {
      message.error('获取店铺列表失败')
    } finally {
      setLoading(false)
    }
  }

  const fetchCurrentShop = async () => {
    try {
      const res = await shopApi.getCurrentShop()
      setCurrentShop(res.data)
    } catch (error) {
      // 忽略错误
    }
  }

  const handleDelete = async (id) => {
    try {
      await shopApi.delete(id)
      message.success('删除成功')
      fetchShops()
    } catch (error) {
      message.error('删除失败')
    }
  }

  const handleSwitch = async (shopId) => {
    try {
      await shopApi.switchShop(shopId)
      message.success('切换成功')
      fetchCurrentShop()
    } catch (error) {
      message.error('切换失败')
    }
  }

  const platformColors = {
    ozon: 'blue',
    shopee: 'orange',
    wildberries: 'purple',
    amazon: 'gold',
  }

  const statusColors = {
    active: 'success',
    suspended: 'warning',
    closed: 'error',
  }

  const statusLabels = {
    active: '正常',
    suspended: '暂停',
    closed: '关闭',
  }

  const columns = [
    {
      title: '店铺',
      key: 'shop',
      width: 250,
      render: (_, record) => (
        <Space>
          <ShopOutlined style={{ fontSize: 20, color: '#1890ff' }} />
          <div>
            <div>
              <Text strong>{record.shopName}</Text>
              {record.isDefault && (
                <Tag color="green" style={{ marginLeft: 8 }}>默认</Tag>
              )}
              {currentShop?.id === record.id && (
                <Tag color="blue" style={{ marginLeft: 4 }}>当前</Tag>
              )}
            </div>
            <Text type="secondary" style={{ fontSize: 12 }}>{record.shopCode}</Text>
          </div>
        </Space>
      ),
    },
    {
      title: '平台',
      dataIndex: 'platform',
      key: 'platform',
      width: 100,
      render: (platform) => (
        <Tag color={platformColors[platform] || 'default'}>
          {platform?.toUpperCase()}
        </Tag>
      ),
    },
    {
      title: '市场',
      dataIndex: 'market',
      key: 'market',
      width: 80,
    },
    {
      title: '卖家ID',
      dataIndex: 'sellerId',
      key: 'sellerId',
      width: 120,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status) => (
        <Badge 
          status={statusColors[status] || 'default'} 
          text={statusLabels[status] || status} 
        />
      ),
    },
    {
      title: '时区/货币',
      key: 'locale',
      width: 150,
      render: (_, record) => (
        <div>
          <div>{record.timezone || '-'}</div>
          <Text type="secondary">{record.currency || '-'}</Text>
        </div>
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 160,
      render: (val) => val ? new Date(val).toLocaleString('zh-CN') : '-',
    },
    {
      title: '操作',
      key: 'action',
      width: 280,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Tooltip title="切换到此店铺">
            <Button 
              type={currentShop?.id === record.id ? 'primary' : 'default'}
              size="small" 
              icon={<SyncOutlined />}
              onClick={() => handleSwitch(record.id)}
              disabled={currentShop?.id === record.id}
            >
              切换
            </Button>
          </Tooltip>
          <Tooltip title="凭证管理">
            <Button 
              size="small" 
              icon={<KeyOutlined />}
              onClick={() => navigate(`/shops/${record.id}/credential`)}
            >
              凭证
            </Button>
          </Tooltip>
          <Tooltip title="账号管理">
            <Button 
              size="small" 
              icon={<UserOutlined />}
              onClick={() => navigate(`/shops/${record.id}/accounts`)}
            >
              账号
            </Button>
          </Tooltip>
          <Tooltip title="编辑">
            <Button 
              size="small" 
              icon={<EditOutlined />}
              onClick={() => navigate(`/shops/${record.id}/edit`)}
            />
          </Tooltip>
          <Popconfirm
            title="确定删除此店铺？"
            description="删除后将同时删除关联的凭证和账号信息"
            onConfirm={() => handleDelete(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Tooltip title="删除">
              <Button size="small" danger icon={<DeleteOutlined />} />
            </Tooltip>
          </Popconfirm>
        </Space>
      ),
    },
  ]

  return (
    <div>
      <Card>
        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
          <Title level={4} style={{ margin: 0 }}>店铺管理</Title>
          <Space>
            {currentShop && (
              <Text type="secondary">
                当前店铺: <Text strong>{currentShop.shopName}</Text>
              </Text>
            )}
            <Button 
              type="primary" 
              icon={<PlusOutlined />}
              onClick={() => navigate('/shops/new')}
            >
              添加店铺
            </Button>
          </Space>
        </div>

        <Table
          columns={columns}
          dataSource={shops}
          rowKey="id"
          loading={loading}
          scroll={{ x: 1200 }}
          pagination={{
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total) => `共 ${total} 条`,
          }}
        />
      </Card>
    </div>
  )
}

export default ShopList
