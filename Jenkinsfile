pipeline {
    agent any

    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Git Branch')
    }

    environment {
        APP_NAME = 'CategoryProduct'
    }

    tools {
            maven 'maven-3.8.6'  // Make sure this is installed in Jenkins
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: "${params.BRANCH_NAME}", url: 'https://github.com/Yogeshjathar/CategoryProduct.git'
//                 checkout scm
            }
        }

        stage('Build') {
            steps {
                echo "Building ${env.APP_NAME} from ${params.BRANCH_NAME} branch"
                bat 'mvn clean install -DskipTests'
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests...'
                bat 'mvn test'
            }
        }

        stage('Package') {
            steps {
                echo 'Packaging application...'
                bat 'mvn package'
            }
        }
    }

    post {
        success {
            echo 'Pipeline executed successfully.'
        }
        failure {
            echo 'Pipeline failed. Please check the logs.'
        }
    }
}
