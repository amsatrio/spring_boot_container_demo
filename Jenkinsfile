#!groovy
pipeline {
    agent any
    stages {
        stage('Build & Test') {
            agent {
                docker {
                        image 'eclipse-temurin:21-jdk'
                        args '--network host -u root -v /var/run/docker.sock:/var/run/docker.sock -v /root/.m2:/root/.m2'
                }
            }
            steps {
                script {
                    sh '''
                    sed -i 's|        <appender-ref ref="KAFKA" />|        <!-- <appender-ref ref="KAFKA" /> -->|g' src/main/resources/logback-spring.xml;
                    sed -i 's|        <!-- <appender-ref ref="STDOUT" /> -->|        <appender-ref ref="STDOUT" />|g' src/main/resources/logback-spring.xml;
                    '''
                    sh './mvnw clean test -f pom.xml'
                }
            }
        }

        stage('Code Coverage') {
            agent {
                docker {
                        image 'eclipse-temurin:21-jdk'
                        args '--network host -u root -v /var/run/docker.sock:/var/run/docker.sock -v /root/.m2:/root/.m2'
                }
            }
            steps {
                jacoco(
                    execPattern: 'target/jacoco.exec',
                    classPattern: 'target/classes',
                    sourcePattern: 'src/main/java',
                    exclusionPattern: 'src/test/*'
                )
            }
        }

        stage('Docker Build') {
            agent any
            steps {
                script {
                    sh '''
                    sed -i 's|        <!-- <appender-ref ref="KAFKA" /> -->|        <appender-ref ref="KAFKA" />|g' src/main/resources/logback-spring.xml;
                    sed -i 's|        <appender-ref ref="STDOUT" />|        <!-- <appender-ref ref="STDOUT" /> -->|g' src/main/resources/logback-spring.xml;
                    '''
                    sh 'docker rmi -f localhost:4000/spring-boot-container-demo:latest'
                    sh 'docker compose -f ./container/docker/compose.yaml build'
                }
            }
        }
        stage('Push the Image to Local Registry') {
            agent any
            steps {
                script {
                    sh 'docker tag spring-boot-container-demo localhost:4000/spring-boot-container-demo'
                    sh 'docker push localhost:4000/spring-boot-container-demo'
                }
            }
        }

        stage('Kubernates Deploy') {
            agent any
            steps {
                script {
				  sh '''
				  kubectl apply -f container/kubernates/service.yaml --validate=false
                  kubectl apply -f container/kubernates/deployment.yaml --validate=false
				  '''
                }
            }
        }
    }
    post {
        always {
            jacoco()
            cleanWs(cleanWhenNotBuilt: false,
                    deleteDirs: true,
                    disableDeferredWipeout: true,
                    notFailBuild: true,
                    patterns: [[pattern: '.gitignore', type: 'INCLUDE'],
                               [pattern: '.propsfile', type: 'EXCLUDE']])
        }
    }
}
