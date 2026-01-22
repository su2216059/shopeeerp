import request from '../utils/request'

// 认证相关API
export const authApi = {
  login: (data) => request.post('/api/auth/login', data),
  register: (data) => request.post('/api/auth/register', data),
  logout: () => request.post('/api/auth/logout'),
  getCurrentUser: () => request.get('/api/auth/me'),
  changePassword: (data) => request.post('/api/auth/change-password', data),
}

// 客户相关API
export const customerApi = {
  list: () => request.get('/customers'),
  getById: (id) => request.get(`/customers/${id}`),
  create: (data) => request.post('/customers', data),
  update: (id, data) => request.put(`/customers/${id}`, data),
  delete: (id) => request.delete(`/customers/${id}`),
  getByEmail: (email) => request.get(`/customers/email/${email}`),
}

// 客户支持相关API
export const customerSupportApi = {
  list: () => request.get('/customer-support'),
  getById: (id) => request.get(`/customer-support/${id}`),
  create: (data) => request.post('/customer-support', data),
  update: (id, data) => request.put(`/customer-support/${id}`, data),
  delete: (id) => request.delete(`/customer-support/${id}`),
  getByCustomerId: (customerId) => request.get(`/customer-support/customer/${customerId}`),
}

// 产品相关API
export const productApi = {
  list: () => request.get('/products'),
  getById: (id) => request.get(`/products/${id}`),
  create: (data) => request.post('/products', data),
  update: (id, data) => request.put(`/products/${id}`, data),
  delete: (id) => request.delete(`/products/${id}`),
  getBySku: (sku) => request.get(`/products/sku/${sku}`),
  getByCategoryId: (categoryId) => request.get(`/products/category/${categoryId}`),
}

// Ozon 商品同步数据
export const ozonProductApi = {
  list: (params) => request.get('/ozon/products', { params }),
  sync: (params) => request.get('/ozon/products/sync', { params }),
}

// Ozon 订单同步数据
export const ozonOrderApi = {
  list: (params) => request.get('/ozon/orders', { params }),
  sync: (params) => request.get('/ozon/orders/sync', { params }),
  updatePurchaseAmount: (postingNumber, purchaseAmount) =>
    request.put(`/ozon/orders/${encodeURIComponent(postingNumber)}/purchase-amount`, { purchaseAmount }),
}

// Ozon 财务（利润）同步
export const ozonProfitApi = {
  sync: (params) => request.post('/ozon/profit/sync', null, { params }),
}

// 订单相关API
export const orderApi = {
  list: () => request.get('/orders'),
  getById: (id) => request.get(`/orders/${id}`),
  create: (data) => request.post('/orders', data),
  update: (id, data) => request.put(`/orders/${id}`, data),
  delete: (id) => request.delete(`/orders/${id}`),
  getByCustomerId: (customerId) => request.get(`/orders/customer/${customerId}`),
}

// 订单项相关API
export const orderItemApi = {
  list: () => request.get('/order-items'),
  getById: (id) => request.get(`/order-items/${id}`),
  create: (data) => request.post('/order-items', data),
  update: (id, data) => request.put(`/order-items/${id}`, data),
  delete: (id) => request.delete(`/order-items/${id}`),
  getByOrderId: (orderId) => request.get(`/order-items/order/${orderId}`),
  getByProductId: (productId) => request.get(`/order-items/product/${productId}`),
}

// 支付相关API
export const paymentApi = {
  list: () => request.get('/payments'),
  getById: (id) => request.get(`/payments/${id}`),
  create: (data) => request.post('/payments', data),
  update: (id, data) => request.put(`/payments/${id}`, data),
  delete: (id) => request.delete(`/payments/${id}`),
  getByOrderId: (orderId) => request.get(`/payments/order/${orderId}`),
}

// 发票相关API
export const invoiceApi = {
  list: () => request.get('/invoices'),
  getById: (id) => request.get(`/invoices/${id}`),
  create: (data) => request.post('/invoices', data),
  update: (id, data) => request.put(`/invoices/${id}`, data),
  delete: (id) => request.delete(`/invoices/${id}`),
  getByOrderId: (orderId) => request.get(`/invoices/order/${orderId}`),
}

// 库存相关API
export const inventoryApi = {
  list: () => request.get('/inventory'),
  getById: (id) => request.get(`/inventory/${id}`),
  create: (data) => request.post('/inventory', data),
  update: (id, data) => request.put(`/inventory/${id}`, data),
  delete: (id) => request.delete(`/inventory/${id}`),
  getByProductId: (productId) => request.get(`/inventory/product/${productId}`),
  getByWarehouseId: (warehouseId) => request.get(`/inventory/warehouse/${warehouseId}`),
}

// 仓库相关API
export const warehouseApi = {
  list: () => request.get('/ozon/warehouses'),
  sync: () => request.post('/ozon/warehouses/sync'),
  getById: (id) => request.get(`/warehouses/${id}`),
  create: (data) => request.post('/warehouses', data),
  update: (id, data) => request.put(`/warehouses/${id}`, data),
  delete: (id) => request.delete(`/warehouses/${id}`),
}

// 销售数据相关API
export const salesDataApi = {
  list: () => request.get('/sales-data'),
  getById: (id) => request.get(`/sales-data/${id}`),
  create: (data) => request.post('/sales-data', data),
  update: (id, data) => request.put(`/sales-data/${id}`, data),
  delete: (id) => request.delete(`/sales-data/${id}`),
  getByProductId: (productId) => request.get(`/sales-data/product/${productId}`),
  getByOrderId: (orderId) => request.get(`/sales-data/order/${orderId}`),
}

// 用户相关API
export const userApi = {
  list: () => request.get('/users'),
  getById: (id) => request.get(`/users/${id}`),
  create: (data) => request.post('/users', data),
  update: (id, data) => request.put(`/users/${id}`, data),
  delete: (id) => request.delete(`/users/${id}`),
  getByUsername: (username) => request.get(`/users/username/${username}`),
  getByRoleId: (roleId) => request.get(`/users/role/${roleId}`),
}

// 角色相关API
export const roleApi = {
  list: () => request.get('/roles'),
  getById: (id) => request.get(`/roles/${id}`),
  create: (data) => request.post('/roles', data),
  update: (id, data) => request.put(`/roles/${id}`, data),
  delete: (id) => request.delete(`/roles/${id}`),
}

// 市场信号相关API
export const marketSignalApi = {
  // 商品快照
  ingestSnapshots: (data) => request.post('/market/snapshots/ingest', data),
  
  // 销量估算
  getEstimate: (platform, productId, periodType = 'weekly') => 
    request.get(`/market/estimate/${platform}/${productId}`, { params: { periodType } }),
  getEstimateHistory: (platform, productId, periodType = 'weekly', limit = 10) => 
    request.get(`/market/estimate/${platform}/${productId}/history`, { params: { periodType, limit } }),
  getTrendSignal: (platform, productId) => 
    request.get(`/market/estimate/${platform}/${productId}/trend`),
  
  // 批量计算
  calculateDaily: (platform, date) =>
    request.post('/market/estimate/calculate-daily', null, { params: { platform, date } }),
  calculateWeekly: (platform, weekEndDate) =>
    request.post('/market/estimate/calculate-weekly', null, { params: { platform, weekEndDate } }),
  calculateMonthly: (platform, monthEndDate) =>
    request.post('/market/estimate/calculate-monthly', null, { params: { platform, monthEndDate } }),
  calculateTrend: (platform, date) =>
    request.post('/market/estimate/calculate-trend', null, { params: { platform, date } }),
}

// 市场商品相关API
export const marketProductApi = {
  list: (params) => request.get('/market/products', { params }),
  getById: (platform, productId) => request.get(`/market/products/${platform}/${productId}`),
  getSnapshots: (platform, productId, params) =>
    request.get(`/market/products/${platform}/${productId}/snapshots`, { params }),
  getFilters: (platform) => request.get('/market/products/filters', { params: { platform } }),
  getTrending: (params) => request.get('/market/products/trending', { params }),
  ozonSalesProxy: (data) => request.post('/market/products/ozon-sales-proxy', data),
}

// 店铺管理API
export const shopApi = {
  // 店铺CRUD
  list: () => request.get('/api/shops'),
  getById: (id) => request.get(`/api/shops/${id}`),
  create: (data) => request.post('/api/shops', data),
  update: (id, data) => request.put(`/api/shops/${id}`, data),
  delete: (id) => request.delete(`/api/shops/${id}`),
  getDefault: () => request.get('/api/shops/default'),
  getByPlatform: (platform) => request.get(`/api/shops/platform/${platform}`),
  
  // 凭证管理
  getCredential: (shopId) => request.get(`/api/shops/${shopId}/credential`),
  saveCredential: (shopId, data) => request.post(`/api/shops/${shopId}/credential`, data),
  verifyCredential: (shopId) => request.post(`/api/shops/${shopId}/credential/verify`),
  
  // 账号管理
  getAccounts: (shopId) => request.get(`/api/shops/${shopId}/accounts`),
  addAccount: (shopId, data) => request.post(`/api/shops/${shopId}/accounts`, data),
  updateAccount: (shopId, accountId, data) => request.put(`/api/shops/${shopId}/accounts/${accountId}`, data),
  deleteAccount: (shopId, accountId) => request.delete(`/api/shops/${shopId}/accounts/${accountId}`),
  getAccountDetail: (shopId, accountId) => request.get(`/api/shops/${shopId}/accounts/${accountId}/detail`),
  
  // 店铺切换
  switchShop: (shopId) => request.post(`/api/shops/${shopId}/switch`),
  getCurrentShop: () => request.get('/api/shops/current'),
}
