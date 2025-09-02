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
                coverage([
                    execPattern: 'target/site/jacoco/jacoco.exec', // Adjust the path as needed
                    classPattern: 'target/classes',
                    sourcePattern: 'src/main/java',
                    inclusionPattern: '**/*.class',
                    exclusionPattern: '**/test/**'
                ])
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
            junit 'target/surefire-reports/*.xml' // Archive test results
        }
    }
}