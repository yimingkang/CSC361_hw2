CC=javac
SRV=Server.java
CLI=Client.java

all: srv cli

srv:
	$(CC) $(SRV)
cli:
	$(CC) $(CLI)

clean:
	rm -f ./*.class
