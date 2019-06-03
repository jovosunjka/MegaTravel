INSERT INTO `ftp_application` (`id`,`organizational_unit_name`,`host`,`port`,`username`,`password`) VALUES (1, 'MegaTravelRootCA', NULL, NULL, NULL, NULL);
INSERT INTO `ftp_application` (`id`,`organizational_unit_name`,`host`,`port`,`username`,`password`) VALUES (2, 'MegaTravelSiemCenter','localhost', 2121, 'siem_center_ftp', 'siem_center_ftp_pass');
INSERT INTO `ftp_application` (`id`,`organizational_unit_name`,`host`,`port`,`username`,`password`) VALUES (3, 'MegaTravelSiemWindowsAgent','localhost', 2122, 'siem_windows_agent_ftp', 'siem_windows_agent_ftp_pass');

INSERT INTO `application_address` (`id`,`organizational_unit_name`,`url`) VALUES (1, 'MegaTravelSiemCenter','https://localhost:8081/api/certificate/send-request');
INSERT INTO `application_address` (`id`,`organizational_unit_name`,`url`) VALUES (2, 'MegaTravelSiemWindowsAgent','https://localhost:8082/api/certificate/send-request');