import React, { useEffect, useRef, useState } from 'react'
import { Table, message, Button, Space, DatePicker, Tag, Image, Select, InputNumber, Modal } from 'antd'
import { ozonOrderApi, ozonProfitApi } from '../../api'
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

const toNumber = (value) => {
  if (value === null || value === undefined || value === '') return 0
  const num = Number(value)
  return Number.isNaN(num) ? 0 : num
}

const formatMoney = (value) => {
  if (value === null || value === undefined || value === '') return '-'
  const num = Number(value)
  if (Number.isNaN(num)) return '-'
  return num.toFixed(2)
}

const convertByRate = (value, rate) => {
  const num = toNumber(value)
  if (!rate || Number.isNaN(rate) || rate <= 0) {
    return num
  }
  return num / rate
}

const OzonOrderList = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [syncing, setSyncing] = useState(false)
  const [syncingProfit, setSyncingProfit] = useState(false)
  const [searchRange, setSearchRange] = useState([])
  const [searchStatus, setSearchStatus] = useState('')
  const [syncRange, setSyncRange] = useState([])
  const [syncModalOpen, setSyncModalOpen] = useState(false)
  const [selectedRowKeys, setSelectedRowKeys] = useState([])
  const [selectedRows, setSelectedRows] = useState([])
  const [exchangeRate, setExchangeRate] = useState(null)
  const purchaseDraftsRef = useRef({})

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
    loadExchangeRate()
  }, [])

  const loadExchangeRate = async () => {
    const today = new Date().toISOString().slice(0, 10)
    const cacheKey = 'ozonRubRate'
    try {
      const cached = localStorage.getItem(cacheKey)
      if (cached) {
        const parsed = JSON.parse(cached)
        if (parsed?.date === today && parsed?.rate > 0) {
          setExchangeRate(parsed.rate)
          return
        }
      }
    } catch (e) {
      // ignore cache errors
    }

    try {
      const response = await fetch('https://open.er-api.com/v6/latest/CNY')
      const data = await response.json()
      const rate = data?.rates?.RUB
      if (rate && rate > 0) {
        setExchangeRate(rate)
        localStorage.setItem(cacheKey, JSON.stringify({ date: today, rate }))
        return
      }
    } catch (e) {
      // ignore fetch errors
    }
    setExchangeRate(null)
  }

  const fetchData = async (params = {}) => {
    setLoading(true)
    try {
      const result = await ozonOrderApi.list(params)
      setData(result || [])
    } catch (error) {
      message.error('加载数据失败')
    } finally {
      setLoading(false)
    }
  }

  const handleSync = async (range = []) => {
    setSyncing(true)
    try {
      const start = range?.[0] ? range[0].toISOString() : undefined
      const end = range?.[1] ? range[1].toISOString() : undefined
      const res = await ozonOrderApi.sync({ start, end })
      const msg = res?.message || '同步任务已启动，请稍后刷新列表'
      message.success(msg)
      fetchData()
      setTimeout(() => {
        window.location.reload()
      }, 800)
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

  const handleSyncConfirm = async () => {
    await handleSync(syncRange)
    setSyncModalOpen(false)
  }

  const handleSyncProfit = async () => {
    if (!selectedRows.length) {
      message.warning('请先选择订单')
      return
    }
    const orderIds = selectedRows
      .map((row) => row?.orderId)
      .filter((value) => value !== null && value !== undefined && value !== '')
    if (!orderIds.length) {
      message.warning('选中的订单没有可用的订单ID')
      return
    }
    setSyncingProfit(true)
    try {
      const res = await ozonProfitApi.sync({ order_ids: orderIds.join(',') })
      const msg = res?.message || '财务更新已触发，请稍后刷新'
      message.success(msg)
    } catch (error) {
      const msg = error?.response?.data?.message
      message.error(msg || '更新财务失败')
    } finally {
      setSyncingProfit(false)
    }
  }

  const handleSearch = () => {
    const created_from = searchRange?.[0] ? searchRange[0].toISOString() : undefined
    const created_to = searchRange?.[1] ? searchRange[1].toISOString() : undefined
    const status = searchStatus || undefined
    fetchData({ created_from, created_to, status })
  }

  const handlePurchaseAmountChange = (postingNumber, value) => {
    if (!postingNumber) return
    const normalized = value === null || value === undefined ? 0 : value
    purchaseDraftsRef.current[postingNumber] = normalized
    setData((prev) =>
      (prev || []).map((item) =>
        item.postingNumber === postingNumber ? { ...item, purchaseAmount: normalized } : item
      )
    )
  }

  const handlePurchaseAmountSave = async (record) => {
    const postingNumber = record?.postingNumber
    if (!postingNumber) return
    const draft = purchaseDraftsRef.current[postingNumber]
    const normalized = toNumber(draft !== undefined ? draft : record.purchaseAmount)
    try {
      await ozonOrderApi.updatePurchaseAmount(postingNumber, normalized)
      setData((prev) =>
        (prev || []).map((item) =>
          item.postingNumber === postingNumber ? { ...item, purchaseAmount: normalized } : item
        )
      )
    } catch (error) {
      message.error('保存采购金额失败')
    }
  }

  const columns = [
    {
      title: '货件号',
      dataIndex: 'postingNumber',
      key: 'postingNumber',
      width: 260,
      render: (text) => renderValue(text),
    },
    {
      title: '内容图片',
      dataIndex: 'imageUrl',
      key: 'imageUrl',
      width: 140,
      render: (_, record) => {
        const url =
          record.imageUrl ||
          record.productImage ||
          record.image ||
          (record.images && record.images[0]) ||
          null
        return url ? <Image src={url} width={60} height={60} style={{ objectFit: 'cover' }} /> : '-'
      },
    },
    {
      title: '价格',
      dataIndex: 'price',
      key: 'price',
      width: 120,
      render: (text) => renderValue(text),
    },
    {
      title: '\u91C7\u8D2D\u91D1\u989D',
      dataIndex: 'purchaseAmount',
      key: 'purchaseAmount',
      width: 140,
      render: (_, record) => (
        <InputNumber
          min={0}
          precision={2}
          value={toNumber(record.purchaseAmount)}
          onChange={(value) => handlePurchaseAmountChange(record.postingNumber, value)}
          onBlur={() => handlePurchaseAmountSave(record)}
          style={{ width: 120 }}
        />
      ),
    },
    {
      title: 'ozon税费',
      key: 'ozonFees',
      width: 520,
      render: (_, record) => {
        const parts = [
          ['税费', record.ozonTaxFee],
          ['销售额', formatMoney(convertByRate(record.ozonSalesAmount, exchangeRate))],
          ['物流费', formatMoney(convertByRate(record.ozonLogisticsFee, exchangeRate))],
          ['佣金', formatMoney(convertByRate(record.ozonCommission, exchangeRate))],
          ['服务费', formatMoney(convertByRate(record.ozonServiceFee, exchangeRate))],
          ['退款', formatMoney(convertByRate(record.ozonRefund, exchangeRate))],
        ]
        return (
          <div style={{ lineHeight: '20px' }}>
            {parts.map(([label, value], index) => (
              <div key={label} style={{ whiteSpace: 'nowrap' }}>
                {label}:{renderValue(value)}
              </div>
            ))}
          </div>
        )
      },
    },
    {
      title: '\u5229\u6DA6',
      key: 'profit',
      width: 120,
      render: (_, record) => {
        const profit =
          convertByRate(record.ozonSalesAmount, exchangeRate) +
          convertByRate(record.ozonLogisticsFee, exchangeRate) +
          convertByRate(record.ozonCommission, exchangeRate) +
          convertByRate(record.ozonServiceFee, exchangeRate) +
          convertByRate(record.ozonRefund, exchangeRate) -
          toNumber(record.purchaseAmount)
        return formatMoney(profit)
      },
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 140,
      render: (text, record) => {
        const statusLabel = statusDict[text]
        const subLabel = substatusDict[record.substatus]
        const showSub = subLabel && subLabel !== statusLabel
        return (
          <Space>
            {statusLabel ? <Tag color="blue">{statusLabel}</Tag> : '-'}
            {showSub ? <Tag color="gold">{subLabel}</Tag> : null}
          </Space>
        )
      },
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
          <Button type="primary" loading={syncing} onClick={() => setSyncModalOpen(true)}>
            同步订单
          </Button>
          <Button loading={syncingProfit} onClick={handleSyncProfit}>
            更新财务
          </Button>
        </Space>
      </div>
      <Modal
        title="选择同步时间"
        open={syncModalOpen}
        onOk={handleSyncConfirm}
        onCancel={() => setSyncModalOpen(false)}
        okButtonProps={{ loading: syncing }}
      >
        <DatePicker.RangePicker
          showTime
          value={syncRange}
          onChange={(value) => setSyncRange(value || [])}
          placeholder={['同步开始时间', '同步结束时间']}
          style={{ width: '100%' }}
        />
      </Modal>
      <div style={{ marginBottom: 16 }}>
        <Space wrap>
          <DatePicker.RangePicker
            showTime
            value={searchRange}
            onChange={(v) => setSearchRange(v || [])}
            placeholder={['订单创建开始时间', '订单创建结束时间']}
          />
          <Select
            allowClear
            placeholder="订单状态"
            value={searchStatus || undefined}
            onChange={(value) => setSearchStatus(value || '')}
            style={{ width: 200 }}
            options={Object.keys(statusDict).map((key) => ({
              value: key,
              label: statusDict[key] || key,
            }))}
          />
          <Button onClick={handleSearch}>搜索</Button>
        </Space>
      </div>
      <Table
        columns={columns}
        dataSource={data}
        rowKey="postingNumber"
        loading={loading}
        rowSelection={{
          selectedRowKeys,
          onChange: (keys, rows) => {
            setSelectedRowKeys(keys)
            setSelectedRows(rows || [])
          },
        }}
        pagination={{ pageSize: 10 }}
        scroll={{ x: 1900 }}
      />
    </div>
  )
}

export default OzonOrderList
