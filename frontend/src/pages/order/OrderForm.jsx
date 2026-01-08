import React, { useState, useEffect } from 'react'
import { Form, InputNumber, Button, message, Space, Select } from 'antd'
import { useNavigate, useParams } from 'react-router-dom'
import { orderApi } from '../../api'

const OrderForm = () => {
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
      const result = await orderApi.getById(id)
      form.setFieldsValue(result)
    } catch (error) {
      message.error('加载数据失败')
    }
  }

  const handleSubmit = async (values) => {
    setLoading(true)
    try {
      if (isEdit) {
        await orderApi.update(id, values)
        message.success('更新成功')
      } else {
        await orderApi.create(values)
        message.success('创建成功')
      }
      navigate('/orders')
    } catch (error) {
      message.error(isEdit ? '更新失败' : '创建失败')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      <h1>{isEdit ? '编辑订单' : '新增订单'}</h1>
      <Form
        form={form}
        layout="vertical"
        onFinish={handleSubmit}
        style={{ maxWidth: 600, marginTop: 24 }}
      >
        <Form.Item
          name="customerId"
          label="客户ID"
          rules={[{ required: true, message: '请输入客户ID' }]}
        >
          <InputNumber min={1} style={{ width: '100%' }} />
        </Form.Item>
        <Form.Item
          name="orderStatus"
          label="订单状态"
          rules={[{ required: true, message: '请选择订单状态' }]}
        >
          <Select>
            <Select.Option value="待处理">待处理</Select.Option>
            <Select.Option value="处理中">处理中</Select.Option>
            <Select.Option value="已完成">已完成</Select.Option>
            <Select.Option value="已取消">已取消</Select.Option>
          </Select>
        </Form.Item>
        <Form.Item
          name="totalAmount"
          label="总金额"
          rules={[{ required: true, message: '请输入总金额' }]}
        >
          <InputNumber min={0} precision={2} style={{ width: '100%' }} />
        </Form.Item>
        <Form.Item
          name="paymentStatus"
          label="支付状态"
          rules={[{ required: true, message: '请选择支付状态' }]}
        >
          <Select>
            <Select.Option value="未支付">未支付</Select.Option>
            <Select.Option value="已支付">已支付</Select.Option>
            <Select.Option value="部分支付">部分支付</Select.Option>
          </Select>
        </Form.Item>
        <Form.Item name="shippingStatus" label="配送状态">
          <Select>
            <Select.Option value="待发货">待发货</Select.Option>
            <Select.Option value="已发货">已发货</Select.Option>
            <Select.Option value="已送达">已送达</Select.Option>
          </Select>
        </Form.Item>
        <Form.Item>
          <Space>
            <Button type="primary" htmlType="submit" loading={loading}>
              提交
            </Button>
            <Button onClick={() => navigate('/orders')}>取消</Button>
          </Space>
        </Form.Item>
      </Form>
    </div>
  )
}

export default OrderForm
