import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Popconfirm, message, Tag } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { customerSupportApi } from '../../api'
import { formatDateTime } from '../../utils/dateUtils'

const CustomerSupportList = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  useEffect(() => {
    fetchData()
  }, [])

  const fetchData = async () => {
    setLoading(true)
    try {
      const result = await customerSupportApi.list()
      setData(result || [])
    } catch (error) {
      message.error('加载数据失败')
    } finally {
      setLoading(false)
    }
  }

  const handleDelete = async (id) => {
    try {
      await customerSupportApi.delete(id)
      message.success('删除成功')
      fetchData()
    } catch (error) {
      message.error('删除失败')
    }
  }

  const columns = [
    {
      title: 'ID',
      dataIndex: 'supportId',
      key: 'supportId',
      width: 80,
    },
    {
      title: '客户ID',
      dataIndex: 'customerId',
      key: 'customerId',
    },
    {
      title: '问题描述',
      dataIndex: 'issueDescription',
      key: 'issueDescription',
      ellipsis: true,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (text) => <Tag color="blue">{text}</Tag>,
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
          <Button
            type="link"
            icon={<EditOutlined />}
            onClick={() => navigate(`/customer-support/edit/${record.supportId}`)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定要删除吗？"
            onConfirm={() => handleDelete(record.supportId)}
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
        <h1>客户支持</h1>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={() => navigate('/customer-support/new')}
        >
          新增支持记录
        </Button>
      </div>
      <Table
        columns={columns}
        dataSource={data}
        rowKey="supportId"
        loading={loading}
        pagination={{ pageSize: 10 }}
      />
    </div>
  )
}

export default CustomerSupportList
