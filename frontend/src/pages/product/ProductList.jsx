import React, { useEffect, useState } from 'react'
import { Table, message, Image, Button, Space, Select, Input, DatePicker, Tabs } from 'antd'
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
  const [visibility, setVisibility] = useState('MODERATED')
  const [listVisibility, setListVisibility] = useState('IN_SALE')
  const [searchTitle, setSearchTitle] = useState('')
  const [searchProductCode, setSearchProductCode] = useState('')
  const [searchRange, setSearchRange] = useState([])

  useEffect(() => {
    fetchData(buildQueryParams({ visibility: listVisibility }))
  }, [])

  const fetchData = async (params = {}) => {
    setLoading(true)
    try {
      const result = await ozonProductApi.list(params)
      setData(result?.data || [])
    } catch (error) {
      message.error('加载数据失败')
    } finally {
      setLoading(false)
    }
  }

  const buildQueryParams = (overrides = {}) => {
    const created_from = searchRange?.[0] ? searchRange[0].toISOString() : undefined
    const created_to = searchRange?.[1] ? searchRange[1].toISOString() : undefined
    const title = searchTitle?.trim() || undefined
    const product_code = searchProductCode?.trim() || undefined
    return {
      title,
      product_code,
      created_from,
      created_to,
      visibility: listVisibility,
      ...overrides,
    }
  }

  const handleSync = async () => {
    setSyncing(true)
    try {
      const res = await ozonProductApi.sync({ visibility })
      const msg = res?.message || '同步任务已启动，请稍后刷新列表'
      message.success(msg)
      fetchData(buildQueryParams())
    } catch (error) {
      const status = error?.response?.status
      const msg = error?.response?.data?.message
      if (status === 429) {
        message.warning(msg || '同步进行中，请稍后再试')
      } else {
        message.error(msg || '同步失败')
      }
    } finally {
      setSyncing(false)
    }
  }

  const handleSearch = () => {
    fetchData(buildQueryParams())
  }

  const handleReset = () => {
    setSearchTitle('')
    setSearchProductCode('')
    setSearchRange([])
    fetchData(
      buildQueryParams({
        title: undefined,
        product_code: undefined,
        created_from: undefined,
        created_to: undefined,
      })
    )
  }

  const handleTabChange = (key) => {
    setListVisibility(key)
    fetchData(buildQueryParams({ visibility: key }))
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
      render: (_, record) => {
        const archived = record?.archived || record?.autoArchived
        const stock = Number(record?.stock ?? 0)
        if (archived) {
          return '档案'
        }
        if (stock > 0) {
          return '在售'
        }
        return '准备出售'
      },
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
          <Select
            value={visibility}
            onChange={(value) => setVisibility(value)}
            style={{ width: 180 }}
            options={[
              { value: 'MODERATED', label: '全部' },
              { value: 'IN_SALE', label: '在售' },
              { value: 'VISIBLE', label: '可见' },
              { value: 'ARCHIVED', label: '归档' },
              { value: 'EMPTY_STOCK', label: '库存不足' },
            ]}
          />
          <Button type="primary" loading={syncing} onClick={handleSync}>
            同步商品
          </Button>
        </Space>
      </div>
      <Tabs
        activeKey={listVisibility}
        onChange={handleTabChange}
        items={[
          { key: 'IN_SALE', label: '在售' },
          { key: 'TO_SUPPLY', label: '准备出售' },
          { key: 'ARCHIVED', label: '档案' },
        ]}
        style={{ marginBottom: 12 }}
      />
      <div style={{ marginBottom: 16 }}>
        <Space wrap>
          <Input
            placeholder="标题"
            value={searchTitle}
            onChange={(e) => setSearchTitle(e.target.value)}
            style={{ width: 200 }}
          />
          <Input
            placeholder="货号"
            value={searchProductCode}
            onChange={(e) => setSearchProductCode(e.target.value)}
            style={{ width: 180 }}
          />
          <DatePicker.RangePicker
            showTime
            value={searchRange}
            onChange={(value) => setSearchRange(value || [])}
            placeholder={['创建开始时间', '创建结束时间']}
          />
          <Button onClick={handleSearch}>搜索</Button>
          <Button onClick={handleReset}>重置</Button>
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
