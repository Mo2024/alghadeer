#!/bin/bash

# Variables
DATE=$(date +%d%m%Y)
CONTAINER_NAME="postgres"
BACKUP_DIR="/var/lib/postgresql/backups"
BACKUP_NAME="alghadeer_${DATE}"
TAR_FILE="$BACKUP_NAME.tar.gz"

# Step 1: Run backup 
pg_basebackup -U postgres -Fp -Xs -D "$BACKUP_DIR/$BACKUP_NAME"
tar -czf "$BACKUP_DIR/$TAR_FILE" -C "$BACKUP_DIR" "$BACKUP_NAME"
rm -rf "$BACKUP_DIR/$BACKUP_NAME"

# Step 3: Run rclone on host
rclone copy $BACKUP_DIR/$TAR_FILE gdrive:/backups \
  --progress \
  --transfers=4 \
  --checkers=8 \
  --retries=3 \
  --low-level-retries=10 \
  --config=/var/lib/postgresql/.config/rclone/rclone.conf


# Step 4: Cleanup local file
rm $BACKUP_DIR/$TAR_FILE

echo "✅ Backup completed: $TAR_FILE"