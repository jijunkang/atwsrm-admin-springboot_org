INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1319570247229366279', 0, 'aps', 'deliveryModifyAudit', 'menu', '/aps/deliveryModifyAudit', NULL, 1, 1, 0, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1319570247229366280', '1319570247229366279', 'deliveryModifyAudit_add', '新增', 'add', '/aps/deliveryModifyAudit/add', 'plus', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1319570247229366281', '1319570247229366279', 'deliveryModifyAudit_edit', '修改', 'edit', '/aps/deliveryModifyAudit/edit', 'form', 2, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1319570247229366282', '1319570247229366279', 'deliveryModifyAudit_delete', '删除', 'delete', '/api/aps/deliveryModifyAudit/remove', 'delete', 3, 2, 3, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1319570247229366283', '1319570247229366279', 'deliveryModifyAudit_view', '查看', 'view', '/aps/deliveryModifyAudit/view', 'file-text', 4, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1319570247229366283', '1319570247229366279', 'deliveryModifyAudit_add', '导出', 'export', '/aps/deliveryModifyAudit/export', 'export', 1, 2, 1, 1, NULL, 0);