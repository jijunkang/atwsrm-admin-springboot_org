INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1315963901502283782', 0, 'aps', 'apsReportExdev', 'menu', '/aps/apsReportExdev', NULL, 1, 1, 0, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1315963901502283786', '1315963901502283782', 'apsReportExdev_view', '查看', 'view', '/aps/apsReportExdev/view', 'file-text', 4, 2, 2, 1, NULL, 0);


INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1315963901502283783', '1315963901502283782', 'apsReportExdev_add', '新增', 'add', '/aps/apsReportExdev/add', 'plus', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1315963901502283784', '1315963901502283782', 'apsReportExdev_edit', '修改', 'edit', '/aps/apsReportExdev/edit', 'form', 2, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1315963901502283785', '1315963901502283782', 'apsReportExdev_delete', '删除', 'delete', '/api/aps/apsReportExdev/remove', 'delete', 3, 2, 3, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1315963901502283786', '1315963901502283782', 'apsReportExdev_view', '查看', 'view', '/aps/apsReportExdev/view', 'file-text', 4, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1315963901502283787', '1315963901502283782', 'apsReportExdev_add', '导出', 'export', '/aps/apsReportExdev/export', 'export', 1, 2, 1, 1, NULL, 0);