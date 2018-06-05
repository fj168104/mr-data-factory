# noinspection SqlNoDataSourceInspectionForFile

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `oti_field_library`
-- ----------------------------
DROP TABLE IF EXISTS `FINANCE_MONITOR_PUNISH`;
CREATE TABLE `FINANCE_MONITOR_PUNISH` (
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
  `IRREGULARITIES` TEXT DEFAULT NULL COMMENT '违规情况=处理事由|全国中小企业股转系统、上交所、证监会',
  `RELATED_LAW` TEXT DEFAULT NULL COMMENT '相关法规=违反条例|全国中小企业股转系统、证监会',
  `RELATED_BOND` varchar(1024) DEFAULT 'NULL' COMMENT '涉及债券|深交所',
  `PUNISH_RESULT` varchar(1024) DEFAULT 'NULL' COMMENT '处罚结果|全国中小企业股转系统、证监会',
  `PUNISH_RESULT_SUPPLEMENT` TEXT DEFAULT NULL COMMENT '处罚结果补充情况|全国中小企业股转系统',
  `PUNISH_INSTITUTION` varchar(255) DEFAULT 'NULL' COMMENT '处罚机关=处罚机构|保监会、证监会',
  `PUNISH_DATE` varchar(50) DEFAULT 'NULL' COMMENT '处罚日期=处理日期=处分日期|地方证监局、保监会、上交所、深交所、证监会',
  `REMEDIAL_LIMIT_TIME` varchar(50) DEFAULT 'NULL' COMMENT '整改时限|证监会',
  `PUBLISHER` varchar(255) DEFAULT 'NULL' COMMENT '发布机构|地方证监局、保监会',
  `PUBLISH_DATE` varchar(50) DEFAULT 'NULL' COMMENT '发布日期=发函日期|地方证监局、保监会',
  `LIST_CLASSIFICATION` varchar(1024) DEFAULT 'NULL' COMMENT '监管类型|地方证监局',
  `SUPERVISION_TYPE` varchar(255) DEFAULT 'NULL' COMMENT '名单分类|上交所',
  `DETAILS` TEXT DEFAULT NULL COMMENT '详情=行政处罚详情=全文|地方证监局、保监会、深交所',
  `SOURCE` varchar(255) DEFAULT 'NULL' COMMENT '来源（全国中小企业股转系统、地方证监局、保监会、上交所、深交所、证监会）',
  `URL` varchar(255) DEFAULT 'NULL' COMMENT '来源url',
  `OBJECT` varchar(255) DEFAULT 'NULL' COMMENT '主题（全国中小企业股转系统-监管公告、行政处罚决定、公司监管、债券监管、交易监管、上市公司处罚与处分记录、中介机构处罚与处分记录',
#   `IC_NAME` varchar(255) DEFAULT 'NULL' COMMENT '工商名',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=30000 DEFAULT CHARSET=utf8 COMMENT='爬网数据表';




