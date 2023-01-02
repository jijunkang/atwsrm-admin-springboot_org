INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016636730761222', 0, 'outpr', 'outpritemprocess', 'menu', '/outpr/outpritemprocess', NULL, 1, 1, 0, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016636730761223', '1214016636730761222', 'outpritemprocess_add', '新增', 'add', '/outpr/outpritemprocess/add', 'plus', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016636730761223', '1214016636730761222', 'outpritemprocess_add', '导出', 'export', '/outpr/outpritemprocess/export', 'export', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016636730761224', '1214016636730761222', 'outpritemprocess_edit', '修改', 'edit', '/outpr/outpritemprocess/edit', 'form', 2, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016636730761225', '1214016636730761222', 'outpritemprocess_delete', '删除', 'delete', '/api/outpr/outpritemprocess/remove', 'delete', 3, 2, 3, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016636730761226', '1214016636730761222', 'outpritemprocess_view', '查看', 'view', '/outpr/outpritemprocess/view', 'file-text', 4, 2, 2, 1, NULL, 0);