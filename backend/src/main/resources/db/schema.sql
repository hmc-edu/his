-- 杭州医学院门诊 HIS 系统 数据库结构
-- 库名: his (字符集 utf8mb4)
-- 执行前请先: CREATE DATABASE his DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE his;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- sys_user 系统用户
-- ----------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    username    VARCHAR(50)  NOT NULL COMMENT '登录名',
    password    VARCHAR(100) NOT NULL COMMENT 'BCrypt 密码',
    real_name   VARCHAR(50)  NOT NULL COMMENT '真实姓名',
    role        VARCHAR(20)  NOT NULL COMMENT 'ADMIN/RECEPTION/DOCTOR',
    enabled     TINYINT(1)   NOT NULL DEFAULT 1,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '系统用户';

-- ----------------------------
-- department 科室
-- ----------------------------
DROP TABLE IF EXISTS department;
CREATE TABLE department (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    code        VARCHAR(20)  NOT NULL COMMENT '科室编码',
    name        VARCHAR(50)  NOT NULL COMMENT '科室名称',
    sort        INT          NOT NULL DEFAULT 0 COMMENT '排序',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_dept_code (code)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '科室';

-- ----------------------------
-- doctor 医生
-- ----------------------------
DROP TABLE IF EXISTS doctor;
CREATE TABLE doctor (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    user_id     BIGINT       NULL COMMENT '关联系统用户',
    dept_id     BIGINT       NOT NULL COMMENT '所属科室',
    name        VARCHAR(50)  NOT NULL COMMENT '医生姓名',
    title       VARCHAR(50)  NULL COMMENT '职称',
    reg_fee     DECIMAL(10,2) NOT NULL DEFAULT 10.00 COMMENT '挂号费',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_doctor_dept (dept_id),
    KEY idx_doctor_user (user_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '医生';

-- ----------------------------
-- drug 药品目录
-- ----------------------------
DROP TABLE IF EXISTS drug;
CREATE TABLE drug (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    code        VARCHAR(30)  NOT NULL COMMENT '药品编码',
    name        VARCHAR(100) NOT NULL COMMENT '药品名称',
    spec        VARCHAR(100) NULL COMMENT '规格',
    unit        VARCHAR(20)  NOT NULL DEFAULT '盒' COMMENT '单位',
    price       DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '单价',
    stock       INT          NOT NULL DEFAULT 0 COMMENT '库存',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_drug_code (code)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '药品目录';

-- ----------------------------
-- patient 患者档案
-- ----------------------------
DROP TABLE IF EXISTS patient;
CREATE TABLE patient (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(50)  NOT NULL COMMENT '姓名',
    gender      VARCHAR(10)  NOT NULL DEFAULT '未知' COMMENT '性别 男/女/未知',
    birthday    DATE         NULL,
    phone       VARCHAR(20)  NULL,
    id_card     VARCHAR(30)  NULL COMMENT '身份证号',
    address     VARCHAR(200) NULL,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_patient_id_card (id_card),
    KEY idx_patient_name (name),
    KEY idx_patient_phone (phone)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '患者档案';

-- ----------------------------
-- registration 挂号单
-- ----------------------------
DROP TABLE IF EXISTS registration;
CREATE TABLE registration (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    reg_no      VARCHAR(30)  NOT NULL COMMENT '挂号编号',
    patient_id  BIGINT       NOT NULL,
    doctor_id   BIGINT       NOT NULL,
    dept_id     BIGINT       NOT NULL,
    reg_date    DATE         NOT NULL,
    reg_fee     DECIMAL(10,2) NOT NULL,
    status      VARCHAR(20)  NOT NULL DEFAULT 'WAITING' COMMENT 'WAITING/VISITING/DONE/CANCELLED',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_reg_no (reg_no),
    KEY idx_reg_patient (patient_id),
    KEY idx_reg_doctor_status (doctor_id, status),
    KEY idx_reg_date (reg_date)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '挂号单';

-- ----------------------------
-- visit 就诊/电子病历
-- ----------------------------
DROP TABLE IF EXISTS visit;
CREATE TABLE visit (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    registration_id   BIGINT       NOT NULL,
    chief_complaint   VARCHAR(500) NULL COMMENT '主诉',
    present_illness   VARCHAR(1000) NULL COMMENT '现病史',
    diagnosis         VARCHAR(500) NULL COMMENT '诊断',
    doctor_advice     VARCHAR(1000) NULL COMMENT '医嘱',
    visited_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_visit_reg (registration_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '就诊/电子病历';

-- ----------------------------
-- prescription 处方
-- ----------------------------
DROP TABLE IF EXISTS prescription;
CREATE TABLE prescription (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    visit_id        BIGINT       NOT NULL,
    prescribed_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount    DECIMAL(10,2) NOT NULL DEFAULT 0,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_prescription_visit (visit_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '处方';

-- ----------------------------
-- prescription_item 处方明细
-- ----------------------------
DROP TABLE IF EXISTS prescription_item;
CREATE TABLE prescription_item (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    prescription_id BIGINT       NOT NULL,
    drug_id         BIGINT       NOT NULL,
    dosage          VARCHAR(50)  NULL COMMENT '剂量，如 0.25g',
    frequency       VARCHAR(50)  NULL COMMENT '频次，如 一日三次',
    days            INT          NOT NULL DEFAULT 1,
    qty             INT          NOT NULL DEFAULT 1,
    unit_price      DECIMAL(10,2) NOT NULL,
    subtotal        DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_pi_prescription (prescription_id),
    KEY idx_pi_drug (drug_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '处方明细';

SET FOREIGN_KEY_CHECKS = 1;
