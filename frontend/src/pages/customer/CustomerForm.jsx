import React, { useState, useEffect } from 'react'
import { Form, Input, Button, message, Space } from 'antd'
import { useNavigate, useParams } from 'react-router-dom'
import { customerApi } from '../../api'

const CustomerForm = () => {
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
      const result = await customerApi.getById(id)
      form.setFieldsValue(result)
    } catch (error) {
      message.error('加载数据失败')
    }
  }

  const handleSubmit = async (values) => {
    setLoading(true)
    try {
      if (isEdit) {
        await customerApi.update(id, values)
        message.success('更新成功')
      } else {
        await customerApi.create(values)
        message.success('创建成功')
      }
      navigate('/customers')
    } catch (error) {
      message.error(isEdit ? '更新失败' : '创建失败')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      <h1>{isEdit ? '编辑客户' : '新增客户'}</h1>
      <Form
        form={form}
        layout="vertical"
        onFinish={handleSubmit}
        style={{ maxWidth: 600, marginTop: 24 }}
      >
        <Form.Item
          name="firstName"
          label="名"
          rules={[{ required: true, message: '请输入名' }]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          name="lastName"
          label="姓"
          rules={[{ required: true, message: '请输入姓' }]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          name="email"
          label="邮箱"
          rules={[
            { required: true, message: '请输入邮箱' },
            { type: 'email', message: '请输入有效的邮箱地址' },
          ]}
        >
          <Input />
        </Form.Item>
        <Form.Item name="phone" label="电话">
          <Input />
        </Form.Item>
        <Form.Item name="address" label="地址">
          <Input.TextArea rows={3} />
        </Form.Item>
        <Form.Item>
          <Space>
            <Button type="primary" htmlType="submit" loading={loading}>
              提交
            </Button>
            <Button onClick={() => navigate('/customers')}>取消</Button>
          </Space>
        </Form.Item>
      </Form>
    </div>
  )
}

export default CustomerForm
