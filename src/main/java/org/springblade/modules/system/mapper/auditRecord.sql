INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1272342017057742854', 0, 'system', 'auditRecord', 'menu', '/system/auditRecord', NULL, 1, 1, 0, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1272342017057742855', '1272342017057742854', 'auditRecord_add', '新增', 'add', '/system/auditRecord/add', 'plus', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1272342017057742856', '1272342017057742854', 'auditRecord_edit', '修改', 'edit', '/system/auditRecord/edit', 'form', 2, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1272342017057742857', '1272342017057742854', 'auditRecord_delete', '删除', 'delete', '/api/system/auditRecord/remove', 'delete', 3, 2, 3, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1272342017057742858', '1272342017057742854', 'auditRecord_view', '查看', 'view', '/system/auditRecord/view', 'file-text', 4, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1272342017057742858', '1272342017057742854', 'auditRecord_add', '导出', 'export', '/system/auditRecord/export', 'export', 1, 2, 1, 1, NULL, 0);