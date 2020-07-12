#!/bin/bash

localVersion=$(xml_grep --text_only /artifact-resolution/data/version /home/pi/OceanBox/updater/MetaLatestOceanBox.xml)

ansible-pull local.yml --url https://github.com/OceanStream-X-MiageSorbonne/Boitier.git --checkout dev -i localhost

wget -O /home/pi/OceanBox/updater/MetaLatestOceanBox1.xml "http://devops.oceanstream.fr:8081/nexus/service/local/artifact/maven/resolve?g=oceanbox&a=OceanBox&v=LATEST&r=DevOceanBoxReleases&e=jar"

repoVersion=$(xml_grep --text_only /artifact-resolution/data/version /home/pi/OceanBox/updater/MetaLatestOceanBox.xml)

if [ "$repoVersion" != "$localVersion" ]
then
        wget -P /home/pi/OceanBox/bin "http://devops.oceanstream.fr:8081/nexus/service/local/artifact/maven/redirect?g=oceanbox&a=OceanBox&v=LATEST&r=DevOceanBoxReleases&e=jar" --content-disposition
        rm -f "/home/pi/OceanBox/bin/OceanBox-$localVersion.jar"
        rm -f "/home/pi/OceanBox/updater/MetaLatestOceanBox.xml"
        wget -O /home/pi/OceanBox/updater/MetaLatestOceanBox1.xml "http://devops.oceanstream.fr:8081/nexus/service/local/artifact/maven/resolve?g=oceanbox&a=OceanBox&v=LATEST&r=DevOceanBoxReleases&e=jar"
fi



