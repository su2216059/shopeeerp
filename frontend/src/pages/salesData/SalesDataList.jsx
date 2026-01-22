import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Popconfirm, message } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { salesDataApi } from '../../api'
import { formatDateTime } from '../../utils/dateUtils'

const SalesDataList = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  useEffect(() => {
    fetchData()
  }, [])

  const fetchData = async () => {
    setLoading(true)
    try {
      const result = await salesDataApi.list()
      setData(result?.data || [])
    } catch (error) {
      message.error('加载数据失败')
    } finally {
      setLoading(false)
    }
  }

  const handleDelete = async (id) => {
    try {
      await salesDataApi.delete(id)
      message.success('删除成功')
      fetchData()
    } catch (error) {
      message.error('删除失败')
    }
  }

  const columns = [
    {
      title: 'ID',
      dataIndex: 'salesId',
      key: 'salesId',
      width: 80,
    },
    {
      title: '产品ID',
      dataIndex: 'productId',
      key: 'productId',
    },
    {
      title: '订单ID',
      dataIndex: 'orderId',
      key: 'orderId',
    },
    {
      title: '数量',
      dataIndex: 'quantity',
      key: 'quantity',
    },
    {
      title: '总金额',
      dataIndex: 'totalAmount',
      key: 'totalAmount',
      render: (text) => `¥${text}`,
    },
    {
      title: '销售日期',
      dataIndex: 'salesDate',
      key: 'salesDate',
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
            onClick={() => navigate(`/sales-data/edit/${record.salesId}`)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定要删除吗？"
            onConfirm={() => handleDelete(record.salesId)}
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
        <h1>销售数据</h1>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={() => navigate('/sales-data/new')}
        >
          新增销售记录
        </Button>
      </div>
      <Table
        columns={columns}
        dataSource={data}
        rowKey="salesId"
        loading={loading}
        pagination={{ pageSize: 10 }}
      />
    </div>
  )
}

export default SalesDataList
