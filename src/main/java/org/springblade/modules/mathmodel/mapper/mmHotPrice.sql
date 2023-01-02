INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946671611920', 0, 'mathmodel', 'mmHotPrice', 'menu', '/mathmodel/mmHotPrice', NULL, 1, 1, 0, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946671611921', '1288679946671611920', 'mmHotPrice_add', '新增', 'add', '/mathmodel/mmHotPrice/add', 'plus', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946671611922', '1288679946671611920', 'mmHotPrice_edit', '修改', 'edit', '/mathmodel/mmHotPrice/edit', 'form', 2, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946671611923', '1288679946671611920', 'mmHotPrice_delete', '删除', 'delete', '/api/mathmodel/mmHotPrice/remove', 'delete', 3, 2, 3, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946671611924', '1288679946671611920', 'mmHotPrice_view', '查看', 'view', '/mathmodel/mmHotPrice/view', 'file-text', 4, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1288679946671611924', '1288679946671611920', 'mmHotPrice_add', '导出', 'export', '/mathmodel/mmHotPrice/export', 'export', 1, 2, 1, 1, NULL, 0);