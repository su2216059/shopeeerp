import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Popconfirm, message, Tag } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { paymentApi } from '../../api'
import { formatDateTime } from '../../utils/dateUtils'

const PaymentList = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  useEffect(() => {
    fetchData()
  }, [])

  const fetchData = async () => {
    setLoading(true)
    try {
      const result = await paymentApi.list()
      setData(result || [])
    } catch (error) {
      message.error('加载数据失败')
    } finally {
      setLoading(false)
    }
  }

  const handleDelete = async (id) => {
    try {
      await paymentApi.delete(id)
      message.success('删除成功')
      fetchData()
    } catch (error) {
      message.error('删除失败')
    }
  }

  const columns = [
    {
      title: 'ID',
      dataIndex: 'paymentId',
      key: 'paymentId',
      width: 80,
    },
    {
      title: '订单ID',
      dataIndex: 'orderId',
      key: 'orderId',
    },
    {
      title: '支付金额',
      dataIndex: 'paymentAmount',
      key: 'paymentAmount',
      render: (text) => `¥${text}`,
    },
    {
      title: '支付方式',
      dataIndex: 'paymentMethod',
      key: 'paymentMethod',
    },
    {
      title: '支付状态',
      dataIndex: 'paymentStatus',
      key: 'paymentStatus',
      render: (text) => <Tag color={text === '已支付' ? 'green' : 'orange'}>{text}</Tag>,
    },
    {
      title: '支付时间',
      dataIndex: 'paymentDate',
      key: 'paymentDate',
      render: (text) => formatDateTime(text),
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_, record) => (
        <Space>
          <Button
            type="link"
            icon={<EditOutlined />}
            onClick={() => navigate(`/payments/edit/${record.paymentId}`)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定要删除吗？"
            onConfirm={() => handleDelete(record.paymentId)}
          >
            <Button type="link" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ]

  return (
    <div>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
        <h1>支付管理</h1>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={() => navigate('/payments/new')}
        >
          新增支付记录
        </Button>
      </div>
      <Table
        columns={columns}
        dataSource={data}
        rowKey="paymentId"
        loading={loading}
        pagination={{ pageSize: 10 }}
      />
    </div>
  )
}

export default PaymentList
