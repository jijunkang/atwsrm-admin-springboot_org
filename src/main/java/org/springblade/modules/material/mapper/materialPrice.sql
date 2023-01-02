INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1253648758793113606', 0, 'material', 'materialPrice', 'menu', '/material/materialPrice', NULL, 1, 1, 0, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1253648758793113607', '1253648758793113606', 'materialPrice_add', '新增', 'add', '/material/materialPrice/add', 'plus', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1253648758793113607', '1253648758793113606', 'materialPrice_add', '导出', 'export', '/material/materialPrice/export', 'export', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1253648758793113608', '1253648758793113606', 'materialPrice_edit', '修改', 'edit', '/material/materialPrice/edit', 'form', 2, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1253648758793113609', '1253648758793113606', 'materialPrice_delete', '删除', 'delete', '/api/material/materialPrice/remove', 'delete', 3, 2, 3, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1253648758793113610', '1253648758793113606', 'materialPrice_view', '查看', 'view', '/material/materialPrice/view', 'file-text', 4, 2, 2, 1, NULL, 0);