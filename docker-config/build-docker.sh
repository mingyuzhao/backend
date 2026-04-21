#!/bin/bash
source /mnt/work/docker-applications/env.sh
docker build --file ./docker-config/dockerfile --tag ${repository_tag}:@project.version@ .


