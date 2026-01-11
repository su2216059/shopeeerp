import request from '../utils/request'

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
  list: () => request.get('/ozon/products'),
  sync: () => request.get('/ozon/products/sync'),
}

// Ozon 订单同步数据
export const ozonOrderApi = {
  list: () => request.get('/ozon/orders'),
  sync: () => request.get('/ozon/orders/sync'),
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
  list: () => request.get('/warehouses'),
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
