INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016691390930945', 0, 'outpr', 'outsupitemprocessprice', 'menu', '/outpr/outsupitemprocessprice', NULL, 1, 1, 0, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016691390930946', '1214016691390930945', 'outsupitemprocessprice_add', '新增', 'add', '/outpr/outsupitemprocessprice/add', 'plus', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016691390930946', '1214016691390930945', 'outsupitemprocessprice_add', '导出', 'export', '/outpr/outsupitemprocessprice/export', 'export', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016691390930947', '1214016691390930945', 'outsupitemprocessprice_edit', '修改', 'edit', '/outpr/outsupitemprocessprice/edit', 'form', 2, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016691390930948', '1214016691390930945', 'outsupitemprocessprice_delete', '删除', 'delete', '/api/outpr/outsupitemprocessprice/remove', 'delete', 3, 2, 3, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016691390930949', '1214016691390930945', 'outsupitemprocessprice_view', '查看', 'view', '/outpr/outsupitemprocessprice/view', 'file-text', 4, 2, 2, 1, NULL, 0);