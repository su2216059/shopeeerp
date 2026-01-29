import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Popconfirm, message, Tag } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { orderApi } from '../../api'
import { formatDateTime } from '../../utils/dateUtils'
import { useAuth } from '../../context/AuthContext'

const OrderList = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()
  const { hasPermission } = useAuth()
  const canCreate = hasPermission('ORDER_CREATE')
  const canUpdate = hasPermission('ORDER_UPDATE')
  const canDelete = hasPermission('ORDER_DELETE')

  useEffect(() => {
    fetchData()
  }, [])

  const fetchData = async () => {
    setLoading(true)
    try {
      const result = await orderApi.list()
      setData(result?.data || [])
    } catch (error) {
      message.error('加载数据失败')
    } finally {
      setLoading(false)
    }
  }

  const handleDelete = async (id) => {
    try {
      await orderApi.delete(id)
      message.success('删除成功')
      fetchData()
    } catch (error) {
      message.error('删除失败')
    }
  }

  const columns = [
    {
      title: 'ID',
      dataIndex: 'orderId',
      key: 'orderId',
      width: 80,
    },
    {
      title: '客户ID',
      dataIndex: 'customerId',
      key: 'customerId',
    },
    {
      title: '订单状态',
      dataIndex: 'orderStatus',
      key: 'orderStatus',
      render: (text) => <Tag color="blue">{text}</Tag>,
    },
    {
      title: '总金额',
      dataIndex: 'totalAmount',
      key: 'totalAmount',
      render: (text) => `¥${text}`,
    },
    {
      title: '支付状态',
      dataIndex: 'paymentStatus',
      key: 'paymentStatus',
      render: (text) => <Tag color={text === '已支付' ? 'green' : 'orange'}>{text}</Tag>,
    },
    {
      title: '配送状态',
      dataIndex: 'shippingStatus',
      key: 'shippingStatus',
      render: (text) => text || '-',
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (text) => formatDateTime(text),
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_, record) => (
        <Space>
          {canUpdate && (
            <Button
              type="link"
              icon={<EditOutlined />}
              onClick={() => navigate(`/orders/edit/${record.orderId}`)}
            >
              Edit
            </Button>
          )}
          {canDelete && (
            <Popconfirm
              title="Confirm delete?"
              onConfirm={() => handleDelete(record.orderId)}
            >
              <Button type="link" danger icon={<DeleteOutlined />}>
                Delete
              </Button>
            </Popconfirm>
          )}
        </Space>
      ),
    },
  ]

  return (
    <div>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
        <h1>订单管理</h1>
        {canCreate && (
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={() => navigate('/orders/new')}
          >
            New Order
          </Button>
        )}
      </div>
      <Table
        columns={columns}
        dataSource={data}
        rowKey="orderId"
        loading={loading}
        pagination={{ pageSize: 10 }}
      />
    </div>
  )
}

export default OrderList
