import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Popconfirm, message } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { inventoryApi } from '../../api'
import { formatDateTime } from '../../utils/dateUtils'

const InventoryList = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  useEffect(() => {
    fetchData()
  }, [])

  const fetchData = async () => {
    setLoading(true)
    try {
      const result = await inventoryApi.list()
      setData(result?.data || [])
    } catch (error) {
      message.error('加载数据失败')
    } finally {
      setLoading(false)
    }
  }

  const handleDelete = async (id) => {
    try {
      await inventoryApi.delete(id)
      message.success('删除成功')
      fetchData()
    } catch (error) {
      message.error('删除失败')
    }
  }

  const columns = [
    {
      title: 'ID',
      dataIndex: 'inventoryId',
      key: 'inventoryId',
      width: 80,
    },
    {
      title: '产品ID',
      dataIndex: 'productId',
      key: 'productId',
    },
    {
      title: '仓库ID',
      dataIndex: 'warehouseId',
      key: 'warehouseId',
    },
    {
      title: '数量',
      dataIndex: 'quantity',
      key: 'quantity',
    },
    {
      title: '最后更新',
      dataIndex: 'lastUpdated',
      key: 'lastUpdated',
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
            onClick={() => navigate(`/inventory/edit/${record.inventoryId}`)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定要删除吗？"
            onConfirm={() => handleDelete(record.inventoryId)}
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
        <h1>库存管理</h1>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={() => navigate('/inventory/new')}
        >
          新增库存记录
        </Button>
      </div>
      <Table
        columns={columns}
        dataSource={data}
        rowKey="inventoryId"
        loading={loading}
        pagination={{ pageSize: 10 }}
      />
    </div>
  )
}

export default InventoryList
