CREATE TABLE pg_adm_authority (
  code               VARCHAR(50) DEFAULT NULL :: CHARACTER VARYING NOT NULL,
  name               VARCHAR(50)  DEFAULT NULL :: CHARACTER VARYING,
  description        VARCHAR(50)  DEFAULT NULL :: CHARACTER VARYING,
  created_date       DATE         DEFAULT NULL,
  last_modified_date DATE         DEFAULT NULL,
  created_by         VARCHAR(255) DEFAULT NULL :: CHARACTER VARYING,
  last_modified_by   VARCHAR      DEFAULT NULL :: CHARACTER VARYING
);
COMMENT ON TABLE pg_adm_authority IS '用户权限';


CREATE SEQUENCE pg_adm_dictionary_id_seq
  INCREMENT 1
  START 1 /*这里根据需求改变*/
  MINVALUE 1
  MAXVALUE 9223372036854775807
  CACHE 1;

CREATE TABLE pg_adm_dictionary (
  id                 INT4 NOT NULL DEFAULT nextval('pg_adm_dictionary_id_seq' :: REGCLASS),
  name               VARCHAR(255)  DEFAULT NULL :: CHARACTER VARYING,
  created_by         VARCHAR(255)  DEFAULT NULL :: CHARACTER VARYING,
  created_date       TIMESTAMP(0)  DEFAULT NULL,
  last_modified_by   VARCHAR(255)  DEFAULT NULL :: CHARACTER VARYING,
  last_modified_date TIMESTAMP(0)  DEFAULT NULL,
  is_disabled        BOOL NOT NULL DEFAULT NULL,
  is_locked          BOOL NOT NULL DEFAULT NULL,
  code               VARCHAR(255)  DEFAULT NULL :: CHARACTER VARYING,
  description        VARCHAR(255)  DEFAULT NULL :: CHARACTER VARYING,
  is_deleted         BOOL NOT NULL DEFAULT NULL
);
ALTER TABLE pg_adm_dictionary
  ADD CONSTRAINT pg_adm_dictionary_pkey PRIMARY KEY ("id");


CREATE SEQUENCE pg_adm_dictionary_item_id_seq
  INCREMENT 1
  START 1 /*这里根据需求改变*/
  MINVALUE 1
  MAXVALUE 9223372036854775807
  CACHE 1;

CREATE TABLE pg_adm_dictionary_item (
  id                 INT4 NOT NULL DEFAULT nextval('pg_adm_dictionary_item_id_seq' :: REGCLASS),
  dic_id             INT4          DEFAULT NULL,
  assoc_id           INT4          DEFAULT NULL,
  code               VARCHAR(255)  DEFAULT NULL :: CHARACTER VARYING,
  name               VARCHAR(255)  DEFAULT NULL :: CHARACTER VARYING,
  created_by         VARCHAR(255)  DEFAULT NULL :: CHARACTER VARYING,
  created_date       TIMESTAMP(0)  DEFAULT NULL,
  last_modified_by   VARCHAR(255)  DEFAULT NULL :: CHARACTER VARYING,
  last_modified_date TIMESTAMP(0)  DEFAULT NULL,
  is_disabled        BOOL NOT NULL DEFAULT NULL,
  is_locked          VARCHAR(255)  DEFAULT NULL :: CHARACTER VARYING,
  description        VARCHAR(255)  DEFAULT NULL :: CHARACTER VARYING,
  is_deleted         BOOL NOT NULL DEFAULT NULL,
  py_code            VARCHAR(255)  DEFAULT NULL :: CHARACTER VARYING
);
ALTER TABLE pg_adm_dictionary_item
  ADD CONSTRAINT pg_adm_dictionary_item_pkey PRIMARY KEY ("id");


CREATE SEQUENCE pg_adm_group_id_seq
  INCREMENT 1
  START 1 /*这里根据需求改变*/
  MINVALUE 1
  MAXVALUE 9223372036854775807
  CACHE 1;

CREATE TABLE pg_adm_group (
  code               VARCHAR(255) DEFAULT NULL :: CHARACTER VARYING NOT NULL,
  name               VARCHAR(255)                                            DEFAULT NULL :: CHARACTER VARYING,
  description        VARCHAR(255)                                            DEFAULT NULL :: CHARACTER VARYING,
  created_by         VARCHAR(255)                                            DEFAULT NULL :: CHARACTER VARYING,
  created_date       DATE                                                    DEFAULT NULL,
  last_modified_by   VARCHAR(255)                                            DEFAULT NULL :: CHARACTER VARYING,
  last_modified_date DATE                                                    DEFAULT NULL,
  id                 INT4                                           NOT NULL DEFAULT nextval(
      'pg_adm_group_id_seq' :: REGCLASS),
  parent_id          INT4                                                    DEFAULT NULL,
  sort               INT2                                                    DEFAULT NULL,
  is_disabled        BOOL                                                    DEFAULT NULL,
  is_deleted         BOOL                                                    DEFAULT NULL
);

CREATE SEQUENCE pg_adm_menu_id_seq
  INCREMENT 1
  START 1 /*这里根据需求改变*/
  MINVALUE 1
  MAXVALUE 9223372036854775807
  CACHE 1;

CREATE TABLE pg_adm_menu (
  id                 INT8                                          NOT NULL DEFAULT nextval(
      'pg_adm_menu_id_seq' :: REGCLASS),
  title              VARCHAR(50) DEFAULT NULL :: CHARACTER VARYING NOT NULL,
  code               VARCHAR(50) DEFAULT NULL :: CHARACTER VARYING NOT NULL,
  icon_cls           VARCHAR(50)                                            DEFAULT NULL :: CHARACTER VARYING,
  href               VARCHAR(100)                                           DEFAULT NULL :: CHARACTER VARYING,
  parameters         VARCHAR(500)                                           DEFAULT NULL :: CHARACTER VARYING,
  description        VARCHAR(200)                                           DEFAULT NULL :: CHARACTER VARYING,
  disabled           BOOL                                          NOT NULL DEFAULT FALSE,
  parent_id          INT8                                          NOT NULL,
  tree_path          VARCHAR(500)                                           DEFAULT NULL :: CHARACTER VARYING,
  tree_level         INT4                                                   DEFAULT NULL,
  sort_no            INT4                                                   DEFAULT NULL,
  created_by         VARCHAR(50) DEFAULT NULL :: CHARACTER VARYING NOT NULL,
  created_date       TIMESTAMP(6)                                           DEFAULT NULL :: TIMESTAMP WITHOUT TIME ZONE,
  last_modified_by   VARCHAR(50) DEFAULT NULL :: CHARACTER VARYING NOT NULL,
  last_modified_date TIMESTAMP(6)                                           DEFAULT NULL :: TIMESTAMP WITHOUT TIME ZONE
);


CREATE TABLE pg_adm_persistent_token (
  token_value VARCHAR(256) DEFAULT NULL :: CHARACTER VARYING NOT NULL,
  token_date  TIMESTAMP(6) DEFAULT NULL :: TIMESTAMP WITHOUT TIME ZONE,
  ip_address  VARCHAR(39)  DEFAULT NULL :: CHARACTER VARYING,
  user_agent  VARCHAR(100) DEFAULT NULL :: CHARACTER VARYING,
  user_login  VARCHAR(50)  DEFAULT NULL :: CHARACTER VARYING
);
COMMENT ON TABLE pg_adm_persistent_token IS '持久token表，用于实现服务端rememberMe';


CREATE TABLE pg_adm_role (
  code               VARCHAR(128) DEFAULT NULL :: CHARACTER VARYING NOT NULL,
  name               VARCHAR(255) DEFAULT NULL :: CHARACTER VARYING NOT NULL,
  description        VARCHAR(512) DEFAULT NULL :: CHARACTER VARYING,
  created_by         VARCHAR(50)  DEFAULT NULL :: CHARACTER VARYING,
  created_date       TIMESTAMP(6) DEFAULT NULL :: TIMESTAMP WITHOUT TIME ZONE,
  last_modified_by   VARCHAR(50)  DEFAULT NULL :: CHARACTER VARYING,
  last_modified_date TIMESTAMP(6) DEFAULT NULL :: TIMESTAMP WITHOUT TIME ZONE
);

CREATE SEQUENCE pg_adm_role_authority_assoc_id_seq
  INCREMENT 1
  START 1 /*这里根据需求改变*/
  MINVALUE 1
  MAXVALUE 9223372036854775807
  CACHE 1;

CREATE TABLE pg_adm_role_authority_assoc (
  id     INT8 NOT NULL DEFAULT nextval('pg_adm_role_authority_assoc_id_seq' :: REGCLASS),
  r_code VARCHAR(64)   DEFAULT NULL :: CHARACTER VARYING,
  a_code VARCHAR(50)   DEFAULT NULL :: CHARACTER VARYING
);

COMMENT ON TABLE pg_adm_role_authority_assoc IS '用户权限关系表';


CREATE TABLE pg_adm_role_menu_assoc (
  id        INT8                                           NOT NULL DEFAULT NULL,
  role_code VARCHAR(128) DEFAULT NULL :: CHARACTER VARYING NOT NULL,
  menu_id   INT8                                           NOT NULL DEFAULT NULL
);


CREATE TABLE pg_adm_user (
  id                 INT8                                           NOT NULL DEFAULT NULL,
  login              VARCHAR(50) DEFAULT NULL :: CHARACTER VARYING  NOT NULL,
  password_hash      VARCHAR(100) DEFAULT NULL :: CHARACTER VARYING NOT NULL,
  first_name         VARCHAR(50)                                             DEFAULT NULL :: CHARACTER VARYING,
  last_name          VARCHAR(50)                                             DEFAULT NULL :: CHARACTER VARYING,
  email              VARCHAR(100)                                            DEFAULT NULL :: CHARACTER VARYING,
  is_activated       BOOL                                           NOT NULL DEFAULT FALSE,
  lang_key           VARCHAR(5)                                              DEFAULT NULL :: CHARACTER VARYING,
  image_url          VARCHAR(256)                                            DEFAULT NULL :: CHARACTER VARYING,
  activation_key     VARCHAR(20)                                             DEFAULT NULL :: CHARACTER VARYING,
  reset_key          VARCHAR(20)                                             DEFAULT NULL :: CHARACTER VARYING,
  reset_date         TIMESTAMP(6)                                            DEFAULT NULL :: TIMESTAMP WITHOUT TIME ZONE,
  created_date       TIMESTAMP(6)                                            DEFAULT NULL :: TIMESTAMP WITHOUT TIME ZONE,
  created_by         VARCHAR(50)                                             DEFAULT NULL :: CHARACTER VARYING,
  last_modified_date TIMESTAMP(6)                                            DEFAULT NULL :: TIMESTAMP WITHOUT TIME ZONE,
  last_modified_by   VARCHAR(50)                                             DEFAULT NULL :: CHARACTER VARYING
);

COMMENT ON TABLE pg_adm_user IS '系统用户';


CREATE SEQUENCE pg_adm_user_group_assoc_id_seq
  INCREMENT 1
  START 1 /*这里根据需求改变*/
  MINVALUE 1
  MAXVALUE 9223372036854775807
  CACHE 1;

CREATE TABLE pg_adm_user_group_assoc (
  id     INT4                                           NOT NULL DEFAULT nextval(
      'pg_adm_user_group_assoc_id_seq' :: REGCLASS),
  uid    INT4                                           NOT NULL DEFAULT NULL,
  g_code VARCHAR(255) DEFAULT NULL :: CHARACTER VARYING NOT NULL
);


CREATE SEQUENCE pg_adm_user_role_assoc_id_seq
  INCREMENT 1
  START 1 /*这里根据需求改变*/
  MINVALUE 1
  MAXVALUE 9223372036854775807
  CACHE 1;

CREATE TABLE pg_adm_user_role_assoc (
  id        INT4                                          NOT NULL DEFAULT nextval(
      'pg_adm_user_role_assoc_id_seq' :: REGCLASS),
  role_code VARCHAR(50) DEFAULT NULL :: CHARACTER VARYING NOT NULL,
  user_id   INT4                                          NOT NULL DEFAULT NULL
);


