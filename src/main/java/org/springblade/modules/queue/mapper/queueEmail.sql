INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1248639353387339777', 0, 'queue', 'queueEmail', 'menu', '/queue/queueEmail', NULL, 1, 1, 0, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1248639353387339778', '1248639353387339777', 'queueEmail_add', '新增', 'add', '/queue/queueEmail/add', 'plus', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1248639353387339778', '1248639353387339777', 'queueEmail_add', '导出', 'export', '/queue/queueEmail/export', 'export', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1248639353387339779', '1248639353387339777', 'queueEmail_edit', '修改', 'edit', '/queue/queueEmail/edit', 'form', 2, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1248639353387339780', '1248639353387339777', 'queueEmail_delete', '删除', 'delete', '/api/queue/queueEmail/remove', 'delete', 3, 2, 3, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1248639353387339781', '1248639353387339777', 'queueEmail_view', '查看', 'view', '/queue/queueEmail/view', 'file-text', 4, 2, 2, 1, NULL, 0);