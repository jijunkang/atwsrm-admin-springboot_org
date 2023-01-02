INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946671611910', 0, 'mathmodel', 'mmDensity', 'menu', '/mathmodel/mmDensity', NULL, 1, 1, 0, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946671611911', '1288679946671611910', 'mmDensity_add', '新增', 'add', '/mathmodel/mmDensity/add', 'plus', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946671611912', '1288679946671611910', 'mmDensity_edit', '修改', 'edit', '/mathmodel/mmDensity/edit', 'form', 2, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946671611913', '1288679946671611910', 'mmDensity_delete', '删除', 'delete', '/api/mathmodel/mmDensity/remove', 'delete', 3, 2, 3, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946671611914', '1288679946671611910', 'mmDensity_view', '查看', 'view', '/mathmodel/mmDensity/view', 'file-text', 4, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946671611914', '1288679946671611910', 'mmDensity_add', '导出', 'export', '/mathmodel/mmDensity/export', 'export', 1, 2, 1, 1, NULL, 0);