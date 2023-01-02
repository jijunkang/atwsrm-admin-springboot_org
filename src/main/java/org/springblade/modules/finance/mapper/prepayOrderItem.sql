INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1266308482517688327', 0, 'finance', 'prepayOrderItem', 'menu', '/finance/prepayOrderItem', NULL, 1, 1, 0, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1266308482517688328', '1266308482517688327', 'prepayOrderItem_add', '新增', 'add', '/finance/prepayOrderItem/add', 'plus', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1266308482517688328', '1266308482517688327', 'prepayOrderItem_add', '导出', 'export', '/finance/prepayOrderItem/export', 'export', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1266308482517688329', '1266308482517688327', 'prepayOrderItem_edit', '修改', 'edit', '/finance/prepayOrderItem/edit', 'form', 2, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1266308482517688330', '1266308482517688327', 'prepayOrderItem_delete', '删除', 'delete', '/api/finance/prepayOrderItem/remove', 'delete', 3, 2, 3, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1266308482517688331', '1266308482517688327', 'prepayOrderItem_view', '查看', 'view', '/finance/prepayOrderItem/view', 'file-text', 4, 2, 2, 1, NULL, 0);