import React, { useState } from 'react'
import { Layout as AntLayout, Menu, theme, Dropdown, Avatar, Space, Typography, message } from 'antd'
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
  LineChartOutlined,
  FundOutlined,
  LogoutOutlined,
  SettingOutlined,
} from '@ant-design/icons'
import { useAuth } from '../context/AuthContext'

const { Header, Sider, Content } = AntLayout
const { Text } = Typography

const Layout = ({ children }) => {
  const [collapsed, setCollapsed] = useState(false)
  const navigate = useNavigate()
  const location = useLocation()
  const { user, logout } = useAuth()
  const {
    token: { colorBgContainer },
  } = theme.useToken()

  const handleLogout = async () => {
    await logout()
    message.success('已退出登录')
    navigate('/login')
  }

  const userMenuItems = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: '个人信息',
    },
    {
      key: 'settings',
      icon: <SettingOutlined />,
      label: '设置',
    },
    {
      type: 'divider',
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
      danger: true,
    },
  ]

  const handleUserMenuClick = ({ key }) => {
    if (key === 'logout') {
      handleLogout()
    } else if (key === 'profile') {
      // TODO: 跳转到个人信息页
    } else if (key === 'settings') {
      // TODO: 跳转到设置页
    }
  }

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
      key: 'market',
      icon: <FundOutlined />,
      label: '市场信号',
      children: [
        { key: '/market/products', label: '商品监控' },
        { key: '/market/trending', label: '热门榜单' },
        { key: '/market/compare', label: '商品比较' },
      ],
    },
    {
      key: 'shop',
      icon: <ShopOutlined />,
      label: '店铺管理',
      children: [
        { key: '/shops', label: '店铺列表' },
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
    if (path.startsWith('/payments')) return ['/payments']
    if (path.startsWith('/invoices')) return ['/invoices']
    if (path.startsWith('/sales-data')) return ['/sales-data']
    if (path.startsWith('/market/products')) return ['/market/products']
    if (path.startsWith('/market/trending')) return ['/market/trending']
    if (path.startsWith('/market/compare')) return ['/market/compare']
    if (path.startsWith('/shops')) return ['/shops']
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
            padding: '0 24px 0 16px',
            background: colorBgContainer,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
          }}
        >
          <span
            style={{ fontSize: 18, cursor: 'pointer' }}
            onClick={() => setCollapsed(!collapsed)}
          >
            ☰
          </span>
          
          <Dropdown
            menu={{ items: userMenuItems, onClick: handleUserMenuClick }}
            placement="bottomRight"
          >
            <Space style={{ cursor: 'pointer' }}>
              <Avatar 
                style={{ backgroundColor: '#1890ff' }} 
                icon={<UserOutlined />} 
              />
              <Text>{user?.username || '用户'}</Text>
            </Space>
          </Dropdown>
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
