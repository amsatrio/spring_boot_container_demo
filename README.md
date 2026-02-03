# spring_boot_container_demo

# jenkins
http://optiplex3050.tailb8a972.ts.net:8080
f595b614b7e52fa8f4706d16cce16484

# gitlab
http://optiplex3050.tailb8a972.ts.net:9180/amsatrio/spring_boot_container_demo.git
glpat-p1mJd8hpkwia05cepkUmEm86MQp1OjQH.01.0w0aval8c

# log stack: ELK - Spring Boot

# kibana - elastic
http://optiplex3050.tailb8a972.ts.net:5601
Menu => Stack Management
    Kibana => Data Views => create new data view
Menu => Analytics => Discover

# check logs in kafka
docker exec -it kafka1   kafka-console-consumer --bootstrap-server localhost:9092   --topic demo-project-log   --from-beginning --max-messages 10