import React, { createContext, useContext, useState, useEffect } from 'react'
import { authApi } from '../api'

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
      } else {
        // token 无效，清除
        localStorage.removeItem('token')
        setToken(null)
        setUser(null)
      }
    } catch (error) {
      localStorage.removeItem('token')
      setToken(null)
      setUser(null)
    } finally {
      setLoading(false)
    }
  }

  const login = async (username, password) => {
    const res = await authApi.login({ username, password })
    if (res.data?.success) {
      localStorage.setItem('token', res.data.token)
      setToken(res.data.token)
      setUser(res.data.user)
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
    setToken(null)
    setUser(null)
  }

  const value = {
    user,
    token,
    loading,
    isAuthenticated: !!user,
    login,
    register,
    logout,
    refreshUser: fetchCurrentUser,
  }

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  )
}

export default AuthContext
