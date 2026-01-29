import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Popconfirm, message, Modal, Checkbox } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { roleApi, permissionApi } from '../../api'
import { useAuth } from '../../context/AuthContext'

const RoleList = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [permissionModalOpen, setPermissionModalOpen] = useState(false)
  const [permissionLoading, setPermissionLoading] = useState(false)
  const [permissionSaving, setPermissionSaving] = useState(false)
  const [allPermissions, setAllPermissions] = useState([])
  const [selectedPermissionIds, setSelectedPermissionIds] = useState([])
  const [activeRole, setActiveRole] = useState(null)
  const navigate = useNavigate()
  const { hasPermission } = useAuth()
  const canManage = hasPermission('ROLE_MANAGE')

  useEffect(() => {
    fetchData()
  }, [])

  const fetchData = async () => {
    setLoading(true)
    try {
      const result = await roleApi.list()
      setData(result?.data || [])
    } catch (error) {
      message.error('加载数据失败')
    } finally {
      setLoading(false)
    }
  }

  const handleDelete = async (id) => {
    try {
      await roleApi.delete(id)
      message.success('删除成功')
      fetchData()
    } catch (error) {
      message.error('删除失败')
    }
  }

  const openPermissionModal = async (role) => {
    setActiveRole(role)
    setPermissionModalOpen(true)
    setPermissionLoading(true)
    try {
      const [permissionResult, rolePermissionResult] = await Promise.all([
        permissionApi.list(),
        roleApi.getPermissions(role.roleId),
      ])
      setAllPermissions(permissionResult?.data || [])
      setSelectedPermissionIds(rolePermissionResult?.data || [])
    } catch (error) {
      message.error('Failed to load permissions')
    } finally {
      setPermissionLoading(false)
    }
  }

  const handleSavePermissions = async () => {
    if (!activeRole) {
      return
    }
    setPermissionSaving(true)
    try {
      await roleApi.updatePermissions(activeRole.roleId, selectedPermissionIds)
      message.success('Permissions updated')
      setPermissionModalOpen(false)
    } catch (error) {
      message.error('Failed to update permissions')
    } finally {
      setPermissionSaving(false)
    }
  }

  const handlePermissionCancel = () => {
    setPermissionModalOpen(false)
    setActiveRole(null)
  }

  const permissionOptions = allPermissions.map((item) => {
    const label = item.name ? `${item.name} (${item.code})` : item.code
    return { label, value: item.permissionId }
  })

  const columns = [
    {
      title: 'ID',
      dataIndex: 'roleId',
      key: 'roleId',
      width: 80,
    },
    {
      title: '角色名称',
      dataIndex: 'roleName',
      key: 'roleName',
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
    },
    {
      title: '操作',
      key: 'action',
      width: 220,
      render: (_, record) => (
        <Space>
          {canManage && (
            <Button type="link" onClick={() => openPermissionModal(record)}>
              Permissions
            </Button>
          )}
          {canManage && (
            <Button
              type="link"
              icon={<EditOutlined />}
              onClick={() => navigate(`/roles/edit/${record.roleId}`)}
            >
              Edit
            </Button>
          )}
          {canManage && (
            <Popconfirm
              title="Confirm delete?"
              onConfirm={() => handleDelete(record.roleId)}
            >
              <Button type="link" danger icon={<DeleteOutlined />}>
                Delete
              </Button>
            </Popconfirm>
          )}
        </Space>
      ),
    },
  ]

  return (
    <div>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
        <h1>角色管理</h1>
        {canManage && (
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={() => navigate('/roles/new')}
          >
            New Role
          </Button>
        )}
      </div>
      <Modal
        title={activeRole ? `Permissions - ${activeRole.roleName}` : 'Permissions'}
        open={permissionModalOpen}
        onOk={handleSavePermissions}
        onCancel={handlePermissionCancel}
        okButtonProps={{ loading: permissionSaving }}
        destroyOnClose
      >
        {permissionLoading ? (
          <div>Loading...</div>
        ) : (
          <Checkbox.Group
            value={selectedPermissionIds}
            onChange={(values) => setSelectedPermissionIds(values)}
          >
            <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
              {permissionOptions.map((option) => (
                <Checkbox key={option.value} value={option.value}>
                  {option.label}
                </Checkbox>
              ))}
            </div>
          </Checkbox.Group>
        )}
      </Modal>
      <Table
        columns={columns}
        dataSource={data}
        rowKey="roleId"
        loading={loading}
        pagination={{ pageSize: 10 }}
      />
    </div>
  )
}

export default RoleList
