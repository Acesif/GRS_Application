# [Grievance Redress System](https://grs.gov.bd/)



## Dependencies

- JDK: Java 8
- Build Tool: Gradle 3.4.1 (Also compatible with Gradle 4.5)
- Database: MySQL 5.6.1

---
## Set up Development Environment

1. Set Gradle version  3.4.1 on your IDE (preferably IntelliJ IDEA)
2. Set JDK version 8
3. Change Active Spring Profile to `dev`
4. Execute `bootRun` from Gradle > Tasks > Application > bootRun

## Database configuration

1. Find the `my.ini` file on Windows machines (usually located in `C:\ProgramData\MySQL\MySQL Server 5.6`)
2. Change the following lines
    ```
    tmp_table_size=99M
    innodb_additional_mem_pool_size=14M
    innodb_log_buffer_size=512M
    innodb_buffer_pool_size=128M
    innodb_log_file_size=512M
    max_allowed_packet=14G
    ```
3. Restart the MySQL service
---


## Running on Terminal

```sh
$ gradle clean build
$ gradle bootRun
```
---
## Set up Production Environment
1. Change Active Spring Profile to `prod`
2. Run `gradle clean build` and relocate the war


### To let GRS run on the background, add it as a service
`vi /etc/systemd/system/grs.service`

```
[Unit]
Description=GRS Service

[Service]
User=nobody
# The configuration file application.properties should be here:
WorkingDirectory=/root/grs/warRun/
ExecStart=/usr/bin/java -Xmx1000m -jar -Dspring.profiles.active=dev grs.war
SuccessExitStatus=143
TimeoutStopSec=10
Restart=on-failure
RestartSec=5

[Install]
WantedBy=multi-user.target

```

### After setting up the service, reload the unix daemon 

```sh
$ systemctl daemon-reload
$ systemctl start grs
$ systemctl stop grs
$ systemctl restart grs
$ systemctl status grs
$ journalctl -u grs.service
```
