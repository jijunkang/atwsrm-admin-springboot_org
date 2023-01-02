INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1201866754570108930', 0, 'pricelib', 'price_lib', 'menu', '/pricelib/price_lib', NULL, 1, 1, 0, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1201866754570108931', '1201866754570108930', 'price_lib_add', '新增', 'add', '/pricelib/price_lib/add', 'plus', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1201866754570108932', '1201866754570108930', 'price_lib_edit', '修改', 'edit', '/pricelib/price_lib/edit', 'form', 2, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1201866754570108933', '1201866754570108930', 'price_lib_delete', '删除', 'delete', '/api/pricelib/price_lib/remove', 'delete', 3, 2, 3, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1201866754570108934', '1201866754570108930', 'price_lib_view', '查看', 'view', '/pricelib/price_lib/view', 'file-text', 4, 2, 2, 1, NULL, 0);
