import React, { useState, useEffect } from 'react'
import { Form, Input, Button, message, Space } from 'antd'
import { useNavigate, useParams } from 'react-router-dom'
import { warehouseApi } from '../../api'

const WarehouseForm = () => {
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
      const result = await warehouseApi.getById(id)
      form.setFieldsValue(result)
    } catch (error) {
      message.error('加载数据失败')
    }
  }

  const handleSubmit = async (values) => {
    setLoading(true)
    try {
      if (isEdit) {
        await warehouseApi.update(id, values)
        message.success('更新成功')
      } else {
        await warehouseApi.create(values)
        message.success('创建成功')
      }
      navigate('/warehouses')
    } catch (error) {
      message.error(isEdit ? '更新失败' : '创建失败')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      <h1>{isEdit ? '编辑仓库' : '新增仓库'}</h1>
      <Form
        form={form}
        layout="vertical"
        onFinish={handleSubmit}
        style={{ maxWidth: 600, marginTop: 24 }}
      >
        <Form.Item
          name="name"
          label="仓库名称"
          rules={[{ required: true, message: '请输入仓库名称' }]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          name="location"
          label="位置"
          rules={[{ required: true, message: '请输入位置' }]}
        >
          <Input />
        </Form.Item>
        <Form.Item>
          <Space>
            <Button type="primary" htmlType="submit" loading={loading}>
              提交
            </Button>
            <Button onClick={() => navigate('/warehouses')}>取消</Button>
          </Space>
        </Form.Item>
      </Form>
    </div>
  )
}

export default WarehouseForm
