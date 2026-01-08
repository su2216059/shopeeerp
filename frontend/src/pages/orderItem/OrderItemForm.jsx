import React, { useState, useEffect } from 'react'
import { Form, InputNumber, Button, message, Space } from 'antd'
import { useNavigate, useParams } from 'react-router-dom'
import { orderItemApi } from '../../api'

const OrderItemForm = () => {
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
      const result = await orderItemApi.getById(id)
      form.setFieldsValue(result)
    } catch (error) {
      message.error('加载数据失败')
    }
  }

  const handleSubmit = async (values) => {
    setLoading(true)
    try {
      if (isEdit) {
        await orderItemApi.update(id, values)
        message.success('更新成功')
      } else {
        await orderItemApi.create(values)
        message.success('创建成功')
      }
      navigate('/order-items')
    } catch (error) {
      message.error(isEdit ? '更新失败' : '创建失败')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      <h1>{isEdit ? '编辑订单项' : '新增订单项'}</h1>
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
          name="productId"
          label="产品ID"
          rules={[{ required: true, message: '请输入产品ID' }]}
        >
          <InputNumber min={1} style={{ width: '100%' }} />
        </Form.Item>
        <Form.Item
          name="quantity"
          label="数量"
          rules={[{ required: true, message: '请输入数量' }]}
        >
          <InputNumber min={1} style={{ width: '100%' }} />
        </Form.Item>
        <Form.Item
          name="price"
          label="单价"
          rules={[{ required: true, message: '请输入单价' }]}
        >
          <InputNumber min={0} precision={2} style={{ width: '100%' }} />
        </Form.Item>
        <Form.Item
          name="total"
          label="总价"
          rules={[{ required: true, message: '请输入总价' }]}
        >
          <InputNumber min={0} precision={2} style={{ width: '100%' }} />
        </Form.Item>
        <Form.Item>
          <Space>
            <Button type="primary" htmlType="submit" loading={loading}>
              提交
            </Button>
            <Button onClick={() => navigate('/order-items')}>取消</Button>
          </Space>
        </Form.Item>
      </Form>
    </div>
  )
}

export default OrderItemForm
