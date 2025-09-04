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
                  sh 'kubectl apply -f k8s.yaml'
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