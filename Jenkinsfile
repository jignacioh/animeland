@Library('jenkins-shared-library')_
def err = null
List environment = ["GOOGLE_APPLICATIONS_CREDENTIALS=$HOME/.android/service-credential-animeland.json"]
pipeline {
	agent any
	stages {
		stage ('Clone sources') {
			steps {
				git url: 'https://github.com/jignacioh/animeland.git'
			} 	
		}
		stage('Clean Build') {
			steps {
		    		sh './gradlew clean'
			} 
		}
        	stage('Compile') {
			// Compile the app and its dependencies
			steps {
				sh './gradlew compileDebugSources'
			}
		  
		}
		stage('Build APK') {
			// Finish building and packaging the APK
			steps {
				sh './gradlew assembleDebug'

				// Archive the APKs so that they can be downloaded from Jenkins
				archiveArtifacts artifacts: '**/*.apk', fingerprint: true, onlyIfSuccessful: true   
			}
		  
		}
		stage('Distribute') {
			// Finish building and packaging the APK
			steps {
				withEnv(environment) {
					sh './gradlew assembleDebug appDistributionUploadDebug'   
				}
			}
		}
	}
	post {
		always {
		   	 /* Use slackNotifier.groovy from shared library and provide current build result as parameter */  
			script {
		    		slackNotifier(currentBuild.currentResult)
			}
		    	cleanWs()
		}
    	}
}
  
