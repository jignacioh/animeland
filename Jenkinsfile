@Library('jenkins-shared-library')_
def err = null
List environment = ["GOOGLE_APPLICATION_CREDENTIALS=$HOME/.android/service-credential-animeland.json"]
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
				sh './gradlew compileReleaseSources'
			}
		  
		}
		stage('Build APK') {
			// Finish building and packaging the APK
			steps {
				sh './gradlew assembleRelease'

				// Archive the APKs so that they can be downloaded from Jenkins
				archiveArtifacts artifacts: '**/*.apk', fingerprint: true, onlyIfSuccessful: true   
			}
		  
		}
		stage('Distribute Firebase') {
			// Finish building and packaging the APK
			steps {
				withEnv(environment) {
					sh './gradlew assembleRelease appDistributionUploadRelease --stacktrace'   
				}
			}
		}
		
		stage('Distribute AppCenter') {
		    steps {
			appCenter apiToken: 'e95a24bb79c5401c532a391d7c1548737f7f57d0',
				ownerName: 'jignacioh',
				appName: 'Animeland',
				pathToApp: 'app/build/outputs/apk/release/app-release.apk',
				distributionGroups: 'test-group'
		    }
		}
	}
	post {
		always {
		   	 /* Use slackNotifier.groovy from shared library and provide current build result as parameter */  
			script {
		    		slackNotifier(currentBuild.currentResult)
			}
		    	//cleanWs()
		}
    	}
}
  
