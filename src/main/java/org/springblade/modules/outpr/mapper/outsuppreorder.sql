INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016714296025090', 0, 'outpr', 'outsuppreorder', 'menu', '/outpr/outsuppreorder', NULL, 1, 1, 0, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016714296025091', '1214016714296025090', 'outsuppreorder_add', '新增', 'add', '/outpr/outsuppreorder/add', 'plus', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016714296025091', '1214016714296025090', 'outsuppreorder_add', '导出', 'export', '/outpr/outsuppreorder/export', 'export', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016714296025092', '1214016714296025090', 'outsuppreorder_edit', '修改', 'edit', '/outpr/outsuppreorder/edit', 'form', 2, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016714296025093', '1214016714296025090', 'outsuppreorder_delete', '删除', 'delete', '/api/outpr/outsuppreorder/remove', 'delete', 3, 2, 3, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1214016714296025094', '1214016714296025090', 'outsuppreorder_view', '查看', 'view', '/outpr/outsuppreorder/view', 'file-text', 4, 2, 2, 1, NULL, 0);