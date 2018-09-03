
-- ----------------------------
-- Table structure for `admin_punish`
-- ----------------------------
DROP TABLE IF EXISTS `admin_punish`;
CREATE TABLE `admin_punish` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '本条记录创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '本条记录最后更新时间',
  `source` varchar(100) NOT NULL COMMENT '数据来源',
  `subject` varchar(100) NOT NULL COMMENT '主题',
  `unique_key` varchar(767) NOT NULL COMMENT '唯一性标识(同一数据来源的同一主题内唯一)',
  `url` varchar(255) NOT NULL COMMENT 'url',
  `object_type` varchar(2) NOT NULL COMMENT '主体类型: 01-企业 02-个人',
  `enterprise_name` varchar(100) DEFAULT NULL COMMENT '企业名称',
  `enterprise_code1` varchar(30) DEFAULT NULL COMMENT '统一社会信用代码',
  `enterprise_code2` varchar(30) DEFAULT NULL COMMENT '营业执照注册号',
  `enterprise_code3` varchar(30) DEFAULT NULL COMMENT '组织机构代码',
  `enterprise_code4` varchar(30) DEFAULT NULL COMMENT '税务登记号',
  `person_name` varchar(100) DEFAULT NULL COMMENT '法定代表人|负责人姓名',
  `person_id` varchar(30) DEFAULT NULL COMMENT '法定代表人身份证号|负责人身份证号',
  `punish_type` varchar(100) DEFAULT NULL COMMENT '处罚类型',
  `punish_reason` text COMMENT '处罚事由',
  `punish_according` text COMMENT '处罚依据',
  `punish_result` text COMMENT '处罚结果',
  `judge_no` varchar(100) DEFAULT NULL COMMENT '执行文号',
  `judge_date` varchar(30) DEFAULT NULL COMMENT '执行时间',
  `judge_auth` varchar(100) DEFAULT NULL COMMENT '判决机关',
  `publish_date` varchar(30) DEFAULT NULL COMMENT '发布日期',
  `status` varchar(20) DEFAULT NULL COMMENT '当前状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `source` (`source`,`subject`,`unique_key`),
  KEY `admin_punish_ix2` (`unique_key`)
) ENGINE=InnoDB AUTO_INCREMENT=213864 DEFAULT CHARSET=utf8 COMMENT='行政处罚';

-- ----------------------------
-- Records of admin_punish
-- ----------------------------

-- ----------------------------
-- Table structure for `custom_ent`
-- ----------------------------
DROP TABLE IF EXISTS `custom_ent`;
CREATE TABLE `custom_ent` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '本条记录创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '本条记录最后更新时间',
  `url` varchar(1023) NOT NULL COMMENT 'url',
  `source` varchar(100) NOT NULL COMMENT '数据来源{中国海关总署}',
  `credit_level` varchar(100) NOT NULL COMMENT '信用等级',
  `list_name` varchar(100) NOT NULL COMMENT '所属名录{异常企业名录，失信企业名录}',
  `custom_name` varchar(100) NOT NULL COMMENT '注册海关',
  `enterprise_name` varchar(100) DEFAULT NULL COMMENT '企业名称',
  `enterprise_code1` varchar(30) DEFAULT NULL COMMENT '统一社会信用代码',
  `enterprise_code2` varchar(30) DEFAULT NULL COMMENT '营业执照注册号',
  `enterprise_code3` varchar(30) DEFAULT NULL COMMENT '组织机构代码',
  `start_date` date DEFAULT NULL COMMENT '适用信用等级时间、移入名录时间',
  `list_reason` date DEFAULT NULL COMMENT '移入名录原因',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='海关企业名录（失信，异常）';

-- ----------------------------
-- Records of custom_ent
-- ----------------------------

-- ----------------------------
-- Table structure for `discredit_blacklist`
-- ----------------------------
DROP TABLE IF EXISTS `discredit_blacklist`;
CREATE TABLE `discredit_blacklist` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '本条记录创建时间',
  `updated_at` timestamp NULL DEFAULT NULL COMMENT '本条记录最后更新时间',
  `source` varchar(100) NOT NULL COMMENT '数据来源',
  `subject` varchar(100) NOT NULL COMMENT '主题',
  `unique_key` varchar(767) NOT NULL COMMENT '唯一性标识(同一数据来源的同一主题内唯一)',
  `url` varchar(255) NOT NULL COMMENT 'url',
  `object_type` varchar(2) NOT NULL COMMENT '主体类型: 01-企业 02-个人',
  `enterprise_name` varchar(100) DEFAULT NULL COMMENT '企业名称',
  `enterprise_code1` varchar(30) DEFAULT NULL COMMENT '统一社会信用代码',
  `enterprise_code2` varchar(30) DEFAULT NULL COMMENT '营业执照注册号',
  `enterprise_code3` varchar(30) DEFAULT NULL COMMENT '组织机构代码',
  `enterprise_code4` varchar(30) DEFAULT NULL COMMENT '税务登记号',
  `person_name` varchar(100) DEFAULT NULL COMMENT '法定代表人|负责人姓名',
  `person_id` varchar(30) DEFAULT NULL COMMENT '法定代表人身份证号|负责人身份证号',
  `discredit_type` varchar(100) DEFAULT NULL COMMENT '失信类型',
  `discredit_action` varchar(2048) DEFAULT NULL COMMENT '失信行为',
  `punish_reason` varchar(2048) DEFAULT NULL COMMENT '列入原因',
  `punish_result` varchar(1024) DEFAULT NULL COMMENT '处罚结果',
  `judge_no` varchar(100) DEFAULT NULL COMMENT '执行文号',
  `judge_date` varchar(30) DEFAULT NULL COMMENT '执行时间',
  `judge_auth` varchar(100) DEFAULT NULL COMMENT '判决机关',
  `publish_date` varchar(30) DEFAULT NULL COMMENT '发布日期',
  `status` varchar(20) DEFAULT NULL COMMENT '当前状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `source` (`source`,`subject`,`unique_key`)
) ENGINE=InnoDB AUTO_INCREMENT=56051 DEFAULT CHARSET=utf8 COMMENT='失信黑名单';

-- ----------------------------
-- Records of discredit_blacklist
-- ----------------------------

-- ----------------------------
-- Table structure for `finance_monitor_punish`
-- ----------------------------
DROP TABLE IF EXISTS `finance_monitor_punish`;
CREATE TABLE `finance_monitor_punish` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `PRIMARY_KEY` varchar(1024) NOT NULL COMMENT '业务主键 | punish_no+punish_title+party_institution+punish_date',
  `PUNISH_NO` varchar(255) DEFAULT 'NULL' COMMENT '处罚文号=函号 | 地方证监局、深交所、保监会',
  `PUNISH_TITLE` varchar(255) DEFAULT 'NULL' COMMENT '标题名称=函件标题 | 地方证监局、深交所、保监会、上交所、深交所、证监会',
  `PARTY_INSTITUTION` varchar(1024) DEFAULT 'NULL' COMMENT '当事人（公司）=处罚对象=机构当事人名称=涉及对象=中介机构名称=处分对象 | 全国中小企业股转系统、地方证监局、保监会、深交所、证监会',
  `PARTY_PERSON` varchar(1024) DEFAULT 'NULL' COMMENT '当事人（个人）=处罚对象=当事人集合(当事人姓名)=涉及对象=处分对象 | 全国中小企业股转系统、地方证监局、保监会、上交所、深交所',
  `PARTY_PERSON_ID` varchar(1024) DEFAULT 'NULL' COMMENT '当事人集合(当事人身份证号)|保监会',
  `PARTY_PERSON_TITLE` varchar(1024) DEFAULT 'NULL' COMMENT '当事人集合(当事人职务) | 保监会',
  `PARTY_PERSON_DOMI` varchar(1024) DEFAULT 'NULL' COMMENT '当事人集合(当事人住址)-机构所在地（保险公司分公司）|保监会',
  `UNICODE` varchar(1024) DEFAULT 'NULL' COMMENT '一码通代码（当事人为个人）| 全国中小企业股转系统',
  `PARTY_CATEGORY` varchar(255) DEFAULT 'NULL' COMMENT '处分对象类型|深交所',
  `DOMICILE` varchar(1024) DEFAULT 'NULL' COMMENT '住所地=机构当事人住所|全国中小企业股转系统',
  `LEGAL_REPRESENTATIVE` varchar(255) DEFAULT 'NULL' COMMENT '法定代表人=机构负责人姓名|全国中小企业股转系统、保监会',
  `PARTY_SUPPLEMENT` varchar(1024) DEFAULT 'NULL' COMMENT '当事人补充情况|全国中小企业股转系统',
  `COMPANY_FULL_NAME` varchar(1024) DEFAULT 'NULL' COMMENT '公司全称|深交所、全国中小企业股转系统',
  `INTERMEDIARY_CATEGORY` varchar(255) DEFAULT 'NULL' COMMENT '中介机构类别|深交所',
  `COMPANY_SHORT_NAME` varchar(50) DEFAULT 'NULL' COMMENT '公司简称=涉及公司简称|深交所',
  `COMPANY_CODE` varchar(255) DEFAULT 'NULL' COMMENT '公司代码=涉及公司代码|深交所',
  `STOCK_CODE` varchar(30) DEFAULT 'NULL' COMMENT '证券代码|上交所',
  `STOCK_SHORT_NAME` varchar(255) DEFAULT 'NULL' COMMENT '证券简称|上交所',
  `PUNISH_CATEGORY` varchar(50) DEFAULT 'NULL' COMMENT '处分类别|深交所',
  `IRREGULARITIES` text COMMENT '违规情况=处理事由|全国中小企业股转系统、上交所、证监会',
  `RELATED_LAW` text COMMENT '相关法规=违反条例|全国中小企业股转系统、证监会',
  `RELATED_BOND` varchar(1024) DEFAULT 'NULL' COMMENT '涉及债券|深交所',
  `PUNISH_RESULT` varchar(1024) DEFAULT 'NULL' COMMENT '处罚结果|全国中小企业股转系统、证监会',
  `PUNISH_RESULT_SUPPLEMENT` text COMMENT '处罚结果补充情况|全国中小企业股转系统',
  `PUNISH_INSTITUTION` varchar(255) DEFAULT 'NULL' COMMENT '处罚机关=处罚机构|保监会、证监会',
  `PUNISH_DATE` varchar(50) DEFAULT 'NULL' COMMENT '处罚日期=处理日期=处分日期|地方证监局、保监会、上交所、深交所、证监会',
  `REMEDIAL_LIMIT_TIME` varchar(50) DEFAULT 'NULL' COMMENT '整改时限|证监会',
  `PUBLISHER` varchar(255) DEFAULT 'NULL' COMMENT '发布机构|地方证监局、保监会',
  `PUBLISH_DATE` varchar(50) DEFAULT 'NULL' COMMENT '发布日期=发函日期|地方证监局、保监会',
  `LIST_CLASSIFICATION` varchar(1024) DEFAULT 'NULL' COMMENT '监管类型|地方证监局',
  `SUPERVISION_TYPE` varchar(255) DEFAULT 'NULL' COMMENT '名单分类|上交所',
  `DETAILS` text COMMENT '详情=行政处罚详情=全文|地方证监局、保监会、深交所',
  `SOURCE` varchar(255) DEFAULT 'NULL' COMMENT '来源（全国中小企业股转系统、地方证监局、保监会、上交所、深交所、证监会）',
  `URL` varchar(255) DEFAULT 'NULL' COMMENT '来源url',
  `OBJECT` varchar(255) DEFAULT 'NULL' COMMENT '主题（全国中小企业股转系统-监管公告、行政处罚决定、公司监管、债券监管、交易监管、上市公司处罚与处分记录、中介机构处罚与处分记录',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=44160 DEFAULT CHARSET=utf8 COMMENT='爬网数据表';

-- ----------------------------
-- Records of finance_monitor_punish
-- ----------------------------

-- ----------------------------
-- Table structure for `production_quality`
-- ----------------------------
DROP TABLE IF EXISTS `production_quality`;
CREATE TABLE `production_quality` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '本条记录创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '本条记录最后更新时间',
  `url` varchar(1023) NOT NULL COMMENT 'url',
  `source` varchar(100) NOT NULL COMMENT '数据来源{工信部，商务部}',
  `enterprise_name` varchar(100) DEFAULT NULL COMMENT '企业名称',
  `enterprise_code1` varchar(30) DEFAULT NULL COMMENT '统一社会信用代码',
  `enterprise_code2` varchar(30) DEFAULT NULL COMMENT '营业执照注册号',
  `enterprise_code3` varchar(30) DEFAULT NULL COMMENT '组织机构代码',
  `oper_production` varchar(100) DEFAULT NULL COMMENT '检查产品',
  `oper_result` varchar(1024) DEFAULT NULL COMMENT '检查结果',
  `oper_org` varchar(100) DEFAULT NULL COMMENT '检查机关',
  `publish_date` varchar(30) DEFAULT NULL COMMENT '发布日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1209 DEFAULT CHARSET=utf8 COMMENT='质检曝光数据(工信部，商务部)';

-- ----------------------------
-- Records of production_quality
-- ----------------------------

-- ----------------------------
-- Table structure for `proxypool`
-- ----------------------------
DROP TABLE IF EXISTS `proxypool`;
CREATE TABLE `proxypool` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `IPAddress` varchar(20) DEFAULT NULL,
  `IPPort` varchar(10) DEFAULT NULL,
  `serverAddress` varchar(20) DEFAULT NULL,
  `IPType` varchar(10) DEFAULT NULL,
  `IPSpeed` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5205 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of proxypool
-- ----------------------------

-- ----------------------------
-- Table structure for `scrapy_data`
-- ----------------------------
DROP TABLE IF EXISTS `scrapy_data`;
CREATE TABLE `scrapy_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `url` varchar(1023) NOT NULL COMMENT 'url',
  `source` varchar(100) NOT NULL COMMENT '数据来源{工信部，商务部，海关}',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '本条记录创建时间',
  `hash_key` varchar(1024) NOT NULL COMMENT 'url的md5结果（如有附件，则保存在此目录中）',
  `attachment_type` varchar(40) DEFAULT NULL COMMENT '附件类型（pdf,doc,xls,jpg,tiff...）',
  `html` mediumtext COMMENT '正文html',
  `text` mediumtext COMMENT '正文text，提取到的正文',
  `fields` text COMMENT '提取到的关键数据',
  PRIMARY KEY (`id`),
  KEY `url` (`url`)
) ENGINE=InnoDB AUTO_INCREMENT=29314 DEFAULT CHARSET=utf8 COMMENT='爬网原始数据及中间数据';

-- ----------------------------
-- Records of scrapy_data
-- ----------------------------
