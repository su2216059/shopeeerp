import React, { useEffect, useState } from 'react'
import { Card, Row, Col, Statistic, Tag, Button, Select, Empty, Spin, message, Divider, Table, Tooltip } from 'antd'
import { 
  PlusOutlined, DeleteOutlined, RiseOutlined, FallOutlined, StarFilled, 
  TrophyOutlined, SwapOutlined, LineChartOutlined
} from '@ant-design/icons'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { marketProductApi } from '../../api'
import TrendChart from '../../components/TrendChart'

const MarketCompare = () => {
  const [searchParams, setSearchParams] = useSearchParams()
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [products, setProducts] = useState([])
  const [allProducts, setAllProducts] = useState([])
  const [loadingProducts, setLoadingProducts] = useState(false)
  const [selectedIds, setSelectedIds] = useState([])
  const [compareData, setCompareData] = useState([])

  // 从 URL 参数获取初始选中的商品
  useEffect(() => {
    const ids = searchParams.get('ids')
    if (ids) {
      setSelectedIds(ids.split(','))
    }
    fetchAllProducts()
  }, [])

  // 当选中的商品变化时，获取详情
  useEffect(() => {
    if (selectedIds.length > 0) {
      fetchCompareData()
      // 更新 URL 参数
      setSearchParams({ ids: selectedIds.join(',') })
    } else {
      setCompareData([])
      setSearchParams({})
    }
  }, [selectedIds])

  const fetchAllProducts = async () => {
    setLoadingProducts(true)
    try {
      const result = await marketProductApi.list({ platform: 'ozon', size: 500 })
      setAllProducts(result?.data?.data || [])
    } catch (error) {
      message.error('加载商品列表失败')
    } finally {
      setLoadingProducts(false)
    }
  }

  const fetchCompareData = async () => {
    setLoading(true)
    try {
      const data = await Promise.all(
        selectedIds.map(async (id) => {
          const [platform, productId] = id.split(':')
          const detail = await marketProductApi.getById(platform || 'ozon', productId)
          const snapshots = await marketProductApi.getSnapshots(platform || 'ozon', productId, { limit: 30 })
          return {
            ...(detail?.data || {}),
            snapshots: Array.isArray(snapshots?.data) ? snapshots.data : [],
          }
        })
      )
      setCompareData(data)
    } catch (error) {
      message.error('加载商品详情失败')
    } finally {
      setLoading(false)
    }
  }

  const handleAddProduct = (value) => {
    if (selectedIds.length >= 4) {
      message.warning('最多可比较4个商品')
      return
    }
    if (!selectedIds.includes(value)) {
      setSelectedIds([...selectedIds, value])
    }
  }

  const handleRemoveProduct = (id) => {
    setSelectedIds(selectedIds.filter((i) => i !== id))
  }

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

  const renderConfidenceStars = (score) => {
    if (!score) return '-'
    const stars = Math.round(score / 20)
    return (
      <span>
        {[1, 2, 3, 4, 5].map((i) => (
          <StarFilled key={i} style={{ color: i <= stars ? '#faad14' : '#d9d9d9', marginRight: 2 }} />
        ))}
      </span>
    )
  }

  const compareFields = [
    { key: 'price', label: '价格', render: (d) => d.latestSnapshot?.price ? `₽${d.latestSnapshot.price}` : '-' },
    { key: 'rating', label: '评分', render: (d) => d.latestSnapshot?.rating?.toFixed(1) || '-' },
    { key: 'reviewCount', label: '评论数', render: (d) => d.latestSnapshot?.reviewCount || '-' },
    { key: 'categoryRank', label: '分类排名', render: (d) => d.latestSnapshot?.categoryRank || '-' },
    { key: 'availabilityStatus', label: '库存状态', render: (d) => (
      <Tag color={d.latestSnapshot?.availabilityStatus === 'in_stock' ? 'green' : 'red'}>
        {d.latestSnapshot?.availabilityStatus === 'in_stock' ? '有货' : '缺货'}
      </Tag>
    )},
    { key: 'trend7d', label: '7天趋势', render: (d) => renderTrend(d.trendSignal?.trend7d) },
    { key: 'trend30d', label: '30天趋势', render: (d) => renderTrend(d.trendSignal?.trend30d) },
    { key: 'reviewVelocity', label: '评论增速', render: (d) => d.trendSignal?.reviewVelocity ? `${d.trendSignal.reviewVelocity.toFixed(1)}/天` : '-' },
    { key: 'weeklySales', label: '周销量估算', render: (d) => d.weeklyEstimate ? `${d.weeklyEstimate.estimatedSalesMin} ~ ${d.weeklyEstimate.estimatedSalesMax}` : '-' },
    { key: 'monthlySales', label: '月销量估算', render: (d) => d.monthlyEstimate ? `${d.monthlyEstimate.estimatedSalesMin} ~ ${d.monthlyEstimate.estimatedSalesMax}` : '-' },
    { key: 'confidence', label: '置信度', render: (d) => renderConfidenceStars(d.weeklyEstimate?.confidenceScore) },
  ]

  return (
    <div>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1 style={{ margin: 0 }}><SwapOutlined /> 商品比较</h1>
        <span style={{ color: '#8c8c8c' }}>最多可比较4个商品</span>
      </div>

      {/* 商品选择区 */}
      <Card style={{ marginBottom: 16 }}>
        <Row gutter={16} align="middle">
          <Col flex="auto">
            <Select
              placeholder="搜索并添加商品进行比较"
              style={{ width: '100%' }}
              showSearch
              filterOption={(input, option) =>
                (option?.label ?? '').toLowerCase().includes(input.toLowerCase())
              }
              loading={loadingProducts}
              onChange={handleAddProduct}
              value={null}
              options={allProducts.map((p) => ({
                value: `${p.platform}:${p.platformProductId}`,
                label: `${p.title?.substring(0, 50) || p.platformProductId} ${p.brand ? `[${p.brand}]` : ''}`,
              }))}
            />
          </Col>
        </Row>
        
        {/* 已选商品标签 */}
        <div style={{ marginTop: 12 }}>
          {selectedIds.map((id) => {
            const [platform, productId] = id.split(':')
            const product = allProducts.find((p) => p.platform === platform && p.platformProductId === productId)
            return (
              <Tag 
                key={id} 
                closable 
                onClose={() => handleRemoveProduct(id)}
                style={{ marginBottom: 8 }}
              >
                {product?.title?.substring(0, 30) || productId}
              </Tag>
            )
          })}
        </div>
      </Card>

      {/* 加载中 */}
      {loading && (
        <div style={{ textAlign: 'center', padding: 50 }}>
          <Spin size="large" />
        </div>
      )}

      {/* 空状态 */}
      {!loading && compareData.length === 0 && (
        <Card>
          <Empty description="请选择商品进行比较">
            <Button type="primary" onClick={() => navigate('/market/products')}>
              去商品列表选择
            </Button>
          </Empty>
        </Card>
      )}

      {/* 比较结果 */}
      {!loading && compareData.length > 0 && (
        <>
          {/* 商品信息头部 */}
          <Card style={{ marginBottom: 16 }}>
            <Row gutter={16}>
              <Col span={4}>
                <div style={{ fontWeight: 'bold', color: '#8c8c8c' }}>比较项</div>
              </Col>
              {compareData.map((item, index) => (
                <Col span={Math.floor(20 / compareData.length)} key={index}>
                  <div style={{ textAlign: 'center' }}>
                    <div style={{ fontWeight: 'bold', marginBottom: 8 }}>
                      <Tooltip title={item.product?.title}>
                        {item.product?.title?.substring(0, 25) || '-'}
                        {item.product?.title?.length > 25 ? '...' : ''}
                      </Tooltip>
                    </div>
                    <div style={{ fontSize: 12, color: '#8c8c8c' }}>
                      {item.product?.brand && <Tag>{item.product.brand}</Tag>}
                    </div>
                    <Button 
                      type="link" 
                      size="small"
                      icon={<LineChartOutlined />}
                      onClick={() => navigate(`/market/products/${item.product?.platform}/${item.product?.platformProductId}`)}
                    >
                      详情
                    </Button>
                  </div>
                </Col>
              ))}
            </Row>
          </Card>

          {/* 数据比较表 */}
          <Card title="数据对比" style={{ marginBottom: 16 }}>
            {compareFields.map((field) => (
              <Row gutter={16} key={field.key} style={{ padding: '12px 0', borderBottom: '1px solid #f0f0f0' }}>
                <Col span={4}>
                  <span style={{ fontWeight: 500 }}>{field.label}</span>
                </Col>
                {compareData.map((item, index) => (
                  <Col span={Math.floor(20 / compareData.length)} key={index} style={{ textAlign: 'center' }}>
                    {field.render(item)}
                  </Col>
                ))}
              </Row>
            ))}
          </Card>

          {/* 价格趋势对比图 */}
          <Card title="价格趋势对比" style={{ marginBottom: 16 }}>
            <Row gutter={16}>
              {compareData.map((item, index) => (
                <Col span={Math.floor(24 / compareData.length)} key={index}>
                  <div style={{ textAlign: 'center', marginBottom: 8, fontWeight: 500 }}>
                    {item.product?.title?.substring(0, 20) || '-'}
                  </div>
                  <TrendChart data={item.snapshots || []} type="price" height={200} />
                </Col>
              ))}
            </Row>
          </Card>

          {/* 评论增长对比图 */}
          <Card title="评论增长对比" style={{ marginBottom: 16 }}>
            <Row gutter={16}>
              {compareData.map((item, index) => (
                <Col span={Math.floor(24 / compareData.length)} key={index}>
                  <div style={{ textAlign: 'center', marginBottom: 8, fontWeight: 500 }}>
                    {item.product?.title?.substring(0, 20) || '-'}
                  </div>
                  <TrendChart data={item.snapshots || []} type="review" height={200} />
                </Col>
              ))}
            </Row>
          </Card>
        </>
      )}

      {/* 说明 */}
      <Card size="small">
        <p style={{ color: '#8c8c8c', margin: 0 }}>
          <strong>使用说明：</strong>
          通过搜索框添加想要比较的商品，最多可同时比较4个商品。
          也可以从商品列表页选择多个商品后点击"比较"按钮进入此页面。
        </p>
      </Card>
    </div>
  )
}

export default MarketCompare
