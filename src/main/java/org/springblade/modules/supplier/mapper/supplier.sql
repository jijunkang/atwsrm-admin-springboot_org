INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1197073321267580934', 0, 'supplier', 'supplier', 'menu', '/supplier/supplier', NULL, 1, 1, 0, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1197073321267580935', '1197073321267580934', 'supplier_add', '新增', 'add', '/supplier/supplier/add', 'plus', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1197073321267580936', '1197073321267580934', 'supplier_edit', '修改', 'edit', '/supplier/supplier/edit', 'form', 2, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1197073321267580937', '1197073321267580934', 'supplier_delete', '删除', 'delete', '/api/supplier/supplier/remove', 'delete', 3, 2, 3, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1197073321267580938', '1197073321267580934', 'supplier_view', '查看', 'view', '/supplier/supplier/view', 'file-text', 4, 2, 2, 1, NULL, 0);
