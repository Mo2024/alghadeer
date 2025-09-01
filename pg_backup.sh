#!/bin/bash

# Variables
DATE=$(date +%d%m%Y)
CONTAINER_NAME="postgres"
CONTAINER_BACKUP_DIR="/var/lib/postgresql/backups"
LOCAL_BACKUP_DIR="/opt/backups"
BACKUP_NAME="alghadeer_$DATE"
TAR_FILE="$BACKUP_NAME.tar.gz"

# Step 1: Run backup inside container
sudo docker exec -u root $CONTAINER_NAME bash -c "
  pg_basebackup -U mohamed -Fp -Xs -D $CONTAINER_BACKUP_DIR/$BACKUP_NAME &&
  tar -czf $CONTAINER_BACKUP_DIR/$TAR_FILE -C $CONTAINER_BACKUP_DIR $BACKUP_NAME &&
  rm -rf $CONTAINER_BACKUP_DIR/$BACKUP_NAME
"

# Step 3: Run rclone on host
#sudo rclone copy $LOCAL_BACKUP_DIR/$TAR_FILE gdrive:/backups --progress --transfers=4 --checkers=8 --retries=3 --low-level-retries=10
sudo rclone copy $LOCAL_BACKUP_DIR/$TAR_FILE gdrive:/backups \
  --progress \
  --transfers=4 \
  --checkers=8 \
  --retries=3 \
  --low-level-retries=10 \
  --config=/home/ec2-user/.config/rclone/rclone.conf


# Step 4: Cleanup local file
sudo rm $LOCAL_BACKUP_DIR/$TAR_FILE

echo "✅ Backup completed: $TAR_FILE"


