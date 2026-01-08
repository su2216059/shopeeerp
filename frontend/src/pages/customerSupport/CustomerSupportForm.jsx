import React, { useState, useEffect } from 'react'
import { Form, Input, InputNumber, Button, message, Space, Select } from 'antd'
import { useNavigate, useParams } from 'react-router-dom'
import { customerSupportApi } from '../../api'

const CustomerSupportForm = () => {
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
      const result = await customerSupportApi.getById(id)
      form.setFieldsValue(result)
    } catch (error) {
      message.error('加载数据失败')
    }
  }

  const handleSubmit = async (values) => {
    setLoading(true)
    try {
      if (isEdit) {
        await customerSupportApi.update(id, values)
        message.success('更新成功')
      } else {
        await customerSupportApi.create(values)
        message.success('创建成功')
      }
      navigate('/customer-support')
    } catch (error) {
      message.error(isEdit ? '更新失败' : '创建失败')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      <h1>{isEdit ? '编辑客户支持' : '新增客户支持'}</h1>
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
          name="issueDescription"
          label="问题描述"
          rules={[{ required: true, message: '请输入问题描述' }]}
        >
          <Input.TextArea rows={4} />
        </Form.Item>
        <Form.Item
          name="status"
          label="状态"
          rules={[{ required: true, message: '请选择状态' }]}
        >
          <Select>
            <Select.Option value="待处理">待处理</Select.Option>
            <Select.Option value="处理中">处理中</Select.Option>
            <Select.Option value="已解决">已解决</Select.Option>
            <Select.Option value="已关闭">已关闭</Select.Option>
          </Select>
        </Form.Item>
        <Form.Item>
          <Space>
            <Button type="primary" htmlType="submit" loading={loading}>
              提交
            </Button>
            <Button onClick={() => navigate('/customer-support')}>取消</Button>
          </Space>
        </Form.Item>
      </Form>
    </div>
  )
}

export default CustomerSupportForm
