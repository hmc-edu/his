APPNAME=his
VERSION=0.0.1

run:
	./gradlew bootRun

build:
	./gradlew clean bootJar

docker-build:
	docker build --rm -t $(APPNAME):$(VERSION) . -f Dockerfile

docker-run:
	docker run -d -p 8080:8080 --name $(APPNAME) $(APPNAME):$(VERSION)

test:
	./gradlew test
