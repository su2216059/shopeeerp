import React, { useEffect, useState } from 'react'
import { Card, Table, Tag, Tabs, message, Spin, Row, Col, Statistic, Tooltip, Button, Empty } from 'antd'
import { RiseOutlined, FallOutlined, TrophyOutlined, FireOutlined, StarFilled, ReloadOutlined, LineChartOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { marketProductApi } from '../../api'

const MarketTrending = () => {
  const [loading, setLoading] = useState(true)
  const [activeTab, setActiveTab] = useState('trend7d')
  const [data, setData] = useState([])
  const [signalDate, setSignalDate] = useState('')
  const navigate = useNavigate()

  useEffect(() => {
    fetchData(activeTab)
  }, [activeTab])

  const fetchData = async (type) => {
    setLoading(true)
    try {
      const result = await marketProductApi.getTrending({ platform: 'ozon', type, limit: 50 })
      setData(result?.data?.data || [])
      setSignalDate(result?.data?.signalDate || '')
    } catch (error) {
      message.error('加载数据失败')
      setData([])
    } finally {
      setLoading(false)
    }
  }

  const renderTrend = (value) => {
    if (value === null || value === undefined) return '-'
    const num = Number(value)
    if (num > 0) {
      return <Tag color="green" style={{ fontWeight: 'bold' }}><RiseOutlined /> +{num.toFixed(1)}%</Tag>
    } else if (num < 0) {
      return <Tag color="red"><FallOutlined /> {num.toFixed(1)}%</Tag>
    }
    return <Tag>0%</Tag>
  }

  const renderRankChange = (value) => {
    if (value === null || value === undefined) return '-'
    const num = Number(value)
    if (num > 0) {
      return <Tag color="green"><RiseOutlined /> ↑{num}</Tag>
    } else if (num < 0) {
      return <Tag color="red"><FallOutlined /> ↓{Math.abs(num)}</Tag>
    }
    return <Tag>-</Tag>
  }

  const columns = [
    {
      title: '排名',
      key: 'rank',
      width: 60,
      render: (_, __, index) => {
        const rankColors = ['#f5222d', '#fa8c16', '#faad14']
        return (
          <span style={{ 
            fontWeight: 'bold', 
            fontSize: index < 3 ? 18 : 14,
            color: rankColors[index] || '#8c8c8c'
          }}>
            {index + 1}
          </span>
        )
      },
    },
    {
      title: '商品',
      key: 'product',
      width: 350,
      render: (_, record) => {
        const product = record.product || {}
        return (
          <div>
            <a 
              onClick={() => navigate(`/market/products/${product.platform}/${product.platformProductId}`)}
              style={{ fontWeight: 500 }}
            >
              {product.title?.substring(0, 60) || product.platformProductId}
              {product.title?.length > 60 ? '...' : ''}
            </a>
            <div style={{ fontSize: 12, color: '#8c8c8c', marginTop: 4 }}>
              {product.brand && <Tag size="small">{product.brand}</Tag>}
              {product.categoryPath && (
                <span style={{ marginLeft: 4 }}>
                  {product.categoryPath.split(' > ').slice(-2).join(' > ')}
                </span>
              )}
            </div>
          </div>
        )
      },
    },
    {
      title: '价格',
      key: 'price',
      width: 100,
      render: (_, record) => {
        const snapshot = record.latestSnapshot || {}
        return snapshot.price ? `₽${snapshot.price}` : '-'
      },
    },
    {
      title: '评论数',
      key: 'reviewCount',
      width: 100,
      render: (_, record) => {
        const snapshot = record.latestSnapshot || {}
        return snapshot.reviewCount || '-'
      },
    },
    {
      title: '7天趋势',
      key: 'trend7d',
      width: 120,
      render: (_, record) => renderTrend(record.signal?.trend7d),
    },
    {
      title: '30天趋势',
      key: 'trend30d',
      width: 120,
      render: (_, record) => renderTrend(record.signal?.trend30d),
    },
    {
      title: '排名变化',
      key: 'rankChange',
      width: 100,
      render: (_, record) => renderRankChange(record.signal?.rankChange7d),
    },
    {
      title: '评论增速',
      key: 'reviewVelocity',
      width: 100,
      render: (_, record) => {
        const v = record.signal?.reviewVelocity
        return v ? `${v.toFixed(1)}/天` : '-'
      },
    },
    {
      title: '操作',
      key: 'action',
      width: 80,
      render: (_, record) => (
        <Button
          type="link"
          icon={<LineChartOutlined />}
          size="small"
          onClick={() => navigate(`/market/products/${record.product?.platform}/${record.product?.platformProductId}`)}
        >
          详情
        </Button>
      ),
    },
  ]

  const tabItems = [
    {
      key: 'trend7d',
      label: (
        <span>
          <FireOutlined style={{ color: '#f5222d' }} /> 增长最快
        </span>
      ),
      children: null,
    },
    {
      key: 'rankRising',
      label: (
        <span>
          <TrophyOutlined style={{ color: '#faad14' }} /> 排名上升
        </span>
      ),
      children: null,
    },
    {
      key: 'reviewVelocity',
      label: (
        <span>
          <StarFilled style={{ color: '#52c41a' }} /> 评论增长
        </span>
      ),
      children: null,
    },
  ]

  return (
    <div>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1 style={{ margin: 0 }}>热门商品榜单</h1>
        <div>
          {signalDate && (
            <Tag color="blue" style={{ marginRight: 8 }}>数据日期: {signalDate}</Tag>
          )}
          <Button icon={<ReloadOutlined />} onClick={() => fetchData(activeTab)}>
            刷新
          </Button>
        </div>
      </div>

      {/* 统计卡片 */}
      <Row gutter={16} style={{ marginBottom: 16 }}>
        <Col span={6}>
          <Card>
            <Statistic 
              title="上榜商品数" 
              value={data.length} 
              prefix={<FireOutlined style={{ color: '#f5222d' }} />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic 
              title="平均7天增长" 
              value={data.length > 0 ? (data.reduce((sum, d) => sum + (d.signal?.trend7d || 0), 0) / data.length).toFixed(1) : 0}
              suffix="%"
              valueStyle={{ color: '#52c41a' }}
              prefix={<RiseOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic 
              title="平均评论增速" 
              value={data.length > 0 ? (data.reduce((sum, d) => sum + (d.signal?.reviewVelocity || 0), 0) / data.length).toFixed(1) : 0}
              suffix="/天"
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic 
              title="平台" 
              value="Ozon" 
              valueStyle={{ color: '#005bff' }}
            />
          </Card>
        </Col>
      </Row>

      {/* 榜单选项卡 */}
      <Card>
        <Tabs
          activeKey={activeTab}
          onChange={setActiveTab}
          items={tabItems}
        />

        {loading ? (
          <div style={{ textAlign: 'center', padding: 50 }}>
            <Spin size="large" />
          </div>
        ) : data.length > 0 ? (
          <Table
            columns={columns}
            dataSource={data}
            rowKey={(record) => record.signal?.platformProductId || Math.random()}
            pagination={{ pageSize: 20, showQuickJumper: true }}
            scroll={{ x: 1200 }}
          />
        ) : (
          <Empty 
            description="暂无趋势数据，请先运行销量计算任务"
            style={{ padding: 50 }}
          >
            <Button type="primary" onClick={() => navigate('/market/products')}>
              去商品监控
            </Button>
          </Empty>
        )}
      </Card>

      {/* 说明 */}
      <Card size="small" style={{ marginTop: 16 }}>
        <p style={{ color: '#8c8c8c', margin: 0 }}>
          <strong>榜单说明：</strong>
          增长最快 = 7天评论增长率最高；
          排名上升 = 7天内分类排名上升最多；
          评论增长 = 每日评论增加数量最多。
          数据基于公开信息估算，仅供选品参考。
        </p>
      </Card>
    </div>
  )
}

export default MarketTrending
