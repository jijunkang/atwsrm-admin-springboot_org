INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1197094541237178375', 0, 'forecast', 'forecast', 'menu', '/forecast/forecast', NULL, 1, 1, 0, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1197094541237178376', '1197094541237178375', 'forecast_add', '新增', 'add', '/forecast/forecast/add', 'plus', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1197094541237178377', '1197094541237178375', 'forecast_edit', '修改', 'edit', '/forecast/forecast/edit', 'form', 2, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1197094541237178378', '1197094541237178375', 'forecast_delete', '删除', 'delete', '/api/forecast/forecast/remove', 'delete', 3, 2, 3, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1197094541237178379', '1197094541237178375', 'forecast_view', '查看', 'view', '/forecast/forecast/view', 'file-text', 4, 2, 2, 1, NULL, 0);
