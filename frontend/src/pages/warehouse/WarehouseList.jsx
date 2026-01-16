import React, { useState, useEffect } from 'react'
import { Table, Button, message } from 'antd'
import { warehouseApi } from '../../api'

const WarehouseList = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [syncing, setSyncing] = useState(false)

  useEffect(() => {
    fetchData()
  }, [])

  const fetchData = async () => {
    setLoading(true)
    try {
      const result = await warehouseApi.list()
      setData(result || [])
    } catch (error) {
      message.error('\u52a0\u8f7d\u6570\u636e\u5931\u8d25')
    } finally {
      setLoading(false)
    }
  }

  const handleSync = async () => {
    setSyncing(true)
    try {
      await warehouseApi.sync()
      message.success('\u540c\u6b65\u4efb\u52a1\u5df2\u542f\u52a8')
      fetchData()
    } catch (error) {
      message.error('\u540c\u6b65\u5931\u8d25')
    } finally {
      setSyncing(false)
    }
  }

  const columns = [
    {
      title: 'ID',
      dataIndex: 'warehouseId',
      key: 'warehouseId',
      width: 80,
    },
    {
      title: '\u540d\u79f0',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '\u5e97\u94fa',
      dataIndex: 'storeName',
      key: 'storeName',
    },
    {
      title: '\u5de5\u4f5c\u65f6\u95f4',
      dataIndex: 'workingDays',
      key: 'workingDays',
      width: 140,
    },
    {
      title: '\u72b6\u6001',
      dataIndex: 'status',
      key: 'status',
      width: 120,
    },
    {
      title: '\u6700\u5c0f\u5de5\u4f5c\u65e5',
      dataIndex: 'minWorkingDays',
      key: 'minWorkingDays',
      width: 120,
    },
    {
      title: '\u6d3e\u9001\u9650\u5236',
      dataIndex: 'postingsLimit',
      key: 'postingsLimit',
      width: 120,
    },
    {
      title: '\u6700\u5c0f\u6d3e\u9001\u9650\u5236',
      dataIndex: 'minPostingsLimit',
      key: 'minPostingsLimit',
      width: 140,
    },
    {
      title: '\u662f\u5426rfbs',
      dataIndex: 'isRfbs',
      key: 'isRfbs',
      width: 100,
      render: (value) => (value ? '\u662f' : '\u5426'),
    },
    {
      title: '\u521b\u5efa\u65f6\u95f4',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 180,
    },
    {
      title: '\u914d\u9001\u65b9\u5f0f',
      dataIndex: 'deliveryMethods',
      key: 'deliveryMethods',
      render: (methods) => {
        if (!methods || methods.length === 0) {
          return '-'
        }
        return (
          <div>
            {methods.map((item) => (
              <div key={item.id}>
                {item.name || '-'} {item.status ? `(${item.status})` : ''}
              </div>
            ))}
          </div>
        )
      },
    },
  ]

  return (
    <div>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
        <h1>{'\u4ed3\u5e93\u7ba1\u7406'}</h1>
        <Button type="primary" onClick={handleSync} loading={syncing}>
          {'\u540c\u6b65\u4ed3\u5e93'}
        </Button>
      </div>
      <Table
        columns={columns}
        dataSource={data}
        rowKey="warehouseId"
        loading={loading}
        pagination={{ pageSize: 10 }}
      />
    </div>
  )
}

export default WarehouseList
