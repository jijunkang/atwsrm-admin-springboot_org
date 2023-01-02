INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1234653223591628807', 0, 'poreceive', 'poReceive', 'menu', '/poreceive/poReceive', NULL, 1, 1, 0, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1234653223591628808', '1234653223591628807', 'poReceive_add', '新增', 'add', '/poreceive/poReceive/add', 'plus', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1234653223591628808', '1234653223591628807', 'poReceive_add', '导出', 'export', '/poreceive/poReceive/export', 'export', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1234653223591628809', '1234653223591628807', 'poReceive_edit', '修改', 'edit', '/poreceive/poReceive/edit', 'form', 2, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1234653223591628810', '1234653223591628807', 'poReceive_delete', '删除', 'delete', '/api/poreceive/poReceive/remove', 'delete', 3, 2, 3, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1234653223591628811', '1234653223591628807', 'poReceive_view', '查看', 'view', '/poreceive/poReceive/view', 'file-text', 4, 2, 2, 1, NULL, 0);