import React from 'react'
import { Navigate, useLocation } from 'react-router-dom'
import { Spin } from 'antd'
import { useAuth } from '../context/AuthContext'

const PermissionRoute = ({ children, permission, anyPermissions }) => {
  const { permissions, loading } = useAuth()
  const location = useLocation()

  if (loading) {
    return (
      <div style={{
        height: '100vh',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
      }}>
        <Spin size="large" tip="加载中..." />
      </div>
    )
  }

  const permissionList = anyPermissions || []
  const hasPermission = permission ? permissions.includes(permission) : true
  const hasAnyPermission = permissionList.length === 0
    ? true
    : permissionList.some((item) => permissions.includes(item))

  if (!hasPermission || !hasAnyPermission) {
    return <Navigate to="/dashboard" state={{ from: location }} replace />
  }

  return children
}

export default PermissionRoute
