CC=javac
CLI=OptionalClient.java
LIB=RunnableFileWriter.java RunnableSocketReader.java RunnableSocketWriter.java

all: lib cli

lib:
	$(CC) $(LIB)
cli:
	$(CC) $(CLI)

clean:
	rm -f ./*.class
