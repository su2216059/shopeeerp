import React, { useMemo } from 'react'
import ReactECharts from 'echarts-for-react'
import { Empty } from 'antd'

/**
 * 趋势图表组件
 * @param {Array} data - 数据数组，格式: [{ date, price, reviewCount, categoryRank, rating }]
 * @param {string} type - 图表类型: 'price' | 'review' | 'rank' | 'all'
 * @param {number} height - 图表高度
 */
const TrendChart = ({ data = [], type = 'all', height = 300 }) => {
  const chartOption = useMemo(() => {
    if (!data || data.length === 0) return null

    // 按日期排序
    const sortedData = [...data].sort((a, b) => 
      new Date(a.snapshotDate || a.date) - new Date(b.snapshotDate || b.date)
    )

    const dates = sortedData.map(d => d.snapshotDate || d.date)
    const prices = sortedData.map(d => d.price)
    const reviews = sortedData.map(d => d.reviewCount)
    const ranks = sortedData.map(d => d.categoryRank)
    const ratings = sortedData.map(d => d.rating)

    const series = []
    const yAxis = []
    const legend = []

    if (type === 'price' || type === 'all') {
      series.push({
        name: '价格 (₽)',
        type: 'line',
        data: prices,
        yAxisIndex: type === 'all' ? 0 : 0,
        smooth: true,
        itemStyle: { color: '#1890ff' },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(24, 144, 255, 0.3)' },
              { offset: 1, color: 'rgba(24, 144, 255, 0.05)' }
            ]
          }
        }
      })
      legend.push('价格 (₽)')
    }

    if (type === 'review' || type === 'all') {
      series.push({
        name: '评论数',
        type: 'line',
        data: reviews,
        yAxisIndex: type === 'all' ? 1 : 0,
        smooth: true,
        itemStyle: { color: '#52c41a' },
      })
      legend.push('评论数')
    }

    if (type === 'rank' || type === 'all') {
      series.push({
        name: '分类排名',
        type: 'line',
        data: ranks,
        yAxisIndex: type === 'all' ? 2 : 0,
        smooth: true,
        itemStyle: { color: '#faad14' },
      })
      legend.push('分类排名')
    }

    // 配置 Y 轴
    if (type === 'all') {
      yAxis.push(
        {
          type: 'value',
          name: '价格',
          position: 'left',
          axisLabel: { formatter: '₽{value}' },
          splitLine: { show: false },
        },
        {
          type: 'value',
          name: '评论数',
          position: 'right',
          splitLine: { show: false },
        },
        {
          type: 'value',
          name: '排名',
          position: 'right',
          offset: 60,
          inverse: true, // 排名越小越好
          splitLine: { show: false },
        }
      )
    } else if (type === 'price') {
      yAxis.push({
        type: 'value',
        name: '价格 (₽)',
        axisLabel: { formatter: '₽{value}' },
      })
    } else if (type === 'review') {
      yAxis.push({
        type: 'value',
        name: '评论数',
      })
    } else if (type === 'rank') {
      yAxis.push({
        type: 'value',
        name: '分类排名',
        inverse: true,
      })
    }

    return {
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'cross' }
      },
      legend: {
        data: legend,
        top: 0,
      },
      grid: {
        left: '3%',
        right: type === 'all' ? '15%' : '3%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: dates,
        boundaryGap: false,
      },
      yAxis,
      series,
    }
  }, [data, type])

  if (!data || data.length === 0) {
    return <Empty description="暂无历史数据" style={{ padding: '40px 0' }} />
  }

  return (
    <ReactECharts
      option={chartOption}
      style={{ height: height }}
      opts={{ renderer: 'canvas' }}
    />
  )
}

export default TrendChart
