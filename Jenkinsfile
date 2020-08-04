def err = null

node {
      try {
  
        stage('Clean Build') {
            dir("android") {
               sh './gradlew clean'
            }   
        }
       
        stage('Compile') {
		  steps {
			// Compile the app and its dependencies
			sh './gradlew compileDebugSources'
		  }
		}
		stage('Build APK') {
		  steps {
			// Finish building and packaging the APK
			sh './gradlew assembleDebug'

			// Archive the APKs so that they can be downloaded from Jenkins
			archiveArtifacts artifacts: '**/*.apk', fingerprint: true, onlyIfSuccessful: true   
		  }
		}
		
		}catch (caughtError) { 
    
			err = caughtError
			currentBuild.result = "FAILURE"

		} finally {
		
			if(currentBuild.result == "FAILURE"){
					  sh "echo 'Build FAILURE'"
			}else{
				 sh "echo 'Build SUCCESSFUL'"
			}
	   
		}
}
  
