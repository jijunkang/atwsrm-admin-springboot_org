INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1266218928288817158', 0, 'finance', 'prepayOrder', 'menu', '/finance/prepayOrder', NULL, 1, 1, 0, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1266218928288817159', '1266218928288817158', 'prepayOrder_add', '新增', 'add', '/finance/prepayOrder/add', 'plus', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1266218928288817160', '1266218928288817158', 'prepayOrder_edit', '修改', 'edit', '/finance/prepayOrder/edit', 'form', 2, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1266218928288817161', '1266218928288817158', 'prepayOrder_delete', '删除', 'delete', '/api/finance/prepayOrder/remove', 'delete', 3, 2, 3, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1266218928288817162', '1266218928288817158', 'prepayOrder_view', '查看', 'view', '/finance/prepayOrder/view', 'file-text', 4, 2, 2, 1, NULL, 0);
