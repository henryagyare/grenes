This is a Kotlin Multiplatform project targeting Android, iOS, Server.

* The project was compiled with jdk 21

* `/server` is for the Ktor server application.
    * Firebase auth emulator must be running as a call is made to auth during server startup.
      A service-account.json file is required in the `server/src/resources` directory to run the
      server.
      Environment variables  `DB_USER` and `DB_PASSWORD` are required to connect to the database if
      using Google Cloud SQL.
      Testing locally with a local database does not require these environment variables and can be
      run
      with `./gradlew server:run` with command line arguments `-config=test_application.yaml` to use
      the
      local configuration.
      To upload the server to GAE, replace `/server/sample_app.yaml` with `/server/app.yaml` and
      fill in the required environment variables. Then run `./gradlew server:appengineDeploy` to
      deploy the


* `/admin` is vue js application for the admin dashboard.
    * The admin dashboard is a separate project and has a separate README.md file.


* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
    - `commonMain` is for code that’s common for all targets.
    - Other folders are for Kotlin code that will be compiled for only the platform indicated in the
      folder name.
    - To run the app, use the `:composeApp:run` Gradle task. You can also run the app from the
      command line with `./gradlew composeApp:run`. A flag to turn on the use of the emulator is
      found in `/composeApp/src/commonMain/kotlin/App.kt#18` and is set to `true` to
      use production environment. Set to `false` to use the emulator.
    - In production mode, `google-services.json` is required at the root of the `composeApp`
      directory.


* `/iosApp` contains iOS applications.
  > The app has not been tested on ios and is not guaranteed to work.

Learn more
about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…