TODO: Fill in details for DpServer

Docker Build Command:
```
docker build --build-arg BUILD_DATE=$(date -u +'%Y-%m-%dT%H:%M:%SZ') --build-arg BUILD_VERSION="1.0.0-SNAPSHOT" -t dpserver:1.0.0-SNAPSHOT .
```
Run Docker Container:
```
mkdir -p target/data target/localoutput
docker run -it --rm -v ${PWD}/target/data:/opt/DpSserver/target/data:Z -v ${PWD}/target/localoutput:/opt/DpServer/localoutput:Z --name DpServer dpserver:1.0.0-SNAPSHOT
```