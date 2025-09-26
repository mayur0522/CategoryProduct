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

        VAULT_ADDR  = 'http://34.180.3.84:8200'  // Vault host
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
                    sh '''
                        export DB_CREDS=$(vault kv get -format=json secret/data/categorydb)
                        export DB_USERNAME=$(echo $DB_CREDS | jq -r '.data.data.username')
                        export DB_PASSWORD=$(echo $DB_CREDS | jq -r '.data.data.password')
                        export DB_HOST=$(echo $DB_CREDS | jq -r '.data.data.host')
                        export DB_PORT=$(echo $DB_CREDS | jq -r '.data.data.port')
                        export DB_NAME=$(echo $DB_CREDS | jq -r '.data.data.database')
                    '''
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
                sh '''
                    docker build \
                        --build-arg DB_USERNAME=$DB_USERNAME \
                        --build-arg DB_PASSWORD=$DB_PASSWORD \
                        --build-arg DB_HOST=$DB_HOST \
                        --build-arg DB_PORT=$DB_PORT \
                        --build-arg DB_NAME=$DB_NAME \
                        -t ${REPO}:latest -f Dockerfile .
                '''
            }
        }

        stage('Authenticate with GCP') {
            steps {
                withCredentials([file(credentialsId: 'gcp-service-account-key', variable: 'GOOGLE_APPLICATION_CREDENTIALS')]) {
                    sh 'gcloud auth activate-service-account --key-file=$GOOGLE_APPLICATION_CREDENTIALS'
                    sh "gcloud config set project ${PROJECT_ID}"
                    sh "gcloud auth configure-docker ${REGION}-docker.pkg.dev --quiet"
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
                    // Authenticate to GKE
                    sh "gcloud container clusters get-credentials ${CLUSTER} --zone ${ZONE} --project ${PROJECT_ID}"

                    try {
                        // Apply Deployment and Service
                        sh "kubectl apply -f k8s/deployment.yaml --wait --timeout=180s"
                        sh "kubectl apply -f k8s/service.yaml --wait --timeout=60s"
                        echo "✅ Kubernetes resources applied successfully"

                        // Wait for pods to be ready
                        sh "kubectl wait --for=condition=ready pod -l app=springboot-app --timeout=180s"
                        echo "✅ All pods are ready"

                    } catch (err) {
                        echo "❌ Deployment failed, fetching pod info and logs"
                        sh "kubectl get pods -l app=springboot-app -o wide"
                        sh "kubectl describe pod -l app=springboot-app"
                        sh "kubectl logs -l app=springboot-app --all-containers=true || true"
                        error("Deployment failed, see pod status/logs above")
                    }
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
