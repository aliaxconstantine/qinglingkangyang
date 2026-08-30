# QLKY MySQL database setup

Import the schema and crawler seed scripts into MySQL 8.0 or later:

```powershell
mysql -uroot -p -e "source ./qlky-schema-mysql.sql"
mysql -uroot -p qlky -e "source ./qlky-seed-crawler-tasks.sql"
```

The schema script creates all application tables. The seed script creates the bundled crawler definitions, task IDs, and field mappings used by the dashboard data scripts; it can be run repeatedly.

Do not import files from the `..\backup` directory into MySQL. Those files are H2 runtime backups and use H2-only syntax such as `SALT` and `HASH`.

To run the backend after importing, set the MySQL password in the current PowerShell session:

```powershell
$env:QLKY_DB_PASSWORD = 'your-mysql-password'
```
