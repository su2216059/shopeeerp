import React, { useState, useEffect } from 'react'
import { Form, InputNumber, Button, message, Space, Input } from 'antd'
import { useNavigate, useParams } from 'react-router-dom'
import { invoiceApi } from '../../api'

const InvoiceForm = () => {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()
  const { id } = useParams()
  const isEdit = !!id

  useEffect(() => {
    if (isEdit) {
      fetchData()
    }
  }, [id])

  const fetchData = async () => {
    try {
      const result = await invoiceApi.getById(id)
      form.setFieldsValue(result)
    } catch (error) {
      message.error('加载数据失败')
    }
  }

  const handleSubmit = async (values) => {
    setLoading(true)
    try {
      if (isEdit) {
        await invoiceApi.update(id, values)
        message.success('更新成功')
      } else {
        await invoiceApi.create(values)
        message.success('创建成功')
      }
      navigate('/invoices')
    } catch (error) {
      message.error(isEdit ? '更新失败' : '创建失败')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      <h1>{isEdit ? '编辑发票' : '新增发票'}</h1>
      <Form
        form={form}
        layout="vertical"
        onFinish={handleSubmit}
        style={{ maxWidth: 600, marginTop: 24 }}
      >
        <Form.Item
          name="orderId"
          label="订单ID"
          rules={[{ required: true, message: '请输入订单ID' }]}
        >
          <InputNumber min={1} style={{ width: '100%' }} />
        </Form.Item>
        <Form.Item
          name="invoiceNumber"
          label="发票号"
          rules={[{ required: true, message: '请输入发票号' }]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          name="totalAmount"
          label="总金额"
          rules={[{ required: true, message: '请输入总金额' }]}
        >
          <InputNumber min={0} precision={2} style={{ width: '100%' }} />
        </Form.Item>
        <Form.Item>
          <Space>
            <Button type="primary" htmlType="submit" loading={loading}>
              提交
            </Button>
            <Button onClick={() => navigate('/invoices')}>取消</Button>
          </Space>
        </Form.Item>
      </Form>
    </div>
  )
}

export default InvoiceForm
