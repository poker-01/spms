-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: spms
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping data for table `bill_info`
--


--
-- Dumping data for table `building_info`
--

INSERT INTO `building_info` (`id`, `community_id`, `building_code`, `building_name`, `total_floors`, `total_units`, `status`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`, `update_user`) VALUES (1,1,'A栋','A栋住宅楼',18,72,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(2,1,'B栋','B栋住宅楼',18,72,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(3,1,'C栋','C栋住宅楼',12,48,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(4,2,'1号楼','1号住宅楼',22,88,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(5,2,'2号楼','2号住宅楼',22,88,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL);

--
-- Dumping data for table `community_info`
--

INSERT INTO `community_info` (`id`, `community_code`, `community_name`, `address`, `province`, `city`, `district`, `total_buildings`, `total_units`, `property_company`, `manager_name`, `manager_phone`, `status`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`, `update_user`) VALUES (1,'COMM_001','幸福花园小区','北京市朝阳区幸福路88号','北京市','北京市','朝阳区',12,860,'幸福物业有限公司','张三','13800001111',1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(2,'COMM_002','阳光城小区','上海市浦东新区阳光大道1号','上海市','上海市','浦东新区',8,520,'阳光物业服务公司','李四','13800002222',1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL);

--
-- Dumping data for table `complaint_suggestion`
--


--
-- Dumping data for table `fee_item`
--

INSERT INTO `fee_item` (`id`, `item_code`, `item_name`, `item_type`, `unit_price`, `unit`, `calc_method`, `is_default`, `status`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`, `update_user`) VALUES (1,'FEE_001','物业管理费',1,2.50,'平方米/月',2,1,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(2,'FEE_002','水费',2,3.50,'吨',4,1,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(3,'FEE_003','电费',2,0.60,'度',4,1,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(4,'FEE_004','燃气费',3,2.80,'立方米',4,1,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(5,'FEE_005','停车管理费',4,150.00,'月',1,1,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(6,'FEE_006','垃圾清运费',5,10.00,'月',3,1,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL);

--
-- Dumping data for table `house_info`
--

INSERT INTO `house_info` (`id`, `building_id`, `house_number`, `floor_number`, `house_area`, `house_type`, `owner_name`, `owner_phone`, `status`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`, `update_user`) VALUES (1,1,'101',1,95.50,'三室两厅','王五','13900001111',1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(2,1,'102',1,85.00,'两室两厅','赵六','13900002222',1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(3,1,'201',2,95.50,'三室两厅',NULL,NULL,0,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(4,1,'202',2,85.00,'两室两厅',NULL,NULL,0,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(5,3,'101',1,120.00,'四室两厅','孙七','13900003333',1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(6,3,'102',1,100.00,'三室两厅','周八','13900004444',1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(7,5,'201',2,110.00,'三室两厅',NULL,NULL,0,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(8,5,'202',2,98.00,'三室两厅',NULL,NULL,0,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL);

--
-- Dumping data for table `owner_house_rel`
--


--
-- Dumping data for table `owner_info`
--


--
-- Dumping data for table `payment_record`
--


--
-- Dumping data for table `repair_order`
--


--
-- Dumping data for table `sys_permission_info`
--

INSERT INTO `sys_permission_info` (`id`, `parent_id`, `permission_code`, `permission_name`, `permission_type`, `permission_icon`, `permission_path`, `permission_component`, `permission_str`, `sort_order`, `visible`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`, `update_user`) VALUES (1,0,'dashboard','系统总览',2,'Monitor','/admin/home',NULL,'dashboard:view',1,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',NULL,NULL),(2,0,'system','系统管理',1,'Setting','/system',NULL,NULL,2,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',NULL,NULL),(3,0,'community','小区管理',1,'HomeFilled','/community',NULL,NULL,3,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',NULL,NULL),(4,0,'property','物业服务',1,'Service','/property',NULL,NULL,4,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',NULL,NULL),(5,0,'finance','财务管理',1,'Money','/finance',NULL,NULL,5,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',NULL,NULL),(21,2,'sys:user','用户管理',2,'User','user','/system/user/index',NULL,1,0,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',NULL,NULL),(22,2,'sys:role','角色管理',2,'UserFilled','role','/system/role/index',NULL,2,0,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',NULL,NULL),(23,2,'sys:permission','权限管理',2,'Key','permission','/system/permission/index',NULL,3,0,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',NULL,NULL),(31,3,'community:community','小区信息',2,'OfficeBuilding','community','/community/community/index',NULL,1,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',NULL,NULL),(32,3,'community:building','楼栋管理',2,'Grid','building','/community/building/index',NULL,2,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',NULL,NULL),(33,3,'community:house','房屋管理',2,'HomeFilled','house','/community/house/index',NULL,3,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',NULL,NULL),(34,3,'community:owner','业主管理',2,'User','owner','/community/owner/index',NULL,4,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',NULL,NULL),(41,4,'property:repair','报修管理',2,'Tools','repair','/property/repair/index',NULL,1,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',NULL,NULL),(42,4,'property:complaint','投诉建议',2,'ChatLineSquare','complaint','/property/complaint/index',NULL,2,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',NULL,NULL),(51,5,'finance:fee','费用项目',2,'PriceTag','fee','/finance/fee/index',NULL,1,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',NULL,NULL),(52,5,'finance:bill','账单管理',2,'Document','bill','/finance/bill/index',NULL,2,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',NULL,NULL),(53,5,'finance:payment','缴费记录',2,'Coin','payment','/finance/payment/index',NULL,3,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',NULL,NULL),(211,21,'sys:user:add','新增用户',3,NULL,NULL,NULL,'sys:user:add',1,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',NULL,NULL),(212,21,'sys:user:edit','编辑用户',3,NULL,NULL,NULL,'sys:user:edit',2,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',NULL,NULL),(213,21,'sys:user:delete','删除用户',3,NULL,NULL,NULL,'sys:user:delete',3,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',NULL,NULL),(214,21,'sys:user:reset','重置密码',3,NULL,NULL,NULL,'sys:user:reset',4,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',NULL,NULL),(215,21,'sys:user:assign','分配角色',3,NULL,NULL,NULL,'sys:user:assign',5,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',NULL,NULL),(221,22,'sys:role:add','新增角色',3,NULL,NULL,NULL,'sys:role:add',1,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',NULL,NULL),(222,22,'sys:role:edit','编辑角色',3,NULL,NULL,NULL,'sys:role:edit',2,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',NULL,NULL),(223,22,'sys:role:delete','删除角色',3,NULL,NULL,NULL,'sys:role:delete',3,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',NULL,NULL),(224,22,'sys:role:permission','分配权限',3,NULL,NULL,NULL,'sys:role:permission',4,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',NULL,NULL),(231,23,'sys:permission:add','新增权限',3,NULL,NULL,NULL,'sys:permission:add',1,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',NULL,NULL),(232,23,'sys:permission:edit','编辑权限',3,NULL,NULL,NULL,'sys:permission:edit',2,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',NULL,NULL),(233,23,'sys:permission:delete','删除权限',3,NULL,NULL,NULL,'sys:permission:delete',3,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',NULL,NULL),(235,2,'system:user','用户管理',1,NULL,'/admin/users','/admin/users','system:user:query',1,1,0,0,'2026-07-03 19:11:13','2026-07-03 19:11:13',NULL,NULL),(236,2,'system:role','角色管理',1,NULL,'/admin/roles','/admin/roles','system:role:query',2,1,0,0,'2026-07-03 19:11:13','2026-07-03 19:11:13',NULL,NULL),(237,2,'system:permission','菜单权限',1,NULL,'/admin/permissions','/admin/permissions','system:permission:query',3,1,0,0,'2026-07-03 19:11:13','2026-07-03 19:11:13',NULL,NULL),(238,235,'system:user:add','新增用户',2,NULL,NULL,NULL,'system:user:add',1,0,0,0,'2026-07-03 19:11:13','2026-07-03 19:11:13',NULL,NULL),(239,235,'system:user:edit','编辑用户',2,NULL,NULL,NULL,'system:user:edit',2,0,0,0,'2026-07-03 19:11:13','2026-07-03 19:11:13',NULL,NULL),(240,235,'system:user:delete','删除用户',2,NULL,NULL,NULL,'system:user:delete',3,0,0,0,'2026-07-03 19:11:13','2026-07-03 19:11:13',NULL,NULL),(241,235,'system:user:assign','分配角色',2,NULL,NULL,NULL,'system:user:assign',4,0,0,0,'2026-07-03 19:11:13','2026-07-03 19:11:13',NULL,NULL),(242,236,'system:role:add','新增角色',2,NULL,NULL,NULL,'system:role:add',1,0,0,0,'2026-07-03 19:11:13','2026-07-03 19:11:13',NULL,NULL),(243,236,'system:role:edit','编辑角色',2,NULL,NULL,NULL,'system:role:edit',2,0,0,0,'2026-07-03 19:11:13','2026-07-03 19:11:13',NULL,NULL),(244,236,'system:role:delete','删除角色',2,NULL,NULL,NULL,'system:role:delete',3,0,0,0,'2026-07-03 19:11:13','2026-07-03 19:11:13',NULL,NULL),(245,236,'system:role:assign','分配权限',2,NULL,NULL,NULL,'system:role:assign',4,0,0,0,'2026-07-03 19:11:13','2026-07-03 19:11:13',NULL,NULL),(246,237,'system:permission:add','新增权限',2,NULL,NULL,NULL,'system:permission:add',1,0,0,0,'2026-07-03 19:11:13','2026-07-03 19:11:13',NULL,NULL),(247,237,'system:permission:edit','编辑权限',2,NULL,NULL,NULL,'system:permission:edit',2,0,0,0,'2026-07-03 19:11:13','2026-07-03 19:11:13',NULL,NULL),(248,237,'system:permission:delete','删除权限',2,NULL,NULL,NULL,'system:permission:delete',3,0,0,0,'2026-07-03 19:11:13','2026-07-03 19:11:13',NULL,NULL);

--
-- Dumping data for table `sys_role_info`
--

INSERT INTO `sys_role_info` (`id`, `role_code`, `role_name`, `role_type`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`, `update_user`) VALUES (1,'ROLE_SUPER_ADMIN','超级管理员',0,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(2,'ROLE_ADMIN','管理员',0,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(3,'ROLE_OWNER','业主',1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(4,'ROLE_REPAIR','维修人员',1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL);

--
-- Dumping data for table `sys_role_permission`
--

INSERT INTO `sys_role_permission` (`id`, `role_info_id`, `permission_info_id`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`, `update_user`) VALUES (1,1,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(2,1,2,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(3,1,3,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(4,1,4,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(5,1,5,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(6,1,21,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(7,1,22,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(8,1,23,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(9,1,31,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(10,1,32,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(11,1,33,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(12,1,34,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(13,1,41,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(14,1,42,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(15,1,51,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(16,1,52,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(17,1,53,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(18,1,211,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(19,1,212,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(20,1,213,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(21,1,214,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(22,1,215,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(23,1,221,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(24,1,222,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(25,1,223,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(26,1,224,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(27,1,231,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(28,1,232,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(29,1,233,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(32,2,1,0,0,'2026-07-03 18:05:13','2026-07-03 18:05:13',NULL,NULL),(33,2,3,0,0,'2026-07-03 18:05:13','2026-07-03 18:05:13',NULL,NULL),(34,2,4,0,0,'2026-07-03 18:05:13','2026-07-03 18:05:13',NULL,NULL),(35,2,5,0,0,'2026-07-03 18:05:13','2026-07-03 18:05:13',NULL,NULL),(36,2,31,0,0,'2026-07-03 18:05:13','2026-07-03 18:05:13',NULL,NULL),(37,2,32,0,0,'2026-07-03 18:05:13','2026-07-03 18:05:13',NULL,NULL),(38,2,33,0,0,'2026-07-03 18:05:13','2026-07-03 18:05:13',NULL,NULL),(39,2,34,0,0,'2026-07-03 18:05:13','2026-07-03 18:05:13',NULL,NULL),(40,2,41,0,0,'2026-07-03 18:05:13','2026-07-03 18:05:13',NULL,NULL),(41,2,42,0,0,'2026-07-03 18:05:13','2026-07-03 18:05:13',NULL,NULL),(42,2,51,0,0,'2026-07-03 18:05:13','2026-07-03 18:05:13',NULL,NULL),(43,2,52,0,0,'2026-07-03 18:05:13','2026-07-03 18:05:13',NULL,NULL),(44,2,53,0,0,'2026-07-03 18:05:13','2026-07-03 18:05:13',NULL,NULL),(48,1,235,0,0,'2026-07-03 19:11:13','2026-07-03 19:11:13',NULL,NULL),(49,1,236,0,0,'2026-07-03 19:11:13','2026-07-03 19:11:13',NULL,NULL),(50,1,237,0,0,'2026-07-03 19:11:13','2026-07-03 19:11:13',NULL,NULL),(51,1,238,0,0,'2026-07-03 19:11:13','2026-07-03 19:11:13',NULL,NULL),(52,1,239,0,0,'2026-07-03 19:11:13','2026-07-03 19:11:13',NULL,NULL),(53,1,240,0,0,'2026-07-03 19:11:13','2026-07-03 19:11:13',NULL,NULL),(54,1,241,0,0,'2026-07-03 19:11:13','2026-07-03 19:11:13',NULL,NULL),(55,1,242,0,0,'2026-07-03 19:11:13','2026-07-03 19:11:13',NULL,NULL),(56,1,243,0,0,'2026-07-03 19:11:13','2026-07-03 19:11:13',NULL,NULL),(57,1,244,0,0,'2026-07-03 19:11:13','2026-07-03 19:11:13',NULL,NULL),(58,1,245,0,0,'2026-07-03 19:11:13','2026-07-03 19:11:13',NULL,NULL),(59,1,246,0,0,'2026-07-03 19:11:13','2026-07-03 19:11:13',NULL,NULL),(60,1,247,0,0,'2026-07-03 19:11:13','2026-07-03 19:11:13',NULL,NULL),(61,1,248,0,0,'2026-07-03 19:11:13','2026-07-03 19:11:13',NULL,NULL);

--
-- Dumping data for table `sys_user_info`
--

INSERT INTO `sys_user_info` (`id`, `user_name`, `password`, `full_name`, `phone_number`, `email`, `avatar_address`, `status`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`, `update_user`) VALUES (1,'admin','$2a$10$Lc5eZdvbgytyznkPrWhNgetgG7MJMHc1dhanc89dkgia7CidJX9tC','超级管理员',NULL,NULL,NULL,1,0,0,'2026-07-03 13:45:27','2026-07-03 16:45:33',1,NULL),(2,'testowner','$2a$10$JthzrdRXdG6M98JdveWtPOxX4R/eAwkZJ/anLIcx6Je3xSazHqMuO','Test Owner','13800138000',NULL,NULL,1,0,0,'2026-07-03 16:47:49','2026-07-03 16:47:49',NULL,NULL),(3,'123456','$2a$10$qRXPRXb2r/rV.W4yzKJJZ.OYbr.DuSsZAoo3JzytwtmwZHYyKtLni','张三',NULL,NULL,NULL,1,0,0,'2026-07-03 17:37:16','2026-07-03 17:37:16',NULL,NULL),(4,'property','$2a$10$Lc5eZdvbgytyznkPrWhNgetgG7MJMHc1dhanc89dkgia7CidJX9tC','物业管理员','13800000001',NULL,NULL,1,0,0,'2026-07-03 18:05:41','2026-07-03 18:08:24',NULL,NULL);

--
-- Dumping data for table `sys_user_role`
--

INSERT INTO `sys_user_role` (`id`, `user_info_id`, `role_info_id`, `is_deleted`, `version`, `create_time`, `update_time`, `create_user`, `update_user`) VALUES (1,1,1,0,0,'2026-07-03 13:45:27','2026-07-03 13:45:27',1,NULL),(2,2,3,0,0,'2026-07-03 16:47:49','2026-07-03 16:47:49',NULL,NULL),(3,3,3,0,0,'2026-07-03 17:37:16','2026-07-03 17:37:16',NULL,NULL),(4,4,2,0,0,'2026-07-03 18:05:41','2026-07-03 18:05:41',NULL,NULL);
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-04  9:30:02
