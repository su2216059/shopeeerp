import React, { useEffect, useState } from 'react'
import { Table, message, Button, Space, Input, Select, Tag, Card, Statistic, Row, Col, Tooltip, Progress, Dropdown } from 'antd'
import { SearchOutlined, ReloadOutlined, RiseOutlined, FallOutlined, LineChartOutlined, ExportOutlined, SortAscendingOutlined, FilterOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { marketProductApi, marketSignalApi } from '../../api'
import { formatDateTime } from '../../utils/dateUtils'

const { Search } = Input

const MarketProductList = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [total, setTotal] = useState(0)
  const [page, setPage] = useState(1)
  const [pageSize, setPageSize] = useState(20)
  const [keyword, setKeyword] = useState('')
  const [brand, setBrand] = useState('')
  const [category, setCategory] = useState('')
  const [sortField, setSortField] = useState('lastSeenAt')
  const [sortOrder, setSortOrder] = useState('desc')
  const [brands, setBrands] = useState([])
  const [categories, setCategories] = useState([])
  const [calculating, setCalculating] = useState(false)
  const [selectedRowKeys, setSelectedRowKeys] = useState([])
  const navigate = useNavigate()

  useEffect(() => {
    fetchData()
    fetchFilters()
  }, [page, pageSize, sortField, sortOrder])

  const fetchData = async () => {
    setLoading(true)
    try {
      const params = {
        platform: 'ozon',
        page,
        size: pageSize,
        sortField,
        sortOrder,
      }
      if (keyword) params.keyword = keyword
      if (brand) params.brand = brand
      if (category) params.categoryPath = category

      const result = await marketProductApi.list(params)
      setData(result?.data?.data || [])
      setTotal(result?.data?.total || 0)
    } catch (error) {
      message.error('加载数据失败')
    } finally {
      setLoading(false)
    }
  }

  const fetchFilters = async () => {
    try {
      const result = await marketProductApi.getFilters('ozon')
      setBrands(result?.data?.brands || [])
      setCategories(result?.data?.categories || [])
    } catch (error) {
      // ignore
    }
  }

  const handleSearch = () => {
    setPage(1)
    fetchData()
  }

  const handleCalculate = async () => {
    setCalculating(true)
    try {
      const today = new Date().toISOString().split('T')[0]
      await marketSignalApi.calculateMonthly('ozon', today)
      await marketSignalApi.calculateTrend('ozon', today)
      message.success('计算任务已提交')
      fetchData()
    } catch (error) {
      message.error('计算失败')
    } finally {
      setCalculating(false)
    }
  }

  const handleExport = () => {
    if (data.length === 0) {
      message.warning('没有数据可导出')
      return
    }

    const headers = ['商品ID', '名称', '品牌', '分类', '首次发现', '最后更新']
    const rows = data.map(item => [
      item.platformProductId,
      item.title || '',
      item.brand || '',
      item.categoryPath || '',
      item.firstSeenAt || '',
      item.lastSeenAt || ''
    ])

    const csvContent = [headers.join(','), ...rows.map(r => r.map(c => `"${c}"`).join(','))].join('\n')
    const BOM = '\uFEFF'
    const blob = new Blob([BOM + csvContent], { type: 'text/csv;charset=utf-8;' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `market_products_${new Date().toISOString().split('T')[0]}.csv`
    link.click()
    URL.revokeObjectURL(url)
    message.success('导出成功')
  }

  const sortOptions = [
    { label: '最近更新', value: 'lastSeenAt' },
    { label: '首次发现', value: 'firstSeenAt' },
    { label: '商品名称', value: 'title' },
    { label: '品牌', value: 'brand' },
  ]

  const renderTrend = (value) => {
    if (value === null || value === undefined) return '-'
    const num = Number(value)
    if (num > 0) {
      return <Tag color="green"><RiseOutlined /> +{num.toFixed(1)}%</Tag>
    } else if (num < 0) {
      return <Tag color="red"><FallOutlined /> {num.toFixed(1)}%</Tag>
    }
    return <Tag>0%</Tag>
  }

  const renderConfidence = (score) => {
    if (score === null || score === undefined) return '-'
    let color = 'red'
    if (score >= 80) color = 'green'
    else if (score >= 60) color = 'blue'
    else if (score >= 40) color = 'orange'
    return (
      <Tooltip title={`置信度: ${score}%`}>
        <Progress percent={score} size="small" status={score >= 60 ? 'success' : 'exception'} showInfo={false} style={{ width: 60 }} />
      </Tooltip>
    )
  }

  const columns = [
    {
      title: '商品ID',
      dataIndex: 'platformProductId',
      key: 'platformProductId',
      width: 120,
      render: (text) => (
        <Tooltip title={text}>
          <span style={{ fontFamily: 'monospace' }}>{text?.slice(-8)}</span>
        </Tooltip>
      ),
    },
    {
      title: '商品名称',
      dataIndex: 'title',
      key: 'title',
      width: 300,
      ellipsis: true,
      render: (text, record) => (
        <a onClick={() => navigate(`/market/products/${record.platform}/${record.platformProductId}`)}>
          {text || '-'}
        </a>
      ),
    },
    {
      title: '品牌',
      dataIndex: 'brand',
      key: 'brand',
      width: 120,
      render: (text) => text || '-',
    },
    {
      title: '分类',
      dataIndex: 'categoryPath',
      key: 'categoryPath',
      width: 200,
      ellipsis: true,
      render: (text) => (
        <Tooltip title={text}>
          {text?.split(' > ').slice(-2).join(' > ') || '-'}
        </Tooltip>
      ),
    },
    {
      title: '上架时间',
      dataIndex: 'firstSeenAt',
      key: 'firstSeenAt',
      width: 160,
      render: (text) => formatDateTime(text) || '-',
    },
    {
      title: '最后更新',
      dataIndex: 'lastSeenAt',
      key: 'lastSeenAt',
      width: 160,
      render: (text) => formatDateTime(text) || '-',
    },
    {
      title: '操作',
      key: 'action',
      width: 100,
      render: (_, record) => (
        <Button
          type="link"
          icon={<LineChartOutlined />}
          onClick={() => navigate(`/market/products/${record.platform}/${record.platformProductId}`)}
        >
          详情
        </Button>
      ),
    },
  ]

  return (
    <div>
      <div style={{ marginBottom: 16 }}>
        <h1>市场商品监控</h1>
      </div>

      {/* 统计卡片 */}
      <Row gutter={16} style={{ marginBottom: 16 }}>
        <Col span={6}>
          <Card>
            <Statistic title="监控商品总数" value={total} />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic title="品牌数量" value={brands.length} />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic title="分类数量" value={categories.length} />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic title="平台" value="Ozon" valueStyle={{ color: '#005bff' }} />
          </Card>
        </Col>
      </Row>

      {/* 搜索和操作栏 */}
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', flexWrap: 'wrap', gap: 8 }}>
        <Space wrap>
          <Search
            placeholder="搜索商品名称或ID"
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            onSearch={handleSearch}
            style={{ width: 220 }}
            allowClear
          />
          <Select
            placeholder="选择品牌"
            value={brand || undefined}
            onChange={(v) => { setBrand(v || ''); setPage(1) }}
            style={{ width: 150 }}
            allowClear
            showSearch
            filterOption={(input, option) =>
              (option?.label ?? '').toLowerCase().includes(input.toLowerCase())
            }
            options={brands.map((b) => ({ value: b, label: b }))}
          />
          <Select
            placeholder="选择分类"
            value={category || undefined}
            onChange={(v) => { setCategory(v || ''); setPage(1) }}
            style={{ width: 200 }}
            allowClear
            showSearch
            filterOption={(input, option) =>
              (option?.label ?? '').toLowerCase().includes(input.toLowerCase())
            }
            options={categories.map((c) => ({ value: c, label: c.split(' > ').slice(-2).join(' > ') }))}
          />
          <Button icon={<SearchOutlined />} onClick={handleSearch}>
            搜索
          </Button>
        </Space>
        <Space wrap>
          <Select
            value={sortField}
            onChange={(v) => setSortField(v)}
            style={{ width: 130 }}
            options={sortOptions}
            prefix={<SortAscendingOutlined />}
          />
          <Select
            value={sortOrder}
            onChange={(v) => setSortOrder(v)}
            style={{ width: 90 }}
            options={[
              { label: '降序', value: 'desc' },
              { label: '升序', value: 'asc' },
            ]}
          />
          <Button 
            disabled={selectedRowKeys.length < 2}
            onClick={() => {
              const ids = selectedRowKeys.map(id => {
                const item = data.find(d => d.id === id)
                return item ? `${item.platform}:${item.platformProductId}` : null
              }).filter(Boolean)
              navigate(`/market/compare?ids=${ids.join(',')}`)
            }}
          >
            比较 ({selectedRowKeys.length})
          </Button>
          <Button icon={<ExportOutlined />} onClick={handleExport}>
            导出
          </Button>
          <Button icon={<ReloadOutlined />} onClick={fetchData}>
            刷新
          </Button>
          <Button type="primary" loading={calculating} onClick={handleCalculate}>
            重新计算销量
          </Button>
        </Space>
      </div>

      {/* 商品表格 */}
      <Table
        columns={columns}
        dataSource={data}
        rowKey="id"
        loading={loading}
        rowSelection={{
          selectedRowKeys,
          onChange: setSelectedRowKeys,
        }}
        pagination={{
          current: page,
          pageSize: pageSize,
          total: total,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total, range) => `第 ${range[0]}-${range[1]} 条，共 ${total} 条`,
          onChange: (p, ps) => {
            setPage(p)
            setPageSize(ps)
          },
        }}
        scroll={{ x: 1000 }}
      />
    </div>
  )
}

export default MarketProductList
