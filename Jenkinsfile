pipeline {
    agent any

    environment {
        SCANNER_HOME = tool 'sonar-scanner'
        PROJECT_ID   = 'gke-springboot-472605'
        REGION       = 'asia-south1'
        ZONE         = 'asia-south1-b'
        CLUSTER      = 'gke-cluster'
        IMAGE_NAME   = 'springboot-app'
        REPO         = "asia-south1-docker.pkg.dev/${PROJECT_ID}/springboot-artifacts/${IMAGE_NAME}"
        
        Vault Configuration
        VAULT_ADDR  = 'http://34.180.3.84:8200'  // Replace with Vault host
        VAULT_TOKEN = credentials('vault-token')  // Jenkins secret for Vault token
    }

    tools {
        maven 'maven3'
        jdk 'jdk-21'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/mayur0522/CategoryProduct.git'
            }
        }

        stage('Fetch Secrets from Vault') {
            steps {
                script {
                    // Fetch DB credentials from Vault using CLI
                    sh """
                        export DB_CREDS=\$(vault kv get -format=json secret/categorydb)
                        export DB_USERNAME=\$(echo \$DB_CREDS | jq -r '.data.username')
                        export DB_PASSWORD=\$(echo \$DB_CREDS | jq -r '.data.password')
                        export DB_HOST=\$(echo \$DB_CREDS | jq -r '.data.host')
                        export DB_PORT=\$(echo \$DB_CREDS | jq -r '.data.port')
                        export DB_NAME=\$(echo \$DB_CREDS | jq -r '.data.database')
                    """
                    echo "✅ Fetched DB credentials from Vault"
                }
            }
        }

        stage('Build') {
            steps {
                sh "mvn clean package -DskipTests"
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonar') {
                    withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                        sh """
                            ${env.SCANNER_HOME}/bin/sonar-scanner \
                            -Dsonar.projectKey=springboot-app \
                            -Dsonar.projectName=springboot-app \
                            -Dsonar.java.binaries=target/classes \
                            -Dsonar.token=$SONAR_TOKEN
                        """
                    }
                }
            }
        }

        stage('OWASP Dependency Check') {
            steps {
                dependencyCheck additionalArguments: '--scan ./', odcInstallation: 'DC'
                dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
            }
        }

        stage('Deploy to Nexus') {
            steps {
                withMaven(globalMavenSettingsConfig: 'global-maven', jdk: 'jdk-21', maven: 'maven3') {
                    sh "mvn deploy -DskipTests"
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh """
                    docker build \
                        --build-arg DB_USERNAME=$DB_USERNAME \
                        --build-arg DB_PASSWORD=$DB_PASSWORD \
                        --build-arg DB_HOST=$DB_HOST \
                        --build-arg DB_PORT=$DB_PORT \
                        --build-arg DB_NAME=$DB_NAME \
                        -t ${REPO}:latest -f Dockerfile .
                """
            }
        }

        stage('Authenticate with GCP') {
            steps {
                withCredentials([file(credentialsId: 'gcp-service-account-key', variable: 'GOOGLE_APPLICATION_CREDENTIALS')]) {
                    sh 'gcloud auth activate-service-account --key-file=$GOOGLE_APPLICATION_CREDENTIALS'
                    sh "gcloud config set project ${PROJECT_ID}"
                    sh "gcloud auth configure-docker ${REGION}-docker.pkg.dev"
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                sh "docker push ${REPO}:latest"
            }
        }

        stage('Deploy to GKE') {
            steps {
                script {
                    // Set env variables for Kubernetes deployment if needed
                    sh 'kubectl apply -f k8s/deployment.yaml --record --wait'
                    sh 'kubectl apply -f k8s/service.yaml --record --wait'
                    echo "✅ Kubernetes resources applied successfully"
                }
            }
        }

    }

    post {
        success {
            echo '✅ Spring Boot project deployed successfully to GKE!'
        }
        failure {
            echo '❌ Deployment failed.'
        }
    }
}
