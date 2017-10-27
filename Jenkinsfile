pipeline {
    agent {
            dockerfile { args '-v /eicke/.m2:/root/.m2 --user root' }

        }

    stages {
        stage('Build') {
            steps {
                sh 'mvn -B -DskipTests clean package'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Integration Tests') {
            steps {
                sh 'mvn verify -Dskip.surefire.tests -e'
            }
        }
    }
}
