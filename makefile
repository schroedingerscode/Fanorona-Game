all: main run

main: Fanorona.java Grid.java Piece.java
	javac Fanorona.java Grid.java Piece.java

run: Fanorona.class Grid.class Piece.class
		java Fanorona
