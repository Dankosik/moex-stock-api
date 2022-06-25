pipeline {
    agent any
    stages{
        stage('Build project'){
            steps{
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/Dankosik/moex-stock-api']]])
                withGradle(){
                   sh './gradlew clean build'
                }
            }
        }
        stage('Build docker image'){
            steps{
                script{
                    sh 'docker build -t dankos/moex-stock-api .'
                }
            }
        }
        stage('Push image to dockerhub'){
            steps{
                script{
                   withCredentials([string(credentialsId: 'dockerhub-pwd', variable: 'dockerhubpwd')]) {
                        sh 'docker login -u dankosik -p ${dockerhubpwd}'
                   }
                   sh 'docker push dankosik/moex-stock-service'
                }
            }
        }
    }
}