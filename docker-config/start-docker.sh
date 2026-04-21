#!/bin/bash
source /mnt/work/docker-applications/env.sh
docker-compose --file docker-config/server-compose.yml up --detach
