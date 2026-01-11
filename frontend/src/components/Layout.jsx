import React, { useState } from 'react'
import { Layout as AntLayout, Menu, theme } from 'antd'
import { useNavigate, useLocation } from 'react-router-dom'
import {
  DashboardOutlined,
  UserOutlined,
  CustomerServiceOutlined,
  ShoppingOutlined,
  ShoppingCartOutlined,
  FileTextOutlined,
  DollarOutlined,
  InboxOutlined,
  DatabaseOutlined,
  ShopOutlined,
  BarChartOutlined,
  TeamOutlined,
  SafetyOutlined,
} from '@ant-design/icons'

const { Header, Sider, Content } = AntLayout

const Layout = ({ children }) => {
  const [collapsed, setCollapsed] = useState(false)
  const navigate = useNavigate()
  const location = useLocation()
  const {
    token: { colorBgContainer },
  } = theme.useToken()

  const menuItems = [
    {
      key: '/dashboard',
      icon: <DashboardOutlined />,
      label: '仪表盘',
    },
    {
      key: 'customer',
      icon: <UserOutlined />,
      label: '客户管理',
      children: [
        { key: '/customers', label: '客户列表' },
        { key: '/customer-support', label: '客户支持' },
      ],
    },
    {
      key: 'product',
      icon: <ShoppingOutlined />,
      label: '产品管理',
      children: [
        { key: '/products', label: '产品列表' },
        { key: '/inventory', label: '库存管理' },
        { key: '/warehouses', label: '仓库管理' },
      ],
    },
    {
      key: 'order',
      icon: <ShoppingCartOutlined />,
      label: '订单管理',
      children: [
        { key: '/orders', label: '订单列表' },
        { key: '/order-items', label: '订单项' },
        { key: '/ozon/orders', label: 'Ozon订单' },
        { key: '/payments', label: '支付记录' },
        { key: '/invoices', label: '发票管理' },
      ],
    },
    {
      key: 'sales',
      icon: <BarChartOutlined />,
      label: '销售数据',
      children: [
        { key: '/sales-data', label: '销售记录' },
      ],
    },
    {
      key: 'system',
      icon: <SafetyOutlined />,
      label: '系统管理',
      children: [
        { key: '/users', label: '用户管理' },
        { key: '/roles', label: '角色管理' },
      ],
    },
  ]

  const handleMenuClick = ({ key }) => {
    navigate(key)
  }

  const getSelectedKeys = () => {
    const path = location.pathname
    if (path.startsWith('/customers')) return ['/customers']
    if (path.startsWith('/customer-support')) return ['/customer-support']
    if (path.startsWith('/products')) return ['/products']
    if (path.startsWith('/inventory')) return ['/inventory']
    if (path.startsWith('/warehouses')) return ['/warehouses']
    if (path.startsWith('/ozon/orders')) return ['/ozon/orders']
    if (path.startsWith('/orders')) return ['/orders']
    if (path.startsWith('/order-items')) return ['/order-items']
    if (path.startsWith('/payments')) return ['/payments']
    if (path.startsWith('/invoices')) return ['/invoices']
    if (path.startsWith('/sales-data')) return ['/sales-data']
    if (path.startsWith('/users')) return ['/users']
    if (path.startsWith('/roles')) return ['/roles']
    return [path]
  }

  return (
    <AntLayout style={{ minHeight: '100vh' }}>
      <Sider trigger={null} collapsible collapsed={collapsed}>
        <div style={{ 
          height: 32, 
          margin: 16, 
          background: 'rgba(255, 255, 255, 0.3)',
          borderRadius: 4,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          color: 'white',
          fontWeight: 'bold'
        }}>
          {collapsed ? 'ERP' : 'Shopee ERP'}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={getSelectedKeys()}
          items={menuItems}
          onClick={handleMenuClick}
        />
      </Sider>
      <AntLayout>
        <Header
          style={{
            padding: 0,
            background: colorBgContainer,
            display: 'flex',
            alignItems: 'center',
            paddingLeft: 16,
          }}
        >
          <span
            style={{ fontSize: 18, cursor: 'pointer' }}
            onClick={() => setCollapsed(!collapsed)}
          >
            {collapsed ? '☰' : '☰'}
          </span>
        </Header>
        <Content
          style={{
            margin: '24px 16px',
            padding: 24,
            minHeight: 280,
            background: colorBgContainer,
          }}
        >
          {children}
        </Content>
      </AntLayout>
    </AntLayout>
  )
}

export default Layout
