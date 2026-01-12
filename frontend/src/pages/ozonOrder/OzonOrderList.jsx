import React, { useEffect, useState } from 'react'
import { Table, message, Button, Space, DatePicker, Tag } from 'antd'
import { ozonOrderApi } from '../../api'
import { formatDateTime } from '../../utils/dateUtils'

const renderValue = (value) => {
  if (value === null || value === undefined || value === '') {
    return '-'
  }
  if (typeof value === 'object') {
    try {
      return JSON.stringify(value)
    } catch (e) {
      return '-'
    }
  }
  return value
}

const OzonOrderList = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [syncing, setSyncing] = useState(false)
  const [range, setRange] = useState([])

  useEffect(() => {
    fetchData()
  }, [])

  const fetchData = async () => {
    setLoading(true)
    try {
      const result = await ozonOrderApi.list()
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
      const start = range?.[0] ? range[0].toISOString() : undefined
      const end = range?.[1] ? range[1].toISOString() : undefined
      const res = await ozonOrderApi.sync({ start, end })
      const msg = res?.message || '同步任务已启动，请稍后刷新列表'
      message.success(msg)
      fetchData()
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

  const columns = [
    {
      title: '货件号',
      dataIndex: 'postingNumber',
      key: 'postingNumber',
      width: 180,
      render: renderValue,
    },
    {
      title: '订单号',
      dataIndex: 'orderNumber',
      key: 'orderNumber',
      width: 160,
      render: renderValue,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 140,
      render: (text, record) => (
        <Space>
          {text ? <Tag color="blue">{text}</Tag> : '-'}
          {record.substatus ? <Tag color="gold">{record.substatus}</Tag> : null}
        </Space>
      ),
    },
    {
      title: '追踪号',
      dataIndex: 'trackingNumber',
      key: 'trackingNumber',
      width: 180,
      render: renderValue,
    },
    {
      title: '配送方式',
      dataIndex: 'deliveryMethodName',
      key: 'deliveryMethodName',
      width: 220,
      render: renderValue,
    },
    {
      title: '仓库',
      dataIndex: 'warehouseName',
      key: 'warehouseName',
      width: 200,
      render: renderValue,
    },
    {
      title: '物流商',
      dataIndex: 'tplProviderName',
      key: 'tplProviderName',
      width: 160,
      render: renderValue,
    },
    {
      title: '处理开始',
      dataIndex: 'inProcessAt',
      key: 'inProcessAt',
      width: 180,
      render: (text) => renderValue(formatDateTime(text)),
    },
    {
      title: '装运时间',
      dataIndex: 'shipmentDate',
      key: 'shipmentDate',
      width: 180,
      render: (text) => renderValue(formatDateTime(text)),
    },
    {
      title: '最迟装运',
      dataIndex: 'shipmentDateWithoutDelay',
      key: 'shipmentDateWithoutDelay',
      width: 180,
      render: (text) => renderValue(formatDateTime(text)),
    },
    {
      title: '交付时间',
      dataIndex: 'deliveringDate',
      key: 'deliveringDate',
      width: 180,
      render: (text) => renderValue(formatDateTime(text)),
    },
    {
      title: '最后变更',
      dataIndex: 'lastChangedAt',
      key: 'lastChangedAt',
      width: 180,
      render: (text) => renderValue(formatDateTime(text)),
    },
  ]

  return (
    <div>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
        <h1>Ozon 订单列表</h1>
        <Space>
          <DatePicker.RangePicker
            showTime
            value={range}
            onChange={(v) => setRange(v || [])}
            placeholder={['开始时间', '结束时间']}
          />
          <Button type="primary" loading={syncing} onClick={handleSync}>
            同步订单
          </Button>
        </Space>
      </div>
      <Table
        columns={columns}
        dataSource={data}
        rowKey="postingNumber"
        loading={loading}
        pagination={{ pageSize: 10 }}
        scroll={{ x: 1600 }}
      />
    </div>
  )
}

export default OzonOrderList
