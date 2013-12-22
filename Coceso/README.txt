   ______            ______           _____
  / ____/  ____     / ____/  ___     / ___/   ____
 / /      / __ \   / /      / _ \    \__ \   / __ \
/ /___   / /_/ /  / /___   /  __/   ___/ /  / /_/ /
\____/   \____/   \____/   \___/   /____/   \____/

   Communication       Center      Software

INSTALL GUIDE:

1. REQUIRED SYSTEM
Server:
- Java JRE (version 1.6 and newer tested)
- Tomcat (version 7 and newer tested)
- PostgreSQL DB (9.1 and newer tested)
Client:
- Web Browser (full features only in Mozilla Firefox and Google Chrome tested)

2. INSTALLATION
- Set up a Database for the Project
- Write your DB Login Data to src/main/webapp/WEB-INF/classes/coceso.properties
- Execute src/db_files/create.sql on the Database
- Execute YOUR MODIFIED create_operator.sql on the Database
- Build the Project via Maven (i.e. ´mvn package´)
- Load the Project into Tomcat

On First Login, the Password used for Login will NOT be validated, only set into DB. When the Password is set (for the
user as in 'create_operator.sql), change in coceso.properties the Flag 'firstUse' to false. (In file in the Tomcat
 Subdirectory) After a restart (or redeploy) only the set Password will be accepted. The first User is automatically
Superuser and can add new User and set their Passwords.