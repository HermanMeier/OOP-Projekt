# XML to SQL

This Java program should help merging XML data into a MySQL database.

In the final version you should be able to select corresponding columns from XML file (local or web based) and merge them into already existing MySQL database.
Works in command line, no GUI planned.

To run the program you must first run the server and then the main class.
For testing purposes you can use:
1. xmlFiles directory as the XML files source;
2. Database with these credentials:
* user: xmltosql
* password: xmltosql
* database name: xmltosql
* database host: db4free.net
3. Admin user
* username: admin
* password: xmltosql


Users
All new accounts will not have admin rights. Admin account has all commands available. When running Main with commandline arguments, then the login stage will be skipped.
Users are stored in resources/users.dat.

Commands
Most commands accept parameters. The correct syntax is <commandName> <param1> <param2> ... . When running Main with commandline arguments,
the arguments will be interpreted as separate commands. Syntax for commandline arguments is <1CommandName>;<param1>;<param2> <2commandName>;<param1>;<param2> ...
All commands can be displayed using "?" command.

List of all commands:

