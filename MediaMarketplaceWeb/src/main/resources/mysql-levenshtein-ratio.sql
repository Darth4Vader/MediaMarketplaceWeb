DROP FUNCTION IF EXISTS `levenshtein_ratio`;
DELIMITER $$

use market $$

CREATE FUNCTION `levenshtein_ratio`(
    `s1` VARCHAR(1024) CHARACTER SET utf8,
    `s2` VARCHAR(1024) CHARACTER SET utf8
)
RETURNS TINYINT UNSIGNED
DETERMINISTIC
NO SQL
COMMENT 'Levenshtein ratio between strings'
BEGIN
    DECLARE s1_len TINYINT UNSIGNED DEFAULT CHAR_LENGTH(s1);
    DECLARE s2_len TINYINT UNSIGNED DEFAULT CHAR_LENGTH(s2);
    RETURN ((levenshtein(s1, s2) / IF(s1_len > s2_len, s1_len, s2_len)) * 100);
END$$

DELIMITER ;