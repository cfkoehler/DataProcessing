TODO: Fill in details for DpFeeder

Docker Build Command:
This does a full maven compile and verify
```
docker build -t dp-feeder:test .
```
Run Docker Container:
```
docker run --env-file=.env dp-feeder:test
```