javac -classpath .;rogets.jar *.java
jar -cf LetterFrequencies.jar *.class *.properties *.csv
if "%1"=="zip" jar -cfM LetterFrequencies.zip *.jar *.txt *.bat *.java *.xls roget_elkb/*
