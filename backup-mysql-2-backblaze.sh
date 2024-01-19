#!/bin/bash

date=$(date +%Y-%m-%d)
log_folder="/var/crontab/logs"
log_file_name="mysql-backup-log.log"
log_file="$log_folder/$log_file_name"

if test ! -e $log_folder; then
    mkdir $log_folder
fi

if test ! -e $log_file; then
    touch $log_file
fi

mysql_host="127.0.0.1"
mysql_port="3306"
mysql_username="root"
mysql_passwd="123456"
databases=("moneywhere")

backblaze_upload_helper_path="/var/crontab/lib/backblaze-update-helper-1.0.jar"
app_key_id=""
app_key=""
bucket_name=""
bucket_folder=""

update2BackBlaze() {
    file_name=$1
    java -jar $backblaze_upload_helper_path $1 $log_file $app_key_id $app_key $bucket_name $bucket_folder
    return 1
}

echo "=====" >>$log_file
echo `date` >>$log_file
echo "开始备份" >>$log_file

for database in ${databases[*]}; do
    file_name=$database-$date.sql
    file=/tmp/$file_name
    mysqldump -u$mysql_username -p$mysql_passwd -h$mysql_host -P$mysql_port $database >$file
    update2BackBlaze $file
    test $? -eq 1 && echo "$database 备份成功" >>$log_file || echo "$database 备份失败" >>$log_file
    rm $file
done

echo "结束备份" >>$log_file