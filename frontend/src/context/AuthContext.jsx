import React, { createContext, useContext, useState, useEffect } from 'react'
import { authApi, shopApi } from '../api'

const AuthContext = createContext(null)

export const useAuth = () => {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider')
  }
  return context
}

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)
  const [token, setToken] = useState(localStorage.getItem('token'))
  const [permissions, setPermissions] = useState([])

  useEffect(() => {
    if (token) {
      fetchCurrentUser()
    } else {
      setLoading(false)
    }
  }, [token])

  const fetchCurrentUser = async () => {
    try {
      const res = await authApi.getCurrentUser()
      if (res.data?.success) {
        setUser(res.data.user)
        setPermissions(res.data.permissions || [])
        syncCurrentShop()
      } else {
        // token 无效，清除
        localStorage.removeItem('token')
        setToken(null)
        setUser(null)
        setPermissions([])
      }
    } catch (error) {
      localStorage.removeItem('token')
      setToken(null)
      setUser(null)
      setPermissions([])
    } finally {
      setLoading(false)
    }
  }

  const syncCurrentShop = async () => {
    try {
      const res = await shopApi.getCurrentShop()
      if (res.data?.id) {
        localStorage.setItem('currentShopId', String(res.data.id))
      }
    } catch (error) {
      // ignore
    }
  }

  const login = async (username, password) => {
    const res = await authApi.login({ username, password })
    if (res.data?.success) {
      localStorage.setItem('token', res.data.token)
      setToken(res.data.token)
      setUser(res.data.user)
      setPermissions(res.data.permissions || [])
      if (res.data?.currentShopId) {
        localStorage.setItem('currentShopId', String(res.data.currentShopId))
      }
      syncCurrentShop()
      return { success: true }
    }
    return { success: false, message: res.data?.message || '登录失败' }
  }

  const register = async (username, password, confirmPassword) => {
    const res = await authApi.register({ username, password, confirmPassword })
    if (res.data?.success) {
      localStorage.setItem('token', res.data.token)
      setToken(res.data.token)
      setUser(res.data.user)
      setPermissions(res.data.permissions || [])
      if (res.data?.currentShopId) {
        localStorage.setItem('currentShopId', String(res.data.currentShopId))
      }
      syncCurrentShop()
      return { success: true }
    }
    return { success: false, message: res.data?.message || '注册失败' }
  }

  const logout = async () => {
    try {
      await authApi.logout()
    } catch (error) {
      // 忽略错误
    }
    localStorage.removeItem('token')
    localStorage.removeItem('currentShopId')
    setToken(null)
    setUser(null)
    setPermissions([])
  }

  const hasPermission = (permission) => permissions.includes(permission)
  const hasAnyPermission = (permissionList = []) =>
    permissionList.some((permission) => hasPermission(permission))

  const value = {
    user,
    token,
    loading,
    permissions,
    isAuthenticated: !!user,
    login,
    register,
    logout,
    refreshUser: fetchCurrentUser,
    hasPermission,
    hasAnyPermission,
  }

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  )
}

export default AuthContext
