import React, { useState, useEffect } from 'react'
import { Table, message, Image, Button, Space } from 'antd'
import { ozonProductApi } from '../../api'
import { formatDateTime } from '../../utils/dateUtils'

const renderValue = (value, suffix = '') => {
  if (value === null || value === undefined || value === '') {
    return '-'
  }
  if (Array.isArray(value)) {
    return value.length ? value.join(', ') : '-'
  }
  if (typeof value === 'object') {
    try {
      return JSON.stringify(value)
    } catch (e) {
      return '-'
    }
  }
  return `${value}${suffix}`
}

const ProductList = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [syncing, setSyncing] = useState(false)

  useEffect(() => {
    fetchData()
  }, [])

  const fetchData = async () => {
    setLoading(true)
    try {
      const result = await ozonProductApi.list()
      setData(result || [])
    } catch (error) {
      message.error('加载数据失败')
    } finally {
      setLoading(false)
    }
  }

  const handleSync = async () => {
    setSyncing(true)
    try {
      const res = await ozonProductApi.sync()
      const count = res?.count ?? 0
      message.success(`同步完成，新增/更新 ${count} 条商品`)
      fetchData()
    } catch (error) {
      message.error('同步失败')
    } finally {
      setSyncing(false)
    }
  }

  const columns = [
    {
      title: '图片',
      dataIndex: 'imageUrl',
      key: 'imageUrl',
      width: 100,
      render: (text) =>
        text ? <Image src={text} alt="商品图片" width={60} height={60} /> : '-',
    },
    {
      title: '标题',
      dataIndex: 'title',
      key: 'title',
    },
    {
      title: '店铺',
      dataIndex: 'store',
      key: 'store',
      width: 120,
      render: (text) => (text ?? '-'),
    },
    {
      title: '货号',
      dataIndex: 'productCode',
      key: 'productCode',
      width: 140,
      render: (text) => (text ?? '-'),
    },
    {
      title: '变体数量',
      dataIndex: 'variantCount',
      key: 'variantCount',
      width: 100,
      render: renderValue,
    },
    {
      title: '库存',
      dataIndex: 'stock',
      key: 'stock',
      width: 90,
      render: (text) => (text ?? '-'),
    },
    {
      title: '颜色',
      dataIndex: 'color',
      key: 'color',
      render: (text) => (text ?? '-'),
    },
    {
      title: '定时任务',
      dataIndex: 'schedule',
      key: 'schedule',
      render: renderValue,
    },
    {
      title: '型号',
      dataIndex: 'model',
      key: 'model',
      render: renderValue,
    },
    {
      title: '重量',
      dataIndex: 'weight',
      key: 'weight',
      width: 90,
      render: (text) => renderValue(text),
    },
    {
      title: '长',
      dataIndex: 'length',
      key: 'length',
      width: 80,
      render: renderValue,
    },
    {
      title: '宽',
      dataIndex: 'width',
      key: 'width',
      width: 80,
      render: renderValue,
    },
    {
      title: '高',
      dataIndex: 'height',
      key: 'height',
      width: 80,
      render: renderValue,
    },
    {
      title: '价格',
      dataIndex: 'price',
      key: 'price',
      width: 120,
      render: (text) => renderValue(text),
    },
    {
      title: '最低价格',
      dataIndex: 'minPrice',
      key: 'minPrice',
      width: 120,
      render: (text) => renderValue(text),
    },
    {
      title: '折扣前价格',
      dataIndex: 'oldPrice',
      key: 'oldPrice',
      width: 120,
      render: (text) => renderValue(text),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: renderValue,
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 180,
      render: (text) => renderValue(formatDateTime(text)),
    },
    {
      title: '更新时间',
      dataIndex: 'updatedAt',
      key: 'updatedAt',
      width: 180,
      render: (text) => renderValue(formatDateTime(text)),
    },
  ]

  return (
    <div>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
        <h1>Ozon 商品列表</h1>
        <Space>
          <Button type="primary" loading={syncing} onClick={handleSync}>
            同步商品
          </Button>
        </Space>
      </div>
      <Table
        columns={columns}
        dataSource={data}
        rowKey="id"
        loading={loading}
        pagination={{ pageSize: 10 }}
        scroll={{ x: 1400 }}
      />
    </div>
  )
}

export default ProductList
