runServer:
	javac -cp .:../junit5.jar *.java

runTests:
	javac -cp .:../junit5.jar *.java
	java -jar ../junit5.jar --class-path=. --select-class=FrontendTests

clean:
	rm -r *.class
