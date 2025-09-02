#!groovy
pipeline {
    agent any
    stages {

        stage('Test') {
            steps {
                sh 'make tes'
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
            steps {
                script {
                  sh 'docker compose up --build -d'
                }
            }
        }

        // stage('Create Release') {
        //     steps {
        //         script {
        //             // def version = sh script: 'mvn help:evaluate -Dexpression=project.version -q -DforceStdout'
        //             def version = '0.0.1'
        //             def gitlabProjectId = 'spring-hospital'
        //             def tagName = "v$version"
        //             def releaseName = "Release $version"
        //             def releaseNotes = "Release $version"

        //             withCredentials([usernamePassword(credentialsId: '1501f162-4ec1-494a-bc46-d62c3df7755e', usernameVariable: 'GITLAB_USER', passwordVariable: 'GITLAB_TOKEN')]) {
        //                 sh "curl --header \"PRIVATE-TOKEN: \${GITLAB_TOKEN}\" --request POST --form \"tag_name=\${tagName}\" --form \"name=\${releaseName}\" --form \"description=\${releaseNotes}\" http://asus456.tailb8a972.ts.net/api/v4/projects/\${gitlabProjectId}/releases"
        //             }
        //         }
        //     }
        // }
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