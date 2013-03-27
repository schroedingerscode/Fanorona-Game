all: main run

main: Fanorona.java Grid.java Piece.java State.java StateMachine.java Vector.java
	javac Fanorona.java Grid.java Piece.java StateMachine.java State.java Vector.java

run: Fanorona.class Grid.class Piece.class StateMachine.class State.class Vector.class
		java Fanorona
