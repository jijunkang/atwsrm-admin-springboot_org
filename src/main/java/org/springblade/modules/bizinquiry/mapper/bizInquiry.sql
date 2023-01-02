INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1273427347978575874', 0, 'bizinquiry', 'bizInquiry', 'menu', '/bizinquiry/bizInquiry', NULL, 1, 1, 0, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1273427347978575875', '1273427347978575874', 'bizInquiry_add', '新增', 'add', '/bizinquiry/bizInquiry/add', 'plus', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1273427347978575876', '1273427347978575874', 'bizInquiry_edit', '修改', 'edit', '/bizinquiry/bizInquiry/edit', 'form', 2, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1273427347978575877', '1273427347978575874', 'bizInquiry_delete', '删除', 'delete', '/api/bizinquiry/bizInquiry/remove', 'delete', 3, 2, 3, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1273427347978575878', '1273427347978575874', 'bizInquiry_view', '查看', 'view', '/bizinquiry/bizInquiry/view', 'file-text', 4, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1273427347978575878', '1273427347978575874', 'bizInquiry_add', '导出', 'export', '/bizinquiry/bizInquiry/export', 'export', 1, 2, 1, 1, NULL, 0);