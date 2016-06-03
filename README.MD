Deployment Instruction:

The system is deployed in Ubuntu 14.04. 

1. download dependencies
sudo apt-get update
sudo apt-get install default-jdk
sudo apt-get install maven
sudo apt-get install git-core

2. copy code from github
git clone https://github.com/lixinyuan1216/beta1

3. add mirror to maven XML config file in the project
<mirrors>
    <mirror>
      <id>nexus</id>
      <mirrorOf>*,!hef,!rubygems-proxy</mirrorOf>
      <url>https://mvn.aurin.org.au/nexus/content/groups/public/</url>
    </mirror>
  </mirrors>
  
 4. build the system
 mvn clean install
