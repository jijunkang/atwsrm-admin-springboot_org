INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1200310222774681606', 0, 'pr', 'u9_pr', 'menu', '/pr/u9_pr', NULL, 1, 1, 0, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1200310222774681607', '1200310222774681606', 'u9_pr_add', '新增', 'add', '/pr/u9_pr/add', 'plus', 1, 2, 1, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1200310222774681608', '1200310222774681606', 'u9_pr_edit', '修改', 'edit', '/pr/u9_pr/edit', 'form', 2, 2, 2, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1200310222774681609', '1200310222774681606', 'u9_pr_delete', '删除', 'delete', '/api/pr/u9_pr/remove', 'delete', 3, 2, 3, 1, NULL, 0);
INSERT INTO `blade_menu`(`id`, `parent_id`, `code`, `name`, `alias`, `path`, `source`, `sort`, `category`, `action`, `is_open`, `remark`, `is_deleted`)
VALUES ('1200310222774681610', '1200310222774681606', 'u9_pr_view', '查看', 'view', '/pr/u9_pr/view', 'file-text', 4, 2, 2, 1, NULL, 0);
