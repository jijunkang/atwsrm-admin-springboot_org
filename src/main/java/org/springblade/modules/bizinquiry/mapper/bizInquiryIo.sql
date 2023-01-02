INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1273427348116987906', 0, 'bizinquiry', 'bizInquiryIo', 'menu', '/bizinquiry/bizInquiryIo', NULL, 1, 1, 0, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1273427348116987907', '1273427348116987906', 'bizInquiryIo_add', '新增', 'add', '/bizinquiry/bizInquiryIo/add', 'plus', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1273427348116987908', '1273427348116987906', 'bizInquiryIo_edit', '修改', 'edit', '/bizinquiry/bizInquiryIo/edit', 'form', 2, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1273427348116987909', '1273427348116987906', 'bizInquiryIo_delete', '删除', 'delete', '/api/bizinquiry/bizInquiryIo/remove', 'delete', 3, 2, 3, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1273427348116987910', '1273427348116987906', 'bizInquiryIo_view', '查看', 'view', '/bizinquiry/bizInquiryIo/view', 'file-text', 4, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1273427348116987910', '1273427348116987906', 'bizInquiryIo_add', '导出', 'export', '/bizinquiry/bizInquiryIo/export', 'export', 1, 2, 1, 1, NULL, 0);