pipeline {
    agent any
    tools{
        gradle 'gradle-7.4.1'
    }
    stages{
        stage('Build project'){
            steps{
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/Dankosik/moex-stock-api']]])
                sh 'gradle clean build'
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