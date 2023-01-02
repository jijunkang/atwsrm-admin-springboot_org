INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946671611930', 0, 'mathmodel', 'mmInMargin', 'menu', '/mathmodel/mmInMargin', NULL, 1, 1, 0, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946671611931', '1288679946671611930', 'mmInMargin_add', '新增', 'add', '/mathmodel/mmInMargin/add', 'plus', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946671611932', '1288679946671611930', 'mmInMargin_edit', '修改', 'edit', '/mathmodel/mmInMargin/edit', 'form', 2, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946671611933', '1288679946671611930', 'mmInMargin_delete', '删除', 'delete', '/api/mathmodel/mmInMargin/remove', 'delete', 3, 2, 3, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946671611934', '1288679946671611930', 'mmInMargin_view', '查看', 'view', '/mathmodel/mmInMargin/view', 'file-text', 4, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946671611934', '1288679946671611930', 'mmInMargin_add', '导出', 'export', '/mathmodel/mmInMargin/export', 'export', 1, 2, 1, 1, NULL, 0);