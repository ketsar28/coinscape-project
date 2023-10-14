SHOW DATABASES;

CREATE DATABASE final_project_coinscapedb;
DROP DATABASE final_project_coinscapedb;

USE final_project_coinscapedb;

SHOW TABLES;

DESC m_admin;
DESC m_user;
DESC m_role;
DESC m_user_role;
DESC m_user_credential;
DESC m_payment;
DESC m_transaction;
DESC m_detail_transaction;

SHOW CREATE TABLE m_admin;
SHOW CREATE TABLE m_user;
SHOW CREATE TABLE m_user_info;
SHOW CREATE TABLE m_role;
SHOW CREATE TABLE m_user_role;
SHOW CREATE TABLE m_user_credential;
SHOW CREATE TABLE m_payment;
SHOW CREATE TABLE m_transaction;
SHOW CREATE TABLE m_upgrade_request;
SHOW CREATE TABLE m_detail_transaction;


SELECT * FROM m_admin;
SELECT * FROM m_user;
SELECT * FROM m_role;
SELECT * FROM m_user_role;
SELECT * FROM m_user_credential;
SELECT * FROM m_transaction;
SELECT * FROM m_upgrade_request;
SELECT * FROM m_detail_transaction;


DELETE FROM m_admin;
DELETE FROM m_user_role;
DELETE FROM m_role;
DELETE FROM m_user;
DELETE FROM m_user_credential;
DELETE FROM m_payment;
DELETE FROM m_transaction;
DELETE FROM m_detail_transaction;


DROP TABLE m_admin;
DROP TABLE m_user;
DROP TABLE m_role;
DROP TABLE m_user_role;
DROP TABLE m_user_credential;
DROP TABLE m_payment;
DROP TABLE m_transaction;
DROP TABLE m_detail_transaction;
