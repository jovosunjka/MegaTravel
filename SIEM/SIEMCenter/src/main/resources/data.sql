INSERT INTO `user` (`id`,`name`,`username`,`password`) VALUES (1, 'name_admin','admin', '$2a$10$SGCaOooXdD6TalaQqfiALeKlQQPYwmM/VvQdLWYL0r.WF0C0CQQGC');
INSERT INTO `user` (`id`,`name`,`username`,`password`) VALUES (2, 'name_pera','pera', '$2a$10$rEHWaDmFIB89LHp28Vm6wugnce7kNSpaBVNgIbgUFjGAJDvwn8PZW');
INSERT INTO `user` (`id`,`name`,`username`,`password`) VALUES (3, 'name_zika','zika', '$2a$10$dU2h6kpRjK/0fVMu4we55ON/hPqMVNr5X95nvadZtBVgQb.NLRxpa');
INSERT INTO `user` (`id`,`name`,`username`,`password`) VALUES (4, 'name_mika','mika', '$2a$10$9f80Md8Tp4R0ZP8YHcXr0.cbSiRcpxxMvo/O8blTI2T9981kOVtfW');

INSERT INTO `role` (`id`,`name`) VALUES (1, 'SECURE_ADMINISTRATOR');
INSERT INTO `role` (`id`,`name`) VALUES (2, 'ADMINISTRATOR');
INSERT INTO `role` (`id`,`name`) VALUES (3, 'OPERATER');

INSERT INTO `permission` (`id`,`permission_name`) VALUES (1, 'READ_CERTIFICATE');
INSERT INTO `permission` (`id`,`permission_name`) VALUES (2, 'GENERATE_CERTIFICATE');
INSERT INTO `permission` (`id`,`permission_name`) VALUES (3, 'REVOKE_CERTIFICATE');
INSERT INTO `permission` (`id`,`permission_name`) VALUES (4, 'READ_LOG');
INSERT INTO `permission` (`id`,`permission_name`) VALUES (5, 'WRITE_LOG');
INSERT INTO `permission` (`id`,`permission_name`) VALUES (6, 'READ_ALARM');
INSERT INTO `permission` (`id`,`permission_name`) VALUES (7, 'ADD_ROLE_TO_USER');
INSERT INTO `permission` (`id`,`permission_name`) VALUES (8, 'ADD_PERMISSION_TO_ROLE');
INSERT INTO `permission` (`id`,`permission_name`) VALUES (9, 'ADD_ROLE_AND_PERMISSION');
INSERT INTO `permission` (`id`,`permission_name`) VALUES (10, 'READ_ROLES_AND_PERMISSIONS');
INSERT INTO `permission` (`id`,`permission_name`) VALUES (11, 'CREATE_ADMINISTRATOR');

INSERT INTO `role_permissions` (`role_id`,`permission_id`) VALUES (1, 1);
INSERT INTO `role_permissions` (`role_id`,`permission_id`) VALUES (1, 2);
INSERT INTO `role_permissions` (`role_id`,`permission_id`) VALUES (1, 3);
INSERT INTO `role_permissions` (`role_id`,`permission_id`) VALUES (1, 7);
INSERT INTO `role_permissions` (`role_id`,`permission_id`) VALUES (1, 8);
INSERT INTO `role_permissions` (`role_id`,`permission_id`) VALUES (1, 9);
INSERT INTO `role_permissions` (`role_id`,`permission_id`) VALUES (1, 10);
INSERT INTO `role_permissions` (`role_id`,`permission_id`) VALUES (2, 4);
INSERT INTO `role_permissions` (`role_id`,`permission_id`) VALUES (2, 11);
INSERT INTO `role_permissions` (`role_id`,`permission_id`) VALUES (2, 5);
INSERT INTO `role_permissions` (`role_id`,`permission_id`) VALUES (3, 6);

INSERT INTO `user_roles` (`user_id`,`role_id`) VALUES (1, 1);
INSERT INTO `user_roles` (`user_id`,`role_id`) VALUES (2, 2);
INSERT INTO `user_roles` (`user_id`,`role_id`) VALUES (2, 3);
INSERT INTO `user_roles` (`user_id`,`role_id`) VALUES (3, 2);
INSERT INTO `user_roles` (`user_id`,`role_id`) VALUES (4, 3);