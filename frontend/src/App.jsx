import React from 'react'
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import PrivateRoute from './components/PrivateRoute'
import Layout from './components/Layout'
import Login from './pages/auth/Login'
import Register from './pages/auth/Register'
import Dashboard from './pages/Dashboard'
import CustomerList from './pages/customer/CustomerList'
import CustomerForm from './pages/customer/CustomerForm'
import CustomerSupportList from './pages/customerSupport/CustomerSupportList'
import CustomerSupportForm from './pages/customerSupport/CustomerSupportForm'
import ProductList from './pages/product/ProductList'
import ProductForm from './pages/product/ProductForm'
import OrderList from './pages/order/OrderList'
import OrderForm from './pages/order/OrderForm'
import OrderItemList from './pages/orderItem/OrderItemList'
import OrderItemForm from './pages/orderItem/OrderItemForm'
import PaymentList from './pages/payment/PaymentList'
import PaymentForm from './pages/payment/PaymentForm'
import InvoiceList from './pages/invoice/InvoiceList'
import InvoiceForm from './pages/invoice/InvoiceForm'
import InventoryList from './pages/inventory/InventoryList'
import InventoryForm from './pages/inventory/InventoryForm'
import OzonOrderList from './pages/ozonOrder/OzonOrderList'
import WarehouseList from './pages/warehouse/WarehouseList'
import WarehouseForm from './pages/warehouse/WarehouseForm'
import SalesDataList from './pages/salesData/SalesDataList'
import SalesDataForm from './pages/salesData/SalesDataForm'
import UserList from './pages/user/UserList'
import UserForm from './pages/user/UserForm'
import RoleList from './pages/role/RoleList'
import RoleForm from './pages/role/RoleForm'
import MarketProductList from './pages/marketSignal/MarketProductList'
import MarketProductDetail from './pages/marketSignal/MarketProductDetail'
import MarketTrending from './pages/marketSignal/MarketTrending'
import MarketCompare from './pages/marketSignal/MarketCompare'
import ShopList from './pages/shop/ShopList'
import ShopForm from './pages/shop/ShopForm'
import ShopCredential from './pages/shop/ShopCredential'
import ShopAccounts from './pages/shop/ShopAccounts'

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          {/* 公开路由 */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          
          {/* 受保护的路由 */}
          <Route path="/*" element={
            <PrivateRoute>
              <Layout>
                <Routes>
                  <Route path="/" element={<Navigate to="/dashboard" replace />} />
                  <Route path="/dashboard" element={<Dashboard />} />
          
          {/* 客户管理 */}
          <Route path="/customers" element={<CustomerList />} />
          <Route path="/customers/new" element={<CustomerForm />} />
          <Route path="/customers/edit/:id" element={<CustomerForm />} />
          
          {/* 客户支持 */}
          <Route path="/customer-support" element={<CustomerSupportList />} />
          <Route path="/customer-support/new" element={<CustomerSupportForm />} />
          <Route path="/customer-support/edit/:id" element={<CustomerSupportForm />} />
          
          {/* 产品管理 */}
          <Route path="/products" element={<ProductList />} />
          <Route path="/products/new" element={<ProductForm />} />
          <Route path="/products/edit/:id" element={<ProductForm />} />
          
          {/* 订单管理 */}
          <Route path="/orders" element={<OrderList />} />
          <Route path="/orders/new" element={<OrderForm />} />
          <Route path="/orders/edit/:id" element={<OrderForm />} />
          
          {/* 订单项管理 */}
          <Route path="/order-items" element={<OrderItemList />} />
          <Route path="/order-items/new" element={<OrderItemForm />} />
          <Route path="/order-items/edit/:id" element={<OrderItemForm />} />
          
          {/* 支付管理 */}
          <Route path="/payments" element={<PaymentList />} />
          <Route path="/payments/new" element={<PaymentForm />} />
          <Route path="/payments/edit/:id" element={<PaymentForm />} />

          {/* Ozon 订单 */}
          <Route path="/ozon/orders" element={<OzonOrderList />} />
          
          {/* 发票管理 */}
          <Route path="/invoices" element={<InvoiceList />} />
          <Route path="/invoices/new" element={<InvoiceForm />} />
          <Route path="/invoices/edit/:id" element={<InvoiceForm />} />
          
          {/* 库存管理 */}
          <Route path="/inventory" element={<InventoryList />} />
          <Route path="/inventory/new" element={<InventoryForm />} />
          <Route path="/inventory/edit/:id" element={<InventoryForm />} />
          
          {/* 仓库管理 */}
          <Route path="/warehouses" element={<WarehouseList />} />
          <Route path="/warehouses/new" element={<WarehouseForm />} />
          <Route path="/warehouses/edit/:id" element={<WarehouseForm />} />
          
          {/* 销售数据 */}
          <Route path="/sales-data" element={<SalesDataList />} />
          <Route path="/sales-data/new" element={<SalesDataForm />} />
          <Route path="/sales-data/edit/:id" element={<SalesDataForm />} />
          
          {/* 用户管理 */}
          <Route path="/users" element={<UserList />} />
          <Route path="/users/new" element={<UserForm />} />
          <Route path="/users/edit/:id" element={<UserForm />} />
          
          {/* 角色管理 */}
          <Route path="/roles" element={<RoleList />} />
          <Route path="/roles/new" element={<RoleForm />} />
          <Route path="/roles/edit/:id" element={<RoleForm />} />
          
          {/* 市场信号 */}
          <Route path="/market/products" element={<MarketProductList />} />
          <Route path="/market/products/:platform/:productId" element={<MarketProductDetail />} />
          <Route path="/market/trending" element={<MarketTrending />} />
          <Route path="/market/compare" element={<MarketCompare />} />
          
          {/* 店铺管理 */}
          <Route path="/shops" element={<ShopList />} />
          <Route path="/shops/new" element={<ShopForm />} />
          <Route path="/shops/:id/edit" element={<ShopForm />} />
          <Route path="/shops/:shopId/credential" element={<ShopCredential />} />
          <Route path="/shops/:shopId/accounts" element={<ShopAccounts />} />
                </Routes>
              </Layout>
            </PrivateRoute>
          } />
        </Routes>
      </Router>
    </AuthProvider>
  )
}

export default App
