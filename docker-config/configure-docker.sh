#!/bin/bash
source /mnt/work/docker-applications/env.sh
env
id
deploylocation=/mnt/work/docker-applications/@project.artifactId@
repository_tag=tmmc.logistics/@project.artifactId@
if test -f ${deploylocation}/stop-docker.sh; then
    source ${deploylocation}/stop-docker.sh
fi
mkdir --parents --verbose ${deploylocation}
cp --verbose --recursive ${SYSTEM_ARTIFACTSDIRECTORY}/@project.artifactId@/@project.artifactId@/* ${deploylocation}
mkdir --parents --verbose ${deploylocation}/logs
chmod g+w,o+w --verbose ${deploylocation}/logs
mkdir --parents --verbose ${deploylocation}/externalFiles
chmod g+w,o+w --verbose ${deploylocation}/externalFiles
chmod u+x,g+x,o+x --verbose ${deploylocation}/docker-config/*.sh
mv --verbose ${deploylocation}/docker-config/start*.sh ${deploylocation}
mv --verbose ${deploylocation}/docker-config/stop*.sh ${deploylocation}
mv --verbose ${deploylocation}/docker-config/restart*.sh ${deploylocation}
mv --verbose ${deploylocation}/docker-config/build*.sh ${deploylocation}

cd ${deploylocation}
pwd
ls -la
source ./stop-docker.sh
source ./build-docker.sh
source ./start-docker.sh
