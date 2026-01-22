import React, { useState, useEffect } from 'react'
import { 
  Form, Input, Select, Switch, Button, Card, message, 
  Space, Typography, Divider 
} from 'antd'
import { ArrowLeftOutlined, SaveOutlined } from '@ant-design/icons'
import { useNavigate, useParams } from 'react-router-dom'
import { shopApi } from '../../api'

const { Title } = Typography
const { TextArea } = Input
const { Option } = Select

const ShopForm = () => {
  const navigate = useNavigate()
  const { id } = useParams()
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [submitting, setSubmitting] = useState(false)
  const isEdit = !!id

  useEffect(() => {
    if (isEdit) {
      fetchShop()
    }
  }, [id])

  const fetchShop = async () => {
    setLoading(true)
    try {
      const res = await shopApi.getById(id)
      form.setFieldsValue(res.data)
    } catch (error) {
      message.error('获取店铺信息失败')
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = async (values) => {
    setSubmitting(true)
    try {
      if (isEdit) {
        await shopApi.update(id, values)
        message.success('更新成功')
      } else {
        await shopApi.create(values)
        message.success('创建成功')
      }
      navigate('/shops')
    } catch (error) {
      message.error(isEdit ? '更新失败' : '创建失败')
    } finally {
      setSubmitting(false)
    }
  }

  const platforms = [
    { value: 'ozon', label: 'Ozon' },
    { value: 'shopee', label: 'Shopee' },
    { value: 'wildberries', label: 'Wildberries' },
    { value: 'amazon', label: 'Amazon' },
  ]

  const markets = [
    { value: 'RU', label: '俄罗斯 (RU)' },
    { value: 'CN', label: '中国 (CN)' },
    { value: 'US', label: '美国 (US)' },
    { value: 'EU', label: '欧洲 (EU)' },
    { value: 'KZ', label: '哈萨克斯坦 (KZ)' },
    { value: 'BY', label: '白俄罗斯 (BY)' },
  ]

  const timezones = [
    { value: 'Europe/Moscow', label: '莫斯科 (UTC+3)' },
    { value: 'Asia/Shanghai', label: '上海 (UTC+8)' },
    { value: 'America/New_York', label: '纽约 (UTC-5)' },
    { value: 'Europe/London', label: '伦敦 (UTC+0)' },
  ]

  const currencies = [
    { value: 'RUB', label: '卢布 (₽)' },
    { value: 'CNY', label: '人民币 (¥)' },
    { value: 'USD', label: '美元 ($)' },
    { value: 'EUR', label: '欧元 (€)' },
  ]

  return (
    <div>
      <Card loading={loading}>
        <div style={{ display: 'flex', alignItems: 'center', marginBottom: 24 }}>
          <Button 
            icon={<ArrowLeftOutlined />} 
            onClick={() => navigate('/shops')}
            style={{ marginRight: 16 }}
          >
            返回
          </Button>
          <Title level={4} style={{ margin: 0 }}>
            {isEdit ? '编辑店铺' : '添加店铺'}
          </Title>
        </div>

        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
          initialValues={{
            platform: 'ozon',
            market: 'RU',
            status: 'active',
            timezone: 'Europe/Moscow',
            currency: 'RUB',
            isDefault: false,
          }}
          style={{ maxWidth: 600 }}
        >
          <Divider orientation="left">基本信息</Divider>
          
          <Form.Item
            name="shopCode"
            label="店铺编码"
            rules={[
              { required: true, message: '请输入店铺编码' },
              { pattern: /^[A-Z0-9_]+$/, message: '只允许大写字母、数字和下划线' },
            ]}
            tooltip="唯一标识，如 OZON_MAIN"
          >
            <Input placeholder="如: OZON_MAIN" disabled={isEdit} />
          </Form.Item>

          <Form.Item
            name="shopName"
            label="店铺名称"
            rules={[{ required: true, message: '请输入店铺名称' }]}
          >
            <Input placeholder="如: 我的主店铺" />
          </Form.Item>

          <Form.Item
            name="platform"
            label="平台"
            rules={[{ required: true, message: '请选择平台' }]}
          >
            <Select placeholder="选择平台">
              {platforms.map(p => (
                <Option key={p.value} value={p.value}>{p.label}</Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="market"
            label="市场"
            rules={[{ required: true, message: '请选择市场' }]}
          >
            <Select placeholder="选择市场">
              {markets.map(m => (
                <Option key={m.value} value={m.value}>{m.label}</Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="sellerId"
            label="平台卖家ID"
            tooltip="平台分配的卖家唯一标识"
          >
            <Input placeholder="如: 3207535" />
          </Form.Item>

          <Divider orientation="left">区域设置</Divider>

          <Form.Item
            name="timezone"
            label="时区"
          >
            <Select placeholder="选择时区">
              {timezones.map(tz => (
                <Option key={tz.value} value={tz.value}>{tz.label}</Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="currency"
            label="主要货币"
          >
            <Select placeholder="选择货币">
              {currencies.map(c => (
                <Option key={c.value} value={c.value}>{c.label}</Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="shopUrl"
            label="店铺链接"
          >
            <Input placeholder="https://..." />
          </Form.Item>

          <Divider orientation="left">状态设置</Divider>

          <Form.Item
            name="status"
            label="状态"
          >
            <Select>
              <Option value="active">正常</Option>
              <Option value="suspended">暂停</Option>
              <Option value="closed">关闭</Option>
            </Select>
          </Form.Item>

          <Form.Item
            name="isDefault"
            label="设为默认店铺"
            valuePropName="checked"
          >
            <Switch />
          </Form.Item>

          <Form.Item
            name="description"
            label="备注"
          >
            <TextArea rows={3} placeholder="店铺备注信息..." />
          </Form.Item>

          <Form.Item>
            <Space>
              <Button 
                type="primary" 
                htmlType="submit" 
                loading={submitting}
                icon={<SaveOutlined />}
              >
                {isEdit ? '保存修改' : '创建店铺'}
              </Button>
              <Button onClick={() => navigate('/shops')}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Card>
    </div>
  )
}

export default ShopForm
