INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1210852488685551623', 0, 'po', 'poremind', 'menu', '/po/poremind', NULL, 1, 1, 0, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1210852488685551624', '1210852488685551623', 'poremind_add', '新增', 'add', '/po/poremind/add', 'plus', 1, 2, 1, 1, NULL, 0);

INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1210852488685551625', '1210852488685551623', 'poremind_edit', '修改', 'edit', '/po/poremind/edit', 'form', 2, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1210852488685551626', '1210852488685551623', 'poremind_delete', '删除', 'delete', '/api/po/poremind/remove', 'delete', 3, 2, 3, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1210852488685551627', '1210852488685551623', 'poremind_view', '查看', 'view', '/po/poremind/view', 'file-text', 4, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1210852488685551628', '1210852488685551623', 'poremind_export', '导出', 'export', '/po/poremind/export', 'export', 1, 2, 1, 1, NULL, 0);
