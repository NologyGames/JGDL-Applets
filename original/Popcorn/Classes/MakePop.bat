set path=c:\jdk1.3.1_01\bin
md JGDL
copy ..\..\JGDL\*.class JGDL
jar cvf SPM.jar *.class .\JGDL\*.class
erase .\JGDL\*.class
rem erase *.class
rd JGDL
SPM.html
