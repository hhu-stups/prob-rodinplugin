.PHONY: build
build:
	./gradlew prepareMaven
	mvn -f de.prob.parent/pom.xml install
	echo "You can now update the ProB plugin from Rodin using the local update site file:/.../prob_rodin_plugin/de.prob.repository/target/repository/"