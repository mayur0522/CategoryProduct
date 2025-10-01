pipeline {
    agent {
        docker {
            image 'gcr.io/google.com/cloudsdktool/cloud-sdk:latest'
            args '-u root:root'
        }
    }

    environment {
        SCANNER_HOME = tool 'sonar-scanner'
        PROJECT_ID   = 'springapp-gke'
        REGION       = 'asia-south1'
        ZONE         = 'asia-south1-b'
        CLUSTER      = 'gke-cluster'
        IMAGE_NAME   = 'springboot-app'
        REPO         = "asia-south1-docker.pkg.dev/${PROJECT_ID}/springboot-artifacts/${IMAGE_NAME}"
        PATH         = "/google-cloud-sdk/bin:${env.PATH}" // ensure plugin is found
    }

    stages {

        stage('Install JDK & Maven') {
            steps {
                sh """
                    apt-get update
                    apt-get install -y openjdk-21-jdk maven
                    java -version
                    mvn -version
                """
            }
        }

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/mayur0522/CategoryProduct.git'
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

        stage('Authenticate, Build & Push Docker Image') {
            steps {
                withCredentials([file(credentialsId: 'gcp-service-account-key', variable: 'GOOGLE_APPLICATION_CREDENTIALS')]) {
                    sh """
                        # Authenticate with GCP
                        gcloud auth activate-service-account --key-file=$GOOGLE_APPLICATION_CREDENTIALS
                        gcloud config set project ${PROJECT_ID}
                        gcloud auth configure-docker ${REGION}-docker.pkg.dev --quiet

                        # Build Docker image
                        docker build -t ${REPO}:latest -f Dockerfile .

                        # Push Docker image to Artifact Registry
                        docker push ${REPO}:latest
                    """
                }
            }
        }

        stage('Deploy to GKE') {
            steps {
                script {
                    sh """
                        # Ensure kubectl & plugin installed
                        gcloud components install kubectl gke-gcloud-auth-plugin --quiet

                        # Authenticate and fetch GKE credentials
                        gcloud container clusters get-credentials ${CLUSTER} --zone ${ZONE} --project ${PROJECT_ID}

                        # Apply Kubernetes manifests
                        kubectl apply -f k8s/spring-deployment.yaml --wait --validate=false
                        kubectl apply -f k8s/spring-service.yaml --wait --validate=false
                    """
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
