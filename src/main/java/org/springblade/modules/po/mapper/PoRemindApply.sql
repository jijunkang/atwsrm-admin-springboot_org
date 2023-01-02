INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1209290836680904706', 0, 'po', 'poremindapply', 'menu', '/po/remindapply', NULL, 1, 1, 0, 1, NULL, 0);

INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1209421644049735682', '1209290836680904706', 'poremindapply_add', '新增', 'add', '/po/poremindapply/add', 'plus', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1209421644049735686', '1209290836680904706', 'poremindapply_export', '导出', 'export', '/po/poremindapply/export', 'export', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1209421644049735683', '1209290836680904706', 'poremindapply_edit', '修改', 'edit', '/po/poremindapply/edit', 'form', 2, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1209421644049735684', '1209290836680904706', 'poremindapply_delete', '删除', 'delete', '/api/po/poremindapply/remove', 'delete', 3, 2, 3, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1209421644049735685', '1209290836680904706', 'poremindapply_view', '查看', 'view', '/po/poremindapply/view', 'file-text', 4, 2, 2, 1, NULL, 0);