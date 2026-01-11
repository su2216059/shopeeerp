import React, { useEffect, useState } from 'react'
import { Table, message, Image, Button, Space } from 'antd'
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
      const res = await ozonOrderApi.sync()
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
      title: 'ID/货件号码',
      dataIndex: 'orderNumber',
      key: 'orderNumber',
      width: 180,
      render: (text, record) => (
        <div>
          <div>{renderValue(text)}</div>
          <div style={{ color: '#888' }}>{renderValue(record.shipmentNumber)}</div>
        </div>
      ),
    },
    {
      title: '图片',
      dataIndex: 'imageUrl',
      key: 'imageUrl',
      width: 110,
      render: (text) =>
        text ? <Image src={text} alt="商品图片" width={60} height={60} /> : '-',
    },
    {
      title: '详情',
      dataIndex: 'detail',
      key: 'detail',
      width: 180,
      render: renderValue,
    },
    {
      title: '店铺',
      dataIndex: 'store',
      key: 'store',
      width: 100,
      render: renderValue,
    },
    {
      title: '金额',
      dataIndex: 'amount',
      key: 'amount',
      width: 120,
      render: (text, record) =>
        record.currency ? `${renderValue(text)} ${record.currency}` : renderValue(text),
    },
    {
      title: 'Ozon税费',
      dataIndex: 'ozonTax',
      key: 'ozonTax',
      width: 120,
      render: renderValue,
    },
    {
      title: '成本',
      dataIndex: 'cost',
      key: 'cost',
      width: 120,
      render: renderValue,
    },
    {
      title: '利润',
      dataIndex: 'profit',
      key: 'profit',
      width: 120,
      render: renderValue,
    },
    {
      title: '包表',
      dataIndex: 'packageInfo',
      key: 'packageInfo',
      width: 140,
      render: renderValue,
    },
    {
      title: '数量',
      dataIndex: 'quantity',
      key: 'quantity',
      width: 80,
      render: renderValue,
    },
    {
      title: '采购单号',
      dataIndex: 'purchaseNo',
      key: 'purchaseNo',
      width: 140,
      render: renderValue,
    },
    {
      title: '国内单号',
      dataIndex: 'domesticNo',
      key: 'domesticNo',
      width: 140,
      render: renderValue,
    },
    {
      title: '重量',
      dataIndex: 'weight',
      key: 'weight',
      width: 90,
      render: renderValue,
    },
    {
      title: '追踪号码',
      dataIndex: 'trackingNo',
      key: 'trackingNo',
      width: 160,
      render: renderValue,
    },
    {
      title: '快递类型',
      dataIndex: 'logisticsType',
      key: 'logisticsType',
      width: 120,
      render: renderValue,
    },
    {
      title: '发货状态',
      dataIndex: 'shipStatus',
      key: 'shipStatus',
      width: 120,
      render: renderValue,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: renderValue,
    },
    {
      title: '子状态',
      dataIndex: 'subStatus',
      key: 'subStatus',
      width: 120,
      render: renderValue,
    },
    {
      title: '下单时间',
      dataIndex: 'orderTime',
      key: 'orderTime',
      width: 180,
      render: (text) => renderValue(formatDateTime(text)),
    },
  ]

  return (
    <div>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
        <h1>Ozon 订单列表</h1>
        <Space>
          <Button type="primary" loading={syncing} onClick={handleSync}>
            同步订单
          </Button>
        </Space>
      </div>
      <Table
        columns={columns}
        dataSource={data}
        rowKey="id"
        loading={loading}
        pagination={{ pageSize: 10 }}
        scroll={{ x: 1800 }}
      />
    </div>
  )
}

export default OzonOrderList
