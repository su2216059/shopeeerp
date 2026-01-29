import request from '../utils/request'

const resolveShopId = () => {
  const stored = localStorage.getItem('currentShopId')
  if (!stored) {
    return null
  }
  const parsed = Number(stored)
  return Number.isNaN(parsed) ? null : parsed
}

const withShopId = (params = {}) => {
  const shopId = resolveShopId()
  return { ...params, shopId }
}

// 璁よ瘉鐩稿叧API
export const authApi = {
  login: (data) => request.post('/auth/login', data),
  register: (data) => request.post('/auth/register', data),
  logout: () => request.post('/auth/logout'),
  getCurrentUser: () => request.get('/auth/me'),
  changePassword: (data) => request.post('/auth/change-password', data),
}

// 瀹㈡埛鐩稿叧API
export const customerApi = {
  list: () => request.get('/customers', { params: withShopId() }),
  getById: (id) => request.get(`/customers/${id}`, { params: withShopId() }),
  create: (data) => request.post('/customers', data, { params: withShopId() }),
  update: (id, data) => request.put(`/customers/${id}`, data, { params: withShopId() }),
  delete: (id) => request.delete(`/customers/${id}`, { params: withShopId() }),
  getByEmail: (email) => request.get(`/customers/email/${email}`, { params: withShopId() }),
}

// 瀹㈡埛鏀寔鐩稿叧API
export const customerSupportApi = {
  list: () => request.get('/customer-support', { params: withShopId() }),
  getById: (id) => request.get(`/customer-support/${id}`, { params: withShopId() }),
  create: (data) => request.post('/customer-support', data, { params: withShopId() }),
  update: (id, data) => request.put(`/customer-support/${id}`, data, { params: withShopId() }),
  delete: (id) => request.delete(`/customer-support/${id}`, { params: withShopId() }),
  getByCustomerId: (customerId) =>
    request.get(`/customer-support/customer/${customerId}`, { params: withShopId() }),
}

// 浜у搧鐩稿叧API
export const productApi = {
  list: () => request.get('/products', { params: withShopId() }),
  getById: (id) => request.get(`/products/${id}`, { params: withShopId() }),
  create: (data) => request.post('/products', data, { params: withShopId() }),
  update: (id, data) => request.put(`/products/${id}`, data, { params: withShopId() }),
  delete: (id) => request.delete(`/products/${id}`, { params: withShopId() }),
  getBySku: (sku) => request.get(`/products/sku/${sku}`, { params: withShopId() }),
  getByCategoryId: (categoryId) =>
    request.get(`/products/category/${categoryId}`, { params: withShopId() }),
}

// Ozon 鍟嗗搧鍚屾鏁版嵁
export const ozonProductApi = {
  list: (params) => request.get('/ozon/products', { params: withShopId(params) }),
  sync: (params) => request.get('/ozon/products/sync', { params: withShopId(params) }),
}

// Ozon 璁㈠崟鍚屾鏁版嵁
export const ozonOrderApi = {
  list: (params) => request.get('/ozon/orders', { params: withShopId(params) }),
  sync: (params) => request.get('/ozon/orders/sync', { params: withShopId(params) }),
  updatePurchaseAmount: (postingNumber, purchaseAmount) =>
    request.put(
      `/ozon/orders/${encodeURIComponent(postingNumber)}/purchase-amount`,
      { purchaseAmount },
      { params: withShopId() }
    ),
}

// Ozon 璐㈠姟锛堝埄娑︼級鍚屾
export const ozonProfitApi = {
  sync: (params) => request.post('/ozon/profit/sync', null, { params: withShopId(params) }),
}

// 璁㈠崟鐩稿叧API
export const orderApi = {
  list: () => request.get('/orders', { params: withShopId() }),
  getById: (id) => request.get(`/orders/${id}`, { params: withShopId() }),
  create: (data) => request.post('/orders', data, { params: withShopId() }),
  update: (id, data) => request.put(`/orders/${id}`, data, { params: withShopId() }),
  delete: (id) => request.delete(`/orders/${id}`, { params: withShopId() }),
  getByCustomerId: (customerId) =>
    request.get(`/orders/customer/${customerId}`, { params: withShopId() }),
}

// 璁㈠崟椤圭浉鍏矨PI
export const orderItemApi = {
  list: () => request.get('/order-items', { params: withShopId() }),
  getById: (id) => request.get(`/order-items/${id}`, { params: withShopId() }),
  create: (data) => request.post('/order-items', data, { params: withShopId() }),
  update: (id, data) => request.put(`/order-items/${id}`, data, { params: withShopId() }),
  delete: (id) => request.delete(`/order-items/${id}`, { params: withShopId() }),
  getByOrderId: (orderId) => request.get(`/order-items/order/${orderId}`, { params: withShopId() }),
  getByProductId: (productId) =>
    request.get(`/order-items/product/${productId}`, { params: withShopId() }),
}

// 鍙戠エ鐩稿叧API
export const invoiceApi = {
  list: () => request.get('/invoices', { params: withShopId() }),
  getById: (id) => request.get(`/invoices/${id}`, { params: withShopId() }),
  create: (data) => request.post('/invoices', data, { params: withShopId() }),
  update: (id, data) => request.put(`/invoices/${id}`, data, { params: withShopId() }),
  delete: (id) => request.delete(`/invoices/${id}`, { params: withShopId() }),
  getByOrderId: (orderId) => request.get(`/invoices/order/${orderId}`, { params: withShopId() }),
}

// 搴撳瓨鐩稿叧API
export const inventoryApi = {
  list: () => request.get('/inventory', { params: withShopId() }),
  getById: (id) => request.get(`/inventory/${id}`, { params: withShopId() }),
  create: (data) => request.post('/inventory', data, { params: withShopId() }),
  update: (id, data) => request.put(`/inventory/${id}`, data, { params: withShopId() }),
  delete: (id) => request.delete(`/inventory/${id}`, { params: withShopId() }),
  getByProductId: (productId) => request.get(`/inventory/product/${productId}`, { params: withShopId() }),
  getByWarehouseId: (warehouseId) =>
    request.get(`/inventory/warehouse/${warehouseId}`, { params: withShopId() }),
}

// 浠撳簱鐩稿叧API
export const warehouseApi = {
  list: () => request.get('/ozon/warehouses', { params: withShopId() }),
  sync: () => request.post('/ozon/warehouses/sync', null, { params: withShopId() }),
  getById: (id) => request.get(`/warehouses/${id}`, { params: withShopId() }),
  create: (data) => request.post('/warehouses', data, { params: withShopId() }),
  update: (id, data) => request.put(`/warehouses/${id}`, data, { params: withShopId() }),
  delete: (id) => request.delete(`/warehouses/${id}`, { params: withShopId() }),
}

// 閿€鍞暟鎹浉鍏矨PI
export const salesDataApi = {
  list: () => request.get('/sales-data', { params: withShopId() }),
  getById: (id) => request.get(`/sales-data/${id}`, { params: withShopId() }),
  create: (data) => request.post('/sales-data', data, { params: withShopId() }),
  update: (id, data) => request.put(`/sales-data/${id}`, data, { params: withShopId() }),
  delete: (id) => request.delete(`/sales-data/${id}`, { params: withShopId() }),
  getByProductId: (productId) => request.get(`/sales-data/product/${productId}`, { params: withShopId() }),
  getByOrderId: (orderId) => request.get(`/sales-data/order/${orderId}`, { params: withShopId() }),
}

// 鐢ㄦ埛鐩稿叧API
export const userApi = {
  list: () => request.get('/users'),
  getById: (id) => request.get(`/users/${id}`),
  create: (data) => request.post('/users', data),
  update: (id, data) => request.put(`/users/${id}`, data),
  delete: (id) => request.delete(`/users/${id}`),
  getByUsername: (username) => request.get(`/users/username/${username}`),
  getByRoleId: (roleId) => request.get(`/users/role/${roleId}`),
}

// 瑙掕壊鐩稿叧API
export const roleApi = {
  list: () => request.get('/roles'),
  getById: (id) => request.get(`/roles/${id}`),
  create: (data) => request.post('/roles', data),
  update: (id, data) => request.put(`/roles/${id}`, data),
  delete: (id) => request.delete(`/roles/${id}`),
  getPermissions: (id) => request.get(`/roles/${id}/permissions`),
  updatePermissions: (id, permissionIds) =>
    request.put(`/roles/${id}/permissions`, { permissionIds }),
}

// Permission APIs
export const permissionApi = {
  list: () => request.get('/permissions'),
}

// 甯傚満淇″彿鐩稿叧API
export const marketSignalApi = {
  // 鍟嗗搧蹇収
  ingestSnapshots: (data) => request.post('/market/snapshots/ingest', data),
  
  // 閿€閲忎及绠?
  getEstimate: (platform, productId, periodType = 'weekly') => 
    request.get(`/market/estimate/${platform}/${productId}`, { params: { periodType } }),
  getEstimateHistory: (platform, productId, periodType = 'weekly', limit = 10) => 
    request.get(`/market/estimate/${platform}/${productId}/history`, { params: { periodType, limit } }),
  getTrendSignal: (platform, productId) => 
    request.get(`/market/estimate/${platform}/${productId}/trend`),
  
  // 鎵归噺璁＄畻
  calculateDaily: (platform, date) =>
    request.post('/market/estimate/calculate-daily', null, { params: { platform, date } }),
  calculateWeekly: (platform, weekEndDate) =>
    request.post('/market/estimate/calculate-weekly', null, { params: { platform, weekEndDate } }),
  calculateMonthly: (platform, monthEndDate) =>
    request.post('/market/estimate/calculate-monthly', null, { params: { platform, monthEndDate } }),
  calculateTrend: (platform, date) =>
    request.post('/market/estimate/calculate-trend', null, { params: { platform, date } }),
}

// 甯傚満鍟嗗搧鐩稿叧API
export const marketProductApi = {
  list: (params) => request.get('/market/products', { params }),
  getById: (platform, productId) => request.get(`/market/products/${platform}/${productId}`),
  getSnapshots: (platform, productId, params) =>
    request.get(`/market/products/${platform}/${productId}/snapshots`, { params }),
  getFilters: (platform) => request.get('/market/products/filters', { params: { platform } }),
  getTrending: (params) => request.get('/market/products/trending', { params }),
  ozonSalesProxy: (data) => request.post('/market/products/ozon-sales-proxy', data),
}

// 搴楅摵绠＄悊API
export const shopApi = {
  // 搴楅摵CRUD
  list: () => request.get('/shops'),
  getById: (id) => request.get(`/shops/${id}`),
  create: (data) => request.post('/shops', data),
  update: (id, data) => request.put(`/shops/${id}`, data),
  delete: (id) => request.delete(`/shops/${id}`),
  getDefault: () => request.get('/shops/default'),
  getByPlatform: (platform) => request.get(`/shops/platform/${platform}`),
  bind: (data) => request.post('/shops/bind', data),
  
  // 鍑瘉绠＄悊
  getCredential: (shopId) => request.get(`/shops/${shopId}/credential`),
  saveCredential: (shopId, data) => request.post(`/shops/${shopId}/credential`, data),
  verifyCredential: (shopId) => request.post(`/shops/${shopId}/credential/verify`),
  
  // 璐﹀彿绠＄悊
  getAccounts: (shopId) => request.get(`/shops/${shopId}/accounts`),
  addAccount: (shopId, data) => request.post(`/shops/${shopId}/accounts`, data),
  updateAccount: (shopId, accountId, data) => request.put(`/shops/${shopId}/accounts/${accountId}`, data),
  deleteAccount: (shopId, accountId) => request.delete(`/shops/${shopId}/accounts/${accountId}`),
  getAccountDetail: (shopId, accountId) => request.get(`/shops/${shopId}/accounts/${accountId}/detail`),
  
  // 搴楅摵鍒囨崲
  switchShop: (shopId) => request.post(`/shops/${shopId}/switch`),
  getCurrentShop: () => request.get('/shops/current'),
}

