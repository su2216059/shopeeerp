import React, { useState, useEffect } from 'react'
import { Form, InputNumber, Button, message, Space, Select } from 'antd'
import { useNavigate, useParams } from 'react-router-dom'
import { paymentApi } from '../../api'

const PaymentForm = () => {
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
      const result = await paymentApi.getById(id)
      form.setFieldsValue(result)
    } catch (error) {
      message.error('加载数据失败')
    }
  }

  const handleSubmit = async (values) => {
    setLoading(true)
    try {
      if (isEdit) {
        await paymentApi.update(id, values)
        message.success('更新成功')
      } else {
        await paymentApi.create(values)
        message.success('创建成功')
      }
      navigate('/payments')
    } catch (error) {
      message.error(isEdit ? '更新失败' : '创建失败')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      <h1>{isEdit ? '编辑支付记录' : '新增支付记录'}</h1>
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
          name="paymentAmount"
          label="支付金额"
          rules={[{ required: true, message: '请输入支付金额' }]}
        >
          <InputNumber min={0} precision={2} style={{ width: '100%' }} />
        </Form.Item>
        <Form.Item name="paymentMethod" label="支付方式">
          <Select>
            <Select.Option value="支付宝">支付宝</Select.Option>
            <Select.Option value="微信支付">微信支付</Select.Option>
            <Select.Option value="银行卡">银行卡</Select.Option>
            <Select.Option value="现金">现金</Select.Option>
          </Select>
        </Form.Item>
        <Form.Item
          name="paymentStatus"
          label="支付状态"
          rules={[{ required: true, message: '请选择支付状态' }]}
        >
          <Select>
            <Select.Option value="未支付">未支付</Select.Option>
            <Select.Option value="已支付">已支付</Select.Option>
            <Select.Option value="支付失败">支付失败</Select.Option>
          </Select>
        </Form.Item>
        <Form.Item>
          <Space>
            <Button type="primary" htmlType="submit" loading={loading}>
              提交
            </Button>
            <Button onClick={() => navigate('/payments')}>取消</Button>
          </Space>
        </Form.Item>
      </Form>
    </div>
  )
}

export default PaymentForm
