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

const renderShort = (value, len = 10) => {
  if (value === null || value === undefined || value === '') return '-'
  const str = String(value)
  if (str.length <= len) return str
  return (
    <span title={str}>
      {str.slice(0, len)}
      ...
    </span>
  )
}

const OzonOrderList = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [syncing, setSyncing] = useState(false)
  const [range, setRange] = useState([])

  const statusDict = {
    awaiting_registration: '等待注册',
    acceptance_in_progress: '正在验收',
    awaiting_approve: '等待确认',
    awaiting_packaging: '等待包装',
    awaiting_deliver: '等待装运',
    arbitration: '仲裁',
    client_arbitration: '快递客户仲裁',
    delivering: '运输中',
    driver_pickup: '司机处',
    delivered: '已送达',
    cancelled: '已取消',
    not_accepted: '未被分拣中心接受',
    sent_by_seller: '由卖家发送',
  }

  const substatusDict = {
    posting_acceptance_in_progress: '正在验收',
    posting_in_arbitration: '仲裁',
    posting_created: '已创建',
    posting_in_carriage: '在运输途中',
    posting_not_in_carriage: '未在运输中',
    posting_registered: '已登记',
    posting_transferring_to_delivery: '移交给快递',
    posting_awaiting_passport_data: '等待护照资料',
    posting_awaiting_registration: '等待注册',
    posting_registration_error: '注册错误',
    posting_split_pending: '已创建（拆分中）',
    posting_canceled: '已取消',
    posting_in_client_arbitration: '快递会员仲裁',
    posting_delivered: '已送达',
    posting_received: '已收到',
    posting_conditionally_delivered: '暂时送到',
    posting_in_courier_service: '快递员在路上',
    posting_in_pickup_point: '在取货点',
    posting_on_way_to_city: '发往城市途中',
    posting_on_way_to_pickup_point: '正发往取货点',
    posting_returned_to_warehouse: '返回仓库',
    posting_transferred_to_courier_service: '转交快递员',
    posting_driver_pick_up: '在司机那儿',
    posting_not_in_sort_center: '集散中心未收到',
    ship_failed: '备货失败',
  }

  const actionDict = {
    arbitration: '提出争议',
    awaiting_delivery: '转为等待发运',
    can_create_chat: '开启买家聊天',
    cancel: '取消货件',
    click_track_number: '查看追踪号历史',
    customer_phone_available: '获取买家电话',
    has_weight_products: '含按重量结算商品',
    hide_region_and_city: '隐藏买家地区/城市',
    invoice_get: '获取发票信息',
    invoice_send: '创建发票',
    invoice_update: '编辑发票',
    label_download_big: '下载大标签',
    label_download_small: '下载小标签',
    label_download: '下载标签',
    non_int_delivered: '转为可能已收',
    non_int_delivering: '转为运输中',
    non_int_last_mile: '转为派件中',
    product_cancel: '取消部分商品',
    set_cutoff: '设置发货日期',
    set_timeslot: '修改送货时间',
    set_track_number: '指定/更改追踪号',
    ship_async_in_process: '备货中',
    ship_async_retry: '重试发货',
    ship_async: '备货货件',
    ship_with_additional_info: '填写额外信息发货',
    ship: '备货货件',
    update_cis: '修改附加信息',
  }

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
      render: (text) => renderShort(text),
    },
    {
      title: '订单号',
      dataIndex: 'orderNumber',
      key: 'orderNumber',
      width: 160,
      render: (text) => renderShort(text),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 140,
      render: (text, record) => (
        <Space>
          {text ? (
            <Tag color="blue">
              {statusDict[text] ? `${statusDict[text]} (${text})` : text}
            </Tag>
          ) : (
            '-'
          )}
          {record.substatus ? (
            <Tag color="gold">
              {substatusDict[record.substatus]
                ? `${substatusDict[record.substatus]} (${record.substatus})`
                : record.substatus}
            </Tag>
          ) : null}
        </Space>
      ),
    },
    {
      title: '追踪号',
      dataIndex: 'trackingNumber',
      key: 'trackingNumber',
      width: 180,
      render: (text) => renderShort(text, 12),
    },
    {
      title: '配送方式',
      dataIndex: 'deliveryMethodName',
      key: 'deliveryMethodName',
      width: 220,
      render: (text) => renderShort(text, 12),
    },
    {
      title: '仓库',
      dataIndex: 'warehouseName',
      key: 'warehouseName',
      width: 200,
      render: (text) => renderShort(text, 12),
    },
    {
      title: '物流商',
      dataIndex: 'tplProviderName',
      key: 'tplProviderName',
      width: 160,
      render: (text) => renderShort(text, 12),
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
    {
      title: '可用操作',
      dataIndex: 'availableActions',
      key: 'availableActions',
      width: 260,
      render: (text) => {
        if (!text) return '-'
        let arr = []
        if (Array.isArray(text)) {
          arr = text
        } else {
          try {
            const parsed = JSON.parse(text)
            if (Array.isArray(parsed)) {
              arr = parsed
            }
          } catch (e) {
            arr = []
          }
        }
        if (!arr.length) return '-'
        return (
          <Space wrap>
            {arr.map((a) => (
              <Tag key={a}>{actionDict[a] || a}</Tag>
            ))}
          </Space>
        )
      },
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
