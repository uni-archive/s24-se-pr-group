# Backend Template for SE PR Group Phase

## How to run it

### Start the backed
`mvn spring-boot:run`

### Start the backed with test data
If the database is not clean, the test data won't be inserted

`mvn spring-boot:test-run
-Dspring-boot.run.mainClass=at.ac.tuwien.sepr.groupphase.backend.DevelopmentApplication
-f pom.xml`