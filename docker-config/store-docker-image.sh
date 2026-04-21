#!/bin/bash
source /mnt/work/docker-applications/env.sh
env
id
aws_ecr_location=548302737758.dkr.ecr.us-east-2.amazonaws.com
deploylocation=/mnt/work/docker-applications/@project.artifactId@
repository_tag=tmmc.logistics/@project.artifactId@
if test -f ${deploylocation}/stop-docker.sh; then
    source ${deploylocation}/stop-docker.sh
fi
mkdir --parents --verbose ${deploylocation}
cp --verbose --recursive ${SYSTEM_ARTIFACTSDIRECTORY}/@project.artifactId@/@project.artifactId@/* ${deploylocation}
mkdir --parents --verbose ${deploylocation}/logs
chmod g+w,o+w --verbose ${deploylocation}/logs
mkdir --parents --verbose ${deploylocation}/parts_master
chmod g+w,o+w --verbose ${deploylocation}/parts_master
chmod u+x,g+x,o+x --verbose ${deploylocation}/docker-config/*.sh
mv --verbose ${deploylocation}/docker-config/start*.sh ${deploylocation}
mv --verbose ${deploylocation}/docker-config/stop*.sh ${deploylocation}
mv --verbose ${deploylocation}/docker-config/restart*.sh ${deploylocation}
mv --verbose ${deploylocation}/docker-config/build*.sh ${deploylocation}
cd ${deploylocation}
pwd
ls -la

docker login -u AWS -p $(aws ecr get-login-password --region us-east-2) ${aws_ecr_location}

aws ecr describe-repositories --repository-names ${repository_tag} 2>&1 > /dev/null
status=$?
if [[ ! "${status}" -eq 0 ]]; then
    aws ecr create-repository --repository-name ${repository_tag}
fi

source ./build-docker.sh
docker tag ${repository_tag}:@project.version@ ${aws_ecr_location}/${repository_tag}:@project.version@
docker push ${aws_ecr_location}/${repository_tag}:@project.version@
docker tag ${aws_ecr_location}/${repository_tag}:@project.version@ ${repository_tag}:@project.version@
docker image rm ${aws_ecr_location}/${repository_tag}:@project.version@

aws ecr list-images --repository-name ${repository_tag}

source ./start-docker.sh
