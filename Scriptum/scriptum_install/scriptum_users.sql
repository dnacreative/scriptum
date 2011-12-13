/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

 
CREATE USER openkm@localhost IDENTIFIED BY 'scriptum';
GRANT ALL ON okm_repo.* TO openkm@localhost WITH GRANT OPTION;
GRANT ALL ON okm_app.* TO openkm@localhost WITH GRANT OPTION;

CREATE USER scriptum@localhost IDENTIFIED BY 'scriptum';
GRANT ALL ON scriptum.* TO scriptum@localhost WITH GRANT OPTION;
FLUSH PRIVILEGES;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;