INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946822606884', 0, 'mathmodel', 'mmCoef', 'menu', '/mathmodel/mmCoef', NULL, 1, 1, 0, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946822606885', '1288679946822606884', 'mmCoef_add', '新增', 'add', '/mathmodel/mmCoef/add', 'plus', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946822606886', '1288679946822606884', 'mmCoef_edit', '修改', 'edit', '/mathmodel/mmCoef/edit', 'form', 2, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946822606887', '1288679946822606884', 'mmCoef_delete', '删除', 'delete', '/api/mathmodel/mmCoef/remove', 'delete', 3, 2, 3, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946822606888', '1288679946822606884', 'mmCoef_view', '查看', 'view', '/mathmodel/mmCoef/view', 'file-text', 4, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946822606888', '1288679946822606884', 'mmCoef_add', '导出', 'export', '/mathmodel/mmCoef/export', 'export', 1, 2, 1, 1, NULL, 0);