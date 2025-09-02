#!groovy
pipeline {
   agent {
       docker {
             image 'eclipse-temurin:21-jdk'
             args '--network host -u root -v /var/run/docker.sock:/var/run/docker.sock'
       }
    }
    
    stages {
        stage('Build & Test') {
            steps {
                script {
                    def dockerImage = 'spring-boot-container-demo'
                    docker.image(dockerImage).inside {
                        sh './mvnw clean test -f pom.xml'
                    }
                }
            }
        }

        stage('Code Coverage') {
            steps {
                jacoco(
                    execPattern: 'target/jacoco.exec',
                    classPattern: 'target/classes',
                    sourcePattern: 'src/main/java',
                    exclusionPattern: 'src/test/*'
                )
            }
        }

        stage('Docker Build and Deploy') {
            agent any
            steps {
                script {
                  sh 'docker compose up --build -d'
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