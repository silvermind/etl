#!/bin/bash
###############################################################################################
# ./build.sh : Build all project
# ./build.sh project_example : Build project_example
###############################################################################################
# fail fast
set -e

cd "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

#if no version is set let's use the environment name
if [ -z ${image_version} ]; then
    image_version=$(mvn -q -Dexec.executable='echo' -Dexec.args='${project.version}' --non-recursive exec:exec)
fi


function build() {
    echo build all project
    time mvn clean package
}

function push() {

    echo push adopteunops/$1:${image_version}
    docker push adopteunops/$1:${image_version}
    echo adopteunops/$1:${image_version} is pushed
}

function buildAll() {
  build
  for folder in process-importer retry-importer error-importer etl-backend referential-importer simulate enduser-doc ; do
      push $folder
  done
  echo build all finish !
}

if [ $1 ]; then
    time mvn clean package
    echo push adopteunops/$1:${image_version}
    docker push adopteunops/$1:${image_version}
    echo adopteunops/$1:${image_version} is pushed
else
   buildAll
fi
