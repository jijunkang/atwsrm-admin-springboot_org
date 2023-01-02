INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1210452434023964678', 0, 'po', 'pooffsetview', 'menu', '/po/pooffsetview', NULL, 1, 1, 0, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1210452434023964679', '1210452434023964678', 'pooffsetview_add', '新增', 'add', '/po/pooffsetview/add', 'plus', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1210452434023964679', '1210452434023964678', 'pooffsetview_add', '导出', 'export', '/po/pooffsetview/export', 'export', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1210452434023964680', '1210452434023964678', 'pooffsetview_edit', '修改', 'edit', '/po/pooffsetview/edit', 'form', 2, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1210452434023964681', '1210452434023964678', 'pooffsetview_delete', '删除', 'delete', '/api/po/pooffsetview/remove', 'delete', 3, 2, 3, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1210452434023964682', '1210452434023964678', 'pooffsetview_view', '查看', 'view', '/po/pooffsetview/view', 'file-text', 4, 2, 2, 1, NULL, 0);