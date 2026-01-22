import React, { useState, useEffect } from 'react'
import { 
  Table, Button, Space, Card, Tag, message, Popconfirm, 
  Modal, Form, Input, Select, Typography, Alert, Badge
} from 'antd'
import { 
  ArrowLeftOutlined, PlusOutlined, EditOutlined, DeleteOutlined,
  UserOutlined, EyeOutlined, LockOutlined
} from '@ant-design/icons'
import { useNavigate, useParams } from 'react-router-dom'
import { shopApi } from '../../api'

const { Title, Text } = Typography
const { Option } = Select

const ShopAccounts = () => {
  const navigate = useNavigate()
  const { shopId } = useParams()
  const [form] = Form.useForm()
  const [shop, setShop] = useState(null)
  const [accounts, setAccounts] = useState([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [modalLoading, setModalLoading] = useState(false)
  const [editingAccount, setEditingAccount] = useState(null)
  const [passwordVisible, setPasswordVisible] = useState({})

  useEffect(() => {
    fetchData()
  }, [shopId])

  const fetchData = async () => {
    setLoading(true)
    try {
      const [shopRes, accountsRes] = await Promise.all([
        shopApi.getById(shopId),
        shopApi.getAccounts(shopId),
      ])
      setShop(shopRes.data)
      setAccounts(accountsRes.data || [])
    } catch (error) {
      message.error('获取数据失败')
    } finally {
      setLoading(false)
    }
  }

  const handleAdd = () => {
    setEditingAccount(null)
    form.resetFields()
    form.setFieldsValue({ status: 'active' })
    setModalVisible(true)
  }

  const handleEdit = (record) => {
    setEditingAccount(record)
    form.setFieldsValue({
      accountType: record.accountType,
      accountName: record.accountName,
      username: record.username,
      status: record.status,
      remark: record.remark,
    })
    setModalVisible(true)
  }

  const handleDelete = async (accountId) => {
    try {
      await shopApi.deleteAccount(shopId, accountId)
      message.success('删除成功')
      fetchData()
    } catch (error) {
      message.error('删除失败')
    }
  }

  const handleModalOk = async () => {
    try {
      const values = await form.validateFields()
      setModalLoading(true)

      if (editingAccount) {
        await shopApi.updateAccount(shopId, editingAccount.id, values)
        message.success('更新成功')
      } else {
        await shopApi.addAccount(shopId, values)
        message.success('添加成功')
      }

      setModalVisible(false)
      fetchData()
    } catch (error) {
      if (error.errorFields) {
        return // 表单验证失败
      }
      message.error('操作失败')
    } finally {
      setModalLoading(false)
    }
  }

  const handleViewPassword = async (accountId) => {
    try {
      const res = await shopApi.getAccountDetail(shopId, accountId)
      setPasswordVisible({ ...passwordVisible, [accountId]: res.data?.password })
      
      // 5秒后自动隐藏
      setTimeout(() => {
        setPasswordVisible(prev => {
          const newState = { ...prev }
          delete newState[accountId]
          return newState
        })
      }, 5000)
    } catch (error) {
      message.error('获取密码失败')
    }
  }

  const accountTypes = [
    { value: 'seller_center', label: '卖家中心', color: 'blue' },
    { value: 'warehouse', label: '仓库系统', color: 'green' },
    { value: 'finance', label: '财务系统', color: 'gold' },
    { value: 'admin', label: '管理后台', color: 'red' },
    { value: 'other', label: '其他', color: 'default' },
  ]

  const getAccountTypeTag = (type) => {
    const found = accountTypes.find(t => t.value === type)
    return found ? (
      <Tag color={found.color}>{found.label}</Tag>
    ) : (
      <Tag>{type}</Tag>
    )
  }

  const statusConfig = {
    active: { status: 'success', text: '正常' },
    disabled: { status: 'default', text: '禁用' },
    locked: { status: 'error', text: '锁定' },
  }

  const columns = [
    {
      title: '账号名称',
      dataIndex: 'accountName',
      key: 'accountName',
      width: 150,
      render: (text) => text || '-',
    },
    {
      title: '类型',
      dataIndex: 'accountType',
      key: 'accountType',
      width: 120,
      render: (type) => getAccountTypeTag(type),
    },
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
      width: 200,
      render: (text) => <Text code>{text}</Text>,
    },
    {
      title: '密码',
      key: 'password',
      width: 180,
      render: (_, record) => (
        <Space>
          {passwordVisible[record.id] ? (
            <Text code>{passwordVisible[record.id]}</Text>
          ) : (
            <Text type="secondary">••••••••</Text>
          )}
          <Button
            type="text"
            size="small"
            icon={<EyeOutlined />}
            onClick={() => handleViewPassword(record.id)}
            disabled={passwordVisible[record.id]}
          >
            查看
          </Button>
        </Space>
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status) => {
        const config = statusConfig[status] || { status: 'default', text: status }
        return <Badge status={config.status} text={config.text} />
      },
    },
    {
      title: '最后登录',
      dataIndex: 'lastLoginAt',
      key: 'lastLoginAt',
      width: 160,
      render: (val) => val ? new Date(val).toLocaleString('zh-CN') : '-',
    },
    {
      title: '备注',
      dataIndex: 'remark',
      key: 'remark',
      width: 150,
      ellipsis: true,
    },
    {
      title: '操作',
      key: 'action',
      width: 120,
      render: (_, record) => (
        <Space size="small">
          <Button 
            size="small" 
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定删除此账号？"
            onConfirm={() => handleDelete(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button size="small" danger icon={<DeleteOutlined />} />
          </Popconfirm>
        </Space>
      ),
    },
  ]

  return (
    <div>
      <Card loading={loading}>
        <div style={{ display: 'flex', alignItems: 'center', marginBottom: 24 }}>
          <Button 
            icon={<ArrowLeftOutlined />} 
            onClick={() => navigate('/shops')}
            style={{ marginRight: 16 }}
          >
            返回
          </Button>
          <Title level={4} style={{ margin: 0 }}>
            <UserOutlined style={{ marginRight: 8 }} />
            登录账号管理
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

        <div style={{ marginBottom: 16 }}>
          <Button 
            type="primary" 
            icon={<PlusOutlined />}
            onClick={handleAdd}
          >
            添加账号
          </Button>
        </div>

        <Table
          columns={columns}
          dataSource={accounts}
          rowKey="id"
          scroll={{ x: 1100 }}
          pagination={false}
        />

        <Alert
          message="安全提示"
          description={
            <ul style={{ margin: 0, paddingLeft: 20 }}>
              <li>密码会加密存储，点击"查看"后5秒自动隐藏</li>
              <li>请勿将账号密码泄露给无关人员</li>
              <li>建议定期更换密码</li>
              <li>启用二次验证以提高安全性</li>
            </ul>
          }
          type="warning"
          showIcon
          style={{ marginTop: 24 }}
        />
      </Card>

      <Modal
        title={editingAccount ? '编辑账号' : '添加账号'}
        open={modalVisible}
        onOk={handleModalOk}
        onCancel={() => setModalVisible(false)}
        confirmLoading={modalLoading}
        destroyOnClose
      >
        <Form
          form={form}
          layout="vertical"
          style={{ marginTop: 16 }}
        >
          <Form.Item
            name="accountType"
            label="账号类型"
            rules={[{ required: true, message: '请选择账号类型' }]}
          >
            <Select placeholder="选择账号类型">
              {accountTypes.map(t => (
                <Option key={t.value} value={t.value}>{t.label}</Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="accountName"
            label="账号名称"
            tooltip="便于识别的名称，如：主账号、财务专用"
          >
            <Input placeholder="如: 主账号" />
          </Form.Item>

          <Form.Item
            name="username"
            label="用户名"
            rules={[{ required: true, message: '请输入用户名' }]}
          >
            <Input 
              prefix={<UserOutlined />}
              placeholder="邮箱/手机号/用户名" 
            />
          </Form.Item>

          <Form.Item
            name="password"
            label="密码"
            rules={[{ required: !editingAccount, message: '请输入密码' }]}
            tooltip={editingAccount ? '留空表示不修改' : ''}
          >
            <Input.Password 
              prefix={<LockOutlined />}
              placeholder={editingAccount ? '留空表示不修改' : '输入密码'}
            />
          </Form.Item>

          <Form.Item
            name="status"
            label="状态"
          >
            <Select>
              <Option value="active">正常</Option>
              <Option value="disabled">禁用</Option>
            </Select>
          </Form.Item>

          <Form.Item
            name="remark"
            label="备注"
          >
            <Input.TextArea rows={2} placeholder="备注信息..." />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default ShopAccounts
