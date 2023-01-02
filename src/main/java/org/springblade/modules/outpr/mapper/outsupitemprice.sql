INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016674311725063', 0, 'outpr', 'outsupitemprice', 'menu', '/outpr/outsupitemprice', NULL, 1, 1, 0, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016674311725064', '1214016674311725063', 'outsupitemprice_add', '新增', 'add', '/outpr/outsupitemprice/add', 'plus', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016674311725064', '1214016674311725063', 'outsupitemprice_add', '导出', 'export', '/outpr/outsupitemprice/export', 'export', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016674311725065', '1214016674311725063', 'outsupitemprice_edit', '修改', 'edit', '/outpr/outsupitemprice/edit', 'form', 2, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016674311725066', '1214016674311725063', 'outsupitemprice_delete', '删除', 'delete', '/api/outpr/outsupitemprice/remove', 'delete', 3, 2, 3, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016674311725067', '1214016674311725063', 'outsupitemprice_view', '查看', 'view', '/outpr/outsupitemprice/view', 'file-text', 4, 2, 2, 1, NULL, 0);