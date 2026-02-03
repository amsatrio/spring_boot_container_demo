.SILENT: build
.SILENT: build_skip_test
.SILENT: start
.SILENT: run
.SILENT: tes
.SILENT: clean
.SILENT: ngrok


build:
	./mvnw package -f pom.xml

build_skip_test:
	./mvnw package -f pom.xml -Dmaven.test.skip=true

start: build_skip_test
	java -Xms96m -Xmx256m -XX:+UseG1GC -XX:+UseStringDeduplication -server -jar ./target/spring_boot_container_demo-0.0.1-SNAPSHOT.jar

tes:
	./mvnw test -f pom.xml

clean:
	rm -rf ./target
	rm -rf ./logs
	rm -rf ./mobile_logs
	rm -rf ./db
	rm -rf ./tmp

remove_logs:
	rm -rf ./logs

initialize: clean


code-quality:
	mvn checkstyle:check
	mvn spotbugs:check
code-quality-gui:
	mvn spotbugs:gui

ngrok:
	ngrok http --domain=correct-mink-loved.ngrok-free.app https://localhost:8085

run: remove_logs
	mvn spring-boot:run -X

docker:
	docker rmi -f localhost:5000/spring-boot-container-demo:latest
	docker compose -f ./container/docker/compose.yaml build --no-cache
	docker tag spring-boot-container-demo localhost:5000/spring-boot-container-demo
	docker push localhost:5000/spring-boot-container-demo
	docker compose -f ./container/docker/compose.yaml up -d
	docker logs spring-boot-container-demo -f
docker_down:
	docker compose -f container/docker/compose.yaml down