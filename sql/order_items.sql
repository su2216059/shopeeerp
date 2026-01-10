/*
 Navicat Premium Data Transfer

 Source Server         : 111
 Source Server Type    : MySQL
 Source Server Version : 80044
 Source Host           : localhost:3306
 Source Schema         : shopeeerp

 Target Server Type    : MySQL
 Target Server Version : 80044
 File Encoding         : 65001

 Date: 07/01/2026 21:57:12
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for order_items
-- ----------------------------
DROP TABLE IF EXISTS `order_items`;
CREATE TABLE `order_items`  (
  `order_item_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
  `order_id` bigint(0) NOT NULL COMMENT '订单ID',
  `product_id` bigint(0) NOT NULL COMMENT '产品ID',
  `sku` bigint(0) NOT NULL COMMENT 'SKU编号',
  `offer_id` varchar(255) NULL DEFAULT NULL COMMENT '商品编号',
  `name` varchar(500) NULL DEFAULT NULL COMMENT '商品名称',
  `quantity` int(0) NOT NULL COMMENT '数量',
  `price` decimal(10, 2) NOT NULL COMMENT '单价',
  `old_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '原价',
  `min_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '最低价',
  `total` decimal(10, 2) NOT NULL COMMENT '总价',
  `currency_code` varchar(10) NULL DEFAULT NULL COMMENT '货币代码',
  `vat` varchar(50) NULL DEFAULT NULL COMMENT '增值税',
  `commission_percent` decimal(5, 2) NULL DEFAULT NULL COMMENT '佣金百分比',
  `commission_value` decimal(10, 2) NULL DEFAULT NULL COMMENT '佣金金额',
  `delivery_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '配送金额',
  `return_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '退货金额',
  `sale_schema` varchar(50) NULL DEFAULT NULL COMMENT '销售方案',
  `status` varchar(50) NULL DEFAULT NULL COMMENT '状态',
  `status_description` varchar(500) NULL DEFAULT NULL COMMENT '状态描述',
  `status_name` varchar(100) NULL DEFAULT NULL COMMENT '状态名称',
  `status_failed` varchar(100) NULL DEFAULT NULL COMMENT '状态失败',
  `status_tooltip` varchar(500) NULL DEFAULT NULL COMMENT '状态提示',
  `status_updated_at` datetime NULL DEFAULT NULL COMMENT '状态更新时间',
  `moderate_status` varchar(50) NULL DEFAULT NULL COMMENT '审核状态',
  `validation_status` varchar(50) NULL DEFAULT NULL COMMENT '验证状态',
  `is_discounted` tinyint(1) NULL DEFAULT 0 COMMENT '是否折扣',
  `is_archived` tinyint(1) NULL DEFAULT 0 COMMENT '是否归档',
  `is_autoarchived` tinyint(1) NULL DEFAULT 0 COMMENT '是否自动归档',
  `is_kgt` tinyint(1) NULL DEFAULT 0 COMMENT '是否KGT',
  `is_prepayment_allowed` tinyint(1) NULL DEFAULT 0 COMMENT '是否允许预付款',
  `is_super` tinyint(1) NULL DEFAULT 0 COMMENT '是否超级商品',
  `is_created` tinyint(1) NULL DEFAULT 0 COMMENT '是否已创建',
  `has_stock` tinyint(1) NULL DEFAULT 0 COMMENT '是否有库存',
  `has_price` tinyint(1) NULL DEFAULT 0 COMMENT '是否有价格',
  `has_discounted_fbo_item` tinyint(1) NULL DEFAULT 0 COMMENT '是否有折扣FBO商品',
  `stock_present` int(0) NULL DEFAULT 0 COMMENT '可用库存',
  `stock_reserved` int(0) NULL DEFAULT 0 COMMENT '预留库存',
  `discounted_fbo_stocks` int(0) NULL DEFAULT 0 COMMENT '折扣FBO库存',
  `description_category_id` bigint(0) NULL DEFAULT NULL COMMENT '描述分类ID',
  `type_id` bigint(0) NULL DEFAULT NULL COMMENT '类型ID',
  `model_id` bigint(0) NULL DEFAULT NULL COMMENT '模型ID',
  `model_count` int(0) NULL DEFAULT 0 COMMENT '模型数量',
  `volume_weight` decimal(10, 2) NULL DEFAULT NULL COMMENT '体积重量',
  `images` json NULL DEFAULT NULL COMMENT '图片列表',
  `images360` json NULL DEFAULT NULL COMMENT '360度图片列表',
  `primary_image` json NULL DEFAULT NULL COMMENT '主图片列表',
  `color_image` json NULL DEFAULT NULL COMMENT '颜色图片列表',
  `barcodes` json NULL DEFAULT NULL COMMENT '条形码列表',
  `source` varchar(100) NULL DEFAULT NULL COMMENT '来源',
  `shipment_type` varchar(50) NULL DEFAULT NULL COMMENT '配送类型',
  `quant_code` varchar(100) NULL DEFAULT NULL COMMENT '数量代码',
  `price_index_value` decimal(10, 2) NULL DEFAULT NULL COMMENT '价格索引值',
  `color_index` varchar(50) NULL DEFAULT NULL COMMENT '颜色索引',
  `promotion_type` varchar(50) NULL DEFAULT NULL COMMENT '促销类型',
  `promotion_is_enabled` tinyint(1) NULL DEFAULT 0 COMMENT '促销是否启用',
  `created_at` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`order_item_id`) USING BTREE,
  INDEX `idx_order_id`(`order_id`) USING BTREE,
  INDEX `idx_product_id`(`product_id`) USING BTREE,
  INDEX `idx_sku`(`sku`) USING BTREE,
  INDEX `idx_offer_id`(`offer_id`) USING BTREE,
  CONSTRAINT `fk_order_items_order_id` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_order_items_product_id` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT = '订单项表';

SET FOREIGN_KEY_CHECKS = 1;
