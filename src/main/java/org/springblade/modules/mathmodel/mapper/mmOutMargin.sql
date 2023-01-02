INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946822606864', 0, 'mathmodel', 'mmOutMargin', 'menu', '/mathmodel/mmOutMargin', NULL, 1, 1, 0, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946822606865', '1288679946822606864', 'mmOutMargin_add', '新增', 'add', '/mathmodel/mmOutMargin/add', 'plus', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946822606866', '1288679946822606864', 'mmOutMargin_edit', '修改', 'edit', '/mathmodel/mmOutMargin/edit', 'form', 2, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946822606867', '1288679946822606864', 'mmOutMargin_delete', '删除', 'delete', '/api/mathmodel/mmOutMargin/remove', 'delete', 3, 2, 3, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946822606868', '1288679946822606864', 'mmOutMargin_view', '查看', 'view', '/mathmodel/mmOutMargin/view', 'file-text', 4, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946822606868', '1288679946822606864', 'mmOutMargin_add', '导出', 'export', '/mathmodel/mmOutMargin/export', 'export', 1, 2, 1, 1, NULL, 0);