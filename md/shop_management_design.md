# 多店铺管理系统设计文档

## 1. 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                        前端 UI                               │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐    │
│  │ 店铺列表  │  │ 凭证管理  │  │ 账号管理  │  │ 店铺切换  │    │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     REST API Layer                          │
│                  /api/shops/*                               │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Service Layer                            │
│  ┌──────────────────────────────────────────────────────┐  │
│  │                   ShopService                         │  │
│  │  • 店铺 CRUD                                          │  │
│  │  • 凭证管理 (加密/解密)                                │  │
│  │  • 账号管理 (加密/解密)                                │  │
│  │  • 店铺上下文切换                                      │  │
│  └──────────────────────────────────────────────────────┘  │
│                              │                              │
│                              ▼                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │                  CryptoUtil                           │  │
│  │  • AES-256-GCM 加密                                   │  │
│  │  • 敏感数据保护                                        │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Data Layer                               │
│  ┌────────┐  ┌─────────────────┐  ┌─────────────┐          │
│  │  shop  │──│ shop_credential │  │ shop_account│          │
│  └────────┘  └─────────────────┘  └─────────────┘          │
└─────────────────────────────────────────────────────────────┘
```

## 2. 数据模型

### 2.1 店铺表 (shop)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| shop_code | VARCHAR(32) | 店铺编码 (唯一) |
| shop_name | VARCHAR(128) | 店铺名称 |
| platform | VARCHAR(32) | 平台: ozon/shopee/wildberries |
| market | VARCHAR(10) | 市场: RU/CN/US |
| owner_user_id | BIGINT | 所属用户 |
| status | VARCHAR(20) | 状态: active/suspended/closed |
| is_default | TINYINT | 是否默认店铺 |
| seller_id | VARCHAR(64) | 平台卖家ID |

### 2.2 API凭证表 (shop_credential)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| shop_id | BIGINT | 店铺ID |
| client_id | VARCHAR(128) | Client ID |
| api_key_encrypted | VARCHAR(512) | API Key (AES加密) |
| api_secret_encrypted | VARCHAR(512) | API Secret (AES加密) |
| access_token_encrypted | VARCHAR(1024) | OAuth Token (加密) |
| credential_type | VARCHAR(20) | 类型: api_key/oauth |
| status | VARCHAR(20) | 状态: active/expired/revoked |

### 2.3 登录账号表 (shop_account)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| shop_id | BIGINT | 店铺ID |
| account_type | VARCHAR(32) | 类型: seller_center/warehouse/finance |
| username | VARCHAR(128) | 用户名 |
| password_encrypted | VARCHAR(512) | 密码 (AES加密) |
| two_factor_secret | VARCHAR(128) | 2FA密钥 |
| status | VARCHAR(20) | 状态 |

## 3. API 接口

### 3.1 店铺管理

```
GET    /api/shops                    # 获取所有店铺
GET    /api/shops/{id}               # 获取店铺详情
POST   /api/shops                    # 创建店铺
PUT    /api/shops/{id}               # 更新店铺
DELETE /api/shops/{id}               # 删除店铺
GET    /api/shops/default            # 获取默认店铺
GET    /api/shops/platform/{platform} # 按平台获取店铺
```

### 3.2 凭证管理

```
GET    /api/shops/{shopId}/credential         # 获取凭证 (掩码)
POST   /api/shops/{shopId}/credential         # 保存凭证
POST   /api/shops/{shopId}/credential/verify  # 验证凭证
```

### 3.3 账号管理

```
GET    /api/shops/{shopId}/accounts                   # 获取账号列表
POST   /api/shops/{shopId}/accounts                   # 添加账号
PUT    /api/shops/{shopId}/accounts/{accountId}       # 更新账号
DELETE /api/shops/{shopId}/accounts/{accountId}       # 删除账号
GET    /api/shops/{shopId}/accounts/{accountId}/detail # 获取详情 (含密码)
```

### 3.4 店铺切换

```
POST   /api/shops/{shopId}/switch    # 切换当前店铺
GET    /api/shops/current            # 获取当前店铺
```

## 4. 安全设计

### 4.1 加密方案

- **算法**: AES-256-GCM
- **密钥**: 配置文件中的 32 字符密钥
- **IV**: 每次加密随机生成 12 字节
- **格式**: Base64(IV + Ciphertext + AuthTag)

### 4.2 敏感字段

以下字段在数据库中加密存储：
- `api_key_encrypted` - API密钥
- `api_secret_encrypted` - API密钥
- `access_token_encrypted` - OAuth Token
- `refresh_token_encrypted` - Refresh Token
- `password_encrypted` - 登录密码

### 4.3 API安全

- 凭证详情返回时自动掩码 (如: `f81****0b1`)
- 密码默认不返回到前端
- 需要密码时调用 `/detail` 接口 (需权限控制)

## 5. 使用示例

### 5.1 创建店铺并配置凭证

```java
// 1. 创建店铺
Shop shop = new Shop();
shop.setShopCode("OZON_STORE_1");
shop.setShopName("我的Ozon店铺");
shop.setPlatform("ozon");
shop.setMarket("RU");
shopService.createShop(shop);

// 2. 配置API凭证
shopService.saveCredential(
    shop.getId(),
    "3207535",           // clientId
    "f81516e3-7ab9...",  // apiKey
    null                 // apiSecret (Ozon不需要)
);

// 3. 添加登录账号
ShopAccount account = new ShopAccount();
account.setAccountType("seller_center");
account.setAccountName("卖家中心主账号");
account.setUsername("seller@example.com");
account.setPassword("mypassword123");
shopService.addAccount(shop.getId(), account);
```

### 5.2 切换店铺并调用API

```java
// 切换到指定店铺
shopService.switchShop(shopId);

// 获取当前店铺的凭证
ShopCredential credential = shopService.getCurrentCredential();

// 使用凭证调用平台API
ozonApiClient.setClientId(credential.getClientId());
ozonApiClient.setApiKey(credential.getApiKey());
ozonApiClient.getProducts();
```

## 6. 数据库初始化

执行 SQL 脚本创建表结构：

```bash
mysql -u root -p shopeeerp < sql/shop_management.sql
```

## 7. 配置项

在 `application.properties` 中添加：

```properties
# 加密密钥 (必须32字符，生产环境请更换)
app.crypto.secret-key=your-32-character-secret-key!!
```

## 8. 扩展计划

### 8.1 多平台支持

| 平台 | 凭证类型 | 字段 |
|------|---------|------|
| Ozon | API Key | client_id + api_key |
| Shopee | OAuth | access_token + refresh_token |
| Wildberries | API Key | api_key |
| Amazon | OAuth | client_id + client_secret + refresh_token |

### 8.2 后续功能

- [ ] 用户权限管理 (owner/admin/operator/viewer)
- [ ] API调用日志和统计
- [ ] 凭证自动续期 (OAuth类型)
- [ ] 店铺数据隔离
- [ ] 多店铺数据汇总报表
