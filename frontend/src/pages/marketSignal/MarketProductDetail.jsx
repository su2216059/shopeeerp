import React, { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import {
  Card, Row, Col, Statistic, Tag, Descriptions, Spin, message, Button, Progress, Tooltip, Divider, Empty, Tabs, Table
} from 'antd'
import {
  ArrowLeftOutlined, RiseOutlined, FallOutlined, StarFilled, ShoppingCartOutlined,
  TrophyOutlined, BarChartOutlined, HistoryOutlined, LineChartOutlined, ExportOutlined
} from '@ant-design/icons'
import { marketProductApi, marketSignalApi } from '../../api'
import { formatDateTime } from '../../utils/dateUtils'
import TrendChart from '../../components/TrendChart'

const MarketProductDetail = () => {
  const { platform, productId } = useParams()
  const navigate = useNavigate()
  const [loading, setLoading] = useState(true)
  const [data, setData] = useState(null)
  const [snapshots, setSnapshots] = useState([])

  useEffect(() => {
    fetchData()
  }, [platform, productId])

  const fetchData = async () => {
    setLoading(true)
    try {
      const result = await marketProductApi.getById(platform, productId)
      setData(result?.data || null)
      
      // 获取快照历史
      const snapshotResult = await marketProductApi.getSnapshots(platform, productId, { limit: 30 })
      setSnapshots(Array.isArray(snapshotResult?.data) ? snapshotResult.data : [])
    } catch (error) {
      message.error('加载数据失败')
    } finally {
      setLoading(false)
    }
  }

  // 导出快照数据
  const handleExportSnapshots = () => {
    if (!snapshots || snapshots.length === 0) {
      message.warning('没有数据可导出')
      return
    }

    const headers = ['日期', '价格', '评分', '评论数', '分类排名', '库存状态', '数据来源']
    const rows = snapshots.map(s => [
      s.snapshotDate,
      s.price || '',
      s.rating || '',
      s.reviewCount || '',
      s.categoryRank || '',
      s.availabilityStatus === 'in_stock' ? '有货' : '缺货',
      s.dataSource || ''
    ])

    const csvContent = [headers.join(','), ...rows.map(r => r.join(','))].join('\n')
    const BOM = '\uFEFF'
    const blob = new Blob([BOM + csvContent], { type: 'text/csv;charset=utf-8;' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `product_${productId}_snapshots.csv`
    link.click()
    URL.revokeObjectURL(url)
    message.success('导出成功')
  }

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: 100 }}>
        <Spin size="large" />
      </div>
    )
  }

  if (!data || !data.product) {
    return (
      <div style={{ textAlign: 'center', padding: 100 }}>
        <Empty description="商品不存在" />
        <Button type="primary" onClick={() => navigate('/market/products')}>
          返回列表
        </Button>
      </div>
    )
  }

  const { product, latestSnapshot, weeklyEstimate, monthlyEstimate, trendSignal } = data

  const renderTrend = (value, label) => {
    if (value === null || value === undefined) return <Statistic title={label} value="-" />
    const num = Number(value)
    return (
      <Statistic
        title={label}
        value={Math.abs(num)}
        precision={1}
        valueStyle={{ color: num >= 0 ? '#3f8600' : '#cf1322' }}
        prefix={num >= 0 ? <RiseOutlined /> : <FallOutlined />}
        suffix="%"
      />
    )
  }

  const renderConfidenceStars = (score) => {
    if (!score) return '-'
    const stars = Math.round(score / 20)
    return (
      <Tooltip title={`置信度: ${score}%`}>
        <span>
          {[1, 2, 3, 4, 5].map((i) => (
            <StarFilled key={i} style={{ color: i <= stars ? '#faad14' : '#d9d9d9', marginRight: 2 }} />
          ))}
        </span>
      </Tooltip>
    )
  }

  const getStockRiskTag = (level) => {
    const map = {
      low: { color: 'green', text: '库存充足' },
      medium: { color: 'orange', text: '库存紧张' },
      high: { color: 'red', text: '断货风险' },
    }
    const config = map[level] || { color: 'default', text: level || '未知' }
    return <Tag color={config.color}>{config.text}</Tag>
  }

  return (
    <div>
      {/* 顶部导航 */}
      <div style={{ marginBottom: 16, display: 'flex', alignItems: 'center', gap: 16 }}>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/market/products')}>
          返回列表
        </Button>
        <h1 style={{ margin: 0 }}>商品详情</h1>
      </div>

      {/* 商品基本信息 */}
      <Card title="商品信息" style={{ marginBottom: 16 }}>
        <Descriptions column={3}>
          <Descriptions.Item label="商品ID">{product.platformProductId}</Descriptions.Item>
          <Descriptions.Item label="平台">{product.platform?.toUpperCase()}</Descriptions.Item>
          <Descriptions.Item label="市场">{product.market}</Descriptions.Item>
          <Descriptions.Item label="商品名称" span={3}>{product.title || '-'}</Descriptions.Item>
          <Descriptions.Item label="品牌">{product.brand || '-'}</Descriptions.Item>
          <Descriptions.Item label="分类" span={2}>{product.categoryPath || '-'}</Descriptions.Item>
          <Descriptions.Item label="首次发现">{formatDateTime(product.firstSeenAt) || '-'}</Descriptions.Item>
          <Descriptions.Item label="最后更新">{formatDateTime(product.lastSeenAt) || '-'}</Descriptions.Item>
        </Descriptions>
      </Card>

      {/* 最新快照数据 */}
      {latestSnapshot && (
        <Card title="最新数据" style={{ marginBottom: 16 }}>
          <Row gutter={16}>
            <Col span={4}>
              <Statistic
                title="价格"
                value={latestSnapshot.price || '-'}
                prefix="₽"
                valueStyle={{ color: '#1890ff' }}
              />
            </Col>
            <Col span={4}>
              <Statistic
                title="评分"
                value={latestSnapshot.rating || '-'}
                suffix="/ 5"
                prefix={<StarFilled style={{ color: '#faad14' }} />}
              />
            </Col>
            <Col span={4}>
              <Statistic
                title="评论数"
                value={latestSnapshot.reviewCount || 0}
              />
            </Col>
            <Col span={4}>
              <Statistic
                title="分类排名"
                value={latestSnapshot.categoryRank || '-'}
                prefix={<TrophyOutlined />}
              />
            </Col>
            <Col span={4}>
              <div>
                <div style={{ color: '#8c8c8c', marginBottom: 8 }}>库存状态</div>
                <Tag color={latestSnapshot.availabilityStatus === 'in_stock' ? 'green' : 'red'}>
                  {latestSnapshot.availabilityStatus === 'in_stock' ? '有货' : '缺货'}
                </Tag>
              </div>
            </Col>
            <Col span={4}>
              <div>
                <div style={{ color: '#8c8c8c', marginBottom: 8 }}>数据日期</div>
                <span>{latestSnapshot.snapshotDate}</span>
              </div>
            </Col>
          </Row>
        </Card>
      )}

      {/* 销量估算 */}
      <Row gutter={16} style={{ marginBottom: 16 }}>
        <Col span={12}>
          <Card title={<><ShoppingCartOutlined /> 周销量估算</>}>
            {weeklyEstimate ? (
              <>
                <Row gutter={16}>
                  <Col span={8}>
                    <Statistic
                      title="估算销量（低）"
                      value={weeklyEstimate.estimatedSalesMin || 0}
                    />
                  </Col>
                  <Col span={8}>
                    <Statistic
                      title="估算销量（中）"
                      value={weeklyEstimate.estimatedSalesMid || 0}
                      valueStyle={{ color: '#1890ff', fontWeight: 'bold' }}
                    />
                  </Col>
                  <Col span={8}>
                    <Statistic
                      title="估算销量（高）"
                      value={weeklyEstimate.estimatedSalesMax || 0}
                    />
                  </Col>
                </Row>
                <Divider />
                <Row gutter={16}>
                  <Col span={12}>
                    <div style={{ marginBottom: 8 }}>置信度</div>
                    {renderConfidenceStars(weeklyEstimate.confidenceScore)}
                  </Col>
                  <Col span={12}>
                    <div style={{ marginBottom: 8 }}>估算模型</div>
                    <Tag color="blue">{weeklyEstimate.estimationModel}</Tag>
                  </Col>
                </Row>
                <div style={{ marginTop: 8, color: '#8c8c8c', fontSize: 12 }}>
                  周期: {weeklyEstimate.periodStart} ~ {weeklyEstimate.periodEnd}
                </div>
              </>
            ) : (
              <Empty description="暂无周销量数据" />
            )}
          </Card>
        </Col>
        <Col span={12}>
          <Card title={<><BarChartOutlined /> 月销量估算</>}>
            {monthlyEstimate ? (
              <>
                <Row gutter={16}>
                  <Col span={8}>
                    <Statistic
                      title="估算销量（低）"
                      value={monthlyEstimate.estimatedSalesMin || 0}
                    />
                  </Col>
                  <Col span={8}>
                    <Statistic
                      title="估算销量（中）"
                      value={monthlyEstimate.estimatedSalesMid || 0}
                      valueStyle={{ color: '#1890ff', fontWeight: 'bold' }}
                    />
                  </Col>
                  <Col span={8}>
                    <Statistic
                      title="估算销量（高）"
                      value={monthlyEstimate.estimatedSalesMax || 0}
                    />
                  </Col>
                </Row>
                <Divider />
                <Row gutter={16}>
                  <Col span={12}>
                    <div style={{ marginBottom: 8 }}>置信度</div>
                    {renderConfidenceStars(monthlyEstimate.confidenceScore)}
                  </Col>
                  <Col span={12}>
                    <div style={{ marginBottom: 8 }}>估算模型</div>
                    <Tag color="blue">{monthlyEstimate.estimationModel}</Tag>
                  </Col>
                </Row>
                <div style={{ marginTop: 8, color: '#8c8c8c', fontSize: 12 }}>
                  周期: {monthlyEstimate.periodStart} ~ {monthlyEstimate.periodEnd}
                </div>
              </>
            ) : (
              <Empty description="暂无月销量数据" />
            )}
          </Card>
        </Col>
      </Row>

      {/* 趋势信号 */}
      <Card title="趋势信号" style={{ marginBottom: 16 }}>
        {trendSignal ? (
          <Row gutter={16}>
            <Col span={4}>
              {renderTrend(trendSignal.trend7d, '7天趋势')}
            </Col>
            <Col span={4}>
              {renderTrend(trendSignal.trend30d, '30天趋势')}
            </Col>
            <Col span={4}>
              <Statistic
                title="7天排名变化"
                value={trendSignal.rankChange7d || 0}
                valueStyle={{
                  color: (trendSignal.rankChange7d || 0) > 0 ? '#3f8600' : (trendSignal.rankChange7d || 0) < 0 ? '#cf1322' : '#8c8c8c'
                }}
                prefix={(trendSignal.rankChange7d || 0) > 0 ? '↑' : (trendSignal.rankChange7d || 0) < 0 ? '↓' : ''}
              />
            </Col>
            <Col span={4}>
              <Statistic
                title="评论增速(日)"
                value={trendSignal.reviewVelocity || 0}
                precision={1}
              />
            </Col>
            <Col span={4}>
              <div>
                <div style={{ color: '#8c8c8c', marginBottom: 8 }}>库存风险</div>
                {getStockRiskTag(trendSignal.stockRiskLevel)}
              </div>
            </Col>
            <Col span={4}>
              <div>
                <div style={{ color: '#8c8c8c', marginBottom: 8 }}>信号日期</div>
                <span>{trendSignal.signalDate}</span>
              </div>
            </Col>
          </Row>
        ) : (
          <Empty description="暂无趋势数据" />
        )}
      </Card>

      {/* 历史趋势图表 */}
      <Card 
        title={<><HistoryOutlined /> 历史趋势</>} 
        style={{ marginBottom: 16 }}
      >
        <Tabs
          defaultActiveKey="all"
          items={[
            {
              key: 'all',
              label: '综合趋势',
              children: <TrendChart data={snapshots} type="all" height={350} />,
            },
            {
              key: 'price',
              label: '价格走势',
              children: <TrendChart data={snapshots} type="price" height={300} />,
            },
            {
              key: 'review',
              label: '评论增长',
              children: <TrendChart data={snapshots} type="review" height={300} />,
            },
            {
              key: 'rank',
              label: '排名变化',
              children: <TrendChart data={snapshots} type="rank" height={300} />,
            },
          ]}
        />
      </Card>

      {/* 历史快照表格 */}
      <Card 
        title={<><LineChartOutlined /> 历史快照数据</>}
        style={{ marginBottom: 16 }}
        extra={
          <Button 
            icon={<ExportOutlined />} 
            size="small"
            onClick={() => handleExportSnapshots()}
          >
            导出
          </Button>
        }
      >
        <Table
          dataSource={snapshots}
          rowKey="id"
          size="small"
          pagination={{ pageSize: 10 }}
          columns={[
            {
              title: '日期',
              dataIndex: 'snapshotDate',
              key: 'snapshotDate',
              width: 120,
            },
            {
              title: '价格',
              dataIndex: 'price',
              key: 'price',
              width: 100,
              render: (v) => v ? `₽${v}` : '-',
            },
            {
              title: '评分',
              dataIndex: 'rating',
              key: 'rating',
              width: 80,
              render: (v) => v ? v.toFixed(1) : '-',
            },
            {
              title: '评论数',
              dataIndex: 'reviewCount',
              key: 'reviewCount',
              width: 100,
              render: (v, record, index) => {
                const prev = snapshots[index + 1]
                if (prev && v && prev.reviewCount) {
                  const delta = v - prev.reviewCount
                  if (delta > 0) {
                    return <span>{v} <Tag color="green" style={{ marginLeft: 4 }}>+{delta}</Tag></span>
                  }
                }
                return v || '-'
              },
            },
            {
              title: '分类排名',
              dataIndex: 'categoryRank',
              key: 'categoryRank',
              width: 100,
              render: (v, record, index) => {
                const prev = snapshots[index + 1]
                if (prev && v && prev.categoryRank) {
                  const delta = prev.categoryRank - v // 排名下降是好事
                  if (delta > 0) {
                    return <span>{v} <Tag color="green" style={{ marginLeft: 4 }}>↑{delta}</Tag></span>
                  } else if (delta < 0) {
                    return <span>{v} <Tag color="red" style={{ marginLeft: 4 }}>↓{Math.abs(delta)}</Tag></span>
                  }
                }
                return v || '-'
              },
            },
            {
              title: '库存状态',
              dataIndex: 'availabilityStatus',
              key: 'availabilityStatus',
              width: 100,
              render: (v) => (
                <Tag color={v === 'in_stock' ? 'green' : 'red'}>
                  {v === 'in_stock' ? '有货' : '缺货'}
                </Tag>
              ),
            },
            {
              title: '数据来源',
              dataIndex: 'dataSource',
              key: 'dataSource',
              width: 100,
              render: (v) => {
                const map = {
                  category_page: '分类页',
                  search_page: '搜索页',
                  detail_page: '详情页',
                }
                return map[v] || v || '-'
              },
            },
          ]}
        />
      </Card>

      {/* 数据说明 */}
      <Card title="数据说明" size="small">
        <p style={{ color: '#8c8c8c', margin: 0 }}>
          所有市场信号基于公开信息计算，不代表平台官方数据。销量估算采用评论增量法，
          置信度基于数据密度、评论稳定性和库存一致性综合计算。仅用于选品分析参考，不作为收益承诺。
        </p>
      </Card>
    </div>
  )
}

export default MarketProductDetail
