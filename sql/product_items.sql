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

 Date: 08/01/2026 22:00:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for product_items
-- ----------------------------
DROP TABLE IF EXISTS `product_items`;
CREATE TABLE `product_items`  (
  `item_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `ozon_id` bigint(0) NOT NULL COMMENT 'Ozon商品ID',
  `offer_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品Offer ID',
  `sku` bigint(0) NULL DEFAULT NULL COMMENT 'SKU编号',
  `name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品名称',
  `price` decimal(15, 2) NULL DEFAULT NULL COMMENT '商品价格',
  `old_price` decimal(15, 2) NULL DEFAULT NULL COMMENT '原价',
  `min_price` decimal(15, 2) NULL DEFAULT NULL COMMENT '最低价格',
  `currency_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '货币代码',
  `description_category_id` bigint(0) NULL DEFAULT NULL COMMENT '描述分类ID',
  `type_id` bigint(0) NULL DEFAULT NULL COMMENT '商品类型ID',
  `vat` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '增值税率',
  `volume_weight` decimal(10, 2) NULL DEFAULT NULL COMMENT '体积重量',
  `is_archived` tinyint(1) NULL DEFAULT 0 COMMENT '是否归档',
  `is_autoarchived` tinyint(1) NULL DEFAULT 0 COMMENT '是否自动归档',
  `is_discounted` tinyint(1) NULL DEFAULT 0 COMMENT '是否打折',
  `is_kgt` tinyint(1) NULL DEFAULT 0 COMMENT '是否KGT',
  `is_prepayment_allowed` tinyint(1) NULL DEFAULT 0 COMMENT '是否允许预付款',
  `is_super` tinyint(1) NULL DEFAULT 0 COMMENT '是否超级商品',
  `has_discounted_fbo_item` tinyint(1) NULL DEFAULT 0 COMMENT '是否有打折FBO商品',
  `discounted_fbo_stocks` int(0) NULL DEFAULT 0 COMMENT '打折FBO库存',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品状态',
  `moderate_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核状态',
  `validation_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '验证状态',
  `status_description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '状态描述',
  `status_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '状态名称',
  `status_failed` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '失败状态',
  `is_created` tinyint(1) NULL DEFAULT 0 COMMENT '是否已创建',
  `primary_image` json NULL COMMENT '主图URL列表',
  `images` json NULL COMMENT '商品图片URL列表',
  `images360` json NULL COMMENT '360度图片URL列表',
  `color_image` json NULL COMMENT '颜色图片URL列表',
  `barcodes` json NULL COMMENT '条形码列表',
  `stocks_info` json NULL COMMENT '库存信息JSON',
  `sources_info` json NULL COMMENT '来源信息JSON',
  `availabilities_info` json NULL COMMENT '可用性信息JSON',
  `commissions_info` json NULL COMMENT '佣金信息JSON',
  `promotions_info` json NULL COMMENT '促销信息JSON',
  `price_indexes_info` json NULL COMMENT '价格指数信息JSON',
  `errors_info` json NULL COMMENT '错误信息JSON',
  `model_info` json NULL COMMENT '模型信息JSON',
  `visibility_details` json NULL COMMENT '可见性详情JSON',
  `ozon_created_at` timestamp(0) NULL DEFAULT NULL COMMENT 'Ozon平台创建时间',
  `ozon_updated_at` timestamp(0) NULL DEFAULT NULL COMMENT 'Ozon平台更新时间',
  `status_updated_at` timestamp(0) NULL DEFAULT NULL COMMENT '状态更新时间',
  `created_at` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`item_id`) USING BTREE,
  UNIQUE INDEX `ozon_id`(`ozon_id`) USING BTREE COMMENT 'Ozon ID唯一索引',
  INDEX `offer_id`(`offer_id`) USING BTREE COMMENT 'Offer ID索引',
  INDEX `sku`(`sku`) USING BTREE COMMENT 'SKU索引',
  INDEX `status`(`status`) USING BTREE COMMENT '状态索引',
  INDEX `is_archived`(`is_archived`) USING BTREE COMMENT '归档状态索引',
  INDEX `created_at`(`created_at`) USING BTREE COMMENT '创建时间索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT = 'Ozon商品详情表';

SET FOREIGN_KEY_CHECKS = 1;
