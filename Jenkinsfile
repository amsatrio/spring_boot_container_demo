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
                    sh 'docker compose up --build -d'
                    sh 'docker compose down'
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
				  export KUBECONFIG=/home/jenkins/.kube/config
				  kubectl apply -f k8s.yaml --validate=false
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
