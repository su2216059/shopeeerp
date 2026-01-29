import React from 'react'
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import PrivateRoute from './components/PrivateRoute'
import PermissionRoute from './components/PermissionRoute'
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
          <Route path="/customers" element={
            <PermissionRoute permission="CUSTOMER_VIEW">
              <CustomerList />
            </PermissionRoute>
          } />
          <Route path="/customers/new" element={
            <PermissionRoute permission="CUSTOMER_CREATE">
              <CustomerForm />
            </PermissionRoute>
          } />
          <Route path="/customers/edit/:id" element={
            <PermissionRoute permission="CUSTOMER_UPDATE">
              <CustomerForm />
            </PermissionRoute>
          } />
          
          {/* 客户支持 */}
          <Route path="/customer-support" element={
            <PermissionRoute permission="CUSTOMER_SUPPORT_VIEW">
              <CustomerSupportList />
            </PermissionRoute>
          } />
          <Route path="/customer-support/new" element={
            <PermissionRoute permission="CUSTOMER_SUPPORT_CREATE">
              <CustomerSupportForm />
            </PermissionRoute>
          } />
          <Route path="/customer-support/edit/:id" element={
            <PermissionRoute permission="CUSTOMER_SUPPORT_UPDATE">
              <CustomerSupportForm />
            </PermissionRoute>
          } />
          
          {/* 产品管理 */}
          <Route path="/products" element={
            <PermissionRoute permission="OZON_PRODUCT_VIEW">
              <ProductList />
            </PermissionRoute>
          } />
          <Route path="/products/new" element={
            <PermissionRoute permission="OZON_PRODUCT_VIEW">
              <ProductForm />
            </PermissionRoute>
          } />
          <Route path="/products/edit/:id" element={
            <PermissionRoute permission="OZON_PRODUCT_VIEW">
              <ProductForm />
            </PermissionRoute>
          } />
          
          {/* 订单管理 */}
          <Route path="/orders" element={
            <PermissionRoute permission="ORDER_VIEW">
              <OrderList />
            </PermissionRoute>
          } />
          <Route path="/orders/new" element={
            <PermissionRoute permission="ORDER_CREATE">
              <OrderForm />
            </PermissionRoute>
          } />
          <Route path="/orders/edit/:id" element={
            <PermissionRoute permission="ORDER_UPDATE">
              <OrderForm />
            </PermissionRoute>
          } />
          
          {/* 订单项管理 */}
          <Route path="/order-items" element={<OrderItemList />} />
          <Route path="/order-items/new" element={<OrderItemForm />} />
          <Route path="/order-items/edit/:id" element={<OrderItemForm />} />
          
          {/* Ozon 订单 */}
          <Route path="/ozon/orders" element={
            <PermissionRoute permission="OZON_ORDER_VIEW">
              <OzonOrderList />
            </PermissionRoute>
          } />
          
          {/* 发票管理 */}
          <Route path="/invoices" element={
            <PermissionRoute permission="INVOICE_VIEW">
              <InvoiceList />
            </PermissionRoute>
          } />
          <Route path="/invoices/new" element={
            <PermissionRoute permission="INVOICE_CREATE">
              <InvoiceForm />
            </PermissionRoute>
          } />
          <Route path="/invoices/edit/:id" element={
            <PermissionRoute permission="INVOICE_UPDATE">
              <InvoiceForm />
            </PermissionRoute>
          } />
          
          {/* 库存管理 */}
          <Route path="/inventory" element={<InventoryList />} />
          <Route path="/inventory/new" element={<InventoryForm />} />
          <Route path="/inventory/edit/:id" element={<InventoryForm />} />
          
          {/* 仓库管理 */}
          <Route path="/warehouses" element={
            <PermissionRoute permission="OZON_WAREHOUSE_VIEW">
              <WarehouseList />
            </PermissionRoute>
          } />
          <Route path="/warehouses/new" element={
            <PermissionRoute permission="OZON_WAREHOUSE_VIEW">
              <WarehouseForm />
            </PermissionRoute>
          } />
          <Route path="/warehouses/edit/:id" element={
            <PermissionRoute permission="OZON_WAREHOUSE_VIEW">
              <WarehouseForm />
            </PermissionRoute>
          } />
          
          {/* 销售数据 */}
          <Route path="/sales-data" element={<SalesDataList />} />
          <Route path="/sales-data/new" element={<SalesDataForm />} />
          <Route path="/sales-data/edit/:id" element={<SalesDataForm />} />
          
          {/* 用户管理 */}
          <Route path="/users" element={
            <PermissionRoute permission="USER_MANAGE">
              <UserList />
            </PermissionRoute>
          } />
          <Route path="/users/new" element={
            <PermissionRoute permission="USER_MANAGE">
              <UserForm />
            </PermissionRoute>
          } />
          <Route path="/users/edit/:id" element={
            <PermissionRoute permission="USER_MANAGE">
              <UserForm />
            </PermissionRoute>
          } />
          
          {/* 角色管理 */}
          <Route path="/roles" element={
            <PermissionRoute permission="ROLE_MANAGE">
              <RoleList />
            </PermissionRoute>
          } />
          <Route path="/roles/new" element={
            <PermissionRoute permission="ROLE_MANAGE">
              <RoleForm />
            </PermissionRoute>
          } />
          <Route path="/roles/edit/:id" element={
            <PermissionRoute permission="ROLE_MANAGE">
              <RoleForm />
            </PermissionRoute>
          } />
          
          {/* 市场信号 */}
          <Route path="/market/products" element={
            <PermissionRoute permission="MARKET_VIEW">
              <MarketProductList />
            </PermissionRoute>
          } />
          <Route path="/market/products/:platform/:productId" element={
            <PermissionRoute permission="MARKET_VIEW">
              <MarketProductDetail />
            </PermissionRoute>
          } />
          <Route path="/market/trending" element={
            <PermissionRoute permission="MARKET_VIEW">
              <MarketTrending />
            </PermissionRoute>
          } />
          <Route path="/market/compare" element={
            <PermissionRoute permission="MARKET_VIEW">
              <MarketCompare />
            </PermissionRoute>
          } />
          
          {/* 店铺管理 */}
          <Route path="/shops" element={
            <PermissionRoute permission="SHOP_VIEW">
              <ShopList />
            </PermissionRoute>
          } />
          <Route path="/shops/new" element={
            <PermissionRoute permission="SHOP_CREATE">
              <ShopForm />
            </PermissionRoute>
          } />
          <Route path="/shops/:id/edit" element={
            <PermissionRoute permission="SHOP_UPDATE">
              <ShopForm />
            </PermissionRoute>
          } />
          <Route path="/shops/:shopId/credential" element={
            <PermissionRoute permission="SHOP_CREDENTIAL">
              <ShopCredential />
            </PermissionRoute>
          } />
          <Route path="/shops/:shopId/accounts" element={
            <PermissionRoute permission="SHOP_ACCOUNT">
              <ShopAccounts />
            </PermissionRoute>
          } />
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
