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
Most commands accept parameters. The correct syntax is <commandName> <param1> <param2> ... . Some commands accept * as a parameter meaning all. When running Main with commandline arguments,
the arguments will be interpreted as separate commands. Syntax for commandline arguments is <1CommandName>;<param1>;<param2> <2commandName>;<param1>;<param2> ...
All commands can be displayed using "?" command.

List of all commands:

Command | syntax | Result
------- | ------ | -------
? | `?` | Displays all commands
signup | `signup username password` | Creates a guest account
login | `login username password` | Logs in
logout | `logout` | Logs out
kill | `kill userName` | Removes user with userName
exit | `exit` | Closes client and connection to server
files | `files` | Displays all files in xmlFiles directory
sendFile | `sendFile 1filePath 2filePath ...` | Sends all listed files to server
url | `url 1url 2url ...` | Server downloads all listed files
open | `open 1fileName 2fileName ...` | Opens all listed files, supports *
close | `close 1fileName 2fileName ...` | Closes all listed files, supports *
delete | `delete 1fileName 2fileName ...` | Deletes all listed files
show | `show fileName` | Displays all columns from xml file
rename | `rename oldName newName` | Renames a file
connect | `connect user password dbName hostIP` | Connects to a database
disconnect | `disconnect` | disconnects from database
showAllTables | `showAllTables` | Displays all table names in database
showTableColumns | `showTableColumns tableName` | Displays all column names from table
showTable | `showTable tableName` | Displays all data from table
search | `search string` | Searches for string from all opened xml file and database table column names
insert | `insert xmlFileName tableName xmlColumn tableColumn` | Inserts data from xmlColumn to tableColumn
createTable | `createTable tableName xmlFileName` | Creates a table from an xml file

