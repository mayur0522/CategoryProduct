pipeline {
    agent any

    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Git Branch')
    }

    environment {
        APP_NAME = 'CategoryProduct'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: "${params.BRANCH_NAME}", url: 'https://github.com/Yogeshjathar/CategoryProduct.git'
            }
        }

        stage('Build') {
            steps {
                echo "Building ${env.APP_NAME} from branch ${params.BRANCH_NAME}"
                sh './gradlew build'
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
