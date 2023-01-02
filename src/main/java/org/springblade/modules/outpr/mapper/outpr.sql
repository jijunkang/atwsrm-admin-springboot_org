INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016590689886209', 0, 'outpr', 'outpr', 'menu', '/outpr/outpr', NULL, 1, 1, 0, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016590689886210', '1214016590689886209', 'outpr_add', '新增', 'add', '/outpr/outpr/add', 'plus', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016590689886210', '1214016590689886209', 'outpr_add', '导出', 'export', '/outpr/outpr/export', 'export', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016590689886211', '1214016590689886209', 'outpr_edit', '修改', 'edit', '/outpr/outpr/edit', 'form', 2, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016590689886212', '1214016590689886209', 'outpr_delete', '删除', 'delete', '/api/outpr/outpr/remove', 'delete', 3, 2, 3, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016590689886213', '1214016590689886209', 'outpr_view', '查看', 'view', '/outpr/outpr/view', 'file-text', 4, 2, 2, 1, NULL, 0);