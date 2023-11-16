/*
 Navicat Premium Data Transfer

 Source Server         : 10.35.30.105
 Source Server Type    : PostgreSQL
 Source Server Version : 130006
 Source Host           : 10.35.30.105:5433
 Source Catalog        : asset218
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 130006
 File Encoding         : 65001

 Date: 16/10/2023 10:53:04
*/


-- ----------------------------
-- Table structure for rest_group
-- ----------------------------
DROP TABLE IF EXISTS "public"."rest_group";
CREATE TABLE "public"."rest_group" (
                                       "id" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
                                       "name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
                                       "description" varchar(255) COLLATE "pg_catalog"."default"
)
;

-- ----------------------------
-- Table structure for rest_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."rest_info";
CREATE TABLE "public"."rest_info" (
                                      "code" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
                                      "group_id" varchar(255) COLLATE "pg_catalog"."default",
                                      "name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
                                      "ip" varchar(255) COLLATE "pg_catalog"."default",
                                      "port" varchar(255) COLLATE "pg_catalog"."default",
                                      "suffix_url" varchar(255) COLLATE "pg_catalog"."default",
                                      "rest_type" varchar(255) COLLATE "pg_catalog"."default",
                                      "content_type" varchar(255) COLLATE "pg_catalog"."default",
                                      "is_https" int2,
                                      "params_fix" text COLLATE "pg_catalog"."default",
                                      "param_template" text COLLATE "pg_catalog"."default",
                                      "header" text COLLATE "pg_catalog"."default",
                                      "auth" text COLLATE "pg_catalog"."default",
                                      "before_handler" text COLLATE "pg_catalog"."default",
                                      "after_handler" text COLLATE "pg_catalog"."default",
                                      "description" varchar(255) COLLATE "pg_catalog"."default",
                                      "sign" text COLLATE "pg_catalog"."default"
)
;

-- ----------------------------
-- Primary Key structure for table rest_group
-- ----------------------------
ALTER TABLE "public"."rest_group" ADD CONSTRAINT "group_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table rest_info
-- ----------------------------
CREATE UNIQUE INDEX "code_copy1" ON "public"."rest_info" USING btree (
    "code" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key structure for table rest_info
-- ----------------------------
ALTER TABLE "public"."rest_info" ADD CONSTRAINT "rest_info_copy1_pkey" PRIMARY KEY ("code");
