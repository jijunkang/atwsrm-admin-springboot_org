INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946822606874', 0, 'mathmodel', 'mmProcessFee', 'menu', '/mathmodel/mmProcessFee', NULL, 1, 1, 0, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946822606875', '1288679946822606874', 'mmProcessFee_add', '新增', 'add', '/mathmodel/mmProcessFee/add', 'plus', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946822606876', '1288679946822606874', 'mmProcessFee_edit', '修改', 'edit', '/mathmodel/mmProcessFee/edit', 'form', 2, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946822606877', '1288679946822606874', 'mmProcessFee_delete', '删除', 'delete', '/api/mathmodel/mmProcessFee/remove', 'delete', 3, 2, 3, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946822606878', '1288679946822606874', 'mmProcessFee_view', '查看', 'view', '/mathmodel/mmProcessFee/view', 'file-text', 4, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946822606878', '1288679946822606874', 'mmProcessFee_add', '导出', 'export', '/mathmodel/mmProcessFee/export', 'export', 1, 2, 1, 1, NULL, 0);