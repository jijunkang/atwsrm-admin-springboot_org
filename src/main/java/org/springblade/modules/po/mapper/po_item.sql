INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1200249710414217223', 0, 'poitem', 'po_item', 'menu', '/poitem/po_item', NULL, 1, 1, 0, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1200249710414217224', '1200249710414217223', 'po_item_add', '新增', 'add', '/poitem/po_item/add', 'plus', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1200249710414217225', '1200249710414217223', 'po_item_edit', '修改', 'edit', '/poitem/po_item/edit', 'form', 2, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1200249710414217226', '1200249710414217223', 'po_item_delete', '删除', 'delete', '/api/poitem/po_item/remove', 'delete', 3, 2, 3, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1200249710414217227', '1200249710414217223', 'po_item_view', '查看', 'view', '/poitem/po_item/view', 'file-text', 4, 2, 2, 1, NULL, 0);
