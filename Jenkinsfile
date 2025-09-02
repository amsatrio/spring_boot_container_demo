#!groovy
pipeline {
    
    stages {
        stage('Build & Test') {
            agent {
                docker {
                        image 'eclipse-temurin:21-jdk'
                        args '--network host -u root -v /var/run/docker.sock:/var/run/docker.sock'
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
                        args '--network host -u root -v /var/run/docker.sock:/var/run/docker.sock'
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